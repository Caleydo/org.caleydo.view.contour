/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.Set;

import org.caleydo.core.id.IDType;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;

/**
 * @author Christian
 *
 */
public abstract class ASelectionBasedOperation extends ASetBasedColumnOperation implements IHistoryCommand {

	protected final IEntityCollection sourceCollection;
	protected final Set<Object> selectedElementIDs;
	protected final Set<Object> selectedBroadcastIDs;
	protected final IDType broadcastIDType;
	protected final ConTourElement relationshipExplorer;

	public ASelectionBasedOperation(IEntityCollection sourceCollection, Set<Object> selectedElementIDs,
			Set<Object> selectedBroadcastIDs, IDType broadcastIDType, ESetOperation op,
			ConTourElement relationshipExplorer) {
		super(op);
		this.selectedElementIDs = selectedElementIDs;
		this.selectedBroadcastIDs = selectedBroadcastIDs;
		this.relationshipExplorer = relationshipExplorer;
		this.broadcastIDType = broadcastIDType;
		this.sourceCollection = sourceCollection;
	}

}
