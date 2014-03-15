/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import org.caleydo.view.relationshipexplorer.ui.History;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.detail.CompoundDetailViewWindow;

/**
 * @author Christian
 *
 */
public class UpdateDetailContentWithSelectionCommand implements IHistoryCommand {

	protected final int detailWindowHistoryID;
	protected final boolean update;
	protected final History history;

	public UpdateDetailContentWithSelectionCommand(CompoundDetailViewWindow window, boolean update, History history) {
		this.detailWindowHistoryID = window.getHistoryID();
		this.update = update;
		this.history = history;
	}

	@Override
	public Object execute() {
		CompoundDetailViewWindow window = history.getHistoryObjectAs(CompoundDetailViewWindow.class,
				detailWindowHistoryID);
		window.changeViewOnSelection(update);
		return null;
	}

	@Override
	public String getDescription() {
		CompoundDetailViewWindow window = history.getHistoryObjectAs(CompoundDetailViewWindow.class,
				detailWindowHistoryID);
		return "Detail view of " + window.getCollection().getLabel()
				+ (update ? " updates with selections" : " does not update with selections");
	}
}
