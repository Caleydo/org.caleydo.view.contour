/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.column.operation.ASetBasedColumnOperation.ESetOperation;

/**
 * @author Christian
 *
 */
public class MappingHighlightUpdateOperation extends AMappingUpdateOperation {

	/**
	 * @param srcBroadcastIDs
	 * @param srcColumn
	 * @param op
	 */
	public MappingHighlightUpdateOperation(Set<Object> srcBroadcastIDs, AEntityColumn srcColumn) {
		super(srcBroadcastIDs, srcColumn, ESetOperation.INTERSECTION);
	}

	@Override
	protected void execute(AEntityColumn column, Set<Object> elementIDs) {
		column.setHighlightItems(setOperation.apply(elementIDs, column.getFilteredElementIDs()));
	}

}
