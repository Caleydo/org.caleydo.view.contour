/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.command;

import org.caleydo.view.relationshipexplorer.ui.History;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.ISummaryItemFactoryCreator;

/**
 * @author Christian
 *
 */
public class SetSummaryItemFactoryCommand implements IHistoryCommand {

	protected final int columnHistoryID;
	protected final History history;
	protected final ISummaryItemFactoryCreator creator;
	protected final boolean addFactory;

	public SetSummaryItemFactoryCommand(AEntityColumn column, ISummaryItemFactoryCreator creator, History history,
			boolean addFactory) {
		this.columnHistoryID = column.getHistoryID();
		this.creator = creator;
		this.history = history;
		this.addFactory = addFactory;
	}

	@Override
	public Object execute() {
		AEntityColumn col = history.getHistoryObjectAs(AEntityColumn.class, columnHistoryID);

		if (addFactory)
			col.addSummaryItemFactoryCreator(creator);
		col.setSummaryItemFactoryCreator(creator);

		return null;
	}

	@Override
	public String getDescription() {
		AEntityColumn col = history.getHistoryObjectAs(AEntityColumn.class, columnHistoryID);
		return "Changed Summary Item View for " + col.getLabel();
	}

}
