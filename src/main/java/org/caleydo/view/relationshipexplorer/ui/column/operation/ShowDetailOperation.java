/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;

/**
 * @author Christian
 *
 */
public class ShowDetailOperation implements IHistoryCommand {

	protected AEntityColumn column;

	public ShowDetailOperation(AEntityColumn column) {
		this.column = column;
	}

	@Override
	public void execute() {
		column.showDetailView();

	}

}
