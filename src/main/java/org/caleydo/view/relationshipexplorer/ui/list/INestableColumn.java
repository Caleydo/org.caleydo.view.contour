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
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
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
public class INestableColumn extends GLElementContainer {

	protected static final int CAPTION_HEIGHT = 20;
	protected static final int COLLAPSE_BUTTON_SIZE = 16;
	protected static final int HEADER_TOP_PADDING = 10;
	protected static final int HORIZONTAL_PADDING = 4;
	protected static final int HORIZONTAL_SPACING = 4;
	protected static final int VERTICAL_SPACING = 2;

	protected List<Column> rootColumns = new ArrayList<>();
	protected Map<Column, GLElementContainer> rootContainers = new HashMap<>();

	protected GLElementContainer headerRow;
	protected GLElementContainer bodyRow;

	protected class Column {
		protected ColumnHeader header;
		protected Column parent;
		protected List<Column> children = new ArrayList<>();

		protected Set<NestableItem> items = new HashSet<>();
		protected float columnWidth = 0;

		public float calcMinColumnWidth() {

			float maxItemWidth = Float.MIN_VALUE;
			for (NestableItem item : items) {
				float itemWidth = item.item.getMinSize().x();
				if (itemWidth > maxItemWidth)
					maxItemWidth = itemWidth;
			}
			float minColumnWidth = 0;
			if (parent == null) {
				minColumnWidth = 2 * HORIZONTAL_PADDING + Math.max(maxItemWidth, header.headerItem.getMinSize().x());

			} else {
				minColumnWidth = 2
						* HORIZONTAL_PADDING
						+ Math.max(maxItemWidth + COLLAPSE_BUTTON_SIZE + HORIZONTAL_SPACING, header.headerItem
								.getMinSize().x() + CAPTION_HEIGHT + HORIZONTAL_SPACING);
			}
			return minColumnWidth;
		}

		public void setColumnWidth(float width) {
			this.columnWidth = width;
			// updateSizes();
		}

		public void updateSizeRec() {
			for (Column child : children) {
				child.updateSizeRec();
			}
			updateSizes();
		}

		public void updateSizes() {
			float headerItemWidth = 0;
			float itemWidth = 0;

			if (parent == null) {
				headerItemWidth = columnWidth - 2 * HORIZONTAL_PADDING;
				itemWidth = columnWidth - 2 * HORIZONTAL_PADDING;
			} else {
				headerItemWidth = columnWidth - 2 * HORIZONTAL_PADDING - CAPTION_HEIGHT - HORIZONTAL_SPACING;
				itemWidth = columnWidth - 2 * HORIZONTAL_PADDING - COLLAPSE_BUTTON_SIZE - HORIZONTAL_SPACING;
			}

			header.headerItem.setSize(headerItemWidth, CAPTION_HEIGHT);
			header.updateSize();

			for (NestableItem item : items) {
				item.item.setSize(itemWidth, item.item.getMinSize().y());
				item.updateSize();
			}

			// parent.updateSizes();
		}

		protected float calcNestingWidth() {
			float width = 0;
			for (Column col : children) {
				width += col.calcNestingWidth();
				width += col.columnWidth;
			}
			if (children.size() >= 1)
				width += children.size() * HORIZONTAL_SPACING;

			return width;
		}
	}

	protected class ColumnHeader extends GLElementContainer {

		protected GLElement headerItem;
		protected Column column;

		public ColumnHeader(Column column, String caption, GLElementContainer headerParent) {
			setLayout(new GLSizeRestrictiveFlowLayout2(true, HORIZONTAL_SPACING, new GLPadding(HORIZONTAL_PADDING,
					HEADER_TOP_PADDING, HORIZONTAL_PADDING, 0)));
			setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(this, HORIZONTAL_SPACING,
					new GLPadding(HORIZONTAL_PADDING, HEADER_TOP_PADDING, HORIZONTAL_PADDING, 0)));

			GLElementContainer captionContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true,
					HORIZONTAL_SPACING, GLPadding.ZERO));
			captionContainer.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(
					captionContainer, HORIZONTAL_SPACING, GLPadding.ZERO));
			captionContainer.setSize(Float.NaN, CAPTION_HEIGHT);

			if (headerParent != headerRow) {
				GLButton collapseButton = new GLButton(EButtonMode.CHECKBOX);
				collapseButton.setSelected(false);
				collapseButton.setRenderer(GLRenderers.fillImage("resources/icons/general/minus.png"));
				collapseButton.setSelectedRenderer(GLRenderers.fillImage("resources/icons/general/plus.png"));
				collapseButton.setSize(CAPTION_HEIGHT, CAPTION_HEIGHT);

				captionContainer.add(collapseButton);
			}

			this.column = column;
			headerItem = createTextElement(caption, CAPTION_HEIGHT);

			captionContainer.add(headerItem);

			GLElementContainer spacingContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(false, 0,
					new GLPadding(0, 0, 0, 4)));
			spacingContainer.setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(spacingContainer,
					0, new GLPadding(0, 0, 0, 4)));

			GLElement spacing = new GLElement();
			spacing.setSize(0, Float.NaN);
			spacingContainer.add(spacing);
			spacingContainer.add(captionContainer);
			// spacingContainer.setRenderer(GLRenderers.drawRect(Color.GREEN));

			add(spacingContainer);
			setRenderer(GLRenderers.drawRect(Color.GRAY));

			headerParent.add(this);
		}

		public void updateSize() {
			float width = column.columnWidth;
			width += column.calcNestingWidth();
			setSize(width, Float.NaN);
		}

	}

	protected class NestableItem extends GLElementContainer {

		protected Map<Column, GLElementContainer> nestedContainers = new HashMap<>();
		protected NestableItem parentItem;
		protected GLElement item;
		protected Column column;

		public NestableItem(GLElement item, Column column, NestableItem parentItem) {
			setLayout(new GLSizeRestrictiveFlowLayout2(true, HORIZONTAL_SPACING, GLPadding.ZERO));
			setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(this, HORIZONTAL_SPACING,
					GLPadding.ZERO));
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

			GLElementContainer collapseContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true,
					HORIZONTAL_SPACING, new GLPadding(HORIZONTAL_PADDING, 0)));
			collapseContainer.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(
					collapseContainer, HORIZONTAL_SPACING, new GLPadding(HORIZONTAL_PADDING, 0)));

			GLButton collapseButton = new GLButton(EButtonMode.CHECKBOX);
			collapseButton.setSelected(false);
			collapseButton.setRenderer(GLRenderers.fillImage("resources/icons/general/minus.png"));
			collapseButton.setSelectedRenderer(GLRenderers.fillImage("resources/icons/general/plus.png"));
			collapseButton.setSize(COLLAPSE_BUTTON_SIZE, COLLAPSE_BUTTON_SIZE);
			collapseContainer.add(collapseButton);
			// collapseContainer.setRenderer(GLRenderers.drawRect(Color.CYAN));

			GLElementContainer nestedList = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(false,
					VERTICAL_SPACING, GLPadding.ZERO));
			nestedList.setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(nestedList,
					VERTICAL_SPACING, GLPadding.ZERO));
			// nestedList.setRenderer(GLRenderers.drawRect(Color.YELLOW));
			nestedContainers.put(column, nestedList);

			collapseContainer.add(nestedList);
			add(collapseContainer);

			return nestedList;
		}

		public void updateSize() {
			float width = item.getSize().x();
			width += column.calcNestingWidth();
			setSize(width, getMinSize().y());
			// ((GLElementContainer) getParent()).setSize(width, Float.NaN);
			((GLElementContainer) getParent().getParent()).setSize(width + 2 * HORIZONTAL_PADDING
					+ (column.parent != null ? HORIZONTAL_SPACING + COLLAPSE_BUTTON_SIZE : 0),
					Float.NaN);
		}

	}

	public INestableColumn() {
		setLayout(new GLSizeRestrictiveFlowLayout2(false, VERTICAL_SPACING, GLPadding.ZERO));
		setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(this, VERTICAL_SPACING, GLPadding.ZERO));

		headerRow = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, HORIZONTAL_SPACING, GLPadding.ZERO));
		headerRow.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(headerRow,
				HORIZONTAL_SPACING, GLPadding.ZERO));
		add(headerRow);

		bodyRow = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, HORIZONTAL_SPACING, GLPadding.ZERO));
		bodyRow.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(bodyRow, HORIZONTAL_SPACING,
				GLPadding.ZERO));
		add(bodyRow);

		Column root1 = addRootColumn("Root1");
		root1.setColumnWidth(root1.calcMinColumnWidth());
		// root1.setColumnWidth(300);
		NestableItem ri1 = addElement(createTextElement("root1 item 1", 16), root1, null);
		NestableItem ri2 = addElement(createTextElement("root1 item 2", 16), root1, null);
		NestableItem ri3 = addElement(createTextElement("root1 item 3", 16), root1, null);

		Column nested1 = addNestedColumn("Nested1", root1);
		nested1.setColumnWidth(nested1.calcMinColumnWidth());
		NestableItem ni11 = addElement(createTextElement("nested1 item 1_1", 16), nested1, ri1);
		NestableItem ni12 = addElement(createTextElement("nested1 item 1_2", 16), nested1, ri1);
		NestableItem ni13 = addElement(createTextElement("nested1 item 1_3", 16), nested1, ri1);

		NestableItem ni21 = addElement(createTextElement("nested1 item 2_1", 16), nested1, ri2);
		NestableItem ni22 = addElement(createTextElement("nested1 item 2_2", 16), nested1, ri2);
		NestableItem ni23 = addElement(createTextElement("nested1 item 2_3", 16), nested1, ri2);

		NestableItem ni31 = addElement(createTextElement("nested1 item 3_1", 16), nested1, ri3);
		NestableItem ni32 = addElement(createTextElement("nested1 item 3_2", 16), nested1, ri3);

		Column nested2 = addNestedColumn("Nested2", root1);
		nested2.setColumnWidth(nested2.calcMinColumnWidth());
		// nested2.setColumnWidth(500);
		NestableItem ni211 = addElement(createTextElement("nested2 item 1_1", 16), nested2, ri1);
		NestableItem ni212 = addElement(createTextElement("nested2 item 1_2", 16), nested2, ri1);
		NestableItem ni213 = addElement(createTextElement("nested2 item 1_3", 16), nested2, ri1);
		NestableItem ni214 = addElement(createTextElement("nested2 item 1_4", 16), nested2, ri1);
		Column nested3 = addNestedColumn("Nested3", nested1);
		nested3.setColumnWidth(nested3.calcMinColumnWidth());
		// nested3.setColumnWidth(800);
		NestableItem nni311 = addElement(createTextElement("nested3 item 1_1", 16), nested3, ni11);
		NestableItem nni312 = addElement(createTextElement("nested3 item 1_2", 16), nested3, ni11);
		NestableItem nni323 = addElement(createTextElement("nested3 item 2_1", 16), nested3, ni12);
		NestableItem nni324 = addElement(createTextElement("nested3 item 2_2", 16), nested3, ni12);

		updateSizes();
	}

	public void updateSizes() {
		for (Column rootColumn : rootColumns) {
			rootColumn.updateSizeRec();
		}

		headerRow.setSize(Float.NaN, headerRow.getMinSize().y());
	}

	public Column addRootColumn(String caption) {
		Column column = new Column();
		column.header = new ColumnHeader(column, caption, headerRow);
		rootColumns.add(column);
		addRootContainer(column);
		return column;
	}

	private void addRootContainer(Column column) {
		GLElementContainer nestedList = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(false,
				VERTICAL_SPACING, new GLPadding(HORIZONTAL_PADDING, 0)));
		nestedList.setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(nestedList,
				VERTICAL_SPACING, new GLPadding(HORIZONTAL_PADDING, 0)));
		// nestedList.setRenderer(GLRenderers.drawRect(Color.RED));
		rootContainers.put(column, nestedList);
		bodyRow.add(nestedList);
	}

	// private GLElementContainer createHeader(String caption, GLElementContainer headerParent) {
	// GLElementContainer header = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 4, new GLPadding(4,
	// 10, 4, 0)));
	// header.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(header, 4, new GLPadding(4,
	// 10, 4, 0)));
	//
	// GLElementContainer captionContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 4,
	// GLPadding.ZERO));
	// captionContainer.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(captionContainer, 4,
	// GLPadding.ZERO));
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
	// captionContainer.add(createTextElement(caption, 20));
	//
	// GLElementContainer spacingContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(false, 0,
	// new GLPadding(0, 0, 0, 4)));
	// spacingContainer.setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(spacingContainer, 0,
	// new GLPadding(0, 0, 0, 4)));
	//
	// GLElement spacing = new GLElement();
	// spacingContainer.add(spacing);
	// spacingContainer.add(captionContainer);
	//
	// header.add(spacingContainer);
	// header.setRenderer(GLRenderers.drawRect(Color.GRAY));
	//
	// headerParent.add(header);
	// return header;
	// }

	private GLElement createTextElement(String text, float height) {
		GLElement captionElement = new GLElement(GLRenderers.drawText(text, VAlign.LEFT));
		// GLElement captionElement = new GLElement(GLRenderers.drawRect(Color.RED));
		captionElement.setSize(Float.NaN, height);
		captionElement.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(200, height));
		return captionElement;
	}

	public Column addNestedColumn(String caption, Column parent) {
		Column column = new Column();
		column.header = new ColumnHeader(column, caption, parent.header);
		parent.children.add(column);
		column.parent = parent;

		for (NestableItem item : parent.items) {
			item.addNestedContainer(column);
		}
		return column;
	}

	public NestableItem addElement(GLElement element, Column column, NestableItem parentItem) {
		NestableItem item = new NestableItem(element, column, parentItem);
		if (parentItem == null) {
			rootContainers.get(column).add(item);
		} else {
			parentItem.getNestedContainer(column).add(item);
			// parentItem.updateSize();
		}
		relayout();
		return item;
	}
}
