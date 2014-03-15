/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.pathway;

import java.util.List;
import java.util.Set;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.relationshipexplorer.ui.detail.pathway.CompoundAugmentation.ESelectionMode;

/**
 * Rendering a cluster for a pathway.
 *
 * @author Alexander Lex
 *
 */
public class CompoundRepresentation extends PickableGLElement {

	private Object compoundID;
	private List<Integer> mappedGenes;

	private boolean isSelected = false;
	private boolean isHighlighted = false;
	private boolean isFiltered = false;
	private int maxMappingGenes = 0;

	CompoundRepresentation(Pair<Object, List<Integer>> data, int maxMappingGenes) {
		compoundID = data.getFirst();
		mappedGenes = data.getSecond();
		this.maxMappingGenes = maxMappingGenes;
		setLayoutData(1);
		// setSize(, Float.NaN);
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {

		super.renderImpl(g, w, h);
		Color color;

		if (isHighlighted) {
			color = Color.MOUSE_OVER_ORANGE;
		} else if (isSelected) {
			color = Color.SELECTION_ORANGE;
		} else if (isSelected) {
			color = Color.WHITE;
		} else {
			color = Color.LIGHT_GRAY;
		}

		g.lineWidth(1);
		float spacing = 2;
		float compoundSquareSpace = w / 2 - spacing;

		g.color(color);
		g.fillRect(0, 0, compoundSquareSpace, h);
		g.color(Color.BLACK);
		g.drawRect(0, 0, compoundSquareSpace, h);

		g.color(color);

		float geneFrequencySpace = w / 2;

		float width = geneFrequencySpace * mappedGenes.size() / maxMappingGenes;
		g.fillRect(compoundSquareSpace + spacing, 0, width, h);
		g.color(Color.BLACK);
		g.drawRect(compoundSquareSpace + spacing, 0, width, h);

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
		return "Compound: " + compoundID + "\n Mapping: " + mappedGenes.size();
	}

	public void setHighlighted(ESelectionMode selected, Set<Object> highlightElementIDs) {

		boolean isContained = highlightElementIDs.contains(compoundID);

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
