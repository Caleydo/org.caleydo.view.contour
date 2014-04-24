/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.pathway;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.view.pathway.v2.ui.PathwayElement;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IDCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.detail.DetailViewWindow;

/**
 * @author Christian
 *
 */
public class HTSPathwayDetailViewFactory extends DefaultPathwayDetailViewFactory {

	/**
	 * @param contour
	 * @param geneIDCollection
	 */
	public HTSPathwayDetailViewFactory(ConTourElement contour, IDCollection geneIDCollection) {
		super(contour);
		addForegroundAugmentationFactory(new MultiVertexHighlightAugmentationFactory(geneIDCollection, contour));
	}

	@Override
	public GLElement create(IEntityCollection collection, DetailViewWindow window) {

		PathwayElement pathwayElement = createPathwayElement(collection);

		CompoundGroupPathwayAugmentation aug = new CompoundGroupPathwayAugmentation(
				pathwayElement.getPathwayRepresentation(), contour);
		pathwayElement.addBackgroundAugmentation(aug);

		window.clearTitleElements();
		window.addShowFilteredItems(aug, false);

		return createZoomPanContainer(pathwayElement);
	}

}