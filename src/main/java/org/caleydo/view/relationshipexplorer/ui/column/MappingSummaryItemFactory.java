/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import gleem.linalg.Vec2f;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
import org.caleydo.view.relationshipexplorer.ui.util.KeyBasedGLElementContainer;
import org.caleydo.view.relationshipexplorer.ui.util.SimpleBarRenderer;

/**
 * @author Christian
 *
 */
public class MappingSummaryItemFactory implements ISummaryItemFactory {

	protected static final Integer SELECTED_ELEMENTS_KEY = Integer.valueOf(2);
	protected static final Integer FILTERED_ELEMENTS_KEY = Integer.valueOf(3);
	protected static final Integer ALL_ELEMENTS_KEY = Integer.valueOf(4);

	protected AEntityColumn column;

	public MappingSummaryItemFactory(AEntityColumn column) {
		this.column = column;
	}

	@Override
	public GLElement createSummaryItem(Set<NestableItem> items) {
		if (column.parentColumn == null)
			return new GLElement(GLRenderers.drawText("Summary of " + items.size()));

		Set<Object> parentElementIDs = new HashSet<>();
		int numSelections = 0;
		for (NestableItem item : items) {
			parentElementIDs.addAll(item.getParentItem().getElementData());
			if (item.isSelected())
				numSelections++;
		}

		Set<Object> parentBCIDs = column.parentColumn.getColumnModel().getBroadcastingIDsFromElementIDs(
				parentElementIDs);
		Set<Object> mappedElementIDs = column.getCollection().getElementIDsFromForeignIDs(parentBCIDs,
				column.parentColumn.getColumnModel().getBroadcastingIDType());

		KeyBasedGLElementContainer<SimpleBarRenderer> layeredRenderer = createLayeredBarRenderer();
		// layeredRenderer.setRenderer(GLRenderers.drawRect(Color.RED));
		layeredRenderer.getElement(SELECTED_ELEMENTS_KEY).setValue(numSelections);
		layeredRenderer.getElement(SELECTED_ELEMENTS_KEY).setNormalizedValue(
				(float) numSelections / column.maxParentMappings);
		layeredRenderer.getElement(FILTERED_ELEMENTS_KEY).setValue(items.size());
		layeredRenderer.getElement(FILTERED_ELEMENTS_KEY).setNormalizedValue(
				(float) items.size() / column.maxParentMappings);
		layeredRenderer.getElement(ALL_ELEMENTS_KEY).setValue(mappedElementIDs.size());
		layeredRenderer.getElement(ALL_ELEMENTS_KEY).setNormalizedValue(
				(float) mappedElementIDs.size() / column.maxParentMappings);
		return layeredRenderer;
	}

	protected KeyBasedGLElementContainer<SimpleBarRenderer> createLayeredBarRenderer() {
		KeyBasedGLElementContainer<SimpleBarRenderer> barLayerRenderer = new KeyBasedGLElementContainer<>(
				GLLayouts.LAYERS);

		// barLayerRenderer.setSize(80, Float.NaN);
		barLayerRenderer.setMinSizeProvider(GLMinSizeProviders.createLayeredMinSizeProvider(barLayerRenderer));
		barLayerRenderer.setElement(ALL_ELEMENTS_KEY, createDefaultBarRenderer(Color.LIGHT_GRAY, 0.1f));
		barLayerRenderer.setElement(FILTERED_ELEMENTS_KEY, createDefaultBarRenderer(Color.GRAY, 0.2f));
		barLayerRenderer.setElement(SELECTED_ELEMENTS_KEY,
				createDefaultBarRenderer(SelectionType.SELECTION.getColor(), 0.3f));
		// barLayerRenderer.setRenderer(GLRenderers.drawRect(Color.BLUE));
		return barLayerRenderer;
	}

	protected SimpleBarRenderer createDefaultBarRenderer(Color color, float zDelta) {
		SimpleBarRenderer renderer = new SimpleBarRenderer(0, true);
		renderer.setMinSize(new Vec2f(80, 16));
		// renderer.setSize(80, Float.NaN);
		renderer.setColor(color);
		renderer.setBarWidth(12);
		renderer.setzDelta(zDelta);
		return renderer;
	}

}
