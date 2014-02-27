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

	protected IEntityRepresentation representation;

	/**
	 * @param selectedElementIDs
	 * @param selectedBroadcastIDs
	 * @param op
	 */
	public SelectionBasedHighlightOperation(IEntityRepresentation representation, Set<Object> selectedElementIDs,
			Set<Object> selectedBroadcastIDs, RelationshipExplorerElement relationshipExplorer) {
		super(selectedElementIDs, selectedBroadcastIDs, ESetOperation.INTERSECTION, relationshipExplorer);
		this.representation = representation;
	}

	public void execute() {
		representation.getCollection().setSelectedItems(selectedElementIDs, representation);

		relationshipExplorer.applyIDMappingUpdate(new MappingSelectionUpdateOperation(selectedBroadcastIDs,
				representation), true);
	}

}
