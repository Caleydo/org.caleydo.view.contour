/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.History;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;
import org.caleydo.view.relationshipexplorer.ui.list.NestableColumn;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
import org.caleydo.view.relationshipexplorer.ui.util.EntityMappingUtil;

/**
 * @author Christian
 *
 */
public final class ItemComparators {

	public static final Comparator<NestableItem> SELECTED_ITEMS_COMPARATOR = new Comparator<NestableItem>() {

		@Override
		public int compare(NestableItem o1, NestableItem o2) {
			if (o1.isSelected() && !o2.isSelected()) {
				return -1;
			}
			if (!o1.isSelected() && o2.isSelected()) {
				return 1;
			}

			return 0;
		}

		@Override
		public String toString() {
			return "Item Selection";
		}
	};

	public static abstract class AMappingComparator implements Comparator<NestableItem> {

		protected final int mappingColumnHistoryID;
		protected final History history;

		public AMappingComparator(IColumnModel mappingColumn, History history) {
			this.mappingColumnHistoryID = mappingColumn.getHistoryID();
			this.history = history;
		}

		@Override
		public int compare(NestableItem o1, NestableItem o2) {
			IColumnModel model = getMappingColumn();
			return getNumChildMappings(o2, model.getColumn()) - getNumChildMappings(o1, model.getColumn());
		}

		public IColumnModel getMappingColumn() {
			return history.getHistoryObjectAs(IColumnModel.class, mappingColumnHistoryID);
		}

		protected abstract int getNumChildMappings(NestableItem parent, NestableColumn mappingColumn);
	}

	public static class SelectionMappingComparator extends AMappingComparator {

		/**
		 * @param mappingColumn
		 */
		public SelectionMappingComparator(IColumnModel mappingColumn, History history) {
			super(mappingColumn, history);
		}

		@Override
		protected int getNumChildMappings(NestableItem parent, NestableColumn mappingColumn) {
			List<NestableItem> childItems = parent.getChildItems(mappingColumn);
			Set<Object> elementIDs = new HashSet<>(childItems.size());
			for (NestableItem item : childItems) {
				if (item.isSelected()) {
					elementIDs.addAll(item.getElementData());
				}
			}
			return elementIDs.size();
		}
	}

	public static class VisibleMappingComparator extends AMappingComparator {

		/**
		 * @param mappingColumn
		 */
		public VisibleMappingComparator(IColumnModel mappingColumn, History history) {
			super(mappingColumn, history);
		}

		@Override
		protected int getNumChildMappings(NestableItem parent, NestableColumn mappingColumn) {
			List<NestableItem> childItems = parent.getChildItems(mappingColumn);
			Set<Object> elementIDs = new HashSet<>(childItems.size());
			for (NestableItem item : childItems) {
				elementIDs.addAll(item.getElementData());
			}
			return elementIDs.size();
		}
	}

	public static class TotalMappingComparator extends AMappingComparator {

		/**
		 * @param mappingColumn
		 */
		public TotalMappingComparator(IColumnModel mappingColumn, History history) {
			super(mappingColumn, history);
			// TODO Auto-generated constructor stub
		}

		@Override
		protected int getNumChildMappings(NestableItem parent, NestableColumn mappingColumn) {

			Set<Object> mappedIDs = EntityMappingUtil.getAllMappedElementIDs(parent.getElementData(), parent
					.getColumn().getColumnModel().getCollection(), mappingColumn.getColumnModel().getCollection());
			return mappedIDs.size();
		}
	}

	public static class NumericalAttributeComparator implements Comparator<NestableItem> {

		protected final TabularDataCollection collection;
		protected final int dimensionID;

		public NumericalAttributeComparator(TabularDataCollection collection, int dimensionID) {
			this.collection = collection;
			this.dimensionID = dimensionID;
		}

		@Override
		public int compare(NestableItem item1, NestableItem item2) {
			return Float.compare(getAttributeValue(item2), getAttributeValue(item1));
		}

		protected float getAttributeValue(NestableItem item) {
			return (float) collection.getDataDomain().getRaw(collection.getBroadcastingIDType(),
					(Integer) item.getElementData().iterator().next(),
					collection.getDimensionPerspective().getIdType(), dimensionID);
		}

		/**
		 * @return the dimensionID, see {@link #dimensionID}
		 */
		public int getDimensionID() {
			return dimensionID;
		}

		/**
		 * @return the collection, see {@link #collection}
		 */
		public TabularDataCollection getCollection() {
			return collection;
		}

	}

	public ItemComparators() {

	}

}
