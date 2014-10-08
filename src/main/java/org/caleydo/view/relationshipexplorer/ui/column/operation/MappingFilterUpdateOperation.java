/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.Set;

import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.list.EUpdateCause;

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
	public MappingFilterUpdateOperation(IEntityCollection sourceCollection, Set<Object> srcBroadcastIDs, IDType broadcastIDType, ILabeled updateSource,
			ESetOperation op, ESetOperation multiItemSelectionSetOperation, Set<IEntityCollection> targetCollections) {
		super(srcBroadcastIDs, broadcastIDType, updateSource, op, multiItemSelectionSetOperation, sourceCollection,
				targetCollections);
	}

	@Override
	protected void execute(IEntityCollection collection, Set<Object> elementIDs) {
		collection.setFilteredItems(setOperation.apply(elementIDs, collection.getFilteredElementIDs()));
	}

	@Override
	public void triggerUpdate(IEntityCollection collection) {
		collection.notifyFilterUpdate(updateSource);

	}

	@Override
	public EUpdateCause getUpdateCause() {
		return EUpdateCause.FILTER;
	}

}
