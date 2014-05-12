/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.command;

import org.caleydo.view.relationshipexplorer.ui.History;
import org.caleydo.view.relationshipexplorer.ui.column.IInvertibleComparator;
import org.caleydo.view.relationshipexplorer.ui.column.IScoreProvider;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;

/**
 * @author Christian
 *
 */
public class ReverseColumnSortingCommand extends ColumnSortingCommand {

	/**
	 * @param model
	 * @param comparator
	 * @param scoreProvider
	 * @param history
	 */
	public ReverseColumnSortingCommand(IColumnModel model, IInvertibleComparator<NestableItem> comparator,
			IScoreProvider scoreProvider, History history) {
		super(model, comparator.getInverted(), scoreProvider, history);
	}

	@Override
	public String getDescription() {
		IColumnModel column = history.getHistoryObjectAs(IColumnModel.class, columnHistoryID);
		return "Reversed sorting of " + column.getLabel() + " column";
	}

}
