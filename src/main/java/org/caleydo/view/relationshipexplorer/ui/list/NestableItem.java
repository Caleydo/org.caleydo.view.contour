/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.list;

import java.util.HashMap;
import java.util.Map;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;

/**
 * @author Christian
 *
 */
public class NestableItem extends GLElementContainer {
	protected Map<NestableColumn, CollapsableItemContainer> itemContainers = new HashMap<>();
	protected NestableItem parentItem;
	protected GLElement element;
	protected final NestableColumn column;
	protected final ColumnTree columnTree;

	public NestableItem(GLElement item, NestableColumn column, NestableItem parentItem, ColumnTree columnTree) {
		this.columnTree = columnTree;
		setLayout(new GLSizeRestrictiveFlowLayout2(true, ColumnTreeRenderStyle.HORIZONTAL_SPACING, GLPadding.ZERO));
		setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(this,
				ColumnTreeRenderStyle.HORIZONTAL_SPACING, GLPadding.ZERO));
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

	public void updateSize() {
		if (getParent() == null)
			return;
		float width = element.getSize().x();
		width += column.calcNestingWidth();
		setSize(width, getMinSize().y());

		// if (column == rootColumn)
		// return;
		// ((GLElementContainer) getParent()).setSize(width, Float.NaN);
		((GLElement) getParent().getParent()).setSize(width
				+ 2
				* ColumnTreeRenderStyle.HORIZONTAL_PADDING
				+ (column.parent != null ? ColumnTreeRenderStyle.HORIZONTAL_SPACING
						+ ColumnTreeRenderStyle.COLLAPSE_BUTTON_SIZE : 0), Float.NaN);
	}

	public void updateSummaryItems() {
		for (CollapsableItemContainer container : itemContainers.values()) {
			container.updateSummaryItems();
		}
	}
}
