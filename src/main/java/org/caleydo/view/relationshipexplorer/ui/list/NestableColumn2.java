/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.list;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;

/**
 * @author Christian
 *
 */
public class NestableColumn2 extends AnimatedGLElementContainer {

	protected static final int CAPTION_HEIGHT = 20;

	protected class Column {
		protected ColumnHeader header;
		protected Column parent;
		protected List<Column> children = new ArrayList<>();
		protected Set<NestableItem> items = new HashSet<>();
		protected float columnWidth = 0;

		public void updateSizes() {

			for (Column child : children) {
				child.updateSizes();
			}

			float maxItemWidth = Float.MIN_VALUE;
			for (NestableItem item : items) {
				float itemWidth = item.item.getMinSize().x();
				if (itemWidth > maxItemWidth)
					maxItemWidth = itemWidth;
			}

			float headerItemWidth = 0;
			float itemWidth = 0;

			if (parent == null) {
				columnWidth = 8 + Math.max(maxItemWidth, header.headerItem.getMinSize().x());
				headerItemWidth = columnWidth - 8;
				itemWidth = columnWidth - 8;
			} else {
				columnWidth = 8 + Math.max(maxItemWidth + 16 + 4, header.headerItem.getMinSize().x() + CAPTION_HEIGHT
						+ 4);
				headerItemWidth = columnWidth - 8 - CAPTION_HEIGHT - 4;
				itemWidth = columnWidth - 8 - 16 - 4;
			}

			header.headerItem.setSize(headerItemWidth, CAPTION_HEIGHT);
			// header.updateSize();

			for (NestableItem item : items) {
				item.item.setSize(itemWidth, item.item.getMinSize().y());
				item.updateSize();
			}

		}

		protected float calcNestingWidth() {
			float width = 0;
			for (Column col : children) {
				width += col.calcNestingWidth();
				width += col.columnWidth;
			}
			if (children.size() > 1)
				width += (children.size() - 1) * 4;

			return width;
		}
	}

	protected class ColumnHeader extends GLElementContainer {

		protected GLElement headerItem;
		protected Column column;

		// public ColumnHeader(Column column, String caption, GLElementContainer headerParent) {
		// setLayout(new GLSizeRestrictiveFlowLayout2(true, 4, new GLPadding(4, 10, 4, 0)));
		// setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(this, 4, new GLPadding(4, 10, 4,
		// 0)));
		//
		// GLElementContainer captionContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 4,
		// GLPadding.ZERO));
		// captionContainer.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(
		// captionContainer, 4, GLPadding.ZERO));
		// captionContainer.setSize(Float.NaN, 20);
		//
		// if (headerParent != headerRow) {
		// GLButton collapseButton = new GLButton(EButtonMode.CHECKBOX);
		// collapseButton.setSelected(false);
		// collapseButton.setRenderer(GLRenderers.fillImage("resources/icons/general/minus.png"));
		// collapseButton.setSelectedRenderer(GLRenderers.fillImage("resources/icons/general/plus.png"));
		// collapseButton.setSize(20, 20);
		//
		// captionContainer.add(collapseButton);
		// }
		//
		// this.column = column;
		// headerItem = createTextElement(caption, 20);
		//
		// captionContainer.add(headerItem);
		//
		// GLElementContainer spacingContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(false, 0,
		// new GLPadding(0, 0, 0, 4)));
		// spacingContainer.setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(spacingContainer,
		// 0, new GLPadding(0, 0, 0, 4)));
		//
		// GLElement spacing = new GLElement();
		// spacing.setSize(0, Float.NaN);
		// spacingContainer.add(spacing);
		// spacingContainer.add(captionContainer);
		//
		// add(spacingContainer);
		// setRenderer(GLRenderers.drawRect(Color.GRAY));
		//
		// headerParent.add(this);
		// }
		//
		// public void updateSize() {
		// float width = column.columnWidth + (column.children.size() > 0 ? 8 : 0);
		// width += column.calcNestingWidth();
		// setSize(width, Float.NaN);
		// }

	}

	protected class NestableItem extends GLElementContainer {

		protected Map<Column, GLElementContainer> nestedContainers = new HashMap<>();
		protected NestableItem parentItem;
		protected GLElement item;
		protected Column column;

		public NestableItem(GLElement item, Column column, NestableItem parentItem) {
			setLayout(new GLSizeRestrictiveFlowLayout2(true, 4, GLPadding.ZERO));
			setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(this, 4, GLPadding.ZERO));
			this.column = column;
			setRenderer(GLRenderers.drawRect(Color.BLUE));
			add(item);
			this.item = item;

			setSize(Float.NaN, getMinSize().y());
			for (Column col : column.children) {
				addNestedContainer(col);
			}
			// updateSize();
			column.items.add(this);
		}

		public GLElementContainer getNestedContainer(Column column) {
			GLElementContainer c = nestedContainers.get(column);
			if (c == null)
				c = addNestedContainer(column);
			return c;
		}

		private GLElementContainer addNestedContainer(Column column) {

			GLElementContainer collapseContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 4,
					new GLPadding(4, 0)));
			collapseContainer.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(
					collapseContainer, 4, new GLPadding(4, 0)));

			GLButton collapseButton = new GLButton(EButtonMode.CHECKBOX);
			collapseButton.setSelected(false);
			collapseButton.setRenderer(GLRenderers.fillImage("resources/icons/general/minus.png"));
			collapseButton.setSelectedRenderer(GLRenderers.fillImage("resources/icons/general/plus.png"));
			collapseButton.setSize(16, 16);
			collapseContainer.add(collapseButton);

			GLElementContainer nestedList = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(false, 2,
					GLPadding.ZERO));
			nestedList.setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(nestedList, 2,
					GLPadding.ZERO));
			nestedContainers.put(column, nestedList);

			collapseContainer.add(nestedList);
			add(collapseContainer);

			return nestedList;
		}

		public void updateSize() {
			float width = item.getSize().x();
			width += column.calcNestingWidth();
			setSize(width, getMinSize().y());
		}

	}

}
