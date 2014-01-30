/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.contextmenu.ActionBasedContextMenuItem;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;

/**
 * @author Christian
 *
 */
public class PathwayColumn extends ATextColumn {

	public PathwayColumn() {

	}

	@ListenTo
	public void onApplyIDFilter(IDFilterEvent event) {
		if (event.getSender() == this)
			return;
		Set<?> foreignIDs = event.getIds();
		IDType foreignIDType = event.getIdType();
		Set<Object> mappedPathways = new HashSet<>();
		for (Object foreignID : foreignIDs) {
			Set<PathwayGraph> pathways = PathwayManager.get().getPathwayGraphsByGeneID(foreignIDType,
					(Integer) foreignID);
			if (pathways != null) {
				mappedPathways.addAll(pathways);
			}
		}

		setFilteredItems(mappedPathways);
	}

	@Override
	public String getLabel() {
		return "Pathways";
	}

	@Override
	protected void setContent() {
		List<PathwayGraph> pathways = new ArrayList<>(PathwayManager.get().getAllItems());
		Collections.sort(pathways, new Comparator<PathwayGraph>() {

			@Override
			public int compare(PathwayGraph arg0, PathwayGraph arg1) {
				return arg0.getLabel().toLowerCase().compareTo(arg1.getLabel().toLowerCase());
			}
		});

		for (final PathwayGraph pathway : pathways) {
			MinSizeTextElement item = addTextElement(pathway.getLabel(), pathway);

			// item.onPick(new IPickingListener() {
			//
			// @Override
			// public void pick(Pick pick) {
			// if (pick.getPickingMode() == PickingMode.CLICKED) {
			// // IDType davidIDType = IDType.getIDType(EGeneIDTypes.DAVID.name());
			// // IDFilterEvent event = new IDFilterEvent(PathwayManager.getPathwayGeneIDs(pathway,
			// // IDType.getIDType(EGeneIDTypes.DAVID.name())), davidIDType);
			// // event.setSender(PathwayContentProvider.this);
			// // EventPublisher.trigger(event);
			// //
			// // setFilteredItems(Sets.newHashSet(pathway));
			// }
			//
			// }
			// });
			ActionBasedContextMenuItem contextMenuItem = new ActionBasedContextMenuItem("Apply Filter", new Runnable() {
				@Override
				public void run() {
					IDType davidIDType = IDType.getIDType(EGeneIDTypes.DAVID.name());
					Set<Object> ids = new HashSet<>();
					Set<Object> pathways = new HashSet<>();
					for (GLElement element : itemList.getSelectedElements()) {
						PathwayGraph pw = (PathwayGraph) mapIDToElement.inverse().get(element);
						ids.addAll(PathwayManager.get().getPathwayGeneIDs(pw,
								IDType.getIDType(EGeneIDTypes.DAVID.name())));
						pathways.add(pw);
					}

					IDFilterEvent event = new IDFilterEvent(ids, davidIDType);
					event.setSender(PathwayColumn.this);
					EventPublisher.trigger(event);
					setFilteredItems(pathways);

				}
			});
			itemList.addContextMenuItem(item, contextMenuItem);
		}

	}
}
