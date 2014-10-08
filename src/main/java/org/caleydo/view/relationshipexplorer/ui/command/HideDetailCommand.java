/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.command;

import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;

/**
 * @author Christian
 *
 */
public class HideDetailCommand implements IHistoryCommand {

	protected IEntityCollection collection;
	protected ConTourElement relationshipExplorer;

	public HideDetailCommand(IEntityCollection collection, ConTourElement relationshipExplorer) {
		this.collection = collection;
		this.relationshipExplorer = relationshipExplorer;
	}

	@Override
	public Object execute() {
		relationshipExplorer.hideDetailView(collection);
		return null;
	}

	@Override
	public String getDescription() {
		return "Hide Detail View of " + collection.getLabel();
	}

}
