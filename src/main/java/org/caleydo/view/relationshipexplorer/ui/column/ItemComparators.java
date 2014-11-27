/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoricalClassDescription.ECategoryType;
import org.caleydo.core.io.DataDescription;
import org.caleydo.view.relationshipexplorer.ui.History;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
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

	public static final AInvertibleComparator<NestableItem> SELECTED_ITEMS_COMPARATOR = new AInvertibleComparator<NestableItem>() {

		@Override
		public String toString() {
			return "Item Selection";
		}

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
		public IInvertibleComparator<NestableItem> getInverted() {
			// not invertible
			return this;
		}
	};

	public static abstract class AMappingComparator extends AInvertibleComparator<NestableItem> {

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

	public static class NumericalAttributeComparator extends AInvertibleComparator<NestableItem> {

		protected final TabularDataCollection collection;
		protected final int dimensionID;

		public NumericalAttributeComparator(TabularDataCollection collection, int dimensionID) {
			this.collection = collection;
			this.dimensionID = dimensionID;
		}

		@Override
		public int compare(NestableItem item1, NestableItem item2) {
			return Float.compare(getAttributeValue(item1), getAttributeValue(item2));
		}

		protected float getAttributeValue(NestableItem item) {
			return ((Number) (collection.getDataDomain().getRaw(collection.getBroadcastingIDType(), (Integer) item
					.getElementData().iterator().next(), collection.getDimensionPerspective().getIdType(), dimensionID)))
					.floatValue();
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

	public static class CategoricalAttributeComparator extends AInvertibleComparator<NestableItem> {

		protected final TabularDataCollection collection;
		protected final int dimensionID;
		protected final CategoricalClassDescription<?> categoryDescription;

		public CategoricalAttributeComparator(TabularDataCollection collection, int dimensionID) {
			this.collection = collection;
			this.dimensionID = dimensionID;
			if (collection.getDataDomain().getTable().isDataHomogeneous()) {
				DataDescription dataDesc = collection.getDataDomain().getDataSetDescription().getDataDescription();
				categoryDescription = dataDesc.getCategoricalClassDescription();
			} else {
				categoryDescription = (CategoricalClassDescription<?>) collection.getDataDomain().getTable()
						.getDataClassSpecificDescription(dimensionID);
			}
		}

		@Override
		public int compare(NestableItem item1, NestableItem item2) {
			if (categoryDescription.getCategoryType() == ECategoryType.ORDINAL) {
				return categoryDescription.indexOf(getCategory(item1))
						- categoryDescription.indexOf(getCategory(item2));
			}
			return getCategory(item1).compareTo(getCategory(item2));
		}

		protected String getCategory(NestableItem item) {
			return ((String) (collection.getDataDomain().getRaw(collection.getBroadcastingIDType(), (Integer) item
					.getElementData().iterator().next(), collection.getDimensionPerspective().getIdType(), dimensionID)));
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

	public static class UniqueObjectAttributeComparator extends AInvertibleComparator<NestableItem> {

		protected final TabularDataCollection collection;
		protected final int dimensionID;

		public UniqueObjectAttributeComparator(TabularDataCollection collection, int dimensionID) {
			this.collection = collection;
			this.dimensionID = dimensionID;
		}

		@Override
		public int compare(NestableItem item1, NestableItem item2) {

			return getObject(item1).compareTo(getObject(item2));
		}

		protected String getObject(NestableItem item) {
			String object = ((String) (collection.getDataDomain().getRaw(collection.getBroadcastingIDType(),
					(Integer) item
					.getElementData().iterator().next(), collection.getDimensionPerspective().getIdType(), dimensionID)));
			return object == null ? "" : object;
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

	protected static class TextComparator extends AInvertibleComparator<NestableItem> {

		protected final IEntityCollection collection;

		public TextComparator(IEntityCollection collection) {
			this.collection = collection;
		}

		@Override
		public int compare(NestableItem o1, NestableItem o2) {
			String label1 = collection.getText(o1.getElementData().iterator().next());
			String label2 = collection.getText(o2.getElementData().iterator().next());
			return label1.compareTo(label2);
		}

		@Override
		public String toString() {
			return "Alphabetical";
		}
	}

	private ItemComparators() {

	}

}
