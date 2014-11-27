/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.command;

import org.caleydo.view.relationshipexplorer.ui.History;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.detail.DetailViewWindow;
import org.caleydo.view.relationshipexplorer.ui.detail.IShowSelectedItemsListener;

/**
 * @author Christian
 *
 */
public class UpdateDetailContentWithSelectionCommand implements IHistoryCommand {

	protected final int listenerHistoryID;
	protected final int windowHistoryID;
	protected final boolean update;
	protected final History history;

	public UpdateDetailContentWithSelectionCommand(IShowSelectedItemsListener listener, DetailViewWindow window,
			boolean update, History history) {
		this.listenerHistoryID = listener.getHistoryID();
		this.windowHistoryID = window.getHistoryID();
		this.update = update;
		this.history = history;
	}

	@Override
	public Object execute() {
		IShowSelectedItemsListener listener = history.getHistoryObjectAs(IShowSelectedItemsListener.class,
				listenerHistoryID);
		DetailViewWindow window = history.getHistoryObjectAs(DetailViewWindow.class, windowHistoryID);
		listener.showSelectedItems(update);
		window.selectShowSelectedItemsButton(update);
		return null;
	}

	@Override
	public String getDescription() {
		DetailViewWindow window = history.getHistoryObjectAs(DetailViewWindow.class, windowHistoryID);
		return "Detail view of " + window.getCollection().getLabel()
				+ (update ? " updates with selections" : " does not update with selections");
	}
}
