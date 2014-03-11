/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.list;

import javax.media.opengl.GL2;
import javax.media.opengl.GL2GL3;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;

/**
 * @author Christian
 *
 */
public class ListElement extends GLElementContainer {

	protected class HighlightRenderer extends GLElement {

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			if (isHighlight || isSelected) {
				g.incZ(-0.5f);
				Color primaryColor = selectionColor;
				Color secondaryColor = selectionColor;

				if (isHighlight) {
					if (!isSelected) {
						primaryColor = highlightColor;
					}
					secondaryColor = highlightColor;
				}

				GL2 gl = g.gl;
				g.color(primaryColor.r, primaryColor.g, primaryColor.b, 0.4f);
				gl.glBegin(GL2GL3.GL_QUADS);
				gl.glVertex2f(0, h);
				gl.glVertex2f(w, h);
				g.color(secondaryColor.r, secondaryColor.g, secondaryColor.b, 0.4f);
				gl.glVertex2f(w, 0);
				gl.glVertex2f(0, 0);
				gl.glEnd();
				g.incZ(0.5f);
				// g.gl.glPushAttrib(GL2.GL_LINE_BIT);
				// g.color(highlightColor).lineWidth(3).drawRect(0, 0, w, h);
				// g.gl.glPopAttrib();
			}
		}
	}

	protected Color highlightColor = new Color();
	protected Color selectionColor = new Color();
	boolean isSelected = false;
	boolean isHighlight = false;

	protected HighlightRenderer highlightRenderer;
	protected GLElement content;
	protected GLElementContainer contentContainer;
	protected String tooltip;

	//
	// protected ContextMenuCreator contextMenuCreator = new ContextMenuCreator();

	/**
	 *
	 */
	public ListElement() {
		setLayout(GLLayouts.LAYERS);
		setVisibility(EVisibility.PICKABLE);
		setMinSizeProvider(GLMinSizeProviders.createLayeredMinSizeProvider(this));
		highlightRenderer = new HighlightRenderer();
		add(highlightRenderer);

		contentContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 0, GLPadding.ZERO));
		contentContainer.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(contentContainer, 0,
				GLPadding.ZERO));
		add(contentContainer);
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
	public GLElement getContent() {
		return content;
	}

	/**
	 * @param content
	 *            setter, see {@link content}
	 */
	public void setContent(GLElement content) {
		this.content = content;
		contentContainer.clear();
		contentContainer.add(content);
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

	/**
	 * @return the isHighlight, see {@link #isHighlight}
	 */
	public boolean isHighlight() {
		return isHighlight;
	}

	/**
	 * @param isHighlight
	 *            setter, see {@link isHighlight}
	 */
	public void setHighlight(boolean isHighlight) {
		if (isHighlight == this.isHighlight)
			return;
		this.isHighlight = isHighlight;
		repaint();
	}

	/**
	 * @return the isSelected, see {@link #isSelected}
	 */
	public boolean isSelected() {
		return isSelected;
	}

	/**
	 * @param isSelected
	 *            setter, see {@link isSelected}
	 */
	public void setSelected(boolean isSelected) {
		if (isSelected == this.isSelected)
			return;
		this.isSelected = isSelected;
		repaint();
	}

	/**
	 * @param selectionColor
	 *            setter, see {@link selectionColor}
	 */
	public void setSelectionColor(Color selectionColor) {
		this.selectionColor = selectionColor;
	}

	// public void addContextMenuItem(AContextMenuItem item) {
	// contextMenuCreator.add(item);
	// }
}
