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
public class MappingHighlightUpdateOperation extends AMappingUpdateOperation {

	/**
	 * @param srcBroadcastIDs
	 * @param srcIDType
	 * @param op
	 */
	public MappingHighlightUpdateOperation(Set<Object> srcBroadcastIDs, IDType srcIDType, ESetOperation op) {
		super(srcBroadcastIDs, srcIDType, op);
	}

	@Override
	protected void execute(AEntityColumn column, Set<Object> elementIDs) {
		column.setSelectedItems(setOperation.apply(elementIDs, column.getFilteredElementIDs()));
	}

}
