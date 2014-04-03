/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.Iterator;
import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;
import org.caleydo.view.relationshipexplorer.ui.filter.IFilterCommand;

/**
 * @author Christian
 *
 */
public class SelectionBasedFilterOperation extends ASelectionBasedOperation implements IFilterCommand {

	protected final int representationHistoryID;

	/**
	 * @param selectedElementIDs
	 * @param selectedBroadcastIDs
	 * @param op
	 */
	public SelectionBasedFilterOperation(int representationHistoryID, Set<Object> selectedElementIDs,
			Set<Object> selectedBroadcastIDs, ESetOperation op, ConTourElement relationshipExplorer) {
		super(selectedElementIDs, selectedBroadcastIDs, op, relationshipExplorer);
		this.representationHistoryID = representationHistoryID;
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

		IEntityRepresentation representation = relationshipExplorer.getHistory().getHistoryObjectAs(
				IEntityRepresentation.class, representationHistoryID);

		representation.getCollection().setFilteredItems(
				setOperation.apply(selectedElementIDs, representation.getCollection().getFilteredElementIDs()));
		relationshipExplorer.applyIDMappingUpdate(new MappingFilterUpdateOperation(selectedBroadcastIDs,
				representation, setOperation, relationshipExplorer.getMultiItemSelectionSetOperation()));
		SelectionBasedHighlightOperation o = new SelectionBasedHighlightOperation(representationHistoryID,
				selectedElementIDs, selectedBroadcastIDs, relationshipExplorer);
		o.execute();

		relationshipExplorer.getFilterPipeline().addFilterCommand(this);
		return null;
	}

	@Override
	public String getDescription() {

		IEntityRepresentation representation = relationshipExplorer.getHistory().getHistoryObjectAs(
				IEntityRepresentation.class, representationHistoryID);

		StringBuilder b = new StringBuilder();
		switch (setOperation) {
		case INTERSECTION:
			b.append("Filtered relationships based on selected ").append(representation.getCollection().getLabel())
					.append(":\n");
			break;
		case REPLACE:
			b.append("Replaced relationships based on selected ").append(representation.getCollection().getLabel())
					.append(":\n");
			break;
		case UNION:
			b.append("Added relationships based on selected ").append(representation.getCollection().getLabel())
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
	public IEntityCollection getCollection() {
		IEntityRepresentation representation = relationshipExplorer.getHistory().getHistoryObjectAs(
				IEntityRepresentation.class, representationHistoryID);
		return representation.getCollection();
	}

}
