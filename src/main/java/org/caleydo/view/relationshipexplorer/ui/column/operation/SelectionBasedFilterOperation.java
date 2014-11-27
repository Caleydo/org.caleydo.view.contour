/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryIDOwner;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.filter.IFilterCommand;

/**
 * @author Christian
 *
 */
public class SelectionBasedFilterOperation extends ASelectionBasedOperation implements IFilterCommand {

	protected final int representationHistoryID;
	protected Set<IEntityCollection> targetCollections = new HashSet<>();
	protected final ESetOperation multiItemSelectionSetOperation;

	/**
	 * @param selectedElementIDs
	 * @param selectedBroadcastIDs
	 * @param op
	 */
	public SelectionBasedFilterOperation(IEntityCollection sourceCollection, int representationHistoryID,
			Set<Object> selectedElementIDs, Set<Object> selectedBroadcastIDs, IDType broadcastIDType, ESetOperation op,
			ConTourElement relationshipExplorer) {
		super(sourceCollection, selectedElementIDs, selectedBroadcastIDs, broadcastIDType, op, relationshipExplorer);
		this.representationHistoryID = representationHistoryID;
		targetCollections = new HashSet<>(relationshipExplorer.getEntityCollections());
		multiItemSelectionSetOperation = relationshipExplorer.getMultiItemSelectionSetOperation();
	}

	@Override
	public Object execute() {
		// Set<Object> broadcastIDs = new HashSet<>();
		// Set<Object> elementIDs = new HashSet<>();
		// for (GLElement element : column.itemList.getSelectedElements()) {
		// Object elementID = column.mapIDToElement.inverse().get(element);
		// elementIDs.add(elementID);
		// broadcastIDs.addAll(column.getBroadcastingIDsFromElementID(elementID));
		// }

		ILabeled representation = relationshipExplorer.getHistory().getHistoryObjectAs(ILabeled.class,
				representationHistoryID);
		// IEntityCollection sourceCollection = representation.getCollection();
		//
		if (sourceCollection != null && targetCollections.contains(sourceCollection)) {
			sourceCollection.setFilteredItems(setOperation.apply(selectedElementIDs,
					sourceCollection.getFilteredElementIDs()));
		}

		relationshipExplorer.applyIDMappingUpdate(new MappingFilterUpdateOperation(sourceCollection,
				selectedBroadcastIDs, broadcastIDType, representation, setOperation, multiItemSelectionSetOperation,
				targetCollections));
		SelectionBasedHighlightOperation o = new SelectionBasedHighlightOperation(sourceCollection,
				representationHistoryID,
				selectedElementIDs, selectedBroadcastIDs, broadcastIDType, relationshipExplorer);
		o.execute();

		relationshipExplorer.getFilterPipeline().addFilterCommand(this);
		return null;
	}

	@Override
	public String getDescription() {

		IHistoryIDOwner representation = relationshipExplorer.getHistory().getHistoryObjectAs(IHistoryIDOwner.class,
				representationHistoryID);

		StringBuilder b = new StringBuilder();
		switch (setOperation) {
		case INTERSECTION:
			b.append("Filtered relationships based on selected ").append(representation.getLabel())
					.append(":\n");
			break;
		// case REPLACE:
		// b.append("Replaced relationships based on selected ").append(representation.getCollection().getLabel())
		// .append(":\n");
		// break;
		case UNION:
			b.append("Added relationships based on selected ").append(representation.getLabel())
					.append(":\n");
			break;
		case REMOVE:
			b.append("Removed relationships based on selected ").append(representation.getLabel())
					.append(":\n");
			break;
		default:
			return "";
		}

		Iterator<Object> it = selectedElementIDs.iterator();
		for (int i = 0; i < selectedElementIDs.size() && i < 3; i++) {
			b.append(it.next());
			if (i < selectedElementIDs.size() - 1 && i < 2) {
				b.append(", ");
			}
		}

		if (selectedElementIDs.size() > 3)
			b.append("...");

		return b.toString();
	}

	@Override
	public ILabeled getSource() {
		ILabeled representation = relationshipExplorer.getHistory().getHistoryObjectAs(ILabeled.class,
				representationHistoryID);
		return representation;
	}

	/**
	 * @param targetCollections
	 *            setter, see {@link targetCollections}
	 */
	public void setTargetCollections(Set<IEntityCollection> targetCollections) {
		this.targetCollections = targetCollections;
	}

	public void addTargetCollection(IEntityCollection collection) {
		targetCollections.add(collection);
	}

	@Override
	public Set<IEntityCollection> getTargetCollections() {
		return targetCollections;
	}

}
