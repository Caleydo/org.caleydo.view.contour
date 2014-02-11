/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import java.util.ArrayList;

/**
 * @author Christian
 *
 */
public class CompositeContextMenuCommand extends ArrayList<IContextMenuCommand> implements IContextMenuCommand {

	/**
	 *
	 */
	private static final long serialVersionUID = 2088873633223666782L;

	/**
	 *
	 */
	public CompositeContextMenuCommand() {
	}

	public CompositeContextMenuCommand(IContextMenuCommand... commands) {
		for (IContextMenuCommand c : commands) {
			add(c);
		}
	}

	@Override
	public void execute() {
		for (IContextMenuCommand c : this) {
			c.execute();
		}
	}

}
