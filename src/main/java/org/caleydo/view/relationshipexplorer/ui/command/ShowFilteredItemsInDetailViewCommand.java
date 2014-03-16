/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.command;

import org.caleydo.view.relationshipexplorer.ui.History;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.detail.DetailViewWindow;
import org.caleydo.view.relationshipexplorer.ui.detail.IShowFilteredItemsListener;

/**
 * @author Christian
 *
 */
public class ShowFilteredItemsInDetailViewCommand implements IHistoryCommand {

	protected final int listenerHistoryID;
	protected final int windowHistoryID;
	protected final boolean show;
	protected final History history;

	public ShowFilteredItemsInDetailViewCommand(IShowFilteredItemsListener listener, DetailViewWindow window,
			boolean show, History history) {
		this.listenerHistoryID = listener.getHistoryID();
		this.windowHistoryID = window.getHistoryID();
		this.show = show;
		this.history = history;
	}

	@Override
	public Object execute() {
		IShowFilteredItemsListener listener = history.getHistoryObjectAs(IShowFilteredItemsListener.class,
				listenerHistoryID);
		DetailViewWindow window = history.getHistoryObjectAs(DetailViewWindow.class, windowHistoryID);
		listener.showFilteredItems(show);
		window.selectShowFilteredItemsButton(show);
		return null;
	}

	@Override
	public String getDescription() {
		DetailViewWindow window = history.getHistoryObjectAs(DetailViewWindow.class, windowHistoryID);
		return "Detail view of " + window.getCollection().getLabel()
				+ (show ? " shows only selected items" : " shows all items");
	}

}
