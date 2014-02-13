/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.util;

import gleem.linalg.Vec2f;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.DataDescription;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.geom.Rect;

/**
 * Renders numerical values as bars, and categorical values as colored squares.
 *
 * @author Christian
 *
 */
public class SimpleDataRenderer extends GLElement {

	protected static final int MIN_BAR_WIDTH = 3;
	protected static final int MIN_HEIGHT = 30;

	protected final ATableBasedDataDomain dataDomain;
	protected final int recordID;
	protected final IDType recordIDType;
	protected final Perspective dimensionPerspective;

	public SimpleDataRenderer(ATableBasedDataDomain dataDomain, IDType recordIDType, int recordID,
			Perspective dimensionPerspective) {
		this.dataDomain = dataDomain;
		this.recordID = recordID;
		this.recordIDType = recordIDType;
		this.dimensionPerspective = dimensionPerspective;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		Table table = dataDomain.getTable();
		VirtualArray va = dimensionPerspective.getVirtualArray();
		if (va.size() == 0)
			return;
		float barWidth = w / va.size();
		float currentBarPos = 0;

		if (table.isDataHomogeneous()) {
			DataDescription desc = dataDomain.getDataSetDescription().getDataDescription();
			CategoricalClassDescription<?> categoricalClassDesc = desc.getCategoricalClassDescription();
			for (int dimensionID : dimensionPerspective.getVirtualArray()) {
				if (categoricalClassDesc == null) {
					NumericalTable numericalTable = (NumericalTable) dataDomain.getTable();
					float dataCenter = numericalTable.getDataCenter().floatValue();
					float min = (float) numericalTable.getMin();
					float max = (float) numericalTable.getMax();
					renderNumericalValue(g, currentBarPos, h, barWidth, dimensionID,
							getNormalizedValue(dataCenter, min, max));
				} else {
					renderCategoricalValue(g, currentBarPos, h, barWidth, dimensionID, categoricalClassDesc);
				}
				currentBarPos += barWidth;
			}

		} else {

			for (int dimensionID : dimensionPerspective.getVirtualArray()) {
				Object dataClassDesc = dataDomain.getDataClassSpecificDescription(recordIDType, recordID,
						dimensionPerspective.getIdType(), dimensionID);

				if (dataClassDesc == null || dataClassDesc instanceof NumericalProperties) {
					// TODO: use correct data center
					renderNumericalValue(g, currentBarPos, h, barWidth, dimensionID, 0);
				} else {
					renderCategoricalValue(g, currentBarPos, h, barWidth, dimensionID,
							(CategoricalClassDescription<?>) dataClassDesc);
				}
				currentBarPos += barWidth;

			}
		}

	}

	protected float getNormalizedValue(float rawValue, float min, float max) {
		float value = (rawValue - min) / (max - min);
		if (value > 1)
			return 1;
		if (value < 0)
			return 0;
		return value;
	}

	protected void renderNumericalValue(GLGraphics g, float posX, float h, float width, int dimensionID,
			float normalizedDataCenter) {
		float val = dataDomain
				.getNormalizedValue(recordIDType, recordID, dimensionPerspective.getIdType(), dimensionID);
		g.color(dataDomain.getColor()).fillRect(
				new Rect(posX, normalizedDataCenter * h, width, (normalizedDataCenter * h) - (val * h)));
	}

	protected void renderCategoricalValue(GLGraphics g, float posX, float h, float width, int dimensionID,
			CategoricalClassDescription<?> categoryDescription) {
		CategoryProperty<?> property = categoryDescription.getCategoryProperty(dataDomain.getRaw(recordIDType,
				recordID, dimensionPerspective.getIdType(), dimensionID));
		if (property == null)
			g.color(new Color(1, 1, 1, 0.3f));
		else
			g.color(property.getColor());
		g.fillRect(new Rect(posX, h, width, -h));
	}

	@Override
	public Vec2f getMinSize() {
		return new Vec2f(dimensionPerspective.getVirtualArray().size() * MIN_BAR_WIDTH, MIN_HEIGHT);
	}

	/**
	 * @return the recordID, see {@link #recordID}
	 */
	public int getRecordID() {
		return recordID;
	}

}
