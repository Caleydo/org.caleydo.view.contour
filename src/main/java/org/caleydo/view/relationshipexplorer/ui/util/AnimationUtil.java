/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.util;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementParent;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.animation.Duration;

/**
 * @author Christian
 *
 */
public final class AnimationUtil {
	private AnimationUtil() {

	}

	public static void resizeElement(GLElement element, float width, float height) {
		if (Float.compare(width, element.getSize().x()) != 0 || Float.compare(height, element.getSize().y()) != 0) {
			IGLElementParent parent = element.getParent();

			if (parent != null && parent instanceof AnimatedGLElementContainer) {
				((AnimatedGLElementContainer) parent).resizeChild(element, width, height, new Duration(0));
			} else {
				element.setSize(width, height);
			}
		}
	}
}
