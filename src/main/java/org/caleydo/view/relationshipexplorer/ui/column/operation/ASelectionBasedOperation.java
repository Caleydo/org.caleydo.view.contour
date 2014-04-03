/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;

/**
 * @author Christian
 *
 */
public abstract class ASelectionBasedOperation extends ASetBasedColumnOperation implements IHistoryCommand {

	protected final Set<Object> selectedElementIDs;
	protected final Set<Object> selectedBroadcastIDs;
	protected final ConTourElement relationshipExplorer;

	public ASelectionBasedOperation(Set<Object> selectedElementIDs, Set<Object> selectedBroadcastIDs, ESetOperation op,
			ConTourElement relationshipExplorer) {
		super(op);
		this.selectedElementIDs = selectedElementIDs;
		this.selectedBroadcastIDs = selectedBroadcastIDs;
		this.relationshipExplorer = relationshipExplorer;
	}

}
