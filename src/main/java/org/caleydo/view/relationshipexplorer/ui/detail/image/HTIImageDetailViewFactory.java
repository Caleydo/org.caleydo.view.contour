/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.image;

import java.util.Set;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.datadomain.image.ImageDataDomain;
import org.caleydo.datadomain.image.LayeredImage;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.detail.DetailViewWindow;
import org.caleydo.view.relationshipexplorer.ui.detail.IDetailViewFactory;

/**
 * @author Christian
 *
 */
public class HTIImageDetailViewFactory implements IDetailViewFactory {

	protected final ImageDataDomain dataDomain;
	protected final ConTourElement contour;

	public HTIImageDetailViewFactory(ImageDataDomain dataDomain, ConTourElement contour) {
		this.dataDomain = dataDomain;
		this.contour = contour;
	}

	@Override
	public GLElement createDetailView(IEntityCollection collection, DetailViewWindow window) {

		Set<Object> selectedElements = collection.getSelectedElementIDs();
		Set<Object> highlightedElements = collection.getHighlightElementIDs();
		if (selectedElements.isEmpty() && highlightedElements.isEmpty())
			return null;

		String imageLayerID = (String) collection.getSelectedElementIDs().iterator().next();
		if (selectedElements.size() > 1) {
			imageLayerID = (String) highlightedElements.iterator().next();
		}

		LayeredImage img = dataDomain.getImageSet().getImageForLayer(imageLayerID);
		if (img != null) {
			GLElementContainer minSizeContainer = new GLElementContainer(GLLayouts.LAYERS);
			// The real size of the texture is unknown up to this point -> set meaningful default size
			minSizeContainer.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(500, 500));
			minSizeContainer.add(new AreaImageViewerElement(contour, collection, img));
			return minSizeContainer;
		}
		return new GLElement(GLRenderers.fillRect(Color.BLUE));

	}

	@Override
	public DetailViewWindow createWindow(IEntityCollection collection, ConTourElement contour) {
		return new DetailViewWindow(collection, contour);
	}
}
