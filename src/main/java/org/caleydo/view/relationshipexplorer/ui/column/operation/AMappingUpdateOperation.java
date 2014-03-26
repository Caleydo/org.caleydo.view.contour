/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;
import org.caleydo.view.relationshipexplorer.ui.list.EUpdateCause;

import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public abstract class AMappingUpdateOperation extends ASetBasedColumnOperation implements ICollectionOperation {

	protected final Set<Object> srcBroadcastIDs;
	protected final IEntityRepresentation srcRep;
	protected final ESetOperation multiItemSelectionOperation;

	// protected final IEntityCollection srcCollection;

	public AMappingUpdateOperation(Set<Object> srcBroadcastIDs, IEntityRepresentation srcRep, ESetOperation op,
			ESetOperation multiItemSelectionOperation) {
		super(op);
		this.srcBroadcastIDs = srcBroadcastIDs;
		this.srcRep = srcRep;
		this.multiItemSelectionOperation = multiItemSelectionOperation;
	}

	@Override
	public void execute(IEntityCollection collection) {
		Set<Object> elementIDs = null;
		if (!(collection == srcRep.getCollection())) {
			if (multiItemSelectionOperation == ESetOperation.INTERSECTION) {
				// elementIDs = new HashSet<>();

				for (Object bcID : srcBroadcastIDs) {
					Set<Object> ids = collection.getElementIDsFromForeignIDs(Sets.newHashSet(bcID), srcRep
							.getCollection().getBroadcastingIDType());
					if (elementIDs != null) {
						elementIDs = new HashSet<>(Sets.intersection(elementIDs, ids));
					} else {
						elementIDs = ids;
					}

				}
			} else {
				elementIDs = collection.getElementIDsFromForeignIDs(srcBroadcastIDs, srcRep.getCollection()
						.getBroadcastingIDType());
			}
			if (elementIDs == null)
				elementIDs = new HashSet<>();
			execute(collection, elementIDs);
		}

	}

	public abstract void triggerUpdate(IEntityCollection collection);

	protected abstract void execute(IEntityCollection collection, Set<Object> elementIDs);

	public abstract EUpdateCause getUpdateCause();

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
