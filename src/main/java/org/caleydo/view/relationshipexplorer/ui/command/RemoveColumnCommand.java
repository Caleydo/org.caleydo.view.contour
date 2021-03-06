/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.command;

import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;

/**
 * @author Christian
 *
 */
public class RemoveColumnCommand implements IHistoryCommand {

	protected final ConTourElement relationshipExplorer;
	protected final int columnHistoryID;

	public RemoveColumnCommand(IColumnModel model, ConTourElement relationshipExplorer) {
		this.relationshipExplorer = relationshipExplorer;
		this.columnHistoryID = model.getHistoryID();
	}

	@Override
	public Object execute() {
		IColumnModel model = relationshipExplorer.getHistory().getHistoryObjectAs(IColumnModel.class, columnHistoryID);
		model.getColumn().remove();
		relationshipExplorer.relayout();
		return null;
	}

	@Override
	public String getDescription() {
		IColumnModel model = relationshipExplorer.getHistory().getHistoryObjectAs(IColumnModel.class, columnHistoryID);
		return "Removed " + model.getLabel() + " column";
	}

}
