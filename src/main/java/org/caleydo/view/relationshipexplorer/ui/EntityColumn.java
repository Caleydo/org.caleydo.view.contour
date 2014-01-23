/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import java.util.List;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.event.EventListenerManager.ListenTo;
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
	@DeepScan
	private final IEntityColumnContentProvider contentProvider;

	public static interface IEntityColumnContentProvider extends ILabeled {
		public void setColumnBody(ColumnBody body);

		public List<EntityColumnItem<?>> getContent();

		public void takeDown();
	}

	public class ColumnBody extends GLElementContainer implements IHasMinSize {

		protected ScrollingDecorator scrollingDecorator;

		public ColumnBody(IGLLayout2 layout) {
			super(layout);
		}

		@Override
		public void layout(int deltaTimeMs) {
			super.layout(deltaTimeMs);

			Rect clippingArea = scrollingDecorator.getClipingArea();
			for (GLElement child : this) {
				Rect bounds = child.getRectBounds();
				if (child.getVisibility() != EVisibility.NONE) {
					if (clippingArea.asRectangle2D().intersects(bounds.asRectangle2D())) {
						if (child.getVisibility() != EVisibility.PICKABLE) {
							child.setVisibility(EVisibility.PICKABLE);
							repaintAll();
						}
					} else {
						if (child.getVisibility() != EVisibility.HIDDEN) {
							child.setVisibility(EVisibility.HIDDEN);
							repaintAll();
						}
					}
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
			float maxWidth = Float.MIN_VALUE;
			float sumHeight = 0;
			int numItems = 0;
			for (GLElement el : this) {
				if (el.getVisibility() != EVisibility.NONE) {
					@SuppressWarnings("unchecked")
					EntityColumnItem<? extends IHasMinSize> item = (EntityColumnItem<? extends IHasMinSize>) el;
					Vec2f minSize = item.getContent().getMinSize();
					sumHeight += minSize.y();
					if (maxWidth < minSize.x())
						maxWidth = minSize.x();

					numItems++;
				}
			}
			return new Vec2f(Math.max(maxWidth, 100), sumHeight + (numItems - 1) * EntityColumn.ROW_GAP);
		}

	}

	public EntityColumn(IEntityColumnContentProvider contentProvider) {
		super(GLLayouts.flowVertical(HEADER_BODY_SPACING));
		this.body = new ColumnBody(GLLayouts.flowVertical(ROW_GAP));
		this.contentProvider = contentProvider;
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

	@ListenTo
	public void onIDFilter(IDFilterEvent e) {
		// System.out.println("got it");
	}

	/**
	 * @return the body, see {@link #body}
	 */
	public ColumnBody getBody() {
		return body;
	}

	@Override
	protected void takeDown() {
		contentProvider.takeDown();
		super.takeDown();
	}
}
