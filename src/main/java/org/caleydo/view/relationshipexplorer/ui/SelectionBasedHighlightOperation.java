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
public class SelectionBasedHighlightOperation extends ASelectionBasedOperation {

	/**
	 * @param selectedElementIDs
	 * @param selectedBroadcastIDs
	 * @param op
	 */
	public SelectionBasedHighlightOperation(Set<Object> selectedElementIDs, Set<Object> selectedBroadcastIDs) {
		super(selectedElementIDs, selectedBroadcastIDs, ESetOperation.INTERSECTION);
	}

	@Override
	public void execute(AEntityColumn column) {
		column.setSelectedItems(selectedElementIDs);

		column.relationshipExplorer.applyIDMappingUpdate(new MappingHighlightUpdateOperation(selectedBroadcastIDs,
				column, ESetOperation.INTERSECTION));
	}

}
