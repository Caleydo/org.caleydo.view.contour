/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.collection;

import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.view.relationshipexplorer.ui.column.IScoreProvider;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
import org.caleydo.view.relationshipexplorer.ui.util.EntityMappingUtil;

import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Sets;
import com.google.common.collect.Table;
import com.google.common.collect.Table.Cell;

/**
 * @author Christian
 *
 */
public class EnrichmentScores {

	/**
	 * Holds all {@link EnrichmentScore}s defined via 4 dimensions: MappingCollection, TargetCollection,
	 * EnrichmentCollection, and, whether the score is based on filtered (Pair.second) or all items (Pair.first)
	 */
	protected Map<IEntityCollection, Table<IEntityCollection, IEntityCollection, Pair<EnrichmentScore, EnrichmentScore>>> allScores = new HashMap<>();

	// protected Table<IEntityCollection, IEntityCollection, EnrichmentScore> allScores = HashBasedTable.create();

	public static class EnrichmentScore implements ILabeled {

		protected final IEntityCollection rowCollection;
		protected final IEntityCollection columnCollection;
		protected final IEntityCollection mappingCollection;
		/**
		 * Determines whether the scores are calculated for filtered or all elements of the collections.
		 */
		protected final boolean isFilteredItemScore;
		protected Table<Object, Object, Float> scoreTable = HashBasedTable.create();

		public EnrichmentScore(IEntityCollection rowCollection, IEntityCollection columnCollection,
				IEntityCollection mappingCollection, boolean isFilteredItemScore) {
			this.rowCollection = rowCollection;
			this.columnCollection = columnCollection;
			this.mappingCollection = mappingCollection;
			this.isFilteredItemScore = isFilteredItemScore;
			updateScores();
		}

		public void updateScores() {
			scoreTable.clear();

			Set<Object> rowCollectionIDs = isFilteredItemScore ? rowCollection.getFilteredElementIDs() : rowCollection
					.getAllElementIDs();
			Set<Object> columnCollectionIDs = isFilteredItemScore ? columnCollection.getFilteredElementIDs()
					: columnCollection.getAllElementIDs();

			Map<Object, Pair<Set<Object>, Set<Object>>> rowToMappings = new HashMap<>();

			for (Object pathway : rowCollectionIDs) {
				Set<Object> allMappedIDs = EntityMappingUtil.getAllMappedElementIDs(pathway, rowCollection,
						mappingCollection);
				Set<Object> filteredMappedIDs = EntityMappingUtil.getFilteredElementIDsOf(allMappedIDs,
						mappingCollection);

				rowToMappings.put(pathway, Pair.make(allMappedIDs, filteredMappedIDs));
			}

			Map<Object, Pair<Set<Object>, Set<Object>>> columnToMappings = new HashMap<>();

			for (Object cluster : columnCollectionIDs) {
				Set<Object> allMappedIDs = EntityMappingUtil.getAllMappedElementIDs(cluster, columnCollection,
						mappingCollection);
				Set<Object> filteredMappedIDs = EntityMappingUtil.getFilteredElementIDsOf(allMappedIDs,
						mappingCollection);

				columnToMappings.put(cluster, Pair.make(allMappedIDs, filteredMappedIDs));
			}

			for (Entry<Object, Pair<Set<Object>, Set<Object>>> rowEntry : rowToMappings.entrySet()) {
				for (Entry<Object, Pair<Set<Object>, Set<Object>>> columnEntry : columnToMappings.entrySet()) {
					float a = 0;
					float b = 0;
					if (isFilteredItemScore) {
						a = Sets.intersection(rowEntry.getValue().getSecond(), columnEntry.getValue().getSecond())
								.size();
						b = columnEntry.getValue().getSecond().size() - a;
					} else {
						a = Sets.intersection(rowEntry.getValue().getFirst(), columnEntry.getValue().getFirst()).size();
						b = columnEntry.getValue().getFirst().size() - a;
					}
					float c = rowEntry.getValue().getFirst().size();
					float d = mappingCollection.getAllElementIDs().size() - c;

					float score = (a / (a + b)) / (c / (c + d));
					if (Float.isNaN(score))
						score = 0;

					scoreTable.put(rowEntry.getKey(), columnEntry.getKey(), Float.valueOf(score));

				}
			}
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

		@Override
		public String getLabel() {
			return "Enrichment of " + columnCollection.getLabel() + " for " + rowCollection.getLabel() + " via "
					+ mappingCollection.getLabel() + " considering "
					+ (isFilteredItemScore ? "filtered items" : "all items");
		}
	}

	public static abstract class AEnrichmentScoreComparator implements Comparator<NestableItem>, IScoreProvider {
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

	public EnrichmentScore getOrCreateEnrichmentScore(IEntityCollection targetCollection,
			IEntityCollection enrichmentCollection, IEntityCollection mappingCollection, boolean isFilteredItemScore) {

		Table<IEntityCollection, IEntityCollection, Pair<EnrichmentScore, EnrichmentScore>> table = allScores
				.get(mappingCollection);
		if (table == null) {
			table = HashBasedTable.create();
			allScores.put(mappingCollection, table);
		}
		Pair<EnrichmentScore, EnrichmentScore> scores = table.get(targetCollection, enrichmentCollection);
		if (scores == null) {
			scores = new Pair<>();
			table.put(targetCollection, enrichmentCollection, scores);
		}
		EnrichmentScore score = isFilteredItemScore ? scores.getSecond() : scores.getFirst();
		if (score == null) {
			score = new EnrichmentScore(targetCollection, enrichmentCollection, mappingCollection, isFilteredItemScore);
			if (isFilteredItemScore) {
				scores.setSecond(score);
			} else {
				scores.setFirst(score);
			}
		}

		return score;
	}

	public Collection<EnrichmentScore> getScoresForTargetCollection(IEntityCollection targetCollection) {
		Set<EnrichmentScore> scores = new HashSet<>();
		for (Table<IEntityCollection, IEntityCollection, Pair<EnrichmentScore, EnrichmentScore>> table : allScores
				.values()) {
			Map<IEntityCollection, Pair<EnrichmentScore, EnrichmentScore>> row = table.row(targetCollection);
			if (row != null) {
				for (Pair<EnrichmentScore, EnrichmentScore> pair : row.values()) {
					if (pair.getFirst() != null)
						scores.add(pair.getFirst());
					if (pair.getSecond() != null)
						scores.add(pair.getSecond());
				}
			}
		}
		return scores;
	}

	public Collection<EnrichmentScore> getScoresForEnrichmentCollection(IEntityCollection enrichmentCollection) {
		Set<EnrichmentScore> scores = new HashSet<>();
		for (Table<IEntityCollection, IEntityCollection, Pair<EnrichmentScore, EnrichmentScore>> table : allScores
				.values()) {
			Map<IEntityCollection, Pair<EnrichmentScore, EnrichmentScore>> column = table.column(enrichmentCollection);
			if (column != null) {
				for (Pair<EnrichmentScore, EnrichmentScore> pair : column.values()) {
					if (pair.getFirst() != null)
						scores.add(pair.getFirst());
					if (pair.getSecond() != null)
						scores.add(pair.getSecond());
				}
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
		for (Table<IEntityCollection, IEntityCollection, Pair<EnrichmentScore, EnrichmentScore>> table : allScores
				.values()) {
			for (Cell<IEntityCollection, IEntityCollection, Pair<EnrichmentScore, EnrichmentScore>> cell : table
					.cellSet()) {
				// Only second (filter based scores) need update
				cell.getValue().getSecond().updateScores();
			}
		}
	}

}
