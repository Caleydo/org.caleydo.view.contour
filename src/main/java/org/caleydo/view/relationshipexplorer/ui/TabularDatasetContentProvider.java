/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.view.relationshipexplorer.ui.EntityColumn.ColumnBody;
import org.caleydo.view.relationshipexplorer.ui.EntityColumn.IEntityColumnContentProvider;

/**
 * @author Christian
 *
 */
public class TabularDatasetContentProvider implements IEntityColumnContentProvider {

	protected final ATableBasedDataDomain dataDomain;
	protected final IDCategory itemIDCategory;
	protected final TablePerspective tablePerspective;

	protected List<GLElement> items = new ArrayList<>();

	public TabularDatasetContentProvider(TablePerspective tablePerspective, IDCategory itemIDCategory) {
		dataDomain = tablePerspective.getDataDomain();
		this.itemIDCategory = itemIDCategory;
		this.tablePerspective = tablePerspective;

		// if (dataDomain.getTable().isDataHomogeneous()) {
		VirtualArray recordVA;
		IDType recordIDType;
		Perspective dimensionPerspective;

		if (dataDomain.getDimensionIDCategory() == itemIDCategory) {
			recordVA = tablePerspective.getDimensionPerspective().getVirtualArray();
			recordIDType = tablePerspective.getDimensionPerspective().getIdType();
			dimensionPerspective = tablePerspective.getRecordPerspective();

		} else {
			recordVA = tablePerspective.getRecordPerspective().getVirtualArray();
			recordIDType = tablePerspective.getRecordPerspective().getIdType();
			dimensionPerspective = tablePerspective.getDimensionPerspective();
		}

		for (int id : recordVA) {
			addItemRenderer(dataDomain, recordIDType, id, dimensionPerspective);
		}
		// } else {
		// if (dataDomain.getDimensionIDCategory() == itemIDCategory) {
		// for (int id : tablePerspective.getDimensionPerspective().getVirtualArray()) {
		// addInhomogeneousRenderer(dataDomain, tablePerspective.getDimensionPerspective().getIdType(), id,
		// tablePerspective.getRecordPerspective());
		// }
		// } else {
		// for (int id : tablePerspective.getRecordPerspective().getVirtualArray()) {
		// addInhomogeneousRenderer(dataDomain, tablePerspective.getRecordPerspective().getIdType(), id,
		// tablePerspective.getDimensionPerspective());
		// }
		// }
		// }
	}

	// protected void addBarChartRenderer(ATableBasedDataDomain dd, IDType recordIDType, int recordID,
	// Perspective dimensionPerspective) {
	// BarChartRenderer renderer = new BarChartRenderer(dd, recordIDType, recordID, dimensionPerspective);
	// renderer.setSize(Float.NaN, BarChartRenderer.MIN_HEIGHT);
	// items.add(renderer);
	// }

	protected void addItemRenderer(ATableBasedDataDomain dd, IDType recordIDType, int recordID,
			Perspective dimensionPerspective) {
		SimpleDataRenderer renderer = new SimpleDataRenderer(dd, recordIDType, recordID, dimensionPerspective);
		renderer.setSize(Float.NaN, SimpleDataRenderer.MIN_HEIGHT);
		items.add(renderer);
	}

	protected Vec2f getMinSize() {
		float maxWidth = Float.MIN_VALUE;
		float sumHeight = 0;
		for (GLElement renderer : items) {
			Vec2f minSize = ((IHasMinSize) renderer).getMinSize();
			sumHeight += minSize.y();
			if (maxWidth < minSize.x())
				maxWidth = minSize.x();
		}
		return new Vec2f(maxWidth, sumHeight + (items.size() - 1) * EntityColumn.ROW_GAP);
	}

	@Override
	public void setColumnBody(ColumnBody body) {
		body.setMinSize(getMinSize());
	}

	@Override
	public List<GLElement> getContent() {
		return items;
	}

}
