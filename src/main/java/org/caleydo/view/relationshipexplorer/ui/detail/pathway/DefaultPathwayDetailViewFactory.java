/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.pathway;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLZoomPanContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.pathway.v2.ui.PathwayElement;
import org.caleydo.view.pathway.v2.ui.PathwayTextureRepresentation;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.detail.DetailViewWindow;
import org.caleydo.view.relationshipexplorer.ui.detail.IDetailViewFactory;

/**
 * @author Christian
 *
 */
public class DefaultPathwayDetailViewFactory implements IDetailViewFactory {

	protected final ConTourElement contour;

	protected List<IPathwayAugmentationFactory> foregroundAugmentationFactories = new ArrayList<>();
	protected List<IPathwayAugmentationFactory> backgroundAugmentationFactories = new ArrayList<>();

	public DefaultPathwayDetailViewFactory(ConTourElement contour) {
		this.contour = contour;
	}

	@Override
	public GLElement create(IEntityCollection collection, DetailViewWindow window) {
		return createZoomableElement(createPathwayElement(collection));
	}

	protected GLElement createZoomableElement(GLElement element) {

		GLZoomPanContainer container = new GLZoomPanContainer();
		container.add(element);
		container.setBackgroundColor(Color.TRANSPARENT);
		container.setScaleLimits(0.1f, 1f);
		container.scaleToFit();

		GLElementContainer minSizeContainer = new GLElementContainer(GLLayouts.LAYERS);
		minSizeContainer.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(element.getMinSize()));
		minSizeContainer.add(container);

		return minSizeContainer;
	}

	protected PathwayElement createPathwayElement(IEntityCollection collection) {
		Set<Object> selectedElements = collection.getSelectedElementIDs();
		Set<Object> highlightedElements = collection.getHighlightElementIDs();
		if (selectedElements.isEmpty() && highlightedElements.isEmpty())
			return null;

		PathwayGraph pathway = (PathwayGraph) collection.getSelectedElementIDs().iterator().next();
		if (selectedElements.size() > 1) {
			pathway = (PathwayGraph) highlightedElements.iterator().next();
		}

		PathwayElement pathwayElement = new PathwayElement("dummy_eventspace");
		PathwayTextureRepresentation representation = new PathwayTextureRepresentation(pathway);
		representation.setMinHeight(pathway.getHeight());
		representation.setMinWidth(pathway.getWidth());
		pathwayElement.setPathwayRepresentation(representation);

		for (IPathwayAugmentationFactory f : backgroundAugmentationFactories) {
			pathwayElement.addBackgroundAugmentation(f.create(representation));
		}
		for (IPathwayAugmentationFactory f : foregroundAugmentationFactories) {
			pathwayElement.addForegroundAugmentation(f.create(representation));
		}

		return pathwayElement;
	}

	public DefaultPathwayDetailViewFactory addForegroundAugmentationFactory(IPathwayAugmentationFactory f) {
		foregroundAugmentationFactories.add(f);
		return this;
	}

	public DefaultPathwayDetailViewFactory addBackgroundAugmentationFactory(IPathwayAugmentationFactory f) {
		backgroundAugmentationFactories.add(f);
		return this;
	}

}
