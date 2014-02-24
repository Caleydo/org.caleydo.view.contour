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

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.basic.ScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;

import com.google.common.collect.Lists;

/**
 * @author Christian
 *
 */
public class ColumnTree extends GLElementContainer {

	protected static final int CAPTION_HEIGHT = 20;
	protected static final int COLLAPSE_BUTTON_SIZE = 16;
	protected static final int HEADER_TOP_PADDING = 10;
	protected static final int HORIZONTAL_PADDING = 4;
	protected static final int HORIZONTAL_SPACING = 4;
	protected static final int VERTICAL_SPACING = 2;

	protected Column rootColumn;
	protected ItemContainer rootContainer;

	protected GLElementContainer headerRow;
	// protected GLElementContainer bodyRow;
	protected ScrollingDecorator scrollingDecorator;

	protected Set<Column> allColumns = new HashSet<>();

	protected class Column {
		protected ColumnHeader header;
		protected Column parent;
		protected List<Column> children = new ArrayList<>();

		// protected Set<NestableItem> items = new HashSet<>();
		protected Set<ItemContainer> itemContainers = new HashSet<>();
		// protected Set<NestableItem> summaryItems = new HashSet<>();
		protected float columnWidth = 0;
		protected float relColumnWidth = 0;

		public float calcMinColumnWidth() {

			float maxItemWidth = Float.MIN_VALUE;
			for (ItemContainer container : itemContainers) {
				for (NestableItem item : container.getCurrentItems()) {
					float itemWidth = item.element.getMinSize().x();
					if (itemWidth > maxItemWidth)
						maxItemWidth = itemWidth;
				}
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
			updateSizesRel();
			// updateSizes();
		}

		public void updateSizes() {
			float headerItemWidth = 0;
			float itemWidth = getItemWidth();

			if (parent == null) {
				headerItemWidth = columnWidth - 2 * HORIZONTAL_PADDING;
			} else {
				headerItemWidth = columnWidth - 2 * HORIZONTAL_PADDING - CAPTION_HEIGHT - HORIZONTAL_SPACING;
			}

			header.headerItem.setSize(headerItemWidth, CAPTION_HEIGHT);
			header.updateSize();

			for (ItemContainer container : itemContainers) {
				for (NestableItem item : container.getCurrentItems()) {
					item.element.setSize(itemWidth, item.element.getMinSize().y());
					item.updateSize();
				}
			}
			// for (NestableItem item : items) {
			// item.element.setSize(itemWidth, item.element.getMinSize().y());
			// item.updateSize();
			// }
			// for (NestableItem item : summaryItems) {
			// item.element.setSize(itemWidth, item.element.getMinSize().y());
			// item.updateSize();
			// }

			// parent.updateSizes();
		}

		public void updateSizesRel() {
			float headerItemWidth = 0;
			float itemWidth = getItemWidth();

			if (parent == null) {
				headerItemWidth = columnWidth - 2 * HORIZONTAL_PADDING;
			} else {
				headerItemWidth = columnWidth - 2 * HORIZONTAL_PADDING - CAPTION_HEIGHT - HORIZONTAL_SPACING;
			}

			header.headerItem.setSize(headerItemWidth, CAPTION_HEIGHT);
			header.updateSize();

			for (ItemContainer container : itemContainers) {
				for (NestableItem item : container.getCurrentItems()) {
					item.element.setSize(itemWidth, item.element.getMinSize().y());
					item.updateSize();
				}
			}
			// for (NestableItem item : items) {
			// item.element.setSize(itemWidth, item.element.getMinSize().y());
			// item.updateSize();
			// }
			// for (NestableItem item : summaryItems) {
			// item.element.setSize(itemWidth, item.element.getMinSize().y());
			// item.updateSize();
			// }

			// parent.updateSizes();
		}

		public float getItemWidth() {
			if (parent == null) {
				return columnWidth - 2 * HORIZONTAL_PADDING;
			} else {
				return columnWidth - 2 * HORIZONTAL_PADDING - COLLAPSE_BUTTON_SIZE - HORIZONTAL_SPACING;
			}
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

		public void updateSummaryItems() {
			for (ItemContainer container : itemContainers) {
				container.updateSummaryItems();
				// for (NestableItem item : container.getItems()) {
				// item.updateSummaryItems();
				//
				// }
			}

			for (Column child : children) {
				child.updateSummaryItems();
			}
		}

		public GLElement getSummaryElement(Set<NestableItem> items) {
			return createTextElement("Summary of " + items.size(), 16);
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

	public class ItemContainer extends GLElementContainer {
		public List<NestableItem> getItems() {
			return getCurrentItems();
		}

		public List<NestableItem> getCurrentItems() {
			List<NestableItem> items = new ArrayList<>(size());
			for (GLElement item : this) {
				items.add((NestableItem) item);
			}
			return items;
		}

		public void updateSummaryItems() {

		}
	}

	protected class CollapsableItemContainer extends ItemContainer implements ISelectionCallback {
		protected GLElementContainer itemContainer;
		protected GLButton collapseButton;
		protected List<NestableItem> items = new ArrayList<>();
		protected NestableItem summaryItem;
		protected Column column;
		protected NestableItem parentItem;

		public CollapsableItemContainer(Column column, NestableItem parentItem) {
			this.column = column;
			this.parentItem = parentItem;

			setLayout(new GLSizeRestrictiveFlowLayout2(true, HORIZONTAL_SPACING, new GLPadding(HORIZONTAL_PADDING, 0)));
			setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(this, HORIZONTAL_SPACING,
					new GLPadding(HORIZONTAL_PADDING, 0)));

			collapseButton = new GLButton(EButtonMode.CHECKBOX);
			collapseButton.setSelected(false);
			collapseButton.setRenderer(GLRenderers.fillImage("resources/icons/general/minus.png"));
			collapseButton.setSelectedRenderer(GLRenderers.fillImage("resources/icons/general/plus.png"));
			collapseButton.setSize(COLLAPSE_BUTTON_SIZE, COLLAPSE_BUTTON_SIZE);
			collapseButton.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(COLLAPSE_BUTTON_SIZE,
					COLLAPSE_BUTTON_SIZE));
			column.itemContainers.add(this);
			// add(collapseButton);
			setRenderer(GLRenderers.drawRect(Color.CYAN));
			if (parentItem != null)
				add(collapseButton);

			itemContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(false, VERTICAL_SPACING,
					GLPadding.ZERO));
			add(itemContainer);

			itemContainer.setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(itemContainer,
					VERTICAL_SPACING, GLPadding.ZERO));
			// nestedList.setRenderer(GLRenderers.drawRect(Color.YELLOW));

			summaryItem = new NestableItem(createTextElement("Default Summary", 16), column, parentItem);
			// column.summaryItems.add(summaryItem);

			collapseButton.setVisibility(EVisibility.HIDDEN);

			collapseButton.setCallback(this);
		}

		public boolean isCollapsed() {
			return collapseButton.isSelected();
		}

		@Override
		public void updateSummaryItems() {
			// Set<NestableItem> items = new HashSet<>(itemContainer.size());
			Map<Column, Set<NestableItem>> childColumnItems = new HashMap<>();
			for (NestableItem item : items) {
				// if (item != summaryItem) {
				// items.add((NestableItem) item);
				for (Column col : column.children) {
					Set<NestableItem> childItems = childColumnItems.get(col);
					if (childItems == null) {
						childItems = new HashSet<>();
						childColumnItems.put(col, childItems);
					}
					childItems.addAll(item.getNestedContainer(col).getItems());
				}
				// }
			}
			summaryItem.setElement(column.getSummaryElement(new HashSet<>(items)));
			for (Column col : column.children) {
				// NestableItem childSummaryItem = new NestableItem(col.getSummaryElement(childColumnItems.get(col)),
				// col,
				// summaryItem);
				// summaryItem.addItem(childSummaryItem, col);
				Set<NestableItem> i = childColumnItems.get(col);
				if (i != null) {
					CollapsableItemContainer container = summaryItem.getNestedContainer(col);
					container.summaryItem.setElement(col.getSummaryElement(i));
					container.collapseButton.setSelected(true);
					container.items.clear();
					container.items.addAll(i);
					container.updateButtonVisibility();
				}
				// col.summaryItems.add(childSummaryItem);
			}

		}

		@Override
		public List<NestableItem> getItems() {
			return items;
		}

		@Override
		public List<NestableItem> getCurrentItems() {
			// List<NestableItem> items = new ArrayList<>(itemContainer.size());
			// for (GLElement item : itemContainer) {
			// items.add((NestableItem) item);
			// }
			return isCollapsed() ? Lists.<NestableItem> newArrayList(summaryItem) : getItems();
		}

		public void addItem(NestableItem item) {
			items.add(item);
			if (!isCollapsed())
				itemContainer.add(item);
			updateButtonVisibility();
		}

		protected void updateButtonVisibility() {
			if (items.isEmpty() || parentItem == null)
				collapseButton.setVisibility(EVisibility.HIDDEN);
			else
				collapseButton.setVisibility(EVisibility.PICKABLE);
		}

		@Override
		public void onSelectionChanged(GLButton button, boolean selected) {
			itemContainer.clear();
			if (selected) {
				// setItemVisibility(EVisibility.NONE);

				itemContainer.add(summaryItem);

				for (Column col : column.children) {
					// NestableItem childSummaryItem = new
					// NestableItem(col.getSummaryElement(childColumnItems.get(col)),
					// col,
					// summaryItem);
					// summaryItem.addItem(childSummaryItem, col);

					CollapsableItemContainer container = summaryItem.getNestedContainer(col);
					container.onSelectionChanged(container.collapseButton, collapseButton.isSelected());

					// col.summaryItems.add(childSummaryItem);
				}

				// summaryItem.element.setSize(column.getItemWidth(), summaryItem.element.getMinSize().y());
				// summaryItem.updateSize();

				// summaryItem.setVisibility(EVisibility.PICKABLE);
			} else {
				for (NestableItem item : items) {
					itemContainer.add(item);
					for (Column col : column.children) {
						// NestableItem childSummaryItem = new
						// NestableItem(col.getSummaryElement(childColumnItems.get(col)),
						// col,
						// summaryItem);
						// summaryItem.addItem(childSummaryItem, col);

						CollapsableItemContainer container = item.getNestedContainer(col);
						container.onSelectionChanged(container.collapseButton, container.collapseButton.isSelected());

						// col.summaryItems.add(childSummaryItem);
					}
				}
				// setItemVisibility(EVisibility.PICKABLE);
				// summaryItem.setVisibility(EVisibility.NONE);
			}

			updateSizes();
		}

		// private void setItemVisibility(EVisibility visibility) {
		// for (GLElement element : itemContainer) {
		// element.setVisibility(visibility);
		// }
		// }
	}

	protected class NestableItem extends GLElementContainer {

		protected Map<Column, CollapsableItemContainer> itemContainers = new HashMap<>();
		protected NestableItem parentItem;
		protected GLElement element;
		protected Column column;

		public NestableItem(GLElement item, Column column, NestableItem parentItem) {
			setLayout(new GLSizeRestrictiveFlowLayout2(true, HORIZONTAL_SPACING, GLPadding.ZERO));
			setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(this, HORIZONTAL_SPACING,
					GLPadding.ZERO));
			this.column = column;
			setRenderer(GLRenderers.drawRect(Color.BLUE));
			add(item);
			this.element = item;

			setSize(Float.NaN, getMinSize().y());
			// for (Column col : column.children) {
			// addNestedContainer(col);
			// }
			// updateSize();

		}

		/**
		 * @param item
		 *            setter, see {@link item}
		 */
		public void setElement(GLElement item) {
			remove(this.element);
			this.element = item;
			add(0, item);
		}

		public CollapsableItemContainer getNestedContainer(Column column) {
			CollapsableItemContainer c = itemContainers.get(column);
			if (c == null)
				c = addNestedContainer(column);
			return c;
		}

		public void addItem(NestableItem item, Column column) {
			CollapsableItemContainer c = getNestedContainer(column);
			c.addItem(item);
		}

		// public void setSummaryRenderer(GLElement summaryRenderer, Column column) {
		// GLElement existingRenderer = nestedSummaryRenderers.get(column);
		// GLElementContainer nestedContainer = getNestedContainer(column);
		// if (existingRenderer != null) {
		// nestedContainer.remove(existingRenderer);
		// }
		// nestedSummaryRenderers.put(column, summaryRenderer);
		// nestedContainer.add(summaryRenderer);
		// }

		private CollapsableItemContainer addNestedContainer(Column column) {

			CollapsableItemContainer container = new CollapsableItemContainer(column, this);
			itemContainers.put(column, container);

			add(container);
			return container;
		}

		public void updateSize() {
			if (getParent() == null)
				return;
			float width = element.getSize().x();
			width += column.calcNestingWidth();
			setSize(width, getMinSize().y());

			// if (column == rootColumn)
			// return;
			// ((GLElementContainer) getParent()).setSize(width, Float.NaN);
			((GLElement) getParent().getParent()).setSize(width + 2 * HORIZONTAL_PADDING
					+ (column.parent != null ? HORIZONTAL_SPACING + COLLAPSE_BUTTON_SIZE : 0), Float.NaN);
		}

		public void updateSummaryItems() {
			for (CollapsableItemContainer container : itemContainers.values()) {
				container.updateSummaryItems();
			}
		}

	}

	protected class ScrollableItemList extends ItemContainer {

		public ScrollableItemList() {
			setLayout(new GLSizeRestrictiveFlowLayout2(false, VERTICAL_SPACING, new GLPadding(HORIZONTAL_PADDING, 0)));
			setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(this, VERTICAL_SPACING,
					GLPadding.ZERO));
		}

		@Override
		public void layout(int deltaTimeMs) {
			super.layout(deltaTimeMs);

			Rect clippingArea = scrollingDecorator.getClipingArea();
			for (GLElement child : this) {
				Rect bounds = child.getRectBounds();
				if (child.getVisibility() != EVisibility.NONE) {
					if (clippingArea.asRectangle2D().intersects(bounds.asRectangle2D())) {
						if (child.getVisibility() != EVisibility.PICKABLE) {
							child.setVisibility(EVisibility.PICKABLE);
							repaintAll();
						}
					} else {
						if (child.getVisibility() != EVisibility.HIDDEN) {
							child.setVisibility(EVisibility.HIDDEN);
							repaintAll();
						}
					}
				}
			}
		}
	}

	public ColumnTree() {
		setLayout(new GLSizeRestrictiveFlowLayout2(false, VERTICAL_SPACING, GLPadding.ZERO));
		setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(this, VERTICAL_SPACING, GLPadding.ZERO));

		headerRow = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, HORIZONTAL_SPACING, GLPadding.ZERO));
		headerRow.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(headerRow,
				HORIZONTAL_SPACING, GLPadding.ZERO));
		headerRow.setRenderer(GLRenderers.drawRect(Color.RED));
		add(headerRow);

		// bodyRow = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, HORIZONTAL_SPACING, GLPadding.ZERO));
		// bodyRow.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(bodyRow,
		// HORIZONTAL_SPACING,
		// GLPadding.ZERO));
		// bodyRow.setRenderer(GLRenderers.drawRect(Color.RED));

		// add(bodyRow);
		Column root1 = addRootColumn("Root1");
		// root1.setColumnWidth(root1.calcMinColumnWidth());
		root1.setColumnWidth(100);
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
		// nested2.setColumnWidth(nested2.calcMinColumnWidth());
		nested2.setColumnWidth(100);
		NestableItem ni211 = addElement(createTextElement("nested2 item 1_1", 16), nested2, ri1);
		NestableItem ni212 = addElement(createTextElement("nested2 item 1_2", 16), nested2, ri1);
		NestableItem ni213 = addElement(createTextElement("nested2 item 1_3", 16), nested2, ri1);
		NestableItem ni214 = addElement(createTextElement("nested2 item 1_4", 16), nested2, ri1);
		Column nested3 = addNestedColumn("Nested3", nested1);
		// nested3.setColumnWidth(nested3.calcMinColumnWidth());
		nested3.setColumnWidth(100);
		NestableItem nni311 = addElement(createTextElement("nested3 item 1_1", 16), nested3, ni11);
		NestableItem nni312 = addElement(createTextElement("nested3 item 1_2", 16), nested3, ni11);
		NestableItem nni323 = addElement(createTextElement("nested3 item 2_1", 16), nested3, ni12);
		NestableItem nni324 = addElement(createTextElement("nested3 item 2_2", 16), nested3, ni12);

		updateSummaryItems();
		updateSizes();
	}

	public void updateSummaryItems() {
		rootColumn.updateSummaryItems();
	}

	@Override
	public void layout(int deltaTimeMs) {
		float totalWidth = 0;
		Map<Column, Float> minWidths = new HashMap<>();
		for (Column column : allColumns) {
			float minWidth = column.calcMinColumnWidth();
			totalWidth += minWidth;
			minWidths.put(column, minWidth);
		}
		for (Column column : allColumns) {
			column.setColumnWidth((minWidths.get(column) / totalWidth)
					* (getSize().x() - (allColumns.size() - 1) * HORIZONTAL_SPACING));
		}
		updateSizes();

		super.layout(deltaTimeMs);
	}

	public void updateSizes() {
		rootColumn.updateSizeRec();
		headerRow.setSize(Float.NaN, headerRow.getMinSize().y());
	}

	public Column addRootColumn(String caption) {
		rootColumn = new Column();
		rootColumn.header = new ColumnHeader(rootColumn, caption, headerRow);
		rootContainer = new ScrollableItemList();
		rootContainer.setRenderer(GLRenderers.drawRect(Color.GREEN));
		rootColumn.itemContainers.add(rootContainer);
		scrollingDecorator = new ScrollingDecorator(rootContainer, null, new ScrollBar(false), 8, EDimension.RECORD);
		scrollingDecorator.setMinSizeProvider(rootContainer);
		allColumns.add(rootColumn);

		add(scrollingDecorator);
		// bodyRow.add(scrollingDecorator);
		return rootColumn;
	}

	// private void addRootContainer(Column column) {
	// CollapsableItemContainer itemContainer = new CollapsableItemContainer(column, null);
	// // GLElementContainer nestedList = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(false,
	// // VERTICAL_SPACING, new GLPadding(HORIZONTAL_PADDING, 0)));
	// // nestedList.setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(nestedList,
	// // VERTICAL_SPACING, new GLPadding(HORIZONTAL_PADDING, 0)));
	// // nestedList.setRenderer(GLRenderers.drawRect(Color.RED));
	// rootContainers.put(column, itemContainer);
	// bodyRow.add(itemContainer);
	// }

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
		allColumns.add(column);
		column.parent = parent;
		// for (CollapsableItemContainer container : parent.itemContainers) {
		// for (NestableItem item : container.getItems()) {
		// item.addNestedContainer(column);
		// }
		// }
		return column;
	}

	public NestableItem addElement(GLElement element, Column column, NestableItem parentItem) {
		NestableItem item = new NestableItem(element, column, parentItem);
		// column.items.add(item);
		if (parentItem == null) {
			rootContainer.add(item);
		} else {
			parentItem.addItem(item, column);
			// parentItem.updateSize();
		}
		relayout();
		return item;
	}
}
