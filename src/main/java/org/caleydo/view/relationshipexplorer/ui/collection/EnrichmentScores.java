/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.collection;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.MappingType;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.column.AInvertibleComparator;
import org.caleydo.view.relationshipexplorer.ui.column.IScoreProvider;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
import org.caleydo.view.relationshipexplorer.ui.util.EntityMappingUtil;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;

/**
 * @author Christian
 *
 */
public class EnrichmentScores {

	protected final ConTourElement relationshipExplorer;

	protected List<EnrichmentScore> allScores = new ArrayList<>();

	// protected Table<IEntityCollection, IEntityCollection, EnrichmentScore> allScores = HashBasedTable.create();

	public static class EnrichmentScore implements ILabeled {

		protected final ConTourElement relationshipExplorer;
		protected final IEntityCollection rowCollection;
		protected final IEntityCollection columnCollection;
		protected final IEntityCollection mappingCollection;
		protected final int threshold;
		/**
		 * Determines whether the scores are calculated for filtered or all elements of the collections.
		 */
		protected final boolean isFilteredItemScore;
		protected Table<Object, Object, Float> scoreTable = HashBasedTable.create();

		public EnrichmentScore(IEntityCollection rowCollection, IEntityCollection columnCollection,
				IEntityCollection mappingCollection, boolean isFilteredItemScore, int threshold,
				ConTourElement relationshipExplorer) {
			this.rowCollection = rowCollection;
			this.columnCollection = columnCollection;
			this.mappingCollection = mappingCollection;
			this.isFilteredItemScore = isFilteredItemScore;
			this.relationshipExplorer = relationshipExplorer;
			this.threshold = threshold;
			updateScores();
		}

		public void updateScores() {
			scoreTable.clear();

			Set<Object> rowCollectionIDs = isFilteredItemScore ? rowCollection.getFilteredElementIDs() : rowCollection
					.getAllElementIDs();
			Set<Object> columnCollectionIDs = isFilteredItemScore ? columnCollection.getFilteredElementIDs()
					: columnCollection.getAllElementIDs();

			Map<Object, Pair<Set<Object>, Set<Object>>> rowToMappings = new HashMap<>();

			for (Object id : rowCollectionIDs) {
				Set<Object> allMappedIDs = getMappedElementIDs(id, rowCollection, mappingCollection, false);
				Set<Object> filteredMappedIDs = getMappedElementIDs(id, rowCollection, mappingCollection, true);
				// Set<Object> allMappedIDs = EntityMappingUtil.getAllMappedElementIDs(id, rowCollection,
				// mappingCollection);
				// Set<Object> filteredMappedIDs = EntityMappingUtil.getFilteredElementIDsOf(allMappedIDs,
				// mappingCollection);

				rowToMappings.put(id, Pair.make(allMappedIDs, filteredMappedIDs));
			}

			Map<Object, Pair<Set<Object>, Set<Object>>> columnToMappings = new HashMap<>();

			for (Object id : columnCollectionIDs) {
				Set<Object> allMappedIDs = getMappedElementIDs(id, columnCollection, mappingCollection, false);
				Set<Object> filteredMappedIDs = getMappedElementIDs(id, columnCollection, mappingCollection, true);
				// Set<Object> allMappedIDs = EntityMappingUtil.getAllMappedElementIDs(id, columnCollection,
				// mappingCollection);
				// Set<Object> filteredMappedIDs = EntityMappingUtil.getFilteredElementIDsOf(allMappedIDs,
				// mappingCollection);

				columnToMappings.put(id, Pair.make(allMappedIDs, filteredMappedIDs));
			}

			for (Entry<Object, Pair<Set<Object>, Set<Object>>> rowEntry : rowToMappings.entrySet()) {
				for (Entry<Object, Pair<Set<Object>, Set<Object>>> columnEntry : columnToMappings.entrySet()) {
					float a = 0;
					float b = 0;
					float c = 0;
					if (isFilteredItemScore) {
						// common items of row and column entry from the filtered mapping collection
						a = Sets.intersection(rowEntry.getValue().getSecond(), columnEntry.getValue().getSecond())
								.size();
						// items of the column entry from the whole mapping collection minus a
						b = columnEntry.getValue().getFirst().size() - a;
						// items of the row entry from the filtered mapping collection
						c = rowEntry.getValue().getSecond().size();
					} else {
						a = Sets.intersection(rowEntry.getValue().getFirst(), columnEntry.getValue().getFirst()).size();
						b = columnEntry.getValue().getFirst().size() - a;
						c = rowEntry.getValue().getFirst().size();
					}
					float d = mappingCollection.getAllElementIDs().size() - c;

					float score = a < threshold ? 0 : (a / (a + b)) / (c / (c + d));
					if (Float.isNaN(score))
						score = 0;

					scoreTable.put(rowEntry.getKey(), columnEntry.getKey(), Float.valueOf(score));

				}
			}
		}

		protected Set<Object> getMappedElementIDs(Object elementID, IEntityCollection sourceCollection,
				IEntityCollection targetCollection, boolean filtered) {
			Set<Object> srcBCIDs = sourceCollection.getBroadcastingIDsFromElementID(elementID);
			IDType srcIDType = sourceCollection.getBroadcastingIDType();
			IDType targetIDType = targetCollection.getBroadcastingIDType();

			IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(srcIDType);

			List<MappingType> path = idMappingManager.getIDTypeMapper(srcIDType, targetIDType).getPath();

			IDType fromIDType = srcIDType;
			IDType toIDType = null;
			Set<Object> fromIDs = new HashSet<>(srcBCIDs);
			Set<Object> toIDs = new HashSet<>();

			for (MappingType m : path) {
				toIDType = m.getToIDType();
				toIDs = idMappingManager.getIDTypeMapper(fromIDType, toIDType).apply(fromIDs);

				Set<IEntityCollection> toCollections = relationshipExplorer.getCollectionsWithMappingIDType(toIDType);
				if (toCollections.size() > 0) {
					IEntityCollection toCollection = toCollections.iterator().next();
					Set<Object> toBCIDs = idMappingManager.getIDTypeMapper(toIDType,
							toCollection.getBroadcastingIDType()).apply(toIDs);
					Set<Object> filteredToBCIDs = getFilteredBroadcastIDsOf(toBCIDs, toCollection, filtered);
					toIDs = idMappingManager.getIDTypeMapper(toCollection.getBroadcastingIDType(), toIDType).apply(
							filteredToBCIDs);

				} else {
					toCollections = relationshipExplorer.getCollectionsWithBroadcastIDType(toIDType);
					if (toCollections.size() > 0) {
						IEntityCollection toCollection = toCollections.iterator().next();
						toIDs = getFilteredBroadcastIDsOf(toIDs, toCollection, filtered);
					}
				}

				fromIDs = new HashSet<>(toIDs);
				fromIDType = toIDType;
			}

			Set<Object> elementIDs = new HashSet<>();
			for (Object bcID : toIDs) {
				elementIDs.addAll(targetCollection.getElementIDsFromBroadcastingID(bcID));
			}

			return elementIDs;

		}

		private Set<Object> getFilteredBroadcastIDsOf(Set<Object> broadcastIDs, IEntityCollection collection,
				boolean filtered) {
			Set<Object> elementIDs = new HashSet<>();
			for (Object bcID : broadcastIDs) {
				elementIDs.addAll(collection.getElementIDsFromBroadcastingID(bcID));
			}
			Set<Object> filteredElementIDs = null;
			if (filtered) {
				filteredElementIDs = EntityMappingUtil.getFilteredElementIDsOf(elementIDs, collection);
			} else {
				filteredElementIDs = new HashSet<>(Sets.intersection(elementIDs, collection.getAllElementIDs()));
			}

			Set<Object> filteredBroadcastIDs = collection.getBroadcastingIDsFromElementIDs(filteredElementIDs);
			return filteredBroadcastIDs;
		}

		public Float getScore(Object targetID, Object enrichmentID) {
			return scoreTable.get(targetID, enrichmentID);
		}

		public Float getScore(Object id1, IEntityCollection collection1, Object id2, IEntityCollection collection2) {
			if (collection1 == rowCollection && collection2 == columnCollection) {
				return getScore(id1, id2);
			} else if (collection1 == columnCollection && collection2 == rowCollection) {
				return getScore(id2, id1);
			} else {
				return null;
			}

		}

		public Float getMaxScoreFor(Object id, IEntityCollection collection) {
			if (collection == rowCollection) {
				return Collections.max(scoreTable.row(id).values());
			} else if (collection == columnCollection) {
				return Collections.max(scoreTable.column(id).values());
			}
			return null;
		}

		public boolean hasEnrichmentOrTargetCollection(IEntityCollection collection) {
			return collection == rowCollection || collection == columnCollection;
		}

		/**
		 * @return the columnCollection, see {@link #columnCollection}
		 */
		public IEntityCollection getEnrichmentCollection() {
			return columnCollection;
		}

		/**
		 * @return the rowCollection, see {@link #rowCollection}
		 */
		public IEntityCollection getTargetCollection() {
			return rowCollection;
		}

		/**
		 * @return the mappingCollection, see {@link #mappingCollection}
		 */
		public IEntityCollection getMappingCollection() {
			return mappingCollection;
		}

		/**
		 * @return the threshold, see {@link #threshold}
		 */
		public int getThreshold() {
			return threshold;
		}

		/**
		 * @return the isFilteredItemScore, see {@link #isFilteredItemScore}
		 */
		public boolean isFilteredItemScore() {
			return isFilteredItemScore;
		}

		@Override
		public String getLabel() {
			return "Enrichment of "
					+ columnCollection.getLabel()
					+ " for "
					+ rowCollection.getLabel()
					+ " via "
					+ mappingCollection.getLabel()
					+ " considering "
					+ ((isFilteredItemScore ? "filtered items" : "all items") + " with a threshold of " + threshold + mappingCollection
							.getLabel());
		}
	}

	public static abstract class AEnrichmentScoreComparator extends AInvertibleComparator<NestableItem> implements
			IScoreProvider {
		protected final EnrichmentScore enrichmentScore;

		public AEnrichmentScoreComparator(EnrichmentScore score) {
			this.enrichmentScore = score;
		}

		/**
		 * @return the enrichmentScore, see {@link #enrichmentScore}
		 */
		public EnrichmentScore getEnrichmentScore() {
			return enrichmentScore;
		}
	}

	public static class MaxEnrichmentScoreComparator extends AEnrichmentScoreComparator {

		public MaxEnrichmentScoreComparator(EnrichmentScore score) {
			super(score);
		}

		@Override
		public int compare(NestableItem item1, NestableItem item2) {
			IEntityCollection myCollection = item1.getColumn().getColumnModel().getCollection();

			Float score1 = enrichmentScore.getMaxScoreFor(item1.getElementData().iterator().next(), myCollection);
			Float score2 = enrichmentScore.getMaxScoreFor(item2.getElementData().iterator().next(), myCollection);

			return score2.compareTo(score1);

		}

		@Override
		public Float getScore(Object primaryID, IEntityCollection primaryCollection, Object secondaryID,
				IEntityCollection secondaryCollection) {
			return enrichmentScore.getMaxScoreFor(primaryID, primaryCollection);
		}
	}

	public static class EnrichmentScoreComparator extends AEnrichmentScoreComparator {

		public EnrichmentScoreComparator(EnrichmentScore score) {
			super(score);
		}

		@Override
		public int compare(NestableItem item1, NestableItem item2) {

			IEntityCollection myCollection = item1.getColumn().getColumnModel().getCollection();
			NestableItem parent1 = item1.getParentItem();
			IEntityCollection parentCollection = parent1.getColumn().getColumnModel().getCollection();
			NestableItem parent2 = item2.getParentItem();

			Float score1 = enrichmentScore.getScore(item1.getElementData().iterator().next(), myCollection, parent1
					.getElementData().iterator().next(), parentCollection);
			Float score2 = enrichmentScore.getScore(item2.getElementData().iterator().next(), myCollection, parent2
					.getElementData().iterator().next(), parentCollection);

			return score2.compareTo(score1);
		}

		@Override
		public Float getScore(Object primaryID, IEntityCollection primaryCollection, Object secondaryID,
				IEntityCollection secondaryCollection) {
			return enrichmentScore.getScore(primaryID, primaryCollection, secondaryID, secondaryCollection);
		}

	}

	public EnrichmentScores(ConTourElement relationshipExplorer) {
		this.relationshipExplorer = relationshipExplorer;
	}

	public EnrichmentScore getOrCreateEnrichmentScore(IEntityCollection targetCollection,
			IEntityCollection enrichmentCollection, IEntityCollection mappingCollection, boolean isFilteredItemScore,
			int threshold) {

		EnrichmentScore score = null;
		for (EnrichmentScore s : allScores) {
			if (s.getTargetCollection() == targetCollection && s.getEnrichmentCollection() == enrichmentCollection
					&& s.getMappingCollection() == mappingCollection && s.isFilteredItemScore() == isFilteredItemScore
					&& threshold == s.getThreshold()) {
				score = s;
			}
		}
		if (score == null) {
			score = new EnrichmentScore(targetCollection, enrichmentCollection, mappingCollection, isFilteredItemScore,
					threshold, relationshipExplorer);
			allScores.add(score);
		}

		return score;
	}

	public Collection<EnrichmentScore> getScoresForTargetCollection(IEntityCollection targetCollection) {
		Set<EnrichmentScore> scores = new HashSet<>();
		for (EnrichmentScore s : allScores) {
			if (s.getTargetCollection() == targetCollection) {
				scores.add(s);
			}
		}
		return scores;
	}

	public Collection<EnrichmentScore> getScoresForEnrichmentCollection(IEntityCollection enrichmentCollection) {
		Set<EnrichmentScore> scores = new HashSet<>();
		for (EnrichmentScore s : allScores) {
			if (s.getEnrichmentCollection() == enrichmentCollection) {
				scores.add(s);
			}
		}
		return scores;
	}

	public Collection<EnrichmentScore> getAllScoresForTargetOrEnrichment(IEntityCollection collection) {

		Collection<EnrichmentScore> targetScores = getScoresForTargetCollection(collection);
		Collection<EnrichmentScore> enrichmentScores = getScoresForEnrichmentCollection(collection);

		Set<EnrichmentScore> allScores = new HashSet<>(targetScores.size() + enrichmentScores.size());
		allScores.addAll(targetScores);
		allScores.addAll(enrichmentScores);
		return allScores;
	}

	public void updateScores() {
		for (EnrichmentScore s : allScores) {
			if (s.isFilteredItemScore()) {
				s.updateScores();
			}
		}
	}

}
