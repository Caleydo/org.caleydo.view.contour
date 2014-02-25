/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.pathway;

import java.util.List;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.pathway.v2.ui.PathwayTextureRepresentation;

/**
 * @author Christian
 *
 */
public class CompoundAugmentation extends GLElement {
	protected IPathwayRepresentation pathwayRepresentation;

	public CompoundAugmentation(IPathwayRepresentation pathwayRepresentation) {
		this.pathwayRepresentation = pathwayRepresentation;
		((PathwayTextureRepresentation) pathwayRepresentation).setPadding(new GLPadding(50, 50, 50, 50));
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {

		Rect pathwayBounds = pathwayRepresentation.getPathwayBounds();
		g.color(Color.RED)
				.drawRect(pathwayBounds.x(), pathwayBounds.y(), pathwayBounds.width(), pathwayBounds.height());

		for (PathwayGraph pathway : pathwayRepresentation.getPathways()) {
			for (PathwayVertexRep vertexRep : pathway.vertexSet()) {
				List<Rect> boundsList = pathwayRepresentation.getVertexRepsBounds(vertexRep);
				for (Rect bounds : boundsList) {
					g.fillRect(bounds);
				}
			}
		}
	}
}
