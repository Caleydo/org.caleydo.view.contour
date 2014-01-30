/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import java.util.Map.Entry;
import java.util.Set;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * @author Christian
 *
 */
public abstract class AEntityColumn extends AnimatedGLElementContainer implements ILabeled {
	protected static final int HEADER_HEIGHT = 20;
	protected static final int HEADER_BODY_SPACING = 5;

	protected GLElement header;
	protected GLElementList itemList = new GLElementList();
	protected BiMap<Object, GLElement> mapIDToElement = HashBiMap.create();

	public AEntityColumn() {
		super(GLLayouts.flowVertical(HEADER_BODY_SPACING));
		header = new GLElement();
		header.setSize(Float.NaN, HEADER_HEIGHT);
		add(header);
		add(itemList.asGLElement());
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		setContent();
		header.setRenderer(GLRenderers.drawText(getLabel(), VAlign.CENTER));
	}

	protected abstract void setContent();

	protected void addElement(GLElement element, Object elementID) {
		mapIDToElement.put(elementID, element);
		itemList.add(element);
	}

	protected void setFilteredItems(Set<Object> ids) {
		for (Entry<Object, GLElement> entry : mapIDToElement.entrySet()) {

			GLElement element = entry.getValue();
			boolean visible = false;

			if (ids.contains(entry.getKey())) {
				visible = true;
				itemList.show(element);
				itemList.asGLElement().relayout();
			}

			if (!visible) {
				itemList.hide(element);
				itemList.asGLElement().relayout();
			}

		}
	}

	@Override
	public String getLabel() {
		return "Column";
	}

	@Override
	public Vec2f getMinSize() {
		return itemList.getMinSize();
	}
}
