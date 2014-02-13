/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import gleem.linalg.Vec2f;

import java.util.Comparator;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.util.KeyBasedGLElementContainer;

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
			@SuppressWarnings("unchecked")
			MinSizeTextElement r1 = (MinSizeTextElement) ((KeyBasedGLElementContainer<GLElement>) arg0)
					.getElement(DATA_KEY);
			@SuppressWarnings("unchecked")
			MinSizeTextElement r2 = (MinSizeTextElement) ((KeyBasedGLElementContainer<GLElement>) arg1)
					.getElement(DATA_KEY);
			return r1.getLabel().compareTo(r2.getLabel());
		}
	};

	public class MinSizeTextElement extends GLElement implements ILabeled {

		private String text;
		private Vec2f minSize;

		public MinSizeTextElement(String text) {
			super(GLRenderers.drawText(text));
			this.text = text;
		}

		@Override
		public Vec2f getMinSize() {
			return minSize;
		}

		/**
		 * @param minSize
		 *            setter, see {@link minSize}
		 */
		public void setMinSize(Vec2f minSize) {
			this.minSize = minSize;
		}

		@Override
		public String getLabel() {
			return text;
		}

	}

	public MinSizeTextElement addTextElement(String text, Object elementID) {
		MinSizeTextElement el = new MinSizeTextElement(text);
		el.setMinSize(new Vec2f(MIN_TEXT_WIDTH, ITEM_HEIGHT));
		addElement(el, elementID);
		return el;
	}

	@Override
	public Comparator<GLElement> getDefaultElementComparator() {
		return TEXT_COMPARATOR;
	}

}
