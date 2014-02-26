/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.list;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.view.relationshipexplorer.ui.util.AnimationUtil;
import org.caleydo.view.relationshipexplorer.ui.util.MultiSelectionUtil.IMultiSelectionHandler;

/**
 * @author Christian
 *
 */
public class NestableColumn implements IMultiSelectionHandler<NestableItem> {
	protected final IColumnModel model;
	protected final ColumnTree columnTree;

	protected ColumnHeader header;
	protected NestableColumn parent;
	protected List<NestableColumn> children = new ArrayList<>();

	// protected Set<NestableItem> items = new HashSet<>();
	protected Set<ItemContainer> itemContainers = new HashSet<>();
	// protected Set<NestableItem> summaryItems = new HashSet<>();
	protected float columnWidth = 0;
	protected float relColumnWidth = 0;

	protected Set<NestableItem> selectedItems = new HashSet<>();
	protected Set<NestableItem> highlightedItems = new HashSet<>();

	/**
	 *
	 */
	public NestableColumn(IColumnModel model, NestableColumn parent, ColumnTree columnTree) {
		this.model = model;
		this.parent = parent;
		this.columnTree = columnTree;
	}

	public float calcMinColumnWidth() {

		float maxItemWidth = Float.MIN_VALUE;
		for (ItemContainer container : itemContainers) {
			for (NestableItem item : container.getCurrentItems()) {
				float itemWidth = item.getElement().getMinSize().x();
				if (itemWidth > maxItemWidth)
					maxItemWidth = itemWidth;
			}
		}
		float minColumnWidth = 0;
		if (parent == null) {
			minColumnWidth = 2 * ColumnTreeRenderStyle.HORIZONTAL_PADDING
					+ Math.max(maxItemWidth, header.headerItem.getMinSize().x());

		} else {
			minColumnWidth = 2
					* ColumnTreeRenderStyle.HORIZONTAL_PADDING
					+ Math.max(maxItemWidth + ColumnTreeRenderStyle.COLLAPSE_BUTTON_SIZE
							+ ColumnTreeRenderStyle.HORIZONTAL_SPACING, header.headerItem.getMinSize().x()
							+ ColumnTreeRenderStyle.CAPTION_HEIGHT + ColumnTreeRenderStyle.HORIZONTAL_SPACING);
		}
		return minColumnWidth;
	}

	public void setColumnWidth(float width) {
		this.columnWidth = width;
		// updateSizes();
	}

	public void updateSizeRec() {
		for (NestableColumn child : children) {
			child.updateSizeRec();
		}

		updateSizes();
	}

	public void updateSizes() {
		float headerItemWidth = 0;
		float itemWidth = getItemWidth();

		if (parent == null) {
			headerItemWidth = columnWidth - 2 * ColumnTreeRenderStyle.HORIZONTAL_PADDING;
		} else {
			headerItemWidth = columnWidth - 2 * ColumnTreeRenderStyle.HORIZONTAL_PADDING
					- ColumnTreeRenderStyle.CAPTION_HEIGHT - ColumnTreeRenderStyle.HORIZONTAL_SPACING;
		}

		AnimationUtil.resizeElement(header.headerItem, headerItemWidth, ColumnTreeRenderStyle.CAPTION_HEIGHT);
		// header.headerItem.setSize(headerItemWidth, ColumnTreeRenderStyle.CAPTION_HEIGHT);
		header.updateSize();

		for (ItemContainer container : itemContainers) {
			for (NestableItem item : container.getCurrentItems()) {
				// item.getElement().setSize(itemWidth, item.getElement().getMinSize().y());
				item.updateSize(itemWidth, item.getElement().getMinSize().y());
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

	public void setCollapsed(boolean isCollapsed) {
		if (isRoot())
			return;
		header.collapseButton.setSelected(isCollapsed);
	}

	protected void collapseAll(boolean isCollapsed) {
		for (ItemContainer container : itemContainers) {
			CollapsableItemContainer c = (CollapsableItemContainer) container;
			if (c.isVisible()) {
				c.setCollapsed(isCollapsed, false);
			}
		}

		for (NestableColumn col : children) {
			col.getColumnModel().updateMappings();
		}
	}

	public Set<NestableItem> getVisibleItems() {
		Set<NestableItem> items = new HashSet<>();
		if (isRoot()) {
			for (ItemContainer container : itemContainers) {
				for (NestableItem item : container.getCurrentItems()) {
					items.add(item);
				}
			}
		} else {
			for (ItemContainer container : itemContainers) {
				CollapsableItemContainer c = (CollapsableItemContainer) container;
				if (c.isVisible()) {
					for (NestableItem item : c.getCurrentItems()) {
						items.add(item);
					}
				}
			}
		}
		return items;

	}

	public float getItemWidth() {
		if (parent == null) {
			return columnWidth - 2 * ColumnTreeRenderStyle.HORIZONTAL_PADDING;
		} else {
			return columnWidth - 2 * ColumnTreeRenderStyle.HORIZONTAL_PADDING
					- ColumnTreeRenderStyle.COLLAPSE_BUTTON_SIZE - ColumnTreeRenderStyle.HORIZONTAL_SPACING;
		}
	}

	protected float calcNestingWidth() {
		float width = 0;
		for (NestableColumn col : children) {
			width += col.calcNestingWidth();
			width += col.columnWidth;
		}
		if (children.size() >= 1)
			width += children.size() * ColumnTreeRenderStyle.HORIZONTAL_SPACING;

		return width;
	}

	public void updateSummaryItems() {
		model.updateMappings();
		for (ItemContainer container : itemContainers) {
			container.updateSummaryItems();
			// for (NestableItem item : container.getItems()) {
			// item.updateSummaryItems();
			//
			// }
		}

		for (NestableColumn child : children) {
			child.updateSummaryItems();
		}
	}

	/**
	 * @return the parent, see {@link #parent}
	 */
	public NestableColumn getParent() {
		return parent;
	}

	/**
	 * @return the columnModel, see {@link #model}
	 */
	public IColumnModel getColumnModel() {
		return model;
	}

	/**
	 * @return the children, see {@link #children}
	 */
	public List<NestableColumn> getChildren() {
		return children;
	}

	public GLElement getSummaryElement(Set<NestableItem> items) {
		return model.getSummaryElement(items);
		// return ColumnTree.createTextElement("summary of " + items.size(), 16);
	}

	public boolean isRoot() {
		return parent == null;
	}

	public NestableItem addElement(GLElement element, NestableItem parentItem) {
		return columnTree.addElement(element, this, parentItem);
	}

	public void notifyOfSelectionUpdate() {

	}

	public void notifyOfHighlightUpdate() {

	}

	@Override
	public boolean isSelected(NestableItem item) {
		return selectedItems.contains(item);
	}

	@Override
	public void removeFromSelection(NestableItem item) {
		selectedItems.remove(item);
		item.setSelected(false);
	}

	@Override
	public void addToSelection(NestableItem item) {
		selectedItems.add(item);
		item.setSelected(true);
	}

	@Override
	public void setSelection(NestableItem item) {
		clearSelection();
		addToSelection(item);
	}

	public void addToHighlight(NestableItem item) {
		highlightedItems.add(item);
		item.setHighlight(true);
	}

	@Override
	public boolean isHighlight(NestableItem item) {
		return highlightedItems.contains(item);
	}

	@Override
	public void setHighlight(NestableItem item) {
		clearHighlight();
		addToHighlight(item);
	}

	@Override
	public void removeHighlight(NestableItem item) {
		highlightedItems.remove(item);
		item.setHighlight(false);
	}

	public void clearSelection() {
		for (NestableItem item : selectedItems) {
			item.setSelected(false);
		}
		selectedItems.clear();
	}

	public void clearHighlight() {
		for (NestableItem item : highlightedItems) {
			item.setHighlight(false);
		}
		highlightedItems.clear();
	}
}
