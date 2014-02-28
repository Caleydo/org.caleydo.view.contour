/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import org.caleydo.view.relationshipexplorer.ui.History;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.column.MedianSummaryItemFactory;
import org.caleydo.view.relationshipexplorer.ui.column.TabularDataColumn;

/**
 * @author Christian
 *
 */
public class SetSummaryItemFactoryCommand implements IHistoryCommand {

	protected final int tabularColumnHistoryID;
	protected final History history;

	public SetSummaryItemFactoryCommand(int tabularColumnHistoryID, History history) {
		this.tabularColumnHistoryID = tabularColumnHistoryID;
		this.history = history;
	}

	@Override
	public Object execute() {
		// FIXME parameterize factory type

		TabularDataColumn col = history.getHistoryObjectAs(TabularDataColumn.class, tabularColumnHistoryID);
		MedianSummaryItemFactory f = new MedianSummaryItemFactory(col);
		col.addSummaryItemFactory(f);
		col.setSummaryItemFactory(f);
		return null;
	}

}
