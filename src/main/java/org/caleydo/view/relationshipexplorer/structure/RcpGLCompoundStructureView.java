/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.structure;

import org.caleydo.core.view.ARcpGLElementViewPart;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.AGLElementView;

/**
 * @author Christian
 *
 */
public class RcpGLCompoundStructureView extends ARcpGLElementViewPart {

	/**
	 * @param serializedViewClass
	 */
	public RcpGLCompoundStructureView() {
		super(SerializedCompoundStructureView.class);
	}

	@Override
	protected AGLElementView createView(IGLCanvas canvas) {
		return new GLCompoundStructureView(canvas);
	}

}
