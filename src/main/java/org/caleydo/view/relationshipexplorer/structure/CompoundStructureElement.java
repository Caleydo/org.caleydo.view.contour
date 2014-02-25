/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.structure;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * @author Christian
 *
 */
public class CompoundStructureElement extends GLElement {

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.color(Color.RED).fillRect(0, 0, w, h);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		super.renderPickImpl(g, w, h);
	}

}
