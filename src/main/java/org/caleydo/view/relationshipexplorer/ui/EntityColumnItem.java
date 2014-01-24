/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import javax.media.opengl.GL2;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;

/**
 * @author Christian
 *
 */
public class EntityColumnItem<T extends GLElement & IHasMinSize> extends AnimatedGLElementContainer {

	protected class HighlightRenderer extends GLElement {

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			if (highlight) {
				g.gl.glPushAttrib(GL2.GL_LINE_BIT);
				g.color(highlightColor).lineWidth(3).drawRect(0, 0, w, h);
				g.gl.glPopAttrib();
			}
		}
	}

	protected Color highlightColor = new Color();
	boolean highlight = false;

	protected HighlightRenderer highlightRenderer;
	protected T content;
	protected String tooltip;

	/**
	 *
	 */
	public EntityColumnItem() {
		setLayout(GLLayouts.LAYERS);
		setVisibility(EVisibility.PICKABLE);
		highlightRenderer = new HighlightRenderer();
		add(highlightRenderer);
	}

	/**
	 * @param highlight
	 *            setter, see {@link highlight}
	 */
	public void setHighlight(boolean highlight) {
		if (this.highlight == highlight)
			return;
		this.highlight = highlight;
		repaint();
	}

	/**
	 * @param highlightColor
	 *            setter, see {@link highlightColor}
	 */
	public void setHighlightColor(Color highlightColor) {
		if (this.highlightColor == highlightColor)
			return;
		this.highlightColor = highlightColor;
		repaint();
	}

	/**
	 * @return the content, see {@link #content}
	 */
	public T getContent() {
		return content;
	}

	/**
	 * @param content
	 *            setter, see {@link content}
	 */
	public void setContent(T content) {
		this.content = content;
		add(content);
	}

	/**
	 * @return the highlight, see {@link #highlight}
	 */
	public boolean isHighlight() {
		return highlight;
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

	public void setToolTip(String tooltip) {
		this.tooltip = tooltip;
	}

	/**
	 * @return the tooltip, see {@link #tooltip}
	 */
	public String getTooltip() {
		return tooltip;
	}

}
