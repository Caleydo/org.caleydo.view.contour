/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.list.EUpdateCause;

import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public abstract class AMappingUpdateOperation extends ASetBasedColumnOperation implements ICollectionOperation {

	protected final Set<Object> srcBroadcastIDs;
	protected final IDType broadcastIDType;
	protected final ILabeled updateSource;
	protected final IEntityCollection sourceCollection;
	protected final ESetOperation multiItemSelectionOperation;

	protected final Set<IEntityCollection> targetCollections;

	// protected final IEntityCollection srcCollection;

	public AMappingUpdateOperation(Set<Object> srcBroadcastIDs, IDType broadcastIDType, ILabeled updateSource,
			ESetOperation op, ESetOperation multiItemSelectionOperation, IEntityCollection sourceCollection,
			Set<IEntityCollection> targetCollections) {
		super(op);
		this.broadcastIDType = broadcastIDType;
		this.srcBroadcastIDs = srcBroadcastIDs;
		this.updateSource = updateSource;
		this.multiItemSelectionOperation = multiItemSelectionOperation;
		this.sourceCollection = sourceCollection;
		this.targetCollections = targetCollections;
	}

	@Override
	public void execute(IEntityCollection collection) {
		if (!targetCollections.contains(collection))
			return;
		Set<Object> elementIDs = null;
		if (sourceCollection == null || collection != sourceCollection) {
			if (multiItemSelectionOperation == ESetOperation.INTERSECTION) {
				// elementIDs = new HashSet<>();

				for (Object bcID : srcBroadcastIDs) {
					Set<Object> ids = collection.getElementIDsFromForeignIDs(Sets.newHashSet(bcID), broadcastIDType);
					if (elementIDs != null) {
						elementIDs = new HashSet<>(Sets.intersection(elementIDs, ids));
					} else {
						elementIDs = ids;
					}

				}
			} else {
				elementIDs = collection.getElementIDsFromForeignIDs(srcBroadcastIDs, broadcastIDType);
			}
			if (elementIDs == null)
				elementIDs = new HashSet<>();
			execute(collection, elementIDs);
		}

	}

	public abstract void triggerUpdate(IEntityCollection collection);

	/**
	 * Applies the update oparation to the specified collection with the specified ids.
	 *
	 * @param collection
	 * @param elementIDs
	 */
	protected abstract void execute(IEntityCollection collection, Set<Object> elementIDs);

	public abstract EUpdateCause getUpdateCause();

	/**
	 * @return the srcBroadcastIDs, see {@link #srcBroadcastIDs}
	 */
	public Set<Object> getSrcBroadcastIDs() {
		return srcBroadcastIDs;
	}

	public Set<IEntityCollection> getTargetCollections() {
		return targetCollections;
	}

}
