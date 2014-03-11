/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import gleem.linalg.Vec2f;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.view.relationshipexplorer.ui.util.SimpleBarRenderer;

/**
 * @author Christian
 *
 */
public class ScoreElement extends GLElementContainer {

	protected GLElement element;
	protected SimpleBarRenderer scoreRenderer;

	public ScoreElement(GLElement element) {
		setLayout(new GLSizeRestrictiveFlowLayout2(true, 1, GLPadding.ZERO));
		setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(this, 1, GLPadding.ZERO));
		this.element = element;
		add(element);
	}

	/**
	 * @return the element, see {@link #element}
	 */
	public GLElement getElement() {
		return element;
	}

	/**
	 * @return the scoreRenderer, see {@link #scoreRenderer}
	 */
	public SimpleBarRenderer getScoreRenderer() {
		return scoreRenderer;
	}

	public void showScore() {
		if (scoreRenderer == null) {
			createScoreRenderer(0);
		}
		if (size() < 2) {
			add(0, scoreRenderer);
		}
	}

	public void hideScore() {
		if (size() > 1) {
			remove(0);
		}
	}

	/**
	 * @param value
	 *            A value between 0 and 1
	 */
	public void setScore(float value, float maxValue) {
		if (scoreRenderer == null) {
			createScoreRenderer(value / maxValue);
		} else {
			scoreRenderer.setNormalizedValue(value / maxValue);
			scoreRenderer.setValue(value);
		}

	}

	protected void createScoreRenderer(float value) {
		scoreRenderer = new SimpleBarRenderer(value, true, true);
		scoreRenderer.setMinSize(new Vec2f(40, 16));
		scoreRenderer.setColor(Color.DARK_GRAY);
	}

}
