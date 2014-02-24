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
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;

import com.google.common.collect.Lists;

/**
 * @author Christian
 *
 */
public class CollapsableItemContainer extends ItemContainer implements ISelectionCallback {

	protected final ColumnTree columnTree;
	protected final AnimatedGLElementContainer itemContainer;
	protected final GLButton collapseButton;
	protected final List<NestableItem> items = new ArrayList<>();
	protected final NestableItem summaryItem;
	protected final NestableColumn column;
	protected final NestableItem parentItem;

	public CollapsableItemContainer(NestableColumn column, NestableItem parentItem, ColumnTree columnTree) {
		this.column = column;
		this.parentItem = parentItem;
		this.columnTree = columnTree;

		setLayout(new GLSizeRestrictiveFlowLayout2(true, ColumnTreeRenderStyle.HORIZONTAL_SPACING, new GLPadding(
				ColumnTreeRenderStyle.HORIZONTAL_PADDING, 0)));
		setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(this,
				ColumnTreeRenderStyle.HORIZONTAL_SPACING, new GLPadding(ColumnTreeRenderStyle.HORIZONTAL_PADDING, 0)));

		collapseButton = new GLButton(EButtonMode.CHECKBOX);
		collapseButton.setSelected(false);
		collapseButton.setRenderer(GLRenderers.fillImage("resources/icons/general/minus.png"));
		collapseButton.setSelectedRenderer(GLRenderers.fillImage("resources/icons/general/plus.png"));
		collapseButton.setSize(ColumnTreeRenderStyle.COLLAPSE_BUTTON_SIZE, ColumnTreeRenderStyle.COLLAPSE_BUTTON_SIZE);
		collapseButton.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(
				ColumnTreeRenderStyle.COLLAPSE_BUTTON_SIZE, ColumnTreeRenderStyle.COLLAPSE_BUTTON_SIZE));
		column.itemContainers.add(this);
		// add(collapseButton);
		setRenderer(GLRenderers.drawRect(Color.CYAN));
		if (parentItem != null)
			add(collapseButton);

		itemContainer = new AnimatedGLElementContainer(new GLSizeRestrictiveFlowLayout2(false,
				ColumnTreeRenderStyle.VERTICAL_SPACING, GLPadding.ZERO));
		add(itemContainer);

		itemContainer.setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(itemContainer,
				ColumnTreeRenderStyle.VERTICAL_SPACING, GLPadding.ZERO));
		// nestedList.setRenderer(GLRenderers.drawRect(Color.YELLOW));

		summaryItem = new NestableItem(ColumnTree.createTextElement("Default Summary", 16), column, parentItem,
				columnTree);
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
		Map<NestableColumn, Set<NestableItem>> childColumnItems = new HashMap<>();
		for (NestableItem item : items) {
			// if (item != summaryItem) {
			// items.add((NestableItem) item);
			for (NestableColumn col : column.children) {
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
		for (NestableColumn col : column.children) {
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
		itemContainer.clear(0);
		if (selected) {
			// setItemVisibility(EVisibility.NONE);

			itemContainer.add(summaryItem);

			for (NestableColumn col : column.children) {
				// NestableItem childSummaryItem = new
				// NestableItem(col.getSummaryElement(childColumnItems.get(col)),
				// col,
				// summaryItem);
				// summaryItem.addItem(childSummaryItem, col);

				CollapsableItemContainer container = summaryItem.getNestedContainer(col);
				container.onSelectionChanged(container.collapseButton, container.collapseButton.isSelected());

				// col.summaryItems.add(childSummaryItem);
			}

			// summaryItem.element.setSize(column.getItemWidth(), summaryItem.element.getMinSize().y());
			// summaryItem.updateSize();

			// summaryItem.setVisibility(EVisibility.PICKABLE);
		} else {
			for (NestableItem item : items) {
				itemContainer.add(item);
				for (NestableColumn col : column.children) {
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

		columnTree.relayout();
	}

	// private void setItemVisibility(EVisibility visibility) {
	// for (GLElement element : itemContainer) {
	// element.setVisibility(visibility);
	// }
	// }
}
