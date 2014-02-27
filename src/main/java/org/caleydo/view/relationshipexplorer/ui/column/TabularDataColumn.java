/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import gleem.linalg.Vec2f;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactories;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactories.GLElementSupplier;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.list.NestableColumn;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
import org.caleydo.view.relationshipexplorer.ui.util.KeyBasedGLElementContainer;
import org.caleydo.view.relationshipexplorer.ui.util.SimpleDataRenderer;
import org.eclipse.nebula.widgets.nattable.util.ComparatorChain;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public class TabularDataColumn extends AEntityColumn {

	protected final ATableBasedDataDomain dataDomain;
	protected final IDCategory itemIDCategory;
	protected final TablePerspective tablePerspective;
	protected final IDType itemIDType;
	protected final VirtualArray va;
	protected final Perspective perspective;
	protected final IDType mappingIDType;

	public static final Comparator<GLElement> ID_COMPARATOR = new Comparator<GLElement>() {

		@Override
		public int compare(GLElement arg0, GLElement arg1) {
			@SuppressWarnings("unchecked")
			SimpleDataRenderer r1 = (SimpleDataRenderer) ((KeyBasedGLElementContainer<GLElement>) arg0)
					.getElement(DATA_KEY);
			@SuppressWarnings("unchecked")
			SimpleDataRenderer r2 = (SimpleDataRenderer) ((KeyBasedGLElementContainer<GLElement>) arg1)
					.getElement(DATA_KEY);

			return r1.getRecordID() - r2.getRecordID();
		}
	};

	public static final Comparator<NestableItem> ITEM_ID_COMPARATOR = new Comparator<NestableItem>() {

		@Override
		public int compare(NestableItem arg0, NestableItem arg1) {

			SimpleDataRenderer r1 = (SimpleDataRenderer) arg0.getElement();
			SimpleDataRenderer r2 = (SimpleDataRenderer) arg1.getElement();

			return r1.getRecordID() - r2.getRecordID();
		}
	};

	public TabularDataColumn(TabularDataCollection tabularDataCollection,
			RelationshipExplorerElement relationshipExplorer) {

		super(tabularDataCollection, relationshipExplorer);
		this.itemIDCategory = tabularDataCollection.itemIDCategory;
		this.tablePerspective = tabularDataCollection.tablePerspective;
		dataDomain = tablePerspective.getDataDomain();
		this.mappingIDType = tabularDataCollection.mappingIDType;
		this.va = tabularDataCollection.va;
		this.itemIDType = tabularDataCollection.itemIDType;
		this.perspective = tabularDataCollection.perspective;
		// this.mappingIDType = dataDomain.getDatasetDescriptionIDType(itemIDCategory);
		//
		// if (dataDomain.getDimensionIDCategory() == itemIDCategory) {
		// va = tablePerspective.getDimensionPerspective().getVirtualArray();
		// itemIDType = tablePerspective.getDimensionPerspective().getIdType();
		// perspective = tablePerspective.getRecordPerspective();
		//
		// } else {
		// va = tablePerspective.getRecordPerspective().getVirtualArray();
		// itemIDType = tablePerspective.getRecordPerspective().getIdType();
		// perspective = tablePerspective.getDimensionPerspective();
		// }

		// filteredElementIDs.addAll(va.getIDs());
	}

	protected void addItem(ATableBasedDataDomain dd, final IDType recordIDType, final int recordID,
			Perspective dimensionPerspective) {
		SimpleDataRenderer renderer = new SimpleDataRenderer(dd, recordIDType, recordID, dimensionPerspective);

		addElement(renderer, recordID);
		// IDMappingManager m = IDMappingManagerRegistry.get().getIDMappingManager(recordIDType);
		// IDType origIDType;
		// IDSpecification spec = dd.getDataSetDescription().getColumnIDSpecification();
		// if (spec.getIdCategory().equalsIgnoreCase(recordIDType.getIDCategory().getCategoryName())) {
		// origIDType = IDType.getIDType(spec.getIdType());
		// } else {
		// origIDType = IDType.getIDType(dd.getDataSetDescription().getRowIDSpecification().getIdType());
		// }

		// itemList.add(renderer);
		// Object origID = m.getID(recordIDType, origIDType, recordID);

		// itemList.setElementTooltip(renderer, origID.toString());

	}

	protected void addItem(ATableBasedDataDomain dd, final IDType recordIDType, final int recordID,
			Perspective dimensionPerspective, NestableItem parentItem, NestableColumn column) {
		SimpleDataRenderer renderer = new SimpleDataRenderer(dd, recordIDType, recordID, dimensionPerspective);

		addItem(renderer, recordID, column, parentItem);
		// IDMappingManager m = IDMappingManagerRegistry.get().getIDMappingManager(recordIDType);
		// IDType origIDType;
		// IDSpecification spec = dd.getDataSetDescription().getColumnIDSpecification();
		// if (spec.getIdCategory().equalsIgnoreCase(recordIDType.getIDCategory().getCategoryName())) {
		// origIDType = IDType.getIDType(spec.getIdType());
		// } else {
		// origIDType = IDType.getIDType(dd.getDataSetDescription().getRowIDSpecification().getIdType());
		// }

		// itemList.add(renderer);
		// Object origID = m.getID(recordIDType, origIDType, recordID);

		// itemList.setElementTooltip(renderer, origID.toString());

	}

	@Override
	protected void setContent() {
		for (int id : va) {
			addItem(dataDomain, itemIDType, id, perspective);
		}
	}

	@Override
	public IDType getBroadcastingIDType() {
		return itemIDType;
	}

	@Override
	public Set<Object> getBroadcastingIDsFromElementID(Object elementID) {
		return Sets.newHashSet(elementID);
	}

	@Override
	public Set<Object> getElementIDsFromBroadcastingID(Integer broadcastingID) {
		return Sets.newHashSet((Object) broadcastingID);
	}

	@Override
	public Comparator<GLElement> getDefaultElementComparator() {
		return ID_COMPARATOR;
	}

	@Override
	public IDType getMappingIDType() {
		return mappingIDType;
	}

	@Override
	public void showDetailView() {
		GLElementFactoryContext context = GLElementFactoryContext.builder().withData(tablePerspective).build();
		List<GLElementSupplier> suppliers = GLElementFactories.getExtensions(context, "relexplorer",
				new Predicate<String>() {

					@Override
					public boolean apply(String input) {
						return input.equals("paco");
					}
				});

		GLElement detailView = null;
		if (suppliers.isEmpty()) {
			detailView = new GLElement() {
				@Override
				public Vec2f getMinSize() {
					return new Vec2f(300, 300);
				}
			};
			detailView.setRenderer(GLRenderers.fillRect(Color.BLUE));

		} else {
			detailView = suppliers.get(0).get();
		}

		relationshipExplorer.showDetailView(entityCollection, detailView, this);

	}

	// @Override
	// public void fill(NestableColumn column, NestableColumn parentColumn) {
	// this.column = column;
	// this.parentColumn = parentColumn;
	//
	// if (parentColumn == null) {
	// for (Object id : filteredElementIDs) {
	// addItem(dataDomain, itemIDType, (Integer) id, perspective, null, column);
	// }
	// } else {
	// for (Object id : filteredElementIDs) {
	// Set<Object> foreignElementIDs = parentColumn.getColumnModel().getElementIDsFromForeignIDs(
	// getBroadcastingIDsFromElementID(id), getBroadcastingIDType());
	// Set<NestableItem> parentItems = parentColumn.getColumnModel().getItems(foreignElementIDs);
	//
	// for (NestableItem parentItem : parentItems) {
	// addItem(dataDomain, itemIDType, (Integer) id, perspective, parentItem, column);
	//
	// }
	// }
	// }
	//
	// }

	@Override
	public GLElement createElement(Object elementID) {
		return new SimpleDataRenderer(dataDomain, itemIDType, (Integer) elementID, perspective);
	}

	@SuppressWarnings("unchecked")
	@Override
	public Comparator<NestableItem> getDefaultComparator() {
		return new ComparatorChain<>(Lists.<Comparator<NestableItem>> newArrayList(SELECTED_ITEMS_COMPARATOR,
				ITEM_ID_COMPARATOR));
	}

}
