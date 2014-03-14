/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.list.ColumnTree;

/**
 * @author Christian
 *
 */
public class AddColumnTreeCommand implements IHistoryCommand {

	protected final IEntityCollection collection;
	protected final RelationshipExplorerElement relationshipExplorer;
	protected int index = -1;

	public AddColumnTreeCommand(IEntityCollection collection, RelationshipExplorerElement relationshipExplorer) {
		this.collection = collection;
		this.relationshipExplorer = relationshipExplorer;
	}

	@Override
	public Object execute() {
		ColumnTree tree = new ColumnTree(collection.createColumnModel(), relationshipExplorer);
		if (index == -1)
			relationshipExplorer.addColumn(tree);
		else
			relationshipExplorer.addColumn(index, tree);
		relationshipExplorer.relayout();
		return tree;
	}

	@Override
	public String getDescription() {
		return "Added new column " + collection.getLabel();
	}

	/**
	 * @param index
	 *            setter, see {@link index}
	 */
	public void setIndex(int index) {
		this.index = index;
	}

}
