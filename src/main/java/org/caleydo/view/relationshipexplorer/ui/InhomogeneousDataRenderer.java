/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.layout2.geom.Rect;

/**
 * @author Christian
 *
 */
public class InhomogeneousDataRenderer extends GLElement implements IHasMinSize {

	protected static final int MIN_BAR_WIDTH = 3;
	protected static final int MIN_HEIGHT = 30;

	protected final ATableBasedDataDomain dataDomain;
	protected final int recordID;
	protected final IDType recordIDType;
	protected final Perspective dimensionPerspective;

	public InhomogeneousDataRenderer(ATableBasedDataDomain dataDomain, IDType recordIDType, int recordID,
			Perspective dimensionPerspective) {
		this.dataDomain = dataDomain;
		this.recordID = recordID;
		this.recordIDType = recordIDType;
		this.dimensionPerspective = dimensionPerspective;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		Table table = dataDomain.getTable();
		if (table.isDataHomogeneous())
			return;

		VirtualArray va = dimensionPerspective.getVirtualArray();
		if (va.size() == 0)
			return;

		float barWidth = w / va.size();
		float currentBarPos = 0;

		for (int dimensionID : dimensionPerspective.getVirtualArray()) {
			Object dataClassDesc = dataDomain.getDataClassSpecificDescription(recordIDType, recordID,
					dimensionPerspective.getIdType(), dimensionID);

			if (dataClassDesc == null || dataClassDesc instanceof NumericalProperties) {
				float val = dataDomain.getNormalizedValue(recordIDType, recordID, dimensionPerspective.getIdType(),
						dimensionID);
				g.color(dataDomain.getColor()).fillRect(new Rect(currentBarPos, h, barWidth, -(val * h)));
			} else {
				CategoricalClassDescription<?> categoryDescription = (CategoricalClassDescription<?>) dataClassDesc;
				CategoryProperty<?> property = categoryDescription.getCategoryProperty(dataDomain.getRaw(recordIDType,
						recordID, dimensionPerspective.getIdType(), dimensionID));
				if (property == null)
					g.color(new Color(1, 1, 1, 0.3f));
				else
					g.color(property.getColor());
				g.fillRect(new Rect(currentBarPos, h, barWidth, -h));
			}
			currentBarPos += barWidth;

		}

	}

	@Override
	public Vec2f getMinSize() {
		return new Vec2f(dimensionPerspective.getVirtualArray().size() * MIN_BAR_WIDTH, MIN_HEIGHT);
	}

}
