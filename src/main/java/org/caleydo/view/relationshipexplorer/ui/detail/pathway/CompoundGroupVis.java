/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.pathway;

import java.util.List;
import java.util.Set;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.view.relationshipexplorer.ui.detail.pathway.CompoundAugmentation.GroupData;

/**
 * @author Alexander Lex
 *
 */
public class CompoundGroupVis extends GLElementContainer {

	private GroupData data;
	private boolean isSelected = false;
	private boolean isHighlighted = false;

	private GLSizeRestrictiveFlowLayout layout = new GLSizeRestrictiveFlowLayout(false, 0, new GLPadding(0, 0));
	private GLElementContainer compoundContainer = new GLElementContainer(layout);


	private CompoundGroupRepresentation group;

	CompoundGroupVis(GroupData data, int maxMappingGenes, float width) {
		this.data = data;
		this.setLayout(GLLayouts.flowHorizontal(2));
		setLayoutData(data.containedCompounds.size());

		setSize(width, Float.NaN);

		group = new CompoundGroupRepresentation(data, width / 3);
		add(group);
		add(compoundContainer);
		for (Pair<Object, List<Integer>> compound : data.containedCompounds) {
			CompoundRepresentation rep = new CompoundRepresentation(compound, maxMappingGenes);
			compoundContainer.add(rep);
		}
	}

	public void setClusterHighlighted(boolean selected, Set<Object> highlightElementIDs) {
		group.setHighlighted(selected, highlightElementIDs);
	}

	public void setCompoundHighlighted(boolean selected, Set<Object> highlightElementIDs) {
		// for(CompoundRepresentation rep : compoundContainer.asList()))
	}

}
