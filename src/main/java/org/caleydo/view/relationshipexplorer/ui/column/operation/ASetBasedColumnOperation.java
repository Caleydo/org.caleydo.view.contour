/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;


/**
 * @author Christian
 *
 */
public abstract class ASetBasedColumnOperation {

	protected ESetOperation setOperation;

	public ASetBasedColumnOperation() {

	}

	public ASetBasedColumnOperation(ESetOperation op) {
		this.setOperation = op;
	}

	/**
	 * @param setOperation
	 *            setter, see {@link setOperation}
	 */
	public void setSetOperation(ESetOperation setOperation) {
		this.setOperation = setOperation;
	}

	/**
	 * @return the setOperation, see {@link #setOperation}
	 */
	public ESetOperation getSetOperation() {
		return setOperation;
	}

}
