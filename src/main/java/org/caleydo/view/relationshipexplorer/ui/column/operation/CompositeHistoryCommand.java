/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.ArrayList;

import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;

/**
 * @author Christian
 *
 */
public class CompositeHistoryCommand extends ArrayList<IHistoryCommand> implements IHistoryCommand {

	/**
	 *
	 */
	private static final long serialVersionUID = 8920297872522595779L;

	@Override
	public Object execute() {
		for (IHistoryCommand c : this) {
			c.execute();
		}
		return null;
	}

}
