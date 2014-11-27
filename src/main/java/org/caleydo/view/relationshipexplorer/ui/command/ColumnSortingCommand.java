/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.command;

import org.caleydo.view.relationshipexplorer.ui.History;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.column.IInvertibleComparator;
import org.caleydo.view.relationshipexplorer.ui.column.IScoreProvider;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;

/**
 * @author Christian
 *
 */
public class ColumnSortingCommand implements IHistoryCommand {

	protected final int columnHistoryID;
	protected final IInvertibleComparator<NestableItem> comparator;
	protected final IScoreProvider scoreProvider;
	protected final History history;

	public ColumnSortingCommand(IColumnModel model, IInvertibleComparator<NestableItem> comparator,
			IScoreProvider scoreProvider,
			History history) {
		this.columnHistoryID = model.getHistoryID();
		this.comparator = comparator;
		this.history = history;
		this.scoreProvider = scoreProvider;
	}

	@Override
	public Object execute() {
		IColumnModel column = history.getHistoryObjectAs(IColumnModel.class, columnHistoryID);
		column.setScoreProvider(scoreProvider);
		column.sortBy(comparator);
		return null;
	}

	@Override
	public String getDescription() {
		IColumnModel column = history.getHistoryObjectAs(IColumnModel.class, columnHistoryID);
		return "Sorting of " + column.getLabel() + " column";
	}

}
