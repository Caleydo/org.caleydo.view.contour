/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.pathway;

import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.relationshipexplorer.ui.detail.pathway.CompoundAugmentation.GroupData;

/**
 * Rendering a cluster for a pathway.
 *
 * @author Alexander Lex
 *
 */
public class GroupElement extends PickableGLElement {

	private GroupData data;

	GroupElement(GroupData data) {
		this.data = data;
		System.out.println(data.containedCompounds.size());

		setSize(20, data.containedCompounds.size());
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		// TODO Auto-generated method stub
		super.renderImpl(g, w, h);
		g.drawRect(0, 0, w, h);
	}

	@Override
	protected void onClicked(Pick pick) {
		// TODO Auto-generated method stub
		super.onClicked(pick);


	}

}
