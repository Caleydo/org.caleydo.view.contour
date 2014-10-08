/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.pathway;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IDCollection;

/**
 * @author Christian
 *
 */
public class MultiVertexHighlightAugmentationFactory implements IPathwayAugmentationFactory {

	protected final IDCollection geneIDCollection;
	protected final ConTourElement contour;

	public MultiVertexHighlightAugmentationFactory(IDCollection geneIDCollection, ConTourElement contour) {
		this.geneIDCollection = geneIDCollection;
		this.contour = contour;
	}

	@Override
	public GLElement create(IPathwayRepresentation representation) {
		return new MultiVertexHighlightAugmentation(representation, contour);
	}

}
