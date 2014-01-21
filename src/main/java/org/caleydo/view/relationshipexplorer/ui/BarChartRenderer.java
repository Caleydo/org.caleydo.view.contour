/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.geom.Rect;

/**
 * @author Christian
 *
 */
public class BarChartRenderer extends GLElement {

	protected final ATableBasedDataDomain dataDomain;
	protected final int recordID;
	protected final IDType recordIDType;
	protected final Perspective dimensionPerspective;

	public BarChartRenderer(ATableBasedDataDomain dataDomain, IDType recordIDType, int recordID,
			Perspective dimensionPerspective) {
		this.dataDomain = dataDomain;
		this.recordID = recordID;
		this.recordIDType = recordIDType;
		this.dimensionPerspective = dimensionPerspective;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		Table table = dataDomain.getTable();
		if (!table.isDataHomogeneous())
			return;
		VirtualArray va = dimensionPerspective.getVirtualArray();
		if (va.size() == 0)
			return;

		float barWidth = w / va.size();
		float currentBarPos = 0;

		g.color(dataDomain.getColor());

		for (int dimensionID : dimensionPerspective.getVirtualArray()) {
			float val = dataDomain.getNormalizedValue(recordIDType, recordID, dimensionPerspective.getIdType(),
					dimensionID);
			g.fillRect(new Rect(currentBarPos, h, barWidth, -(val * h)));
			currentBarPos += barWidth;
		}

	}
}
