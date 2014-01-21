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
import org.caleydo.view.relationshipexplorer.ui.EntityColumn.IEntityColumnContentProvider;

/**
 * @author Christian
 *
 */
public class CompoundContentProvider implements IEntityColumnContentProvider {

	protected List<GLElement> content = new ArrayList<>();

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
		renderer.setSize(200, 30);
		content.add(renderer);
	}

	@Override
	public Vec2f getMinSize() {
		return new Vec2f(200, content.size() * 30 + (content.size() - 1) * 2);
	}

	@Override
	public List<GLElement> getContent() {
		return content;
	}

}
