/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.column.IEntityCollection;

/**
 * @author Christian
 *
 */
public abstract class AMappingUpdateOperation extends ASetBasedColumnOperation {

	protected final Set<Object> srcBroadcastIDs;
	protected final IEntityCollection srcCollection;

	public AMappingUpdateOperation(Set<Object> srcBroadcastIDs, IEntityCollection srcCollection, ESetOperation op) {
		super(op);
		this.srcBroadcastIDs = srcBroadcastIDs;
		this.srcCollection = srcCollection;
	}

	@Override
	public void execute(IEntityCollection collection) {
		execute(collection, collection.getElementIDsFromForeignIDs(srcBroadcastIDs, srcCollection.getBroadcastingIDType()));

	}

	protected abstract void execute(IEntityCollection collection, Set<Object> elementIDs);

	/**
	 * @return the srcColumn, see {@link #srcColumn}
	 */
	public IEntityCollection getSrcCollection() {
		return srcCollection;
	}

	/**
	 * @return the srcBroadcastIDs, see {@link #srcBroadcastIDs}
	 */
	public Set<Object> getSrcBroadcastIDs() {
		return srcBroadcastIDs;
	}

}
