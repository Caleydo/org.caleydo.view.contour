/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
import org.caleydo.view.relationshipexplorer.ui.util.SimpleAggregateDataRenderer;

/**
 * @author Christian
 *
 */
public class MedianSummaryItemFactory implements ISummaryItemFactory {

	protected TabularDataColumn column;

	public MedianSummaryItemFactory(TabularDataColumn column) {
		this.column = column;
	}

	@Override
	public GLElement createSummaryItem(Set<NestableItem> items) {

		Table table = column.dataDomain.getTable();
		if (table instanceof NumericalTable) {
			NumericalTable numTable = (NumericalTable) table;
			Set<Integer> indices = new HashSet<>(items.size());
			for (NestableItem item : items) {
				for (Object id : item.getElementData()) {
					indices.add((Integer) id);
				}
			}
			IDType recordIDType = column.dataDomain.getOppositeIDType(column.perspective.getIdType());
			// Perspective dimensionPerspective = column.dataDomain.getDefaultTablePerspective().getPerspective(
			// dimensionIDType);

			return new SimpleAggregateDataRenderer(column.dataDomain, indices, recordIDType, column.perspective,
					(float) numTable.getMin(), (float) numTable.getMax(), numTable
							.getDataCenter().floatValue());
		}

		return new GLElement(GLRenderers.drawText("Summary of " + items.size()));
	}

}
