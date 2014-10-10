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
public class MappingSelectionUpdateOperation extends AMappingUpdateOperation {

	/**
	 * @param srcBroadcastIDs
	 * @param srcIDType
	 * @param op
	 */
	public MappingSelectionUpdateOperation(IEntityCollection sourceCollection, Set<Object> srcBroadcastIDs,
			IDType broadcastIDType, ILabeled updateSource,
			ESetOperation multiItemSelectionSetOperation, Set<IEntityCollection> targetCollections) {
		super(srcBroadcastIDs, broadcastIDType, updateSource, ESetOperation.INTERSECTION,
				multiItemSelectionSetOperation, sourceCollection, targetCollections);
	}

	@Override
	protected void execute(IEntityCollection collection, Set<Object> elementIDs) {
		collection.setSelectedItems(setOperation.apply(elementIDs, collection.getFilteredElementIDs()));
	}


	@Override
	public EUpdateCause getUpdateCause() {
		return EUpdateCause.SELECTION;
	}

	@Override
	public void notify(IMappingUpdateListener listener) {
		listener.selectionChanged(srcBroadcastIDs, broadcastIDType, updateSource);

	}

}
