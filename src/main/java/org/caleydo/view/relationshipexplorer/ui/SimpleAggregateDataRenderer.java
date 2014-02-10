/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.function.AdvancedDoubleStatistics;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.geom.Rect;

/**
 * @author Christian
 *
 */
public class SimpleAggregateDataRenderer extends GLElement {
	protected static final int MIN_BAR_WIDTH = 3;
	protected static final int MIN_HEIGHT = 30;

	protected final ATableBasedDataDomain dataDomain;
	// protected final IDType recordIDType;
	// protected final Perspective dimensionPerspective;

	protected List<Float> aggregatedNormalizedValues = new ArrayList<>();
	protected float normalizedDataCenter;

	public SimpleAggregateDataRenderer(ATableBasedDataDomain dataDomain, List<Integer> recordIDs, IDType recordIDType,
			Perspective dimensionPerspective, float min, float max, float dataCenter) {
		this.dataDomain = dataDomain;
		// this.recordIDType = recordIDType;
		// this.dimensionPerspective = dimensionPerspective;
		VirtualArray va = dimensionPerspective.getVirtualArray();

		for (Integer dimensionID : va) {
			double[] normalizedValues = new double[recordIDs.size()];
			for (int i = 0; i < recordIDs.size(); i++) {
				Integer recordID = recordIDs.get(i);
				normalizedValues[i] = dataDomain.getNormalizedValue(recordIDType, recordID,
						dimensionPerspective.getIdType(), dimensionID);
			}
			aggregatedNormalizedValues.add(new Float(AdvancedDoubleStatistics.of(normalizedValues).getMedian()));
		}
		normalizedDataCenter = getNormalizedValue(dataCenter, min, max);
	}

	protected float getNormalizedValue(float rawValue, float min, float max) {
		float value = (rawValue - min) / (max - min);
		if (value > 1)
			return 1;
		if (value < 0)
			return 0;
		return value;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {

		if (aggregatedNormalizedValues.size() == 0)
			return;
		float barWidth = w / aggregatedNormalizedValues.size();
		float currentBarPos = 0;
		// float spacing = 0;
		// float range = h;
		// dataCenter = -1;
		// showCenterLineAtRowCenter = true;
		// if (showCenterLineAtRowCenter) {
		// float diffMax = maxValue - dataCenter;
		// float diffMin = dataCenter - minValue;
		// float totalValueRange = 0;
		// if (diffMax >= diffMin) {
		// totalValueRange = diffMax * 2.0f;
		// } else {
		// totalValueRange = diffMin * 2.0f;
		// }
		// float numberSpacing = diffMax - diffMin;
		// if (totalValueRange != 0) {
		// spacing = numberSpacing / totalValueRange * y;
		// }
		// range = y - Math.abs(spacing);
		// if (spacing < 0)
		// spacing = 0;
		// }

		// for (Float value : aggregatedNormalizedValues) {
		// renderNumericalValue(g, currentBarPos, h, barWidth, value);
		//
		// // renderSingleBar((normalizedDataCenter * range) + spacing, (value - normalizedDataCenter) * range, x,
		// // selectionTypes, color, columnID, useShading);
		// currentBarPos += barWidth;
		// }
		// currentBarPos = 0;
		List<Vec2f> path = new ArrayList<>(aggregatedNormalizedValues.size());
		for (Float value : aggregatedNormalizedValues) {
			path.add(new Vec2f(currentBarPos, h - (value * h)));
			// renderNumericalValue(g, currentBarPos, h, barWidth, value);

			// renderSingleBar((normalizedDataCenter * range) + spacing, (value - normalizedDataCenter) * range, x,
			// selectionTypes, color, columnID, useShading);
			currentBarPos += barWidth;
		}
		g.gl.glPushAttrib(GL2.GL_LINE_BIT);
		g.lineWidth(2).color(dataDomain.getColor()).drawPath(path, false);
		g.gl.glPopAttrib();
	}

	protected void renderNumericalValue(GLGraphics g, float posX, float h, float width, float val) {

		g.color(dataDomain.getColor()).fillRect(
				new Rect(posX, normalizedDataCenter * h, width, (normalizedDataCenter * h) - (val * h)));
	}

	@Override
	public Vec2f getMinSize() {
		return new Vec2f(aggregatedNormalizedValues.size() * MIN_BAR_WIDTH, MIN_HEIGHT);
	}

}
