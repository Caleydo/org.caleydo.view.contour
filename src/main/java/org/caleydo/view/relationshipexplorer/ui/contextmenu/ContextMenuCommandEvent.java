/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.contextmenu;

import org.caleydo.core.event.ADirectedEvent;

/**
 * @author Christian
 *
 */
public class ContextMenuCommandEvent extends ADirectedEvent {

	protected final IContextMenuCommand command;

	public ContextMenuCommandEvent(IContextMenuCommand command) {
		this.command = command;
	}

	/**
	 * @return the command, see {@link #command}
	 */
	public IContextMenuCommand getCommand() {
		return command;
	}
}
