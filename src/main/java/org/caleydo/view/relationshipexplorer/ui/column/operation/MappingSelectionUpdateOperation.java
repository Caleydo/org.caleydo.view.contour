/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.column.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;

/**
 * @author Christian
 *
 */
public class MappingSelectionUpdateOperation extends AMappingUpdateOperation {

	/**
	 * @param srcBroadcastIDs
	 * @param srcIDType
	 * @param op
	 */
	public MappingSelectionUpdateOperation(Set<Object> srcBroadcastIDs, IEntityRepresentation srcRep) {
		super(srcBroadcastIDs, srcRep, ESetOperation.INTERSECTION);
	}

	@Override
	protected void execute(IEntityCollection collection, Set<Object> elementIDs) {
		collection.setSelectedItems(setOperation.apply(elementIDs, collection.getFilteredElementIDs()));
	}

	@Override
	public void triggerUpdate(IEntityCollection collection) {
		collection.notifySelectionUpdate(srcRep);
	}

}
