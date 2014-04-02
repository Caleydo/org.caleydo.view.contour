/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.util;

import gleem.linalg.Vec2f;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.view.relationshipexplorer.ui.list.ColumnTreeRenderStyle;

/**
 * @author Christian
 *
 */
public class MappingRenderer extends KeyBasedGLElementContainer<SimpleBarRenderer> {
	protected static final Integer SELECTED_ELEMENTS_KEY = Integer.valueOf(2);
	protected static final Integer FILTERED_ELEMENTS_KEY = Integer.valueOf(3);
	protected static final Integer ALL_ELEMENTS_KEY = Integer.valueOf(4);

	protected int maxValue = 1;
	protected float barWidth = ColumnTreeRenderStyle.COLUMN_SUMMARY_BAR_HEIGHT;

	public MappingRenderer(int maxValue) {
		setLayout(GLLayouts.LAYERS);
		this.maxValue = maxValue;

		// setRenderer(GLRenderers.drawRect(Color.GREEN));
		// barLayerRenderer.setSize(80, Float.NaN);
		// Add empty element to get rid of weird highlight overlapping bug...
		add(new GLElement());
		setMinSizeProvider(GLMinSizeProviders.createLayeredMinSizeProvider(this));
		setVisibility(EVisibility.PICKABLE);
		setElement(ALL_ELEMENTS_KEY, createDefaultBarRenderer(Color.LIGHT_GRAY, 0.1f));
		setElement(FILTERED_ELEMENTS_KEY, createDefaultBarRenderer(Color.GRAY, 0.2f));
		setElement(SELECTED_ELEMENTS_KEY, createDefaultBarRenderer(SelectionType.SELECTION.getColor(), 0.3f));
		updateBarWidth();
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		this.onPick(context.getSWTLayer().createTooltip(new ILabeled() {
			@Override
			public String getLabel() {
				return getTooltip();
			}
		}));
	}

	public String getTooltip() {
		StringBuilder b = new StringBuilder();
		b.append("Number of Items\n");
		b.append(String.format("%s:\t%d\n", "Selected", (int) getElement(SELECTED_ELEMENTS_KEY).getValue()));
		b.append(String.format("%s:\t%d\n", "Filtered", (int) getElement(FILTERED_ELEMENTS_KEY).getValue()));
		b.append(String.format("%s:\t%d\n", "All", (int) getElement(ALL_ELEMENTS_KEY).getValue()));
		return b.toString();
	}

	/**
	 * @param maxValue
	 *            setter, see {@link maxValue}
	 */
	public void setMaxValue(int maxValue) {
		this.maxValue = maxValue;
	}

	public void setSelectedValue(int value) {
		setValue(SELECTED_ELEMENTS_KEY, value);
	}

	public void setFilteredValue(int value) {
		setValue(FILTERED_ELEMENTS_KEY, value);
	}

	public void setAllValue(int value) {
		setValue(ALL_ELEMENTS_KEY, value);
	}

	protected void setValue(int key, int value) {
		getElement(key).setValue(value);
		getElement(key).setNormalizedValue((float) value / maxValue);
	}

	public void setBarWidth(float barWidth) {
		this.barWidth = barWidth;
		updateBarWidth();
	}

	protected void updateBarWidth() {
		getElement(SELECTED_ELEMENTS_KEY).setMinSize(new Vec2f(80, barWidth));
		getElement(SELECTED_ELEMENTS_KEY).setBarWidth(barWidth - 4);

		getElement(FILTERED_ELEMENTS_KEY).setMinSize(new Vec2f(80, barWidth));
		getElement(FILTERED_ELEMENTS_KEY).setBarWidth(barWidth - 4);

		getElement(ALL_ELEMENTS_KEY).setMinSize(new Vec2f(80, barWidth));
		getElement(ALL_ELEMENTS_KEY).setBarWidth(barWidth - 4);
	}

	public void setMaximumWidthPercentage(float value) {
		getElement(SELECTED_ELEMENTS_KEY).setMaximumWidthPercentage(value);

		getElement(FILTERED_ELEMENTS_KEY).setMaximumWidthPercentage(value);

		getElement(ALL_ELEMENTS_KEY).setMaximumWidthPercentage(value);
	}

	protected SimpleBarRenderer createDefaultBarRenderer(Color color, float zDelta) {
		SimpleBarRenderer renderer = new SimpleBarRenderer(0, true, false);
		renderer.setMinSize(new Vec2f(80, barWidth));
		// renderer.setSize(80, Float.NaN);
		renderer.setColor(color);
		renderer.setBarWidth(12);
		renderer.setzDelta(zDelta);
		return renderer;
	}

}
