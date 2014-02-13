/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.column.operation.ASetBasedColumnOperation.ESetOperation;

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
			ESetOperation op) {
		super(selectedElementIDs, selectedBroadcastIDs, op);
	}

	@Override
	public void execute(AEntityColumn column) {
		// Set<Object> broadcastIDs = new HashSet<>();
		// Set<Object> elementIDs = new HashSet<>();
		// for (GLElement element : column.itemList.getSelectedElements()) {
		// Object elementID = column.mapIDToElement.inverse().get(element);
		// elementIDs.add(elementID);
		// broadcastIDs.addAll(column.getBroadcastingIDsFromElementID(elementID));
		// }

		column.setFilteredItems(setOperation.apply(selectedElementIDs, column.getFilteredElementIDs()));
		column.getRelationshipExplorer().applyIDMappingUpdate(
				new MappingFilterUpdateOperation(selectedBroadcastIDs, column,
				setOperation), true);
		SelectionBasedHighlightOperation o = new SelectionBasedHighlightOperation(selectedElementIDs,
				selectedBroadcastIDs, false);
		o.execute(column);
	}

}
