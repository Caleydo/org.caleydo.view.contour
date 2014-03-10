/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.list;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.view.relationshipexplorer.ui.column.ScoreElement;
import org.caleydo.view.relationshipexplorer.ui.util.AnimationUtil;
import org.caleydo.view.relationshipexplorer.ui.util.MultiSelectionUtil.IMultiSelectionHandler;

/**
 * @author Christian
 *
 */
public class NestableColumn implements IMultiSelectionHandler<NestableItem> {

	@DeepScan
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

	protected ContextMenuCreator contextMenuCreator = new ContextMenuCreator();

	protected Set<ISelectionUpdateListener> selectionListeners = new HashSet<>();

	public interface ISelectionUpdateListener {
		public void onSelectionChanged(NestableColumn column);

		public void onHighlightChanged(NestableColumn column);
	}

	private class ItemComparatorWrapper implements Comparator<GLElement> {

		private final Comparator<NestableItem> wrappee;

		public ItemComparatorWrapper(Comparator<NestableItem> wrappee) {
			this.wrappee = wrappee;
		}

		@Override
		public int compare(GLElement o1, GLElement o2) {
			if (wrappee == null) {
				System.out.println("wrappee null");
			}

			return wrappee.compare((NestableItem) o1, (NestableItem) o2);
		}
	}

	protected Comparator<GLElement> comparator;

	/**
	 *
	 */
	public NestableColumn(IColumnModel model, NestableColumn parent, ColumnTree columnTree) {
		this.model = model;
		this.parent = parent;
		this.columnTree = columnTree;
		addSelectionUpdateListener(model);
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

		AnimationUtil.resizeElement(header.headerItem, headerItemWidth, header.headerItem.getMinSize().y());
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

	public boolean isCollapsed() {
		return header.collapseButton.isSelected();
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
			col.updateSummaryItems(EUpdateCause.OTHER);
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

	public void updateSummaryItems(EUpdateCause cause) {
		model.updateMappings();
		for (ItemContainer container : itemContainers) {
			container.updateSummaryItems(cause);
			// for (NestableItem item : container.getItems()) {
			// item.updateSummaryItems();
			//
			// }
		}

		for (NestableColumn child : children) {
			child.updateSummaryItems(cause);
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

	public GLElement getSummaryElement(NestableItem parentItem, Set<NestableItem> items, NestableItem summaryItem,
			EUpdateCause cause) {
		return model.getSummaryElement(parentItem, items, summaryItem, cause);
		// return ColumnTree.createTextElement("summary of " + items.size(), 16);
	}

	public boolean isRoot() {
		return parent == null;
	}

	public NestableItem addElement(ScoreElement element, NestableItem parentItem) {
		return columnTree.addElement(element, this, parentItem);
	}

	public void removeItem(NestableItem item) {
		if (isRoot()) {
			for (ItemContainer container : itemContainers) {
				container.remove(item);
			}
		} else {
			for (ItemContainer container : itemContainers) {
				CollapsableItemContainer c = (CollapsableItemContainer) container;
				if (c.items.contains(item)) {
					if (item.getParent() == c.itemContainer)
						c.itemContainer.remove(item);
					c.items.remove(item);
					c.summaryItem.getElementData().removeAll(item.getElementData());
				}
			}
		}
		removeItemContainers(item);
		item.removed = true;
		columnTree.relayout();
	}

	protected void removeContainer(CollapsableItemContainer container) {
		itemContainers.remove(container);
		for (NestableItem item : container.items) {
			removeItemContainers(item);
			item.removed = true;
			// removeItem(item);
		}
		container.items.clear();
		removeItemContainers(container.summaryItem);
		container.summaryItem.removed = true;
	}

	protected void removeItemContainers(NestableItem item) {
		for (Entry<NestableColumn, CollapsableItemContainer> entry : item.itemContainers.entrySet()) {
			entry.getKey().removeContainer(entry.getValue());
		}
		item.itemContainers.clear();
	}

	public void notifyOfSelectionUpdate() {
		for (ISelectionUpdateListener listener : selectionListeners) {
			listener.onSelectionChanged(this);
		}
	}

	public void notifyOfHighlightUpdate() {
		for (ISelectionUpdateListener listener : selectionListeners) {
			listener.onHighlightChanged(this);
		}
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

	public void sortBy(Comparator<NestableItem> comparator) {
		this.comparator = new ItemComparatorWrapper(comparator);
		if (isRoot()) {
			for (ItemContainer container : itemContainers) {
				container.sortBy(this.comparator);
			}
		} else {
			for (ItemContainer container : itemContainers) {
				CollapsableItemContainer c = (CollapsableItemContainer) container;
				c.sortItems(this.comparator);
				// if (c.isVisible()) {
				// for (NestableItem item : c.getCurrentItems()) {
				// items.add(item);
				// }
				// }
			}
		}
	}

	protected Comparator<GLElement> getComparator() {
		return comparator;
	}

	public void addSelectionUpdateListener(ISelectionUpdateListener listener) {
		selectionListeners.add(listener);
	}

	public void removeSelectionUpdateListener(ISelectionUpdateListener listener) {
		selectionListeners.remove(listener);
	}

	/**
	 * @return the selectedItems, see {@link #selectedItems}
	 */
	public Set<NestableItem> getSelectedItems() {
		return selectedItems;
	}

	/**
	 * @return the highlightedItems, see {@link #highlightedItems}
	 */
	public Set<NestableItem> getHighlightedItems() {
		return highlightedItems;
	}

	public boolean isChild(IColumnModel m) {
		for (NestableColumn col : children) {
			if (col.getColumnModel() == m || col.isChild(m))
				return true;
		}
		return false;
	}

	/**
	 * @return the contextMenuCreator, see {@link #contextMenuCreator}
	 */
	protected ContextMenuCreator getContextMenuCreator() {
		return contextMenuCreator;
	}

	public void addContextMenuItem(AContextMenuItem item) {
		contextMenuCreator.add(item);
	}

	public void addContextMenuItems(List<AContextMenuItem> items) {
		contextMenuCreator.addAll(items);
	}

	/**
	 * @return the columnTree, see {@link #columnTree}
	 */
	public ColumnTree getColumnTree() {
		return columnTree;
	}

	/**
	 * @return the header, see {@link #header}
	 */
	public ColumnHeader getHeader() {
		return header;
	}
}
