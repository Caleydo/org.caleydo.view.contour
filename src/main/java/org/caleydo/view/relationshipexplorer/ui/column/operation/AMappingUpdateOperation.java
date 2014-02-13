/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;

/**
 * @author Christian
 *
 */
public abstract class AMappingUpdateOperation extends ASetBasedColumnOperation {

	protected final Set<Object> srcBroadcastIDs;
	protected final AEntityColumn srcColumn;

	public AMappingUpdateOperation(Set<Object> srcBroadcastIDs, AEntityColumn srcColumn, ESetOperation op) {
		super(op);
		this.srcBroadcastIDs = srcBroadcastIDs;
		this.srcColumn = srcColumn;
	}

	@Override
	public void execute(AEntityColumn column) {
		execute(column, column.getElementIDsFromForeignIDs(srcBroadcastIDs, srcColumn.getBroadcastingIDType()));

	}

	protected abstract void execute(AEntityColumn column, Set<Object> elementIDs);

	/**
	 * @return the srcColumn, see {@link #srcColumn}
	 */
	public AEntityColumn getSrcColumn() {
		return srcColumn;
	}

	/**
	 * @return the srcBroadcastIDs, see {@link #srcBroadcastIDs}
	 */
	public Set<Object> getSrcBroadcastIDs() {
		return srcBroadcastIDs;
	}

}
