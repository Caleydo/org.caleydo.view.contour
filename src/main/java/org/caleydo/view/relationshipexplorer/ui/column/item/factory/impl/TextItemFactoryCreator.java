/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.item.factory.impl;

import java.net.URL;

import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.ATextColumn;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.list.EUpdateCause;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;

/**
 * @author Christian
 *
 */
public class TextItemFactoryCreator implements IItemFactoryCreator {

	protected static final URL PLOT_ICON = TextItemFactory.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/hbar.png");

	public static class TextItemFactory implements IItemFactory {

		protected static final int MIN_TEXT_WIDTH = 150;
		protected static final int ITEM_HEIGHT = 16;

		protected final ATextColumn column;

		public TextItemFactory(ATextColumn column) {
			this.column = column;
		}

		@Override
		public GLElement createItem(Object elementID) {
			String text = column.getText(elementID);
			return createTextElement(text);
		}

		public static PickableGLElement createTextElement(String text) {
			PickableGLElement element = new PickableGLElement();
			element.setRenderer(GLRenderers.drawText(text, VAlign.LEFT, new GLPadding(0, 0, 0, 2)));
			element.setTooltip(text);
			element.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(MIN_TEXT_WIDTH, ITEM_HEIGHT));
			return element;
		}

		@Override
		public GLElement createHeaderExtension() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void update() {
		}

		@Override
		public boolean needsUpdate(EUpdateCause cause) {
			return false;
		}



	}

	@Override
	public IItemFactory create(IEntityCollection collection, IColumnModel column, ConTourElement contour) {
		return new TextItemFactory((ATextColumn) column);
	}

	@Override
	public URL getIconURL() {
		return PLOT_ICON;
	}
}
