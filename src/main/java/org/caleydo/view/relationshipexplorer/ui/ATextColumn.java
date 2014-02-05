/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import java.util.Comparator;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;

/**
 * @author Christian
 *
 */
public abstract class ATextColumn extends AEntityColumn {

	/**
	 * @param relationshipExplorer
	 */
	public ATextColumn(RelationshipExplorerElement relationshipExplorer) {
		super(relationshipExplorer);
	}

	protected static final int MIN_TEXT_WIDTH = 150;
	protected static final int ITEM_HEIGHT = 16;

	public static final Comparator<GLElement> TEXT_COMPARATOR = new Comparator<GLElement>() {

		@Override
		public int compare(GLElement arg0, GLElement arg1) {
			MinSizeTextElement r1 = (MinSizeTextElement) ((EntityRow) arg0).getElement(DATA_KEY);
			MinSizeTextElement r2 = (MinSizeTextElement) ((EntityRow) arg1).getElement(DATA_KEY);
			return r1.getLabel().compareTo(r2.getLabel());
		}
	};

	protected class MinSizeTextElement extends GLElement implements ILabeled {

		private String text;

		public MinSizeTextElement(String text) {
			super(GLRenderers.drawText(text));
			this.text = text;
		}

		@Override
		public Vec2f getMinSize() {
			return new Vec2f(MIN_TEXT_WIDTH, ITEM_HEIGHT);
		}

		@Override
		public String getLabel() {
			return text;
		}

	}

	public MinSizeTextElement addTextElement(String text, Object elementID) {
		MinSizeTextElement el = new MinSizeTextElement(text);
		addElement(el, elementID);
		return el;
	}

	@Override
	protected Comparator<GLElement> getDefaultElementComparator() {
		return TEXT_COMPARATOR;
	}

}
