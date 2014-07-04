/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.SimpleTabularDataItemFactory;
import org.caleydo.view.relationshipexplorer.ui.dialog.TabularAttributesFilterDialog;
import org.caleydo.view.relationshipexplorer.ui.filter.IEntityFilter;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

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

	public static final AInvertibleComparator<NestableItem> ITEM_ID_COMPARATOR = new AInvertibleComparator<NestableItem>() {

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
 final ConTourElement relationshipExplorer) {
		super(tabularDataCollection, relationshipExplorer);
		this.itemIDCategory = tabularDataCollection.getItemIDCategory();
		this.tablePerspective = tabularDataCollection.getTablePerspective();
		dataDomain = tablePerspective.getDataDomain();
		this.mappingIDType = tabularDataCollection.getMappingIDType();
		this.va = tabularDataCollection.getVa();
		this.itemIDType = tabularDataCollection.getItemIDType();
		this.perspective = tabularDataCollection.getDimensionPerspective();
		setItemFactory(new SimpleTabularDataItemFactory(tabularDataCollection));

		final GLButton filterButton = addHeaderButton(FILTER_ICON, "Filter by Attributes");

		filterButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				// final Vec2f location = filterButton.getAbsoluteLocation();

				relationshipExplorer.getContext().getSWTLayer().run(new ISWTLayerRunnable() {
					@Override
					public void run(Display display, Composite canvas) {
						// Point loc = canvas.toDisplay((int) location.x(), (int) location.y());
						TabularAttributesFilterDialog dialog = new TabularAttributesFilterDialog(canvas.getShell(),
								TabularDataColumn.this);
						if (dialog.open() == Window.OK) {
							IEntityFilter filter = dialog.getFilter();
							EventPublisher.trigger(new AttributeFilterEvent(filter, entityCollection
									.getFilteredElementIDs(), true).to(TabularDataColumn.this));
						}
					}
				});
			}
		});

		currentComparator = new CompositeComparator<>(ItemComparators.SELECTED_ITEMS_COMPARATOR, getDefaultComparator());
	}

	// @Override
	// public GLElement newElement(Object elementID) {
	//
	// // FIXME: Temporary hack -> use factory to create columns in entitycollection specifying the summary and item
	// // renderers for a column
	// if (dataDomain.getLabel().toLowerCase().contains("activity")) {
	// return new HTSActivityItemFactory(this).createItem(elementID);
	// }
	// return new SimpleDataRenderer(dataDomain, itemIDType, (Integer) elementID, perspective);
	// }

	@Override
	public IInvertibleComparator<NestableItem> getDefaultComparator() {
		return ITEM_ID_COMPARATOR;
	}

}
