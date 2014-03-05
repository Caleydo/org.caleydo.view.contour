/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import org.caleydo.view.relationshipexplorer.ui.History;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;

/**
 * @author Christian
 *
 */
public class ShowDetailCommand implements IHistoryCommand {

	protected final int entityColumnHistoryID;
	protected final History history;

	public ShowDetailCommand(AEntityColumn column, History history) {
		this.entityColumnHistoryID = column.getHistoryID();
		this.history = history;
	}

	@Override
	public Object execute() {
		AEntityColumn column = history.getHistoryObjectAs(AEntityColumn.class, entityColumnHistoryID);
		column.showDetailView();
		return null;
	}

	@Override
	public String getDescription() {
		AEntityColumn column = history.getHistoryObjectAs(AEntityColumn.class, entityColumnHistoryID);
		return "Shoe Detail View of " + column.getLabel();
	}

}
