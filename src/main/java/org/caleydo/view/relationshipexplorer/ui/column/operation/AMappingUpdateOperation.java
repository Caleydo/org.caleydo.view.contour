/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.column.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;

/**
 * @author Christian
 *
 */
public abstract class AMappingUpdateOperation extends ASetBasedColumnOperation implements IColumnOperation {

	protected final Set<Object> srcBroadcastIDs;
	protected final IEntityRepresentation srcRep;

	// protected final IEntityCollection srcCollection;

	public AMappingUpdateOperation(Set<Object> srcBroadcastIDs, IEntityRepresentation srcRep, ESetOperation op) {
		super(op);
		this.srcBroadcastIDs = srcBroadcastIDs;
		this.srcRep = srcRep;
	}

	@Override
	public void execute(IEntityCollection collection) {
		execute(collection,
				collection.getElementIDsFromForeignIDs(srcBroadcastIDs, srcRep.getCollection().getBroadcastingIDType()));

	}

	protected abstract void execute(IEntityCollection collection, Set<Object> elementIDs);

	/**
	 * @return the srcColumn, see {@link #srcColumn}
	 */
	public IEntityRepresentation getSrcRepresentation() {
		return srcRep;
	}

	/**
	 * @return the srcBroadcastIDs, see {@link #srcBroadcastIDs}
	 */
	public Set<Object> getSrcBroadcastIDs() {
		return srcBroadcastIDs;
	}

}
