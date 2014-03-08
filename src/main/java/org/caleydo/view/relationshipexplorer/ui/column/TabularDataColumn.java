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
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactories;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactories.GLElementSupplier;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.ActivityItemFactory;
import org.caleydo.view.relationshipexplorer.ui.detail.parcoords.ParCoordsElement;
import org.caleydo.view.relationshipexplorer.ui.dialog.TabularAttributesFilterDialog;
import org.caleydo.view.relationshipexplorer.ui.filter.IEntityFilter;
import org.caleydo.view.relationshipexplorer.ui.list.NestableColumn;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
import org.caleydo.view.relationshipexplorer.ui.util.SimpleDataRenderer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.google.common.base.Predicate;
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

	// public static final Comparator<GLElement> ID_COMPARATOR = new Comparator<GLElement>() {
	//
	// @Override
	// public int compare(GLElement arg0, GLElement arg1) {
	// @SuppressWarnings("unchecked")
	// SimpleDataRenderer r1 = (SimpleDataRenderer) ((KeyBasedGLElementContainer<GLElement>) arg0)
	// .getElement(DATA_KEY);
	// @SuppressWarnings("unchecked")
	// SimpleDataRenderer r2 = (SimpleDataRenderer) ((KeyBasedGLElementContainer<GLElement>) arg1)
	// .getElement(DATA_KEY);
	//
	// return r1.getRecordID() - r2.getRecordID();
	// }
	// };

	public static final Comparator<NestableItem> ITEM_ID_COMPARATOR = new Comparator<NestableItem>() {

		@Override
		public int compare(NestableItem arg0, NestableItem arg1) {

			// SimpleDataRenderer r1 = (SimpleDataRenderer) arg0.getElement();
			// SimpleDataRenderer r2 = (SimpleDataRenderer) arg1.getElement();

			int recordID1 = (int) arg0.getElementData().iterator().next();
			int recordID2 = (int) arg1.getElementData().iterator().next();

			return recordID1 - recordID2;
		}

		@Override
		public String toString() {
			return "Item ID";
		}
	};

	public TabularDataColumn(TabularDataCollection tabularDataCollection,
			RelationshipExplorerElement relationshipExplorer) {

		super(tabularDataCollection, relationshipExplorer);
		this.itemIDCategory = tabularDataCollection.getItemIDCategory();
		this.tablePerspective = tabularDataCollection.getTablePerspective();
		dataDomain = tablePerspective.getDataDomain();
		this.mappingIDType = tabularDataCollection.getMappingIDType();
		this.va = tabularDataCollection.getVa();
		this.itemIDType = tabularDataCollection.getItemIDType();
		this.perspective = tabularDataCollection.getDimensionPerspective();

		final GLButton filterButton = addHeaderButton(FILTER_ICON);

		filterButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				// final Vec2f location = filterButton.getAbsoluteLocation();

				column.getColumnTree().getContext().getSWTLayer().run(new ISWTLayerRunnable() {
					@Override
					public void run(Display display, Composite canvas) {
						// Point loc = canvas.toDisplay((int) location.x(), (int) location.y());
						TabularAttributesFilterDialog dialog = new TabularAttributesFilterDialog(canvas.getShell(),
								TabularDataColumn.this);
						if (dialog.open() == Window.OK) {
							IEntityFilter filter = dialog.getFilter();
							EventPublisher.trigger(new AttributeFilterEvent(filter).to(TabularDataColumn.this));
						}
					}
				});
			}
		});

		currentComparator = new CompositeComparator<>(ItemComparators.SELECTED_ITEMS_COMPARATOR, getDefaultComparator());
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

	// @ListenTo
	// @Override
	// public void onAttributeFilter(AttributeFilterEvent event) {
	// // if (event.getReceiver() != this)
	// // return;
	// Set<Object> newFilteredItems = FilterUtil.filter(entityCollection.getFilteredElementIDs(), event.getFilter());
	//
	// AttributeFilterCommand c = new AttributeFilterCommand(this, newFilteredItems, relationshipExplorer.getHistory());
	// c.execute();
	// relationshipExplorer.getHistory().addHistoryCommand(c, Color.LIGHT_BLUE);
	// }

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

	// @Override
	// protected void setContent() {
	// // for (int id : va) {
	// // addItem(dataDomain, itemIDType, id, perspective);
	// // }
	// }

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

	// @Override
	// public Comparator<GLElement> getDefaultElementComparator() {
	// return ID_COMPARATOR;
	// }

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

		ParCoordsElement element = new ParCoordsElement(tablePerspective, entityCollection, relationshipExplorer);

		relationshipExplorer.showDetailView(entityCollection, element, this);

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

		// FIXME: Temporary hack -> use factory to create columns in entitycollection specifying the summary and item
		// renderers for a column
		if (dataDomain.getLabel().toLowerCase().contains("activity")) {
			return new ActivityItemFactory(this).createItem(elementID);
		}
		return new SimpleDataRenderer(dataDomain, itemIDType, (Integer) elementID, perspective);
	}

	@Override
	public Comparator<NestableItem> getDefaultComparator() {
		return ITEM_ID_COMPARATOR;
	}

}
