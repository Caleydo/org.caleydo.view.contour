/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import java.util.HashMap;
import java.util.Map;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayout2;

/**
 * @author Christian
 *
 */
public class KeyBasedGLElementContainer<T extends GLElement> extends AnimatedGLElementContainer {

	protected Map<Object, T> contentMap = new HashMap<>();
	protected IHasMinSize minSizeProvider;

	public KeyBasedGLElementContainer() {
	}

	public KeyBasedGLElementContainer(IGLLayout2 layout) {
		super(layout);
	}

	public void setElement(Object key, T element) {
		GLElement existingElement = contentMap.get(key);
		if (existingElement != null)
			remove(existingElement);
		contentMap.put(key, element);
		add(element);
	}

	public T getElement(Object key) {
		return contentMap.get(key);
	}

	public boolean hasElement(Object key) {
		return contentMap.containsKey(key);
	}

	@Override
	public Vec2f getMinSize() {
		if (minSizeProvider == null)
			return super.getMinSize();
		return minSizeProvider.getMinSize();
	}

	/**
	 * @param minSizeProvider
	 *            setter, see {@link minSizeProvider}
	 */
	public void setMinSizeProvider(IHasMinSize minSizeProvider) {
		this.minSizeProvider = minSizeProvider;
	}

	public void setHorizontalFlowMinSizeProvider(final float gap) {
		minSizeProvider = new IHasMinSize() {

			@Override
			public Vec2f getMinSize() {
				float maxHeight = Float.MIN_VALUE;
				float sumWidth = 0;
				int numItems = 0;
				for (GLElement child : KeyBasedGLElementContainer.this) {
					if (child.getVisibility() != EVisibility.NONE) {
						Vec2f minSize = child.getMinSize();
						sumWidth += minSize.x();
						if (maxHeight < minSize.y())
							maxHeight = minSize.y();

						numItems++;
					}
				}
				return new Vec2f(sumWidth + (numItems - 1) * gap, maxHeight);
			}
		};
	}

	public void setVerticalFlowMinSizeProvider(final float gap) {
		minSizeProvider = new IHasMinSize() {

			@Override
			public Vec2f getMinSize() {
				float maxWidth = Float.MIN_VALUE;
				float sumHeight = 0;
				int numItems = 0;
				for (GLElement child : KeyBasedGLElementContainer.this) {
					if (child.getVisibility() != EVisibility.NONE) {
						Vec2f minSize = child.getMinSize();
						sumHeight += minSize.y();
						if (maxWidth < minSize.x())
							maxWidth = minSize.x();

						numItems++;
					}
				}
				return new Vec2f(maxWidth, sumHeight + (numItems - 1) * gap);
			}
		};
	}

	public void setLayeredMinSizeProvider() {
		minSizeProvider = new IHasMinSize() {

			@Override
			public Vec2f getMinSize() {
				float maxHeight = Float.MIN_VALUE;
				float maxWidth = Float.MIN_VALUE;
				for (GLElement child : KeyBasedGLElementContainer.this) {
					if (child.getVisibility() != EVisibility.NONE) {
						Vec2f minSize = child.getMinSize();
						if (maxWidth < minSize.x())
							maxWidth = minSize.x();
						if (maxHeight < minSize.y())
							maxHeight = minSize.y();
					}
				}
				return new Vec2f(maxWidth, maxHeight);
			}
		};
	}

}
