/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import java.util.List;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.ScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout2;

/**
 * @author Christian
 *
 */
public class EntityColumn extends GLElementContainer {

	private final ColumnBody body;

	public static interface IEntityColumnContentProvider extends ScrollingDecorator.IHasMinSize {
		public List<GLElement> getContent();
	}

	private class ColumnBody extends GLElementContainer {

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
	}

	public EntityColumn(GLElement header, IEntityColumnContentProvider contentProvider) {
		super(GLLayouts.flowVertical(5));
		this.body = new ColumnBody(GLLayouts.flowVertical(2));
		add(header);
		ScrollingDecorator scrollingDecorator = new ScrollingDecorator(body, new ScrollBar(true), new ScrollBar(false),
				8, EDimension.RECORD);
		body.setScrollingDecorator(scrollingDecorator);
		scrollingDecorator.setMinSizeProvider(contentProvider);
		add(scrollingDecorator);
		for (GLElement el : contentProvider.getContent()) {
			body.add(el);
		}
	}
}
