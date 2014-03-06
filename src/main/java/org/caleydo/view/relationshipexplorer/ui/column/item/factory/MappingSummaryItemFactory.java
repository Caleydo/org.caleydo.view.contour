/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.item.factory;

import gleem.linalg.Vec2f;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.list.EUpdateCause;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
import org.caleydo.view.relationshipexplorer.ui.util.EntityMappingUtil;
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
	public GLElement createSummaryItem(NestableItem parentItem, Set<NestableItem> items) {
		if (column.getParentColumn() == null)
			return new GLElement(GLRenderers.drawText("Summary of " + items.size()));

		Set<Object> parentElementIDs = parentItem.getElementData();
		int numSelections = 0;
		Set<Object> filteredElementIDs = new HashSet<>(items.size());
		for (NestableItem item : items) {

			if (filteredElementIDs.addAll(item.getElementData()) && item.isSelected())
				numSelections++;
		}

		Set<Object> mappedElementIDs = EntityMappingUtil.getAllMappedElementIDs(parentElementIDs, column
				.getParentColumn().getColumnModel().getCollection(), column.getCollection());

		// Set<Object> parentBCIDs = column.getParentColumn().getColumnModel()
		// .getBroadcastingIDsFromElementIDs(parentElementIDs);
		// Set<Object> mappedElementIDs = column.getCollection().getElementIDsFromForeignIDs(parentBCIDs,
		// column.getParentColumn().getColumnModel().getBroadcastingIDType());

		KeyBasedGLElementContainer<SimpleBarRenderer> layeredRenderer = createLayeredBarRenderer();
		// layeredRenderer.setRenderer(GLRenderers.drawRect(Color.RED));
		layeredRenderer.getElement(SELECTED_ELEMENTS_KEY).setValue(numSelections);
		layeredRenderer.getElement(SELECTED_ELEMENTS_KEY).setNormalizedValue(
				(float) numSelections / column.getMaxParentMappings());

		layeredRenderer.getElement(FILTERED_ELEMENTS_KEY).setValue(filteredElementIDs.size());
		layeredRenderer.getElement(FILTERED_ELEMENTS_KEY).setNormalizedValue(
				(float) filteredElementIDs.size() / column.getMaxParentMappings());

		layeredRenderer.getElement(ALL_ELEMENTS_KEY).setValue(mappedElementIDs.size());
		layeredRenderer.getElement(ALL_ELEMENTS_KEY).setNormalizedValue(
				(float) mappedElementIDs.size() / column.getMaxParentMappings());
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

	@Override
	public boolean needsUpdate(EUpdateCause cause) {
		return true;
	}

}
