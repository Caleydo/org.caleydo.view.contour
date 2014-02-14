/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;

/**
 * @author Christian
 *
 */
public abstract class ASelectionBasedOperation extends ASetBasedColumnOperation {

	protected final Set<Object> selectedElementIDs;
	protected final Set<Object> selectedBroadcastIDs;
	protected final RelationshipExplorerElement relationshipExplorer;

	public ASelectionBasedOperation(Set<Object> selectedElementIDs, Set<Object> selectedBroadcastIDs, ESetOperation op,
			RelationshipExplorerElement relationshipExplorer) {
		super(op);
		this.selectedElementIDs = selectedElementIDs;
		this.selectedBroadcastIDs = selectedBroadcastIDs;
		this.relationshipExplorer = relationshipExplorer;
	}

}
