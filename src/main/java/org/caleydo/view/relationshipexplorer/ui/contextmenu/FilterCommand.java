/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.contextmenu;

import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.column.operation.ASetBasedColumnOperation.ESetOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.SelectionBasedFilterOperation;

/**
 * @author Christian
 *
 */
public class FilterCommand implements IContextMenuCommand {

	protected final ESetOperation setOperation;
	protected final IEntityCollection collection;
	protected final RelationshipExplorerElement relationshipExplorer;

	public FilterCommand(ESetOperation setOperation, IEntityCollection collection,
			RelationshipExplorerElement relationshipExplorer) {
		this.setOperation = setOperation;
		this.collection = collection;
		this.relationshipExplorer = relationshipExplorer;
	}

	@Override
	public void execute() {
		Set<Object> elementIDs = collection.getSelectedElementIDs();
		Set<Object> broadcastIDs = collection.getBroadcastingIDsFromElementIDs(elementIDs);

		SelectionBasedFilterOperation o = new SelectionBasedFilterOperation(elementIDs, broadcastIDs, setOperation,
				relationshipExplorer);
		o.execute(collection);
		relationshipExplorer.getHistory().addColumnOperation(collection, o);
	}

}
