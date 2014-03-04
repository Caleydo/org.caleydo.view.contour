/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.Comparator;

import org.caleydo.view.relationshipexplorer.ui.History;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;

/**
 * @author Christian
 *
 */
public class ColumnSortingCommand implements IHistoryCommand {

	protected final int columnHistoryID;
	protected final Comparator<NestableItem> comparator;
	protected final History history;

	public ColumnSortingCommand(IColumnModel model, Comparator<NestableItem> comparator, History history) {
		this.columnHistoryID = model.getHistoryID();
		this.comparator = comparator;
		this.history = history;
	}

	@Override
	public Object execute() {
		IColumnModel column = history.getHistoryObjectAs(IColumnModel.class, columnHistoryID);
		column.sortBy(comparator);
		return null;
	}

}
