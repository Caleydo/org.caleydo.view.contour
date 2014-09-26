/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.item.factory.impl;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.column.TabularDataColumn;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.ISummaryItemFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.ISummaryItemFactoryConfigurationAddon;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.ISummaryItemFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.list.EUpdateCause;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
import org.caleydo.view.relationshipexplorer.ui.util.SimpleAggregateDataRenderer;

/**
 * @author Christian
 *
 */
public class MedianSummaryConfigurationAddon implements ISummaryItemFactoryConfigurationAddon {

	public static class MedianSummaryItemFactoryCreator implements ISummaryItemFactoryCreator {

		protected static final URL LINE_CHART_ICON = MedianSummaryItemFactory.class
				.getResource("/org/caleydo/view/relationshipexplorer/icons/chart_line.png");

		public static class MedianSummaryItemFactory implements ISummaryItemFactory {

			protected TabularDataCollection collection;

			public MedianSummaryItemFactory(TabularDataColumn column) {
				this.collection = (TabularDataCollection) column.getCollection();
			}

			@Override
			public GLElement createSummaryItem(NestableItem parentItem, Set<NestableItem> items) {

				Table table = collection.getDataDomain().getTable();
				if (table instanceof NumericalTable) {
					NumericalTable numTable = (NumericalTable) table;
					Set<Integer> indices = new HashSet<>(items.size());
					for (NestableItem item : items) {
						for (Object id : item.getElementData()) {
							indices.add((Integer) id);
						}
					}
					IDType recordIDType = collection.getDataDomain().getOppositeIDType(
							collection.getDimensionPerspective().getIdType());
					// Perspective dimensionPerspective = column.dataDomain.getDefaultTablePerspective().getPerspective(
					// dimensionIDType);

					return new SimpleAggregateDataRenderer(collection.getDataDomain(), indices, recordIDType,
							collection.getDimensionPerspective(), (float) numTable.getMin(), (float) numTable.getMax(),
							numTable.getDataCenter().floatValue());
				}

				return new GLElement(GLRenderers.drawText("Summary of " + items.size()));
			}

			@Override
			public boolean needsUpdate(EUpdateCause cause) {
				return cause != EUpdateCause.SELECTION && cause != EUpdateCause.HIGHLIGHT;
			}

		}

		@Override
		public ISummaryItemFactory create(IEntityCollection collection, IColumnModel column, ConTourElement contour) {
			return new MedianSummaryItemFactory((TabularDataColumn) column);
		}

		@Override
		public URL getIconURL() {
			return LINE_CHART_ICON;
		}
	}

	@Override
	public String getLabel() {
		return "Median Numerical Bars";
	}

	@Override
	public boolean accepts(IEntityCollection collection) {
		if (!(collection instanceof TabularDataCollection))
			return false;

		TabularDataCollection coll = (TabularDataCollection) collection;

		for (int dimensionID : coll.getDimensionPerspective().getVirtualArray()) {
			Object dataClassDesc = coll.getDataDomain().getTable().getDataClassSpecificDescription(dimensionID);
			if (dataClassDesc != null && !(dataClassDesc instanceof NumericalProperties)) {
				return false;
			}
		}

		return true;
	}

	@Override
	public void configure(ICallback<ISummaryItemFactoryCreator> callback) {
		callback.on(new MedianSummaryItemFactoryCreator());
	}
}
