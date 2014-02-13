/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.Set;

import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public abstract class ASetBasedColumnOperation implements IColumnOperation {

	public static enum ESetOperation {
		REPLACE, INTERSECTION, UNION;

		public Set<Object> apply(Set<Object> set1, Set<Object> set2) {
			switch (this) {
			case REPLACE:
				return set1;
			case INTERSECTION:
				return Sets.intersection(set1, set2);
			case UNION:
				return Sets.union(set1, set2);
			default:
				return null;
			}
		}
	}

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
