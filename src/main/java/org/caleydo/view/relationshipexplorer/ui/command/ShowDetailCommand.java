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
public class ShowDetailCommand implements IHistoryCommand {

	protected final IEntityCollection collection;
	protected final ConTourElement relationshipExplorer;

	public ShowDetailCommand(IEntityCollection collection, ConTourElement relationshipExplorer) {
		this.collection = collection;
		this.relationshipExplorer = relationshipExplorer;
	}

	@Override
	public Object execute() {
		relationshipExplorer.showDetailView(collection);
		return null;
	}

	@Override
	public String getDescription() {
		return "Shoe Detail View of " + collection.getLabel();
	}

}
