/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.Comparator;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;

/**
 * @author Christian
 *
 */
public class ColumnSortingCommand implements IHistoryCommand {

	protected final AEntityColumn column;
	protected final Comparator<GLElement> comparator;

	public ColumnSortingCommand(AEntityColumn column, Comparator<GLElement> comparator) {
		this.column = column;
		this.comparator = comparator;
	}

	@Override
	public void execute() {
		column.sort(comparator);
	}

}
