/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.pathway;

import java.util.List;
import java.util.Set;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.relationshipexplorer.ui.detail.pathway.CompoundAugmentation.ESelectionMode;
import org.caleydo.view.relationshipexplorer.ui.detail.pathway.CompoundAugmentation.GroupData;

import com.google.common.collect.Sets;

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

	// private CompoundAugmentation parent;

	CompoundGroupVis(final CompoundAugmentation parent, final GroupData data, int maxMappingGenes, float width) {
		// this.parent = parent;
		this.data = data;
		this.setLayout(GLLayouts.flowHorizontal(2));
		setLayoutData(data.containedCompounds.size());

		setSize(width, Float.NaN);

		group = new CompoundGroupRepresentation(data, width / 3);
		add(group);

		group.onPick(new APickingListener() {
			@Override
			protected void clicked(Pick pick) {
				parent.propagateGroupSelection(Sets.newHashSet(data.group));

			}
		});


		add(compoundContainer);
		for (final Pair<Object, List<Integer>> compound : data.containedCompounds) {
			CompoundRepresentation rep = new CompoundRepresentation(compound, maxMappingGenes);
			compoundContainer.add(rep);

			rep.onPick(new APickingListener() {
				@Override
				protected void clicked(Pick pick) {
					parent.getCompoundRepresentation().propagateGroupSelection(Sets.newHashSet(compound.getFirst()));

				}
			});

		}
	}

	public void setClusterHighlighted(ESelectionMode selectionMode, Set<Object> highlightElementIDs) {
		group.setHighlighted(selectionMode, highlightElementIDs);
	}

	public void setCompoundHighlighted(ESelectionMode selected, Set<Object> highlightElementIDs) {
		for (GLElement rep : compoundContainer.asList()) {
			CompoundRepresentation compound = (CompoundRepresentation) rep;
			compound.setHighlighted(selected, highlightElementIDs);
		}
	}

}
