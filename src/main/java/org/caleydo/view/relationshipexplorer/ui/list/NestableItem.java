/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.list;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.relationshipexplorer.ui.util.AnimationUtil;
import org.caleydo.view.relationshipexplorer.ui.util.MultiSelectionUtil;

/**
 * @author Christian
 *
 */
public class NestableItem extends AnimatedGLElementContainer {
	protected Map<NestableColumn, CollapsableItemContainer> itemContainers = new HashMap<>();
	protected final NestableItem parentItem;
	// protected GLElement element;
	protected ListElement listElement;
	protected final NestableColumn column;
	protected final ColumnTree columnTree;
	protected Set<Object> elementData;

	public NestableItem(GLElement element, NestableColumn column, NestableItem parentItem, ColumnTree columnTree) {
		this.columnTree = columnTree;
		this.parentItem = parentItem;
		setLayout(new GLSizeRestrictiveFlowLayout2(true, ColumnTreeRenderStyle.HORIZONTAL_SPACING, GLPadding.ZERO));
		setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(this,
				ColumnTreeRenderStyle.HORIZONTAL_SPACING, GLPadding.ZERO));
		this.column = column;
		this.listElement = new ListElement();

		listElement.setHighlightColor(SelectionType.MOUSE_OVER.getColor());
		listElement.setSelectionColor(SelectionType.SELECTION.getColor());
		listElement.onPick(new IPickingListener() {

			@Override
			public void pick(Pick pick) {

				boolean update = MultiSelectionUtil.handleSelection(pick, NestableItem.this, NestableItem.this.column);
				if (update) {
					NestableItem.this.column.notifyOfSelectionUpdate();
				}
				update = MultiSelectionUtil.handleHighlight(pick, NestableItem.this, NestableItem.this.column);
				if (update) {
					NestableItem.this.column.notifyOfHighlightUpdate();
				}
			}
		});

		// setRenderer(GLRenderers.drawRect(Color.BLUE));
		add(listElement);
		setElement(element);

		// setSize(Float.NaN, getMinSize().y());
		// for (Column col : column.children) {
		// addNestedContainer(col);
		// }
		// updateSize();

	}

	/**
	 * @param element
	 *            setter, see {@link item}
	 */
	public void setElement(GLElement element) {
		listElement.setContent(element);
		// remove(this.element);
		// this.element = element;
		// add(0, element);
	}

	public CollapsableItemContainer getNestedContainer(NestableColumn column) {
		CollapsableItemContainer c = itemContainers.get(column);
		if (c == null)
			c = addNestedContainer(column);
		return c;
	}

	public void addItem(NestableItem item, NestableColumn column) {
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

	private CollapsableItemContainer addNestedContainer(NestableColumn column) {

		CollapsableItemContainer container = new CollapsableItemContainer(column, this, columnTree);
		itemContainers.put(column, container);

		add(container);
		return container;
	}

	public void updateSize(float elementWidth, float elementHeight) {

		if (getParent() == null)
			return;
		AnimationUtil.resizeElement(listElement, elementWidth, Float.NaN);
		AnimationUtil.resizeElement(getElement(), elementWidth, elementHeight);
		getElement().relayout();
		float width = elementWidth;
		width += column.calcNestingWidth();
		// TODO: completely consider scrollbar
		AnimationUtil.resizeElement(this, width, getMinSize().y());
		// AnimationUtil.resizeElement(this, column.isRoot() ? width - 4 : width, getMinSize().y());

		// setSize(width, getMinSize().y());

		// if (column == rootColumn)
		// return;
		// ((GLElementContainer) getParent()).setSize(width, Float.NaN);
		if (column.isRoot()) {
			AnimationUtil.resizeElement((GLElement) getParent().getParent(), width + 2
					* ColumnTreeRenderStyle.HORIZONTAL_PADDING, Float.NaN);
		} else {
			AnimationUtil.resizeElement((GLElement) getParent().getParent(), width + 2
					* ColumnTreeRenderStyle.HORIZONTAL_PADDING + ColumnTreeRenderStyle.HORIZONTAL_SPACING
					+ ColumnTreeRenderStyle.COLLAPSE_BUTTON_SIZE, Float.NaN);
		}

		// ((GLElement) getParent().getParent()).setSize(width
		// + 2
		// * ColumnTreeRenderStyle.HORIZONTAL_PADDING
		// + (column.parent != null ? ColumnTreeRenderStyle.HORIZONTAL_SPACING
		// + ColumnTreeRenderStyle.COLLAPSE_BUTTON_SIZE : 0), Float.NaN);
	}

	public void updateSummaryItems() {
		for (CollapsableItemContainer container : itemContainers.values()) {
			container.updateSummaryItems();
		}
	}

	/**
	 * @param elementData
	 *            setter, see {@link elementData}
	 */
	public void setElementData(Set<Object> elementData) {
		this.elementData = elementData;
	}

	/**
	 * @return the element, see {@link #element}
	 */
	public GLElement getElement() {
		return listElement.getContent();
	}

	/**
	 * @return the elementData, see {@link #elementData}
	 */
	public Set<Object> getElementData() {
		return elementData;
	}

	/**
	 * @return the parentItem, see {@link #parentItem}
	 */
	public NestableItem getParentItem() {
		return parentItem;
	}

	public void setSelected(boolean isSelected) {
		listElement.setSelected(isSelected);
	}

	public void setHighlight(boolean isHighlight) {
		listElement.setHighlight(isHighlight);
	}

	public boolean isSelected() {
		return listElement.isSelected();
	}

	public boolean isHighlight() {
		return listElement.isHighlight();
	}
}
