/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;

/**
 * @author Christian
 *
 */
public class AttributeFilterCommand implements IHistoryCommand {

	protected final AEntityColumn column;
	protected final Set<Object> filteredElementIDs;

	public AttributeFilterCommand(AEntityColumn column, Set<Object> filteredElementIDs) {
		this.column = column;
		this.filteredElementIDs = filteredElementIDs;
	}

	@Override
	public Object execute() {
		column.getCollection().setFilteredItems(filteredElementIDs, column);
		column.updateSorting();

		return null;
		// column.getRelationshipExplorer().applyIDMappingUpdate(
		// new MappingFilterUpdateOperation(column.getBroadcastingIDsFromElementIDs(filteredElementIDs), column,
		// ESetOperation.REPLACE), true);
	}

}
