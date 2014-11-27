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
import org.caleydo.view.relationshipexplorer.ui.detail.pathway.CompoundGroupPathwayAugmentation.ESelectionMode;

/**
 * Rendering a compound and it's frequency of bindings with genes.
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
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {

		super.renderImpl(g, w, h);
		Color primaryColor = null;
		Color secondaryColor = null;

		if (isHighlighted) {
			primaryColor = Color.MOUSE_OVER_ORANGE;
			secondaryColor = Color.MOUSE_OVER_ORANGE;
			if (isSelected) {
				secondaryColor = Color.SELECTION_ORANGE;
			}
		} else if (isSelected && !isHighlighted) {
			primaryColor = Color.SELECTION_ORANGE;
			secondaryColor = Color.SELECTION_ORANGE;
		}

		if (isFiltered) {
			primaryColor = Color.WHITE;
			secondaryColor = Color.WHITE;
		}

		if (!isFiltered && !isSelected && !isHighlighted) {
			primaryColor = Color.LIGHT_GRAY;
			secondaryColor = Color.LIGHT_GRAY;
		}

		g.lineWidth(1);
		float spacing = 2;
		float compoundSquareSpace = w / 2 - spacing;

		g.color(primaryColor);
		g.fillRect(0, 0, compoundSquareSpace, h);
		g.color(Color.BLACK);
		g.drawRect(0, 0, compoundSquareSpace, h);

		g.color(primaryColor);

		float geneFrequencySpace = w / 2;

		float width = geneFrequencySpace * mappedGenes.size() / maxMappingGenes;
		g.fillRect(compoundSquareSpace + spacing, 0, width, h);

		// width += compoundSquareSpace + spacing;
		// g.fillRect(compoundSquareSpace + spacing, 0, width, h);
		g.color(Color.BLACK);
		g.drawRect(compoundSquareSpace + spacing, 0, width, h);
		// g.drawLine(width, h, width, 0);
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
