/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.item.factory;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.column.ATextColumn;

/**
 * @author Christian
 *
 */
public class TextItemFactory implements IItemFactory {

	protected static final int MIN_TEXT_WIDTH = 150;
	protected static final int ITEM_HEIGHT = 16;

	protected final ATextColumn column;

	public TextItemFactory(ATextColumn column) {
		this.column = column;
	}

	@Override
	public GLElement createItem(Object elementID) {
		PickableGLElement element = new PickableGLElement();
		String text = column.getText(elementID);
		element.setRenderer(GLRenderers.drawText(text, VAlign.LEFT, new GLPadding(0, 0, 0, 2)));
		element.setTooltip(text);
		element.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(MIN_TEXT_WIDTH, ITEM_HEIGHT));
		return element;
	}

}
