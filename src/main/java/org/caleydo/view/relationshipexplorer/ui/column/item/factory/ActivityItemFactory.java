/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.item.factory;

import gleem.linalg.Vec2f;

import java.net.URL;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.column.TabularDataColumn;
import org.caleydo.view.relationshipexplorer.ui.util.SimpleBarRenderer;

/**
 * @author Christian
 *
 */
public class ActivityItemFactory implements IItemFactory {

	protected static final URL ACTIVATION_ICON = AEntityColumn.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/arrow_right_up.png");
	protected static final URL INHIBITION_ICON = AEntityColumn.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/arrow_right_down.png");
	protected static final URL INACTIVE_ICON = AEntityColumn.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/abort.png");

	protected final TabularDataColumn column;

	public ActivityItemFactory(TabularDataColumn column) {
		this.column = column;
	}

	@Override
	public GLElement createItem(Object elementID) {

		TabularDataCollection collection = (TabularDataCollection) column.getCollection();
		ATableBasedDataDomain dataDomain = collection.getDataDomain();
		Perspective dimensionPerspective = collection.getDimensionPerspective();

		IDType recordIDType = dataDomain.getOppositeIDType(collection.getDimensionPerspective().getIdType());

		SimpleBarRenderer ic50Renderer = null;
		PickableGLElement interactionTypeRenderer = new PickableGLElement();
		interactionTypeRenderer.setSize(16, 16);
		interactionTypeRenderer.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(16, 16));

		for (int dimensionID : dimensionPerspective.getVirtualArray()) {
			Object dataClassDesc = dataDomain.getDataClassSpecificDescription(recordIDType, (Integer) elementID,
					dimensionPerspective.getIdType(), dimensionID);

			if (dataClassDesc instanceof NumericalProperties) {
				NumericalProperties p = (NumericalProperties) dataClassDesc;
				// TODO: use correct data center
				ic50Renderer = new SimpleBarRenderer();
				ic50Renderer.setHorizontal(true);

				float rawValue = (float) dataDomain.getRaw(recordIDType, (int) elementID,
						dimensionPerspective.getIdType(), dimensionID);
				ic50Renderer.setValue(rawValue);
				if (p.getMax() != null) {
					ic50Renderer.setColor(rawValue > p.getMax() ? dataDomain.getColor().darker().darker() : dataDomain
							.getColor());
				} else {
					ic50Renderer.setColor(dataDomain.getColor());
				}

				float normalizedValue = dataDomain.getNormalizedValue(recordIDType, (int) elementID,
						dimensionPerspective.getIdType(), dimensionID);
				ic50Renderer.setNormalizedValue(normalizedValue);
				ic50Renderer.setMinSize(new Vec2f(50, 16));
			} else {
				CategoryProperty<?> property = ((CategoricalClassDescription<?>) dataClassDesc)
						.getCategoryProperty(dataDomain.getRaw(recordIDType, (int) elementID,
								dimensionPerspective.getIdType(), dimensionID));

				if (property != null) {
					if (property.getCategoryName().equalsIgnoreCase("activation")) {
						interactionTypeRenderer.setRenderer(GLRenderers.fillImage(ACTIVATION_ICON));
						interactionTypeRenderer.setTooltip("Activation");
					} else if (property.getCategoryName().equalsIgnoreCase("inhibition")) {
						interactionTypeRenderer.setRenderer(GLRenderers.fillImage(INHIBITION_ICON));
						interactionTypeRenderer.setTooltip("Inhibition");
					} else if (property.getCategoryName().equalsIgnoreCase("inactive")) {
						interactionTypeRenderer.setRenderer(GLRenderers.fillImage(INACTIVE_ICON));
						interactionTypeRenderer.setTooltip("Inactive");
					}
				}
			}
		}

		GLElementContainer container = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 2, GLPadding.ZERO));
		container.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(container, 2,
				GLPadding.ZERO));
		container.add(interactionTypeRenderer);
		container.add(ic50Renderer);

		return container;
	}
}
