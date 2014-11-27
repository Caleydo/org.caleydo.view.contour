/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.util;

import org.caleydo.view.relationshipexplorer.ui.History;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;

/**
 * @author Christian
 *
 */
public class AddHistoryCommandRunnable implements Runnable {

	private final IHistoryCommand command;
	private final History history;

	public AddHistoryCommandRunnable(IHistoryCommand command, History history) {
		this.command = command;
		this.history = history;
	}

	@Override
	public void run() {
		command.execute();
		history.addHistoryCommand(command);
	}

}
