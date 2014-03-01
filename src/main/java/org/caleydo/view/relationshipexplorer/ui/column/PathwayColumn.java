/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.pathway.v2.ui.PathwayElement;
import org.caleydo.view.pathway.v2.ui.PathwayTextureRepresentation;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
import org.caleydo.view.relationshipexplorer.ui.pathway.CompoundAugmentation;
import org.caleydo.view.relationshipexplorer.ui.pathway.MultiVertexHighlightAugmentation;

/**
 * @author Christian
 *
 */
public class PathwayColumn extends ATextColumn {

	protected final PathwayCollection pathwayCollection;

	/**
	 * @param relationshipExplorer
	 */
	public PathwayColumn(PathwayCollection pathwayCollection, RelationshipExplorerElement relationshipExplorer) {
		super(pathwayCollection, relationshipExplorer);
		this.pathwayCollection = pathwayCollection;
	}

	// @ListenTo
	// public void onApplyIDFilter(IDUpdateEvent event) {
	// if (event.getSender() == this)
	// return;
	// Set<?> foreignIDs = event.getIds();
	// IDType foreignIDType = event.getIdType();
	// Set<Object> mappedPathways = new HashSet<>();
	// for (Object foreignID : foreignIDs) {
	// Set<PathwayGraph> pathways = PathwayManager.get().getPathwayGraphsByGeneID(foreignIDType,
	// (Integer) foreignID);
	// if (pathways != null) {
	// mappedPathways.addAll(pathways);
	// }
	// }
	//
	// setFilteredItems(mappedPathways);
	// }

	@Override
	protected void setContent() {
		List<PathwayGraph> pathways = new ArrayList<>(PathwayManager.get().getAllItems());
		// Collections.sort(pathways, new Comparator<PathwayGraph>() {
		//
		// @Override
		// public int compare(PathwayGraph arg0, PathwayGraph arg1) {
		// return arg0.getLabel().toLowerCase().compareTo(arg1.getLabel().toLowerCase());
		// }
		// });

		for (final PathwayGraph pathway : pathways) {
			addTextElement(pathway.getLabel(), pathway);
			// ActionBasedContextMenuItem contextMenuItem = new ActionBasedContextMenuItem("Apply Filter", new
			// Runnable() {
			// @Override
			// public void run() {
			//
			//
			// }
			// });
			// itemList.addContextMenuItem(item, contextMenuItem);
		}

	}

	@Override
	public IDType getBroadcastingIDType() {

		return IDType.getIDType(EGeneIDTypes.DAVID.name());
	}

	@Override
	public Set<Object> getBroadcastingIDsFromElementID(Object elementID) {
		Set<Object> ids = PathwayManager.get().getPathwayGeneIDs((PathwayGraph) elementID,
				IDType.getIDType(EGeneIDTypes.DAVID.name()));
		return ids;
	}

	@Override
	public Set<Object> getElementIDsFromBroadcastingID(Integer broadcastingID) {
		Set<PathwayGraph> pathways = PathwayManager.get().getPathwayGraphsByGeneID(getBroadcastingIDType(),
				broadcastingID);

		Set<Object> elementIDs = new HashSet<>(pathways != null ? pathways.size() : 0);
		if (pathways != null) {
			for (PathwayGraph pathway : pathways) {
				elementIDs.add(pathway);
			}
		}
		return elementIDs;
	}

	@Override
	public IDType getMappingIDType() {
		return getBroadcastingIDType();
	}

	// @Override
	// protected List<AContextMenuItem> getContextMenuItems() {
	// List<AContextMenuItem> items = super.getContextMenuItems();
	//
	// element.setRenderer(GLRenderers.fillRect(Color.RED));
	//
	// return items;
	// }

	@Override
	public void showDetailView() {
		Set<NestableItem> selectedItems = column.getSelectedItems();

		if (selectedItems.size() != 1)
			return;

		PathwayGraph pathway = (PathwayGraph) selectedItems.iterator().next().getElementData().iterator().next();

		PathwayElement pathwayElement = new PathwayElement("dummy_eventspace");
		PathwayTextureRepresentation representation = new PathwayTextureRepresentation(pathway);
		pathwayElement.setPathwayRepresentation(representation);
		pathwayElement.addForegroundAugmentation(new CompoundAugmentation(representation, getRelationshipExplorer()));

		// FIXME: hacky, we do not know what id type the gene column has...
		Set<IEntityCollection> geneCollections = relationshipExplorer.getCollectionsWithBroadcastIDType(IDType
				.getIDType(EGeneIDTypes.ENTREZ_GENE_ID.name()));
		if (geneCollections.isEmpty())
			return;

		pathwayElement.addForegroundAugmentation(new MultiVertexHighlightAugmentation(representation, geneCollections
				.iterator().next(), relationshipExplorer));

		relationshipExplorer.showDetailView(this, pathwayElement, pathway);
	}

	@Override
	protected GLElement createElement(Object elementID) {
		return createTextItem(((PathwayGraph) elementID).getLabel());
	}
}
