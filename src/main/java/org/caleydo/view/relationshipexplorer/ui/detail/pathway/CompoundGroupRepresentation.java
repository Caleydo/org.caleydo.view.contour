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
import org.caleydo.view.relationshipexplorer.ui.detail.pathway.CompoundAugmentation.ESelectionMode;
import org.caleydo.view.relationshipexplorer.ui.detail.pathway.CompoundAugmentation.GroupData;

/**
 * Rendering a cluster for a pathway.
 *
 * @author Alexander Lex
 *
 */
public class CompoundGroupRepresentation extends PickableGLElement {

	private GroupData data;
	private boolean isSelected = false;
	private boolean isHighlighted = false;
	private boolean isFiltered = false;

	CompoundGroupRepresentation(GroupData data, float width) {
		this.data = data;
		setLayoutData(data.containedCompounds.size());
		setSize(width, Float.NaN);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {

		super.renderImpl(g, w, h);
		Color frameColor;
		Color bodyColor;
		float completeness = (float) data.containedCompounds.size() / data.allCompounds.size() / 3 * 2;
		float alpha = 1;
		if (isSelected) {
			frameColor = Color.SELECTION_ORANGE;
			g.lineWidth(2);

		} else if (isHighlighted) {
			frameColor = Color.MOUSE_OVER_ORANGE;
			g.lineWidth(2);

		} else {
			frameColor = Color.BLACK;
			g.lineWidth(1);
		}

		if (isFiltered) {
			bodyColor = Color.WHITE;
		} else {
			bodyColor = new Color(70, 130, 180);
			alpha = 1 / 3 + completeness;
		}



		g.gl.glColor4f(bodyColor.r, bodyColor.g, bodyColor.b, alpha);
		g.fillRect(0, 0, w, h);
		g.color(frameColor);
		g.drawRect(0, 0, w, h);

	}

	@Override
	protected void onClicked(Pick pick) {
		// TODO Auto-generated method stub
		super.onClicked(pick);
		// this.isSelected = true;

	}

	@Override
	protected void onMouseOver(Pick pick) {
		super.onMouseOver(pick);
	}

	@Override
	public String getTooltip() {
		return "Compounds: " + data.containedCompounds.size() + "/" + data.allCompounds.size();
	}

	public void setHighlighted(ESelectionMode selected, Set<Object> highlightElementIDs) {
		boolean isContained = highlightElementIDs.contains(data.group);

		if (ESelectionMode.SELECTED.equals(selected) && (isContained != isSelected)) {
			isSelected = isContained;
			repaint();
		}
		if (ESelectionMode.HIGHLGHTED.equals(selected) && (isContained != isHighlighted)) {
			isHighlighted = isContained;
			repaint();
		}
		if (ESelectionMode.FILTERED.equals(selected) && (isContained != isHighlighted)) {
			isFiltered = isContained;
			repaint();
		}
	}

}
