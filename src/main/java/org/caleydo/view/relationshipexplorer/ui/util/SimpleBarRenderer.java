/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.util;

import gleem.linalg.Vec2f;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;

/**
 * @author Christian
 *
 */
public class SimpleBarRenderer extends PickableGLElement {

	private boolean isHorizontal = false;
	private float normalizedValue = 0;
	private float value = 0;
	private Color color = Color.GRAY;
	private Vec2f minSize = new Vec2f(0, 0);
	private float barWidth = Float.NaN;
	private boolean showTooltip;
	private float maximumWidthPercentage = 0.9f;

	/**
	 *
	 */
	public SimpleBarRenderer() {
	}

	public SimpleBarRenderer(float value, boolean isHorizontal, boolean showTooltip) {
		this.normalizedValue = value;
		this.isHorizontal = isHorizontal;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		if (isHorizontal) {
			float posY = 0;
			if (!Float.isNaN(barWidth)) {
				posY = (h - barWidth) / 2.0f;
			}
			float barSize = w * maximumWidthPercentage * normalizedValue;
			if (Float.compare(normalizedValue, 0) > 0) {
				barSize = Math.max(barSize, 2);
			}

			g.color(color).fillRect(0, posY, barSize, Float.isNaN(barWidth) ? h : barWidth);

		} else {
			float posX = 0;
			if (!Float.isNaN(barWidth)) {
				posX = (w - barWidth) / 2.0f;
			}
			float barSize = h * maximumWidthPercentage * normalizedValue;
			if (Float.compare(normalizedValue, 0) > 0) {
				barSize = Math.max(barSize, 2);
			}
			g.color(color).fillRect(posX, 0, Float.isNaN(barWidth) ? w : barWidth, barSize);
		}
	}

	/**
	 * @param maximumWidthPercentage
	 *            setter, see {@link maximumWidthPercentage}
	 */
	public void setMaximumWidthPercentage(float maximumWidthPercentage) {
		this.maximumWidthPercentage = maximumWidthPercentage;
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		renderImpl(g, w, h);
	}

	/**
	 * @param isHorizontal
	 *            setter, see {@link isHorizontal}
	 */
	public void setHorizontal(boolean isHorizontal) {
		this.isHorizontal = isHorizontal;
	}

	/**
	 * @param value
	 *            setter, see {@link value}
	 */
	public void setNormalizedValue(float value) {
		this.normalizedValue = value;
		repaintAll();
	}

	/**
	 * @return the isHorizontal, see {@link #isHorizontal}
	 */
	public boolean isHorizontal() {
		return isHorizontal;
	}

	/**
	 * @return the value, see {@link #normalizedValue}
	 */
	public float getNormalizedValue() {
		return normalizedValue;
	}

	/**
	 * @param color
	 *            setter, see {@link color}
	 */
	public void setColor(Color color) {
		this.color = color;
	}

	/**
	 * @return the color, see {@link #color}
	 */
	public Color getColor() {
		return color;
	}

	/**
	 * @param minSize
	 *            setter, see {@link minSize}
	 */
	public void setMinSize(Vec2f minSize) {
		this.minSize = minSize;
	}

	@Override
	public Vec2f getMinSize() {
		return minSize;
	}

	/**
	 * @param barWidth
	 *            setter, see {@link barWidth}
	 */
	public void setBarWidth(float barWidth) {
		this.barWidth = barWidth;
	}

	/**
	 * @return the barWidth, see {@link #barWidth}
	 */
	public float getBarWidth() {
		return barWidth;
	}

	/**
	 * @param value
	 *            setter, see {@link value}
	 */
	public void setValue(float value) {
		this.value = value;
		if (showTooltip)
			setTooltip(String.valueOf(value));
	}

	/**
	 * @return the value, see {@link #value}
	 */
	public float getValue() {
		return value;
	}

}
