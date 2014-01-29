/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.view.relationshipexplorer.ui.EntityColumn.IEntityColumnContentProvider;

/**
 * @author Christian
 *
 */
public abstract class ATextualContentProvider implements IEntityColumnContentProvider {

	protected static final int MIN_TEXT_WIDTH = 150;
	protected static final int ITEM_HEIGHT = 16;

	protected List<MinSizeTextElement> items = new ArrayList<>();
	protected EntityColumn entityColumn;

	// protected ColumnBody columnBody;

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

	// @Override
	// public void setColumnBody(ColumnBody body) {
	// this.columnBody = body;
	//
	// }

	// @Override
	// public List<GLElement> getContent() {
	// List<GLElement> content = new ArrayList<>(items.size());
	// content.addAll(items);
	// return content;
	// }

	public MinSizeTextElement addItem(String text) {
		MinSizeTextElement el = new MinSizeTextElement(GLRenderers.drawText(text));
		// EntityColumnItem<MinSizeTextElement> item = new EntityColumnItem<>();
		// item.setSize(Float.NaN, ITEM_HEIGHT);
		// item.setContent(el);
		items.add(el);
		return el;
	}

	@Override
	public void takeDown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setEntityColumn(EntityColumn column) {
		this.entityColumn = column;
	}
}
