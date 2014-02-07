/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import java.util.Set;

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
	public MappingFilterUpdateOperation(Set<Object> srcBroadcastIDs, AEntityColumn srcColumn, ESetOperation op) {
		super(srcBroadcastIDs, srcColumn, op);
	}

	@Override
	protected void execute(AEntityColumn column, Set<Object> elementIDs) {
		column.setFilteredItems(setOperation.apply(elementIDs, column.getFilteredElementIDs()));
	}

}
