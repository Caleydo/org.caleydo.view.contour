/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityCollection;

/**
 * @author Christian
 *
 */
public class SelectionBasedFilterOperation extends ASelectionBasedOperation {

	/**
	 * @param selectedElementIDs
	 * @param selectedBroadcastIDs
	 * @param op
	 */
	public SelectionBasedFilterOperation(Set<Object> selectedElementIDs, Set<Object> selectedBroadcastIDs,
			ESetOperation op, RelationshipExplorerElement relationshipExplorer) {
		super(selectedElementIDs, selectedBroadcastIDs, op, relationshipExplorer);
	}

	@Override
	public void execute(IEntityCollection collection) {
		// Set<Object> broadcastIDs = new HashSet<>();
		// Set<Object> elementIDs = new HashSet<>();
		// for (GLElement element : column.itemList.getSelectedElements()) {
		// Object elementID = column.mapIDToElement.inverse().get(element);
		// elementIDs.add(elementID);
		// broadcastIDs.addAll(column.getBroadcastingIDsFromElementID(elementID));
		// }

		collection.setFilteredItems(setOperation.apply(selectedElementIDs, collection.getFilteredElementIDs()));
		relationshipExplorer.applyIDMappingUpdate(new MappingFilterUpdateOperation(selectedBroadcastIDs, collection,
				setOperation), true);
		SelectionBasedHighlightOperation o = new SelectionBasedHighlightOperation(selectedElementIDs,
				selectedBroadcastIDs, relationshipExplorer);
		o.execute(collection);
	}

}
