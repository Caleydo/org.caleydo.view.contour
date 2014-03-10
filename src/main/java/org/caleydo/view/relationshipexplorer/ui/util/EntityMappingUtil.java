/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;

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

	public static class MappingOverlap {
		public Object elementID1;
		public Object elementID2;
		public float score;

		public MappingOverlap(Object elementID1, Object elementID2, float score) {
			this.elementID1 = elementID1;
			this.elementID2 = elementID2;
			this.score = score;
		}
	}

	public static List<MappingOverlap> getMappingOverlap(IEntityCollection pathwayCollection,
			IEntityCollection clusterCollection, IEntityCollection compoundCollection) {

		Map<Object, Set<Object>> pathwaysToCompounds = new HashMap<>();

		for (Object pathway : pathwayCollection.getAllElementIDs()) {
			pathwaysToCompounds.put(pathway, getAllMappedElementIDs(pathway, pathwayCollection, compoundCollection));
		}

		Map<Object, Set<Object>> clustersToCompounds = new HashMap<>();

		for (Object cluster : clusterCollection.getAllElementIDs()) {
			clustersToCompounds.put(cluster, getAllMappedElementIDs(cluster, clusterCollection, compoundCollection));
		}

		List<MappingOverlap> overlaps = new ArrayList<>(pathwaysToCompounds.size() * clustersToCompounds.size());

		for (Entry<Object, Set<Object>> pathwayEntry : pathwaysToCompounds.entrySet()) {
			for (Entry<Object, Set<Object>> clusterEntry : clustersToCompounds.entrySet()) {
				float overlap = Sets.intersection(pathwayEntry.getValue(), clusterEntry.getValue()).size();
				float notInOverlap = clusterEntry.getValue().size() - overlap;

				float score = (overlap / (overlap + notInOverlap))
						/ ((pathwayEntry.getValue().size()) / compoundCollection.getAllElementIDs().size());
				overlaps.add(new MappingOverlap(pathwayEntry.getKey(), clusterEntry.getKey(), score));

			}
		}
		return overlaps;
	}
}
