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

/**
 * @author Christian
 *
 */
public class NestableColumn {
	protected IColumnModel columnModel;

	protected ColumnHeader header;
	protected NestableColumn parent;
	protected List<NestableColumn> children = new ArrayList<>();

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

		header.headerItem.setSize(headerItemWidth, ColumnTreeRenderStyle.CAPTION_HEIGHT);
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
	 * @return the columnModel, see {@link #columnModel}
	 */
	public IColumnModel getColumnModel() {
		return columnModel;
	}

	/**
	 * @return the children, see {@link #children}
	 */
	public List<NestableColumn> getChildren() {
		return children;
	}

	public GLElement getSummaryElement(Set<NestableItem> items) {
		return ColumnTree.createTextElement("Summary of " + items.size(), 16);
	}

	public boolean isRoot() {
		return parent == null;
	}
}
