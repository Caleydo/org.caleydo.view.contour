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
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;

/**
 * @author Christian
 *
 */
public class KeyBasedGLElementContainer extends AnimatedGLElementContainer {

	private Map<Object, GLElement> contentMap = new HashMap<>();

	public KeyBasedGLElementContainer() {
		setLayout(GLLayouts.sizeRestrictiveFlowHorizontal(2));
	}

	public void setElement(Object key, GLElement element) {
		GLElement existingElement = contentMap.get(key);
		if (existingElement != null)
			remove(existingElement);
		contentMap.put(key, element);
		add(element);
	}

	public GLElement getElement(Object key) {
		return contentMap.get(key);
	}

	public boolean hasElement(Object key) {
		return contentMap.containsKey(key);
	}

	@Override
	public Vec2f getMinSize() {
		float maxHeight = Float.MIN_VALUE;
		float sumWidth = 0;
		int numItems = 0;
		for (GLElement child : this) {
			if (child.getVisibility() != EVisibility.NONE) {
				Vec2f minSize = child.getMinSize();
				sumWidth += minSize.x();
				if (maxHeight < minSize.y())
					maxHeight = minSize.y();

				numItems++;
			}
		}
		return new Vec2f(sumWidth + (numItems - 1) * 3, maxHeight);
	}

}
