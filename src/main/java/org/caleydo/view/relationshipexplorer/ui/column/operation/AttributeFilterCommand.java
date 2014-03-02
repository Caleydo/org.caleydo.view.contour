/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.History;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityCollection;

/**
 * @author Christian
 *
 */
public class AttributeFilterCommand implements IHistoryCommand {

	protected final IEntityCollection collection;
	protected final Set<Object> filteredElementIDs;
	protected final History history;
	protected final int columnHistoryID;

	public AttributeFilterCommand(AEntityColumn column, Set<Object> filteredElementIDs, History history) {
		this.columnHistoryID = column.getHistoryID();
		this.collection = column.getCollection();
		this.filteredElementIDs = filteredElementIDs;
		this.history = history;
	}

	@Override
	public Object execute() {
		AEntityColumn column = history.getHistoryObjectAs(AEntityColumn.class, columnHistoryID);
		collection.setFilteredItems(filteredElementIDs, column);
		column.updateSorting();
		column.getRelationshipExplorer().applyIDMappingUpdate(
				new MappingFilterUpdateOperation(collection.getBroadcastingIDsFromElementIDs(filteredElementIDs),
						column,
						ESetOperation.INTERSECTION), true);
		return null;

	}

}
