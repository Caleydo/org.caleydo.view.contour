/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

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
				representation));
		return null;
	}

}
