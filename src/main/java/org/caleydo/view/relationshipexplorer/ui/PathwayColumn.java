/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.id.IDType;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;

/**
 * @author Christian
 *
 */
public class PathwayColumn extends ATextColumn {


	/**
	 * @param relationshipExplorer
	 */
	public PathwayColumn(RelationshipExplorerElement relationshipExplorer) {
		super(relationshipExplorer);
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
	public String getLabel() {
		return "Pathways";
	}

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

	// @Override
	// protected AContextMenuItem getFilterContextMenuItem() {
	// ActionBasedContextMenuItem contextMenuItem = new ActionBasedContextMenuItem("Apply Filter", new Runnable() {
	// @Override
	// public void run() {
	// Set<Object> ids = new HashSet<>();
	// Set<Object> pathways = new HashSet<>();
	// for (GLElement element : itemList.getSelectedElements()) {
	// PathwayGraph pw = (PathwayGraph) mapIDToElement.inverse().get(element);
	// ids.addAll(getBroadcastingIDsFromElementID(pw));
	// pathways.add(pw);
	// }
	//
	//
	// IDUpdateEvent event = new IDUpdateEvent(ids, getBroadcastingIDType(), EUpdateType.FILTER);
	// event.setSender(PathwayColumn.this);
	// EventPublisher.trigger(event);
	// setFilteredItems(pathways);
	//
	//
	// Set<Object> broadcastIDs = new HashSet<>();
	// Set<Object> elementIDs = new HashSet<>();
	// for (GLElement element : itemList.getSelectedElements()) {
	// Object elementID = mapIDToElement.inverse().get(element);
	// elementIDs.add(elementID);
	// broadcastIDs.addAll(getBroadcastingIDsFromElementID(elementID));
	// }
	// // Avoid direct calling of setFilteredItems due to synchronization issues
	// EventPublisher.trigger(new FilterEvent(elementIDs).to(AEntityColumn.this));
	//
	// triggerIDUpdate(broadcastIDs, EUpdateType.FILTER);
	// }
	// });
	// return contextMenuItem;
	// }

	@Override
	protected IDType getBroadcastingIDType() {

		return IDType.getIDType(EGeneIDTypes.DAVID.name());
	}

	@Override
	protected Set<Object> getBroadcastingIDsFromElementID(Object elementID) {
		Set<Object> ids = PathwayManager.get().getPathwayGeneIDs((PathwayGraph) elementID,
				IDType.getIDType(EGeneIDTypes.DAVID.name()));
		return ids;
	}

	@Override
	protected Set<Object> getElementIDsFromBroadcastingID(Integer broadcastingID) {
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
	protected IDType getMappingIDType() {
		return getBroadcastingIDType();
	}
}
