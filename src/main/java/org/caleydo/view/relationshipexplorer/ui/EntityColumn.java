/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import java.util.List;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.ScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout2;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;

/**
 * @author Christian
 *
 */
public class EntityColumn extends GLElementContainer {

	public static final int ROW_GAP = 2;

	protected static final int HEADER_HEIGHT = 20;
	protected static final int HEADER_BODY_SPACING = 5;
	protected static final int SCROLLBAR_WIDTH = 8;

	private final ColumnBody body;

	public static interface IEntityColumnContentProvider extends ILabeled {
		public void setColumnBody(ColumnBody body);

		public List<GLElement> getContent();
	}

	public class ColumnBody extends GLElementContainer implements IHasMinSize {

		protected ScrollingDecorator scrollingDecorator;
		protected Vec2f minSize = new Vec2f();

		public ColumnBody(IGLLayout2 layout) {
			super(layout);
		}

		@Override
		public void layout(int deltaTimeMs) {
			super.layout(deltaTimeMs);

			Rect clippingArea = scrollingDecorator.getClipingArea();
			for (GLElement child : this) {
				Rect bounds = child.getRectBounds();
				if (clippingArea.asRectangle2D().intersects(bounds.asRectangle2D())) {
					child.setVisibility(EVisibility.PICKABLE);
				} else {
					child.setVisibility(EVisibility.HIDDEN);
				}
			}
		}

		/**
		 * @param scrollingDecorator
		 *            setter, see {@link scrollingDecorator}
		 */
		public void setScrollingDecorator(ScrollingDecorator scrollingDecorator) {
			this.scrollingDecorator = scrollingDecorator;
		}

		@Override
		public Vec2f getMinSize() {
			if (minSize.x() < 100)
				return new Vec2f(100, minSize.y());
			return minSize;
		}

		/**
		 * @param minSize
		 *            setter, see {@link minSize}
		 */
		public void setMinSize(Vec2f minSize) {
			this.minSize = minSize;
		}

	}

	public EntityColumn(IEntityColumnContentProvider contentProvider) {
		super(GLLayouts.flowVertical(HEADER_BODY_SPACING));
		this.body = new ColumnBody(GLLayouts.flowVertical(ROW_GAP));
		GLElement header = new GLElement(GLRenderers.drawText(contentProvider.getLabel(), VAlign.CENTER));
		header.setSize(Float.NaN, HEADER_HEIGHT);
		add(header);
		ScrollingDecorator scrollingDecorator = new ScrollingDecorator(body, new ScrollBar(true), new ScrollBar(false),
				SCROLLBAR_WIDTH, EDimension.RECORD);
		body.setScrollingDecorator(scrollingDecorator);
		contentProvider.setColumnBody(body);
		scrollingDecorator.setMinSizeProvider(body);
		add(scrollingDecorator);
		for (GLElement el : contentProvider.getContent()) {
			body.add(el);
		}
	}

	/**
	 * @return the body, see {@link #body}
	 */
	public ColumnBody getBody() {
		return body;
	}
}
