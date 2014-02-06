/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import java.util.Set;

import org.caleydo.core.id.IDType;

/**
 * @author Christian
 *
 */
public abstract class AMappingUpdateOperation extends ASetBasedColumnOperation {

	protected final Set<Object> srcBroadcastIDs;
	protected final IDType srcIDType;

	public AMappingUpdateOperation(Set<Object> srcBroadcastIDs, IDType srcIDType, ESetOperation op) {
		super(op);
		this.srcBroadcastIDs = srcBroadcastIDs;
		this.srcIDType = srcIDType;
	}

	@Override
	public void execute(AEntityColumn column) {
		execute(column, column.getElementIDsFromForeignIDs(srcBroadcastIDs, srcIDType));

	}

	protected abstract void execute(AEntityColumn column, Set<Object> elementIDs);

}
