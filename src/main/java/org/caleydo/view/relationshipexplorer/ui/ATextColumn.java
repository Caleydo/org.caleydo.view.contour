/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;

/**
 * @author Christian
 *
 */
public abstract class ATextColumn extends AEntityColumn {

	protected static final int MIN_TEXT_WIDTH = 150;
	protected static final int ITEM_HEIGHT = 16;

	protected class MinSizeTextElement extends GLElement {

		public MinSizeTextElement() {

		}

		public MinSizeTextElement(IGLRenderer renderer) {
			super(renderer);
		}

		@Override
		public Vec2f getMinSize() {
			return new Vec2f(MIN_TEXT_WIDTH, ITEM_HEIGHT);
		}

	}

	public MinSizeTextElement addTextElement(String text, Object elementID) {
		MinSizeTextElement el = new MinSizeTextElement(GLRenderers.drawText(text));
		addElement(el, elementID);
		return el;
	}


}
