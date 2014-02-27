/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.list;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;

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
		// setRenderer(GLRenderers.drawRect(Color.CYAN));
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
		summaryItem.setElementData(new HashSet<>());
		// column.summaryItems.add(summaryItem);

		collapseButton.setVisibility(EVisibility.HIDDEN);

		collapseButton.setCallback(this);
		collapseButton.onPick(new IPickingListener() {

			@Override
			public void pick(Pick pick) {
				if (pick.getPickingMode() == PickingMode.CLICKED) {

				}
			}
		});
	}

	public boolean isCollapsed() {
		return collapseButton.isSelected();
	}

	public void setCollapsed(boolean isCollapsed, boolean updateMappings) {
		if (isCollapsed == isCollapsed())
			return;
		collapseButton.setSelectedSilent(isCollapsed);
		updateCollapseState(updateMappings);
	}

	protected void updateCollapseState(boolean updateMappings) {
		itemContainer.clear(0);
		if (isCollapsed()) {

			itemContainer.add(summaryItem);

			for (NestableColumn col : column.children) {

				CollapsableItemContainer container = summaryItem.getNestedContainer(col);
				container.updateCollapseState(updateMappings);
			}

		} else {
			for (NestableItem item : items) {
				itemContainer.add(item);
				for (NestableColumn col : column.children) {
					CollapsableItemContainer container = item.getNestedContainer(col);
					container.updateCollapseState(updateMappings);
				}
			}
		}

		columnTree.relayout();

		if (updateMappings) {
			for (NestableColumn col : column.children) {
				col.getColumnModel().updateMappings();
			}
		}

		sortItems(column.getComparator());
	}

	public boolean isVisible() {
		return findParent(ColumnTree.class) != null;
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
				for (NestableItem i : item.getNestedContainer(col).getItems()) {
					if (!containsItemWithID(i, childItems))
						childItems.add(i);
				}
			}
			// }
		}
		updateSummaryData(summaryItem, items);
		summaryItem.setElement(column.getSummaryElement(new HashSet<>(items)));
		for (NestableColumn col : column.children) {
			// NestableItem childSummaryItem = new NestableItem(col.getSummaryElement(childColumnItems.get(col)),
			// col,
			// summaryItem);
			// summaryItem.addItem(childSummaryItem, col);
			Set<NestableItem> i = childColumnItems.get(col);
			if (i != null) {
				CollapsableItemContainer container = summaryItem.getNestedContainer(col);

				updateSummaryData(container.summaryItem, i);
				container.summaryItem.setElement(col.getSummaryElement(i));
				container.collapseButton.setSelected(true);
				container.items.clear();
				container.items.addAll(i);
				container.updateButtonVisibility();
			}
			// col.summaryItems.add(childSummaryItem);
		}

	}

	protected void updateSummaryData(NestableItem summaryItem, Collection<NestableItem> items) {
		Set<Object> summaryData = summaryItem.getElementData();
		summaryData.clear();
		for (NestableItem item : items) {
			Set<Object> d = item.getElementData();
			if (d != null) {
				summaryData.addAll(d);
			}
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
		// if (containsItemWithID(item, items))
		// return;
		items.add(item);
		if (!isCollapsed())
			itemContainer.add(item);
		updateButtonVisibility();
	}

	protected boolean containsItemWithID(NestableItem item, Collection<NestableItem> items) {
		for (NestableItem i : items) {
			if (i.getElementData().containsAll(item.getElementData()))
				return true;
		}
		return false;
	}

	protected void updateButtonVisibility() {
		if (items.isEmpty() || parentItem == null)
			collapseButton.setVisibility(EVisibility.HIDDEN);
		else
			collapseButton.setVisibility(EVisibility.PICKABLE);
	}

	@Override
	public void onSelectionChanged(GLButton button, boolean selected) {
		updateCollapseState(true);
	}

	public void sortItems(Comparator<GLElement> comparator) {
		itemContainer.sortBy(comparator);
	}


	// private void setItemVisibility(EVisibility visibility) {
	// for (GLElement element : itemContainer) {
	// element.setVisibility(visibility);
	// }
	// }
}
