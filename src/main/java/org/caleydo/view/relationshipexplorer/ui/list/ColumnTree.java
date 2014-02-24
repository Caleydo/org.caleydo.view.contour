/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.list;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.ScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;

/**
 * @author Christian
 *
 */
public class ColumnTree extends GLElementContainer {

	protected NestableColumn rootColumn;
	protected ItemContainer rootContainer;

	protected GLElementContainer headerRow;
	// protected GLElementContainer bodyRow;
	protected ScrollingDecorator scrollingDecorator;

	protected Set<NestableColumn> allColumns = new HashSet<>();

	protected class ScrollableItemList extends ItemContainer {

		public ScrollableItemList() {
			setLayout(new GLSizeRestrictiveFlowLayout2(false, ColumnTreeRenderStyle.VERTICAL_SPACING, new GLPadding(
					ColumnTreeRenderStyle.HORIZONTAL_PADDING, 0)));
			setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(this,
					ColumnTreeRenderStyle.VERTICAL_SPACING, GLPadding.ZERO));
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
		setLayout(new GLSizeRestrictiveFlowLayout2(false, ColumnTreeRenderStyle.VERTICAL_SPACING, GLPadding.ZERO));
		setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(this,
				ColumnTreeRenderStyle.VERTICAL_SPACING, GLPadding.ZERO));

		headerRow = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true,
				ColumnTreeRenderStyle.HORIZONTAL_SPACING, GLPadding.ZERO));
		headerRow.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(headerRow,
				ColumnTreeRenderStyle.HORIZONTAL_SPACING, GLPadding.ZERO));
		headerRow.setRenderer(GLRenderers.drawRect(Color.RED));
		add(headerRow);

		// bodyRow = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, HORIZONTAL_SPACING, GLPadding.ZERO));
		// bodyRow.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(bodyRow,
		// HORIZONTAL_SPACING,
		// GLPadding.ZERO));
		// bodyRow.setRenderer(GLRenderers.drawRect(Color.RED));

		// add(bodyRow);
		NestableColumn root1 = addRootColumn("Root1");
		// root1.setColumnWidth(root1.calcMinColumnWidth());
		root1.setColumnWidth(100);
		NestableItem ri1 = addElement(createTextElement("root1 item 1", 16), root1, null);
		NestableItem ri2 = addElement(createTextElement("root1 item 2", 16), root1, null);
		NestableItem ri3 = addElement(createTextElement("root1 item 3", 16), root1, null);

		NestableColumn nested1 = addNestedColumn("Nested1", root1);
		nested1.setColumnWidth(nested1.calcMinColumnWidth());
		NestableItem ni11 = addElement(createTextElement("nested1 item 1_1", 16), nested1, ri1);
		NestableItem ni12 = addElement(createTextElement("nested1 item 1_2", 16), nested1, ri1);
		NestableItem ni13 = addElement(createTextElement("nested1 item 1_3", 16), nested1, ri1);

		NestableItem ni21 = addElement(createTextElement("nested1 item 2_1", 16), nested1, ri2);
		NestableItem ni22 = addElement(createTextElement("nested1 item 2_2", 16), nested1, ri2);
		NestableItem ni23 = addElement(createTextElement("nested1 item 2_3", 16), nested1, ri2);

		NestableItem ni31 = addElement(createTextElement("nested1 item 3_1", 16), nested1, ri3);
		NestableItem ni32 = addElement(createTextElement("nested1 item 3_2", 16), nested1, ri3);

		NestableColumn nested2 = addNestedColumn("Nested2", root1);
		// nested2.setColumnWidth(nested2.calcMinColumnWidth());
		nested2.setColumnWidth(100);
		NestableItem ni211 = addElement(createTextElement("nested2 item 1_1", 16), nested2, ri1);
		NestableItem ni212 = addElement(createTextElement("nested2 item 1_2", 16), nested2, ri1);
		NestableItem ni213 = addElement(createTextElement("nested2 item 1_3", 16), nested2, ri1);
		NestableItem ni214 = addElement(createTextElement("nested2 item 1_4", 16), nested2, ri1);
		NestableColumn nested3 = addNestedColumn("Nested3", nested1);
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
		Map<NestableColumn, Float> minWidths = new HashMap<>();
		for (NestableColumn column : allColumns) {
			float minWidth = column.calcMinColumnWidth();
			totalWidth += minWidth;
			minWidths.put(column, minWidth);
		}
		for (NestableColumn column : allColumns) {
			column.setColumnWidth((minWidths.get(column) / totalWidth)
					* (getSize().x() - (allColumns.size() - 1) * ColumnTreeRenderStyle.HORIZONTAL_SPACING));
		}
		updateSizes();

		super.layout(deltaTimeMs);
	}

	public void updateSizes() {
		rootColumn.updateSizeRec();
		headerRow.setSize(Float.NaN, headerRow.getMinSize().y());
	}

	public NestableColumn addRootColumn(String caption) {
		rootColumn = new NestableColumn();
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



	static GLElement createTextElement(String text, float height) {
		GLElement captionElement = new GLElement(GLRenderers.drawText(text, VAlign.LEFT));
		// GLElement captionElement = new GLElement(GLRenderers.drawRect(Color.RED));
		captionElement.setSize(Float.NaN, height);
		captionElement.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(200, height));
		return captionElement;
	}

	public NestableColumn addNestedColumn(String caption, NestableColumn parent) {
		NestableColumn column = new NestableColumn();
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

	public NestableItem addElement(GLElement element, NestableColumn column, NestableItem parentItem) {
		NestableItem item = new NestableItem(element, column, parentItem, this);
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
