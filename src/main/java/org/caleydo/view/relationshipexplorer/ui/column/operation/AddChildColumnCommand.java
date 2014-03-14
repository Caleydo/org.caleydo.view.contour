/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;
import org.caleydo.view.relationshipexplorer.ui.list.NestableColumn;

/**
 * @author Christian
 *
 */
public class AddChildColumnCommand implements IHistoryCommand {

	protected final IEntityCollection collection;
	protected final int parentColumnModelHistoryID;
	protected final RelationshipExplorerElement relationshipExplorer;

	public AddChildColumnCommand(IEntityCollection collection, int parentColumnModelHistoryID,
			RelationshipExplorerElement relationshipExplorer) {
		this.collection = collection;
		this.parentColumnModelHistoryID = parentColumnModelHistoryID;
		this.relationshipExplorer = relationshipExplorer;
	}

	@Override
	public Object execute() {
		IColumnModel model = relationshipExplorer.getHistory().getHistoryObjectAs(IColumnModel.class,
				parentColumnModelHistoryID);
		NestableColumn column = model.getColumn();

		return column.getColumnTree().addNestedColumn(collection.createColumnModel(), column);
	}

	@Override
	public String getDescription() {
		IColumnModel model = relationshipExplorer.getHistory().getHistoryObjectAs(IColumnModel.class,
				parentColumnModelHistoryID);
		return "Added " + collection.getLabel() + "as child of " + model.getLabel();
	}

}
