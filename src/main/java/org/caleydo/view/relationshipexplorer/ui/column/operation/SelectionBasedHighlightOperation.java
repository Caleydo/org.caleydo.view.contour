/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;

/**
 * @author Christian
 *
 */
public class SelectionBasedHighlightOperation extends ASelectionBasedOperation {

	/**
	 * @param selectedElementIDs
	 * @param selectedBroadcastIDs
	 * @param op
	 */
	public SelectionBasedHighlightOperation(Set<Object> selectedElementIDs, Set<Object> selectedBroadcastIDs,
			RelationshipExplorerElement relationshipExplorer) {
		super(selectedElementIDs, selectedBroadcastIDs, ESetOperation.INTERSECTION, relationshipExplorer);
	}

	@Override
	public void execute(IEntityCollection collection) {
		collection.setSelectedItems(selectedElementIDs);

		relationshipExplorer.applyIDMappingUpdate(
				new MappingSelectionUpdateOperation(selectedBroadcastIDs, collection), true);
	}

}
