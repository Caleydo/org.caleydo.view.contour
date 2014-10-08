/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.Iterator;
import java.util.Set;

import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;

/**
 * @author Christian
 *
 */
public class SelectionBasedHighlightOperation extends ASelectionBasedOperation {

	protected final int representationHistoryID;

	/**
	 * @param selectedElementIDs
	 * @param selectedBroadcastIDs
	 * @param op
	 */
	public SelectionBasedHighlightOperation(IEntityCollection sourceCollection, int representationHistoryID,
			Set<Object> selectedElementIDs, Set<Object> selectedBroadcastIDs, IDType broadcastIDType,
			ConTourElement relationshipExplorer) {
		super(sourceCollection, selectedElementIDs, selectedBroadcastIDs, broadcastIDType, ESetOperation.INTERSECTION,
				relationshipExplorer);
		this.representationHistoryID = representationHistoryID;
	}

	@Override
	public Object execute() {
		ILabeled representation = relationshipExplorer.getHistory().getHistoryObjectAs(ILabeled.class,
				representationHistoryID);
		if (sourceCollection != null)
			sourceCollection.setSelectedItems(selectedElementIDs);

		relationshipExplorer.applyIDMappingUpdate(new MappingSelectionUpdateOperation(sourceCollection,
				selectedBroadcastIDs, broadcastIDType, representation, relationshipExplorer
						.getMultiItemSelectionSetOperation(), relationshipExplorer.getEntityCollections()));
		return null;
	}

	@Override
	public String getDescription() {

		IEntityRepresentation representation = relationshipExplorer.getHistory().getHistoryObjectAs(
				IEntityRepresentation.class, representationHistoryID);

		StringBuilder b = new StringBuilder();

		b.append("Selected ").append(representation.getCollection().getLabel()).append(":\n");

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

}
