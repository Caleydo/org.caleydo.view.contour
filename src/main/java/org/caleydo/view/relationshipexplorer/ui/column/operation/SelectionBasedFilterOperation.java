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
public class SelectionBasedFilterOperation extends ASelectionBasedOperation {

	protected final int representationHistoryID;

	/**
	 * @param selectedElementIDs
	 * @param selectedBroadcastIDs
	 * @param op
	 */
	public SelectionBasedFilterOperation(int representationHistoryID, Set<Object> selectedElementIDs,
			Set<Object> selectedBroadcastIDs, ESetOperation op, RelationshipExplorerElement relationshipExplorer) {
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
				setOperation.apply(selectedElementIDs, representation.getCollection().getFilteredElementIDs()),
				representation);
		relationshipExplorer.applyIDMappingUpdate(new MappingFilterUpdateOperation(selectedBroadcastIDs,
				representation, setOperation), true);
		SelectionBasedHighlightOperation o = new SelectionBasedHighlightOperation(representationHistoryID,
				selectedElementIDs, selectedBroadcastIDs, relationshipExplorer);
		o.execute();
		return null;
	}

}
