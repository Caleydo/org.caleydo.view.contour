/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.pathway;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;

/**
 * Factory for pathway augmentations.
 *
 * @author Christian
 * 
 */
public interface IPathwayAugmentationFactory {

	public GLElement create(IPathwayRepresentation representation);

}
