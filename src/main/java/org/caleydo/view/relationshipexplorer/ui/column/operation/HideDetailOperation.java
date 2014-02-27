/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityCollection;

/**
 * @author Christian
 *
 */
public class HideDetailOperation implements IHistoryCommand {

	protected IEntityCollection collection;
	protected RelationshipExplorerElement relationshipExplorer;

	public HideDetailOperation(IEntityCollection collection, RelationshipExplorerElement relationshipExplorer) {
		this.collection = collection;
		this.relationshipExplorer = relationshipExplorer;
	}

	@Override
	public void execute() {
		relationshipExplorer.removeDetailViewOfColumn(collection);
	}

}
