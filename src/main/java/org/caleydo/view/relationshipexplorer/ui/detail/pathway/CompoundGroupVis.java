/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.pathway;

import java.util.List;
import java.util.Set;

import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.FilterContextMenuItems;
import org.caleydo.view.relationshipexplorer.ui.detail.pathway.CompoundGroupPathwayAugmentation.ESelectionMode;
import org.caleydo.view.relationshipexplorer.ui.detail.pathway.CompoundGroupPathwayAugmentation.GroupData;

import com.google.common.collect.Sets;

/**
 * Renders the content of a compound group, including an explicit representation of the group, and individual
 * representations of compounds.
 *
 * @author Alexander Lex
 *
 */
public class CompoundGroupVis extends GLElementContainer {

	private GLSizeRestrictiveFlowLayout layout = new GLSizeRestrictiveFlowLayout(false, 0, new GLPadding(0, 0));
	private GLElementContainer compoundContainer = new GLElementContainer(layout);

	private CompoundGroupRepresentation group;

	// private CompoundAugmentation parent;

	CompoundGroupVis(final CompoundGroupPathwayAugmentation parent, final GroupData data, int maxMappingGenes,
			float width) {

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

			@Override
			protected void mouseOver(Pick pick) {
				parent.propagateGroupHighlight(Sets.newHashSet(data.group));
			}

			@Override
			protected void rightClicked(Pick pick) {
				parent.propagateGroupSelection(Sets.newHashSet(data.group));

				parent.getRelationshipExplorer().addContextMenuItems(
						FilterContextMenuItems.getDefaultFilterItems(
						parent.getRelationshipExplorer(), parent));
				// ContextMenuCreator contextMenuCreator = new ContextMenuCreator();
				// contextMenuCreator.addAll(FilterContextMenuItems.getDefaultFilterItems(
				// parent.getRelationshipExplorer(), parent));
				//
				// context.getSWTLayer().showContextMenu(contextMenuCreator);
			}

		});

		add(compoundContainer);
		for (final Pair<Object, List<Integer>> compound : data.containedCompounds) {
			CompoundRepresentation rep = new CompoundRepresentation(compound, maxMappingGenes);
			compoundContainer.add(rep);

			rep.onPick(new APickingListener() {
				@Override
				protected void clicked(Pick pick) {
					parent.getCompoundRepresentation().propagateCompoundSelection(Sets.newHashSet(compound.getFirst()));

				}

				@Override
				protected void mouseOver(Pick pick) {
					parent.getCompoundRepresentation().propagateCompoundHighlight(Sets.newHashSet(compound.getFirst()));
				}

				@Override
				protected void rightClicked(Pick pick) {
					parent.getCompoundRepresentation().propagateCompoundSelection(Sets.newHashSet(compound.getFirst()));

					ContextMenuCreator contextMenuCreator = new ContextMenuCreator();

					contextMenuCreator.addAll(FilterContextMenuItems.getDefaultFilterItems(
							parent.getRelationshipExplorer(), parent.getCompoundRepresentation()));

					context.getSWTLayer().showContextMenu(contextMenuCreator);
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
