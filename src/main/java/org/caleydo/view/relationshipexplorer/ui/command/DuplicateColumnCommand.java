/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.command;

import org.caleydo.view.relationshipexplorer.ui.History;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.MappingSummaryItemFactory;
import org.caleydo.view.relationshipexplorer.ui.list.ColumnTree;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;
import org.caleydo.view.relationshipexplorer.ui.list.NestableColumn;

/**
 * @author Christian
 *
 */
public class DuplicateColumnCommand implements IHistoryCommand {

	protected final History history;
	protected final ConTourElement relationshipExplorer;
	protected final int columnHistoryID;

	public DuplicateColumnCommand(AEntityColumn column, ConTourElement relationshipExplorer) {
		this.relationshipExplorer = relationshipExplorer;
		this.history = relationshipExplorer.getHistory();
		this.columnHistoryID = column.getHistoryID();
	}

	@Override
	public Object execute() {
		AEntityColumn column = history.getHistoryObjectAs(AEntityColumn.class, columnHistoryID);
		AEntityColumn duplicate = null;
		if (column.getColumn().isRoot()) {
			AddColumnTreeCommand c = new AddColumnTreeCommand(column.getCollection(), relationshipExplorer);
			ColumnTree columnTree = (ColumnTree) c.execute();
			duplicate = (AEntityColumn) columnTree.getRootColumn().getColumnModel();
		} else {
			AddChildColumnCommand c = new AddChildColumnCommand(column.getCollection(), column.getColumn().getParent()
					.getColumnModel().getHistoryID(), relationshipExplorer);
			NestableColumn col = (NestableColumn) c.execute();
			duplicate = (AEntityColumn) col.getColumnModel();
		}

		// TODO: Add all present summary item factories, copy comparators
		if (!(column.getSummaryItemFactory() instanceof MappingSummaryItemFactory)) {
			SetSummaryItemFactoryCommand c = new SetSummaryItemFactoryCommand(duplicate, column.getSummaryItemFactory()
					.getClass(), history, false);
			c.execute();
		}

		return null;
	}

	@Override
	public String getDescription() {
		IColumnModel model = history.getHistoryObjectAs(IColumnModel.class, columnHistoryID);
		return "Duplicated column " + model.getLabel();
	}

}
