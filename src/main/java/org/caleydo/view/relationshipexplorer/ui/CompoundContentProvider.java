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
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.view.relationshipexplorer.ui.EntityColumn.ColumnBody;
import org.caleydo.view.relationshipexplorer.ui.EntityColumn.IEntityColumnContentProvider;

/**
 * @author Christian
 *
 */
public class CompoundContentProvider implements IEntityColumnContentProvider {

	protected List<BarChartRenderer> items = new ArrayList<>();

	public CompoundContentProvider() {
		for (IDataDomain dataDomain : DataDomainManager.get().getAllDataDomains()) {
			if (dataDomain instanceof ATableBasedDataDomain) {
				ATableBasedDataDomain dd = (ATableBasedDataDomain) dataDomain;
				DataSetDescription desc = dd.getDataSetDescription();
				if (desc.getColumnIDSpecification().getIdType().equals("COMPOUND_ID")
						|| desc.getRowIDSpecification().getIdType().equals("COMPOUND_ID")) {

					IDType compoundIDType = IDType.getIDType("COMPOUND_ID");
					if (dd.getDimensionIDCategory() == compoundIDType.getIDCategory()) {
						for (int id : dd.getDefaultTablePerspective().getDimensionPerspective().getVirtualArray()) {
							addBarChartRenderer(dd, dd.getDefaultTablePerspective().getDimensionPerspective()
									.getIdType(), id, dd.getDefaultTablePerspective().getRecordPerspective());
						}
					} else {
						for (int id : dd.getDefaultTablePerspective().getRecordPerspective().getVirtualArray()) {
							addBarChartRenderer(dd, dd.getDefaultTablePerspective().getRecordPerspective().getIdType(),
									id, dd.getDefaultTablePerspective().getDimensionPerspective());
						}
					}
					return;
				}
			}

		}
	}

	protected void addBarChartRenderer(ATableBasedDataDomain dd, IDType recordIDType, int recordID,
			Perspective dimensionPerspective) {
		BarChartRenderer renderer = new BarChartRenderer(dd, recordIDType, recordID, dimensionPerspective);
		renderer.setSize(Float.NaN, BarChartRenderer.MIN_HEIGHT);
		items.add(renderer);
	}

	protected Vec2f getMinSize() {
		float maxWidth = Float.MIN_VALUE;
		float sumHeight = 0;
		for (BarChartRenderer renderer : items) {
			Vec2f minSize = renderer.getMinSize();
			sumHeight += minSize.y();
			if (maxWidth < minSize.x())
				maxWidth = minSize.x();
		}
		return new Vec2f(maxWidth, sumHeight + (items.size() - 1) * EntityColumn.ROW_GAP);
	}

	@Override
	public List<GLElement> getContent() {
		List<GLElement> content = new ArrayList<>();
		content.addAll(items);
		return content;
	}

	@Override
	public void setColumnBody(ColumnBody body) {
		body.setMinSize(getMinSize());
	}

}
