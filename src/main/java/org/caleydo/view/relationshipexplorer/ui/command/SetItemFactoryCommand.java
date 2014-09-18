/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.command;

import org.caleydo.view.relationshipexplorer.ui.History;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactoryCreator;

/**
 * @author Christian
 *
 */
public class SetItemFactoryCommand implements IHistoryCommand {

	protected final int columnHistoryID;
	protected final History history;
	protected final IItemFactoryCreator creator;
	protected final boolean addFactory;

	public SetItemFactoryCommand(AEntityColumn column, IItemFactoryCreator creator, History history,
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
			col.addItemFactoryCreator(creator);
		col.setItemFactoryCreator(creator);

		return null;
	}

	@Override
	public String getDescription() {
		AEntityColumn col = history.getHistoryObjectAs(AEntityColumn.class, columnHistoryID);
		return "Changed Item View for " + col.getLabel();
	}

}
