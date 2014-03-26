/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.Iterator;
import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
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
	public SelectionBasedHighlightOperation(int representationHistoryID, Set<Object> selectedElementIDs,
			Set<Object> selectedBroadcastIDs, RelationshipExplorerElement relationshipExplorer) {
		super(selectedElementIDs, selectedBroadcastIDs, ESetOperation.INTERSECTION, relationshipExplorer);
		this.representationHistoryID = representationHistoryID;
	}

	@Override
	public Object execute() {
		IEntityRepresentation representation = relationshipExplorer.getHistory().getHistoryObjectAs(
				IEntityRepresentation.class, representationHistoryID);
		representation.getCollection().setSelectedItems(selectedElementIDs);

		relationshipExplorer.applyIDMappingUpdate(new MappingSelectionUpdateOperation(selectedBroadcastIDs,
				representation, relationshipExplorer.getMultiItemSelectionSetOperation()));
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
