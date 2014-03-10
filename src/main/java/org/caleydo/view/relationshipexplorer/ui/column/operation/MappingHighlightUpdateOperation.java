/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;
import org.caleydo.view.relationshipexplorer.ui.list.EUpdateCause;

/**
 * @author Christian
 *
 */
public class MappingHighlightUpdateOperation extends AMappingUpdateOperation {

	/**
	 * @param srcBroadcastIDs
	 * @param srcCollection
	 * @param op
	 */
	public MappingHighlightUpdateOperation(Set<Object> srcBroadcastIDs, IEntityRepresentation srcRep) {
		super(srcBroadcastIDs, srcRep, ESetOperation.INTERSECTION);
	}

	@Override
	protected void execute(IEntityCollection collection, Set<Object> elementIDs) {
		collection.setHighlightItems(setOperation.apply(elementIDs, collection.getFilteredElementIDs()));
	}

	@Override
	public void triggerUpdate(IEntityCollection collection) {
		collection.notifyHighlightUpdate(srcRep);

	}

	@Override
	public EUpdateCause getUpdateCause() {
		return EUpdateCause.HIGHLIGHT;
	}

}
