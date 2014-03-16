/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.command;

import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.History;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.column.operation.ESetOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.MappingFilterUpdateOperation;
import org.caleydo.view.relationshipexplorer.ui.filter.FilterUtil;
import org.caleydo.view.relationshipexplorer.ui.filter.IEntityFilter;
import org.caleydo.view.relationshipexplorer.ui.filter.IFilterCommand;

/**
 * @author Christian
 *
 */
public class AttributeFilterCommand implements IFilterCommand {

	protected final IEntityCollection collection;
	// protected final Set<Object> filteredElementIDs;
	protected final IEntityFilter filter;
	protected final History history;
	protected final int columnHistoryID;
	protected final ESetOperation setOperation;

	public AttributeFilterCommand(AEntityColumn column, IEntityFilter filter, ESetOperation setOperation,
			History history) {
		this.columnHistoryID = column.getHistoryID();
		this.collection = column.getCollection();
		this.filter = filter;
		this.history = history;
		this.setOperation = setOperation;
	}

	@Override
	public Object execute() {
		AEntityColumn column = history.getHistoryObjectAs(AEntityColumn.class, columnHistoryID);

		Set<Object> filteredElementIDs = FilterUtil.filter(collection.getFilteredElementIDs(), filter);
		collection.setFilteredItems(filteredElementIDs);
		// column.updateSorting();
		column.getRelationshipExplorer().applyIDMappingUpdate(
				new MappingFilterUpdateOperation(collection.getBroadcastingIDsFromElementIDs(filteredElementIDs),
						column, setOperation));

		column.getRelationshipExplorer().getFilterPipeline().addFilterCommand(this);
		return null;

	}

	@Override
	public String getDescription() {
		return filter.getDescription();
	}

	@Override
	public IEntityCollection getCollection() {
		return collection;
	}

	@Override
	public ESetOperation getSetOperation() {
		return setOperation;
	}

}
