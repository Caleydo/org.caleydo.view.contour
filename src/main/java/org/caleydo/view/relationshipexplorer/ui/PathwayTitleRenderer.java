/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;

/**
 * @author Christian
 *
 */
public class PathwayTitleRenderer extends GLElement {

	protected PathwayGraph pathway;

	public PathwayTitleRenderer(PathwayGraph pathway) {
		this.pathway = pathway;
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.drawText(pathway, 0, 0, w, h);
		// g.fillImage(pathway.getImage().getPath(), 0, 0, w, h);
	}

}
