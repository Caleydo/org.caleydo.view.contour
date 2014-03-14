/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.pathway;

import java.util.Set;

import org.caleydo.core.util.color.Color;
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
	private boolean isSelected = false;
	private boolean isHighlighted = false;

	GroupElement(GroupData data) {
		this.data = data;
		setLayoutData(data.containedCompounds.size());
		setSize(20, Float.NaN);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {

		super.renderImpl(g, w, h);

		if (!isSelected) {
			float completeness = (float) data.containedCompounds.size() / data.allCompounds.size() / 3 * 2;
			Color color = new Color(70, 130, 180);
			color.a = 1 / 3 + completeness;

			g.color(color);
		} else {
			g.color(Color.SELECTION_ORANGE);
		}
		g.fillRect(0, 0, w, h);
		g.color(Color.BLACK);
		g.drawRect(0, 0, w, h);

	}

	@Override
	protected void onClicked(Pick pick) {
		// TODO Auto-generated method stub
		super.onClicked(pick);
		// this.isSelected = true;

		repaint();
	}

	@Override
	protected void onMouseOver(Pick pick) {
		super.onMouseOver(pick);
	}

	@Override
	public String getTooltip() {
		return "Compounds: " + data.containedCompounds.size() + "/" + data.allCompounds.size();
	}

	public void setHighlighted(boolean selected, Set<Object> highlightElementIDs) {
		boolean isContained = highlightElementIDs.contains(data.group);
		if (selected && (isContained != isSelected)) {
			isSelected = isContained;
			repaint();
		}
		if (!selected && (isContained != isHighlighted)) {
			isHighlighted = isContained;
			repaint();
		}
	}

}
