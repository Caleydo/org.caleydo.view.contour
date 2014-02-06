/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import org.caleydo.core.event.AEvent;

/**
 * @author Christian
 *
 */
public class IDMappingUpdateEvent extends AEvent {

	protected AMappingUpdateOperation columnOperation;

	public IDMappingUpdateEvent(AMappingUpdateOperation columnOperation) {
		this.columnOperation = columnOperation;
	}

	@Override
	public boolean checkIntegrity() {
		return columnOperation != null;
	}

	/**
	 * @return the columnOperation, see {@link #columnOperation}
	 */
	public AMappingUpdateOperation getColumnOperation() {
		return columnOperation;
	}

	/**
	 * @param columnOperation
	 *            setter, see {@link columnOperation}
	 */
	public void setColumnOperation(AMappingUpdateOperation columnOperation) {
		this.columnOperation = columnOperation;
	}

}
