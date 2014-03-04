/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.util;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.column.IEntityCollection;

import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public final class EntityMappingUtil {

	private EntityMappingUtil() {

	}

	public static Set<Object> getAllMappedElementIDs(Set<Object> sourceElementIDs, IEntityCollection sourceCollection,
			IEntityCollection targetCollection) {
		Set<Object> bcIDs = sourceCollection.getBroadcastingIDsFromElementIDs(sourceElementIDs);
		return targetCollection.getElementIDsFromForeignIDs(bcIDs, sourceCollection.getBroadcastingIDType());
	}

	public static Set<Object> getAllMappedElementIDs(Object sourceElementID, IEntityCollection sourceCollection,
			IEntityCollection targetCollection) {
		Set<Object> bcIDs = sourceCollection.getBroadcastingIDsFromElementID(sourceElementID);
		return targetCollection.getElementIDsFromForeignIDs(bcIDs, sourceCollection.getBroadcastingIDType());
	}

	public static Set<Object> getFilteredMappedElementIDs(Set<Object> sourceElementIDs,
			IEntityCollection sourceCollection, IEntityCollection targetCollection) {
		Set<Object> allIDs = getAllMappedElementIDs(sourceElementIDs, sourceCollection, targetCollection);
		return new HashSet<>(Sets.intersection(targetCollection.getFilteredElementIDs(), allIDs));
	}

	public static Set<Object> getFilteredMappedElementIDs(Object sourceElementID, IEntityCollection sourceCollection,
			IEntityCollection targetCollection) {
		Set<Object> allIDs = getAllMappedElementIDs(sourceElementID, sourceCollection, targetCollection);
		return new HashSet<>(Sets.intersection(targetCollection.getFilteredElementIDs(), allIDs));
	}

}
