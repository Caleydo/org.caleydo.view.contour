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



}
