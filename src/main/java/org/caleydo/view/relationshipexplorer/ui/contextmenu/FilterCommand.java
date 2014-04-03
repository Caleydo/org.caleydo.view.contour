/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.contextmenu;

import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.ESetOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.SelectionBasedFilterOperation;

/**
 * @author Christian
 *
 */
public class FilterCommand implements IContextMenuCommand {

	protected final ESetOperation setOperation;
	protected final IEntityRepresentation representation;
	protected final ConTourElement relationshipExplorer;

	public FilterCommand(ESetOperation setOperation, IEntityRepresentation representation,
			ConTourElement relationshipExplorer) {
		this.setOperation = setOperation;
		this.representation = representation;
		this.relationshipExplorer = relationshipExplorer;
	}

	@Override
	public void execute() {
		Set<Object> elementIDs = representation.getCollection().getSelectedElementIDs();
		Set<Object> broadcastIDs = representation.getCollection().getBroadcastingIDsFromElementIDs(elementIDs);

		SelectionBasedFilterOperation c = new SelectionBasedFilterOperation(representation.getHistoryID(), elementIDs,
				broadcastIDs, setOperation, relationshipExplorer);
		c.execute();
		relationshipExplorer.getHistory().addHistoryCommand(c);
	}

}
