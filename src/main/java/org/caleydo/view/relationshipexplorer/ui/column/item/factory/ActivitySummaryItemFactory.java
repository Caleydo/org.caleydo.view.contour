/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.item.factory;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.id.IDType;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.column.TabularDataColumn;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;

/**
 * @author Christian
 *
 */
public class ActivitySummaryItemFactory implements ISummaryItemFactory {

	protected final TabularDataCollection collection;
	protected int dimensionID;
	protected NumericalProperties numericalProperties;
	protected IDType recordIDType;

	public ActivitySummaryItemFactory(TabularDataColumn column) {
		collection = (TabularDataCollection) column.getCollection();

		recordIDType = collection.getDataDomain().getOppositeIDType(collection.getDimensionPerspective().getIdType());
		for (int dimensionID : collection.getDimensionPerspective().getVirtualArray()) {
			Object dataClassDesc = collection.getDataDomain().getTable().getDataClassSpecificDescription(dimensionID);
			if (dataClassDesc instanceof NumericalProperties) {
				numericalProperties = (NumericalProperties) dataClassDesc;
				this.dimensionID = dimensionID;
				break;
			}
		}
	}

	@Override
	public GLElement createSummaryItem(NestableItem parentItem, Set<NestableItem> items) {
		// IDType recordIDType = column.dataDomain.getOppositeIDType(column.perspective.getIdType());
		//
		// SimpleBarRenderer ic50Renderer = null;
		// PickableGLElement interactionTypeRenderer = new PickableGLElement();
		// interactionTypeRenderer.setSize(16, 16);
		// interactionTypeRenderer.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(16, 16));
		//
		// for (int dimensionID : column.perspective.getVirtualArray()) {
		// Object dataClassDesc = column.dataDomain.getDataClassSpecificDescription(recordIDType, (Integer) elementID,
		// column.perspective.getIdType(), dimensionID);
		//
		// if (dataClassDesc instanceof NumericalProperties) {
		// NumericalProperties p = (NumericalProperties) dataClassDesc;
		// // TODO: use correct data center
		// ic50Renderer = new SimpleBarRenderer();
		// ic50Renderer.setHorizontal(true);
		//
		// float rawValue = (float) column.dataDomain.getRaw(recordIDType, (int) elementID,
		// column.perspective.getIdType(), dimensionID);
		// ic50Renderer.setValue(rawValue);
		// if (p.getMax() != null) {
		// ic50Renderer.setColor(rawValue > p.getMax() ? column.dataDomain.getColor().darker().darker()
		// : column.dataDomain.getColor());
		// } else {
		// ic50Renderer.setColor(column.dataDomain.getColor());
		// }
		//
		// float normalizedValue = column.dataDomain.getNormalizedValue(recordIDType, (int) elementID,
		// column.perspective.getIdType(), dimensionID);
		// ic50Renderer.setNormalizedValue(normalizedValue);
		// ic50Renderer.setMinSize(new Vec2f(50, 16));
		// break;
		// }
		// }
		//
		// GLElementContainer container = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 2,
		// GLPadding.ZERO));
		// container.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(container, 2,
		// GLPadding.ZERO));
		// container.add(interactionTypeRenderer);
		// container.add(ic50Renderer);
		Set<Object> elementIDs = new HashSet<>(items.size());
		for (NestableItem item : items) {
			elementIDs.addAll(item.getElementData());
		}
		IC50SummaryRenderer renderer = new IC50SummaryRenderer(elementIDs);
		renderer.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(80, 16));

		return renderer;
	}

	protected class IC50SummaryRenderer extends GLElement {

		protected final Set<Object> elementIDs;

		/**
		 *
		 */
		public IC50SummaryRenderer(Set<Object> elementIDs) {
			this.elementIDs = elementIDs;
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {

			for (Object elementID : elementIDs) {
				// TODO: use correct data center

				// float rawValue = (float) column.dataDomain.getRaw(recordIDType, (int) elementID,
				// column.perspective.getIdType(), dimensionID);
				// ic50Renderer.setValue(rawValue);
				// if (p.getMax() != null) {
				// ic50Renderer.setColor(rawValue > p.getMax() ? column.dataDomain.getColor().darker().darker()
				// : column.dataDomain.getColor());
				// } else {
				// ic50Renderer.setColor(column.dataDomain.getColor());
				// }

				float normalizedValue = collection.getDataDomain().getNormalizedValue(recordIDType, (int) elementID,
						collection.getDimensionPerspective().getIdType(), dimensionID);

				g.color(Color.GRAY).drawLine(normalizedValue * (w - 2), 0, normalizedValue * (w - 2), h);
			}
		}
	}

}
