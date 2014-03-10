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

	protected Map<IEntityCollection, Table<IEntityCollection, IEntityCollection, EnrichmentScore>> allScores = new HashMap<>();

	// protected Table<IEntityCollection, IEntityCollection, EnrichmentScore> allScores = HashBasedTable.create();

	public static class EnrichmentScore implements ILabeled {

		protected final IEntityCollection rowCollection;
		protected final IEntityCollection columnCollection;
		protected final IEntityCollection mappingCollection;
		/**
		 * Determines whether the scores are calculated for filtered or all elements of the collections.
		 */
		protected final boolean isFilteredScore;
		protected Table<Object, Object, Float> scoreTable = HashBasedTable.create();

		public EnrichmentScore(IEntityCollection rowCollection, IEntityCollection columnCollection,
				IEntityCollection mappingCollection, boolean isFilteredScore) {
			this.rowCollection = rowCollection;
			this.columnCollection = columnCollection;
			this.mappingCollection = mappingCollection;
			this.isFilteredScore = isFilteredScore;
			updateScores();
		}

		public void updateScores() {
			scoreTable.clear();

			Set<Object> rowCollectionIDs = isFilteredScore ? rowCollection.getFilteredElementIDs() : rowCollection
					.getAllElementIDs();
			Set<Object> columnCollectionIDs = isFilteredScore ? columnCollection.getFilteredElementIDs()
					: columnCollection.getAllElementIDs();
			Set<Object> mappingCollectionIDs = isFilteredScore ? mappingCollection.getFilteredElementIDs()
					: mappingCollection.getAllElementIDs();

			Map<Object, Set<Object>> rowToMappings = new HashMap<>();

			for (Object pathway : rowCollectionIDs) {
				Set<Object> mappedIDs = isFilteredScore ? EntityMappingUtil.getFilteredMappedElementIDs(pathway,
						rowCollection, mappingCollection) : EntityMappingUtil.getAllMappedElementIDs(pathway,
						rowCollection, mappingCollection);

				rowToMappings.put(pathway, mappedIDs);
			}

			Map<Object, Set<Object>> columnToMappings = new HashMap<>();

			for (Object cluster : columnCollectionIDs) {
				Set<Object> mappedIDs = isFilteredScore ? EntityMappingUtil.getFilteredMappedElementIDs(cluster,
						columnCollection, mappingCollection) : EntityMappingUtil.getAllMappedElementIDs(cluster,
						columnCollection, mappingCollection);
				columnToMappings.put(cluster, mappedIDs);
			}

			for (Entry<Object, Set<Object>> rowEntry : rowToMappings.entrySet()) {
				for (Entry<Object, Set<Object>> columnEntry : columnToMappings.entrySet()) {
					float a = Sets.intersection(rowEntry.getValue(), columnEntry.getValue()).size();
					float b = columnEntry.getValue().size() - a;
					float c = rowEntry.getValue().size();
					float d = mappingCollectionIDs.size() - c;

					float score = (a / (a + b)) / (c / c + d);

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
					+ mappingCollection.getLabel();
		}
	}

	public static class MaxEnrichmentScoreComparator implements Comparator<NestableItem> {

		protected final EnrichmentScore score;

		public MaxEnrichmentScoreComparator(EnrichmentScore score) {
			this.score = score;
		}

		@Override
		public int compare(NestableItem item1, NestableItem item2) {
			IEntityCollection myCollection = item1.getColumn().getColumnModel().getCollection();

			Float score1 = score.getMaxScoreFor(item1.getElementData().iterator().next(), myCollection);
			Float score2 = score.getMaxScoreFor(item2.getElementData().iterator().next(), myCollection);

			return score1.compareTo(score2);

		}
	}

	public static class EnrichmentScoreComparator implements Comparator<NestableItem> {

		protected final EnrichmentScore score;

		public EnrichmentScoreComparator(EnrichmentScore score) {
			this.score = score;
		}

		@Override
		public int compare(NestableItem item1, NestableItem item2) {


			IEntityCollection myCollection = item1.getColumn().getColumnModel().getCollection();
			NestableItem parent1 = item1.getParentItem();
			IEntityCollection parentCollection = parent1.getColumn().getColumnModel().getCollection();
			NestableItem parent2 = item2.getParentItem();

			Float score1 = score.getScore(item1.getElementData().iterator().next(), myCollection, parent1
					.getElementData().iterator().next(), parentCollection);
			Float score2 = score.getScore(item2.getElementData().iterator().next(), myCollection, parent2
					.getElementData().iterator().next(), parentCollection);

			return score2.compareTo(score1);
		}

	}

	public EnrichmentScore getOrCreateEnrichmentScore(IEntityCollection targetCollection,
			IEntityCollection enrichmentCollection, IEntityCollection mappingCollection) {

		Table<IEntityCollection, IEntityCollection, EnrichmentScore> table = allScores.get(mappingCollection);
		if (table == null) {
			table = HashBasedTable.create();
			allScores.put(mappingCollection, table);
		}
		EnrichmentScore score = table.get(targetCollection, enrichmentCollection);
		if (score == null) {
			score = new EnrichmentScore(targetCollection, enrichmentCollection, mappingCollection, true);
			table.put(targetCollection, enrichmentCollection, score);
		}

		return score;
	}

	public Collection<EnrichmentScore> getScoresForTargetCollection(IEntityCollection targetCollection) {
		Set<EnrichmentScore> scores = new HashSet<>();
		for (Table<IEntityCollection, IEntityCollection, EnrichmentScore> table : allScores.values()) {
			Map<IEntityCollection, EnrichmentScore> row = table.row(targetCollection);
			if (row != null)
				scores.addAll(row.values());
		}
		return scores;
	}

	public Collection<EnrichmentScore> getScoresForEnrichmentCollection(IEntityCollection enrichmentCollection) {
		Set<EnrichmentScore> scores = new HashSet<>();
		for (Table<IEntityCollection, IEntityCollection, EnrichmentScore> table : allScores.values()) {
			Map<IEntityCollection, EnrichmentScore> column = table.column(enrichmentCollection);
			if (column != null)
				scores.addAll(column.values());
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
		for (Table<IEntityCollection, IEntityCollection, EnrichmentScore> table : allScores.values()) {
			for (Cell<IEntityCollection, IEntityCollection, EnrichmentScore> cell : table.cellSet()) {
				cell.getValue().updateScores();
			}
		}
	}

}
