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
public class MappingFilterUpdateOperation extends AMappingUpdateOperation {

	/**
	 * @param srcBroadcastIDs
	 * @param srcIDType
	 * @param op
	 */
	public MappingFilterUpdateOperation(Set<Object> srcBroadcastIDs, IEntityRepresentation srcRep, ESetOperation op) {
		super(srcBroadcastIDs, srcRep, op);
	}

	@Override
	protected void execute(IEntityCollection collection, Set<Object> elementIDs) {
		collection.setFilteredItems(setOperation.apply(elementIDs, collection.getFilteredElementIDs()));
	}

	@Override
	public void triggerUpdate(IEntityCollection collection) {
		collection.notifyFilterUpdate(srcRep);

	}

}
