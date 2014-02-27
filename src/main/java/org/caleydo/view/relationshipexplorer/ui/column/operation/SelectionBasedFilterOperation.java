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

	protected IEntityRepresentation representation;

	/**
	 * @param selectedElementIDs
	 * @param selectedBroadcastIDs
	 * @param op
	 */
	public SelectionBasedFilterOperation(IEntityRepresentation representation, Set<Object> selectedElementIDs,
			Set<Object> selectedBroadcastIDs, ESetOperation op, RelationshipExplorerElement relationshipExplorer) {
		super(selectedElementIDs, selectedBroadcastIDs, op, relationshipExplorer);
		this.representation = representation;
	}

	public void execute() {
		// Set<Object> broadcastIDs = new HashSet<>();
		// Set<Object> elementIDs = new HashSet<>();
		// for (GLElement element : column.itemList.getSelectedElements()) {
		// Object elementID = column.mapIDToElement.inverse().get(element);
		// elementIDs.add(elementID);
		// broadcastIDs.addAll(column.getBroadcastingIDsFromElementID(elementID));
		// }

		representation.getCollection().setFilteredItems(
				setOperation.apply(selectedElementIDs, representation.getCollection().getFilteredElementIDs()),
				representation);
		relationshipExplorer.applyIDMappingUpdate(new MappingFilterUpdateOperation(selectedBroadcastIDs,
				representation, setOperation), true);
		SelectionBasedHighlightOperation o = new SelectionBasedHighlightOperation(representation, selectedElementIDs,
				selectedBroadcastIDs, relationshipExplorer);
		o.execute();
	}

}
