/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.pathway;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;

/**
 * @author Christian
 *
 */
public class CompoundGroupPathwayAugmentationFactory implements IPathwayAugmentationFactory {

	protected final ConTourElement contour;

	public CompoundGroupPathwayAugmentationFactory(ConTourElement contour) {
		this.contour = contour;
	}

	@Override
	public GLElement create(IPathwayRepresentation representation) {
		return new CompoundGroupPathwayAugmentation(representation, contour);
	}

}
