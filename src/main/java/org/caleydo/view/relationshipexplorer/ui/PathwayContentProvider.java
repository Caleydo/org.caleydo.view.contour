/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.contextmenu.ActionBasedContextMenuItem;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;

import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public class PathwayContentProvider extends ATextualContentProvider {

	protected Map<PathwayGraph, MinSizeTextElement> itemMap = new HashMap<>();

	public PathwayContentProvider() {

	}

	@ListenTo
	public void onApplyIDFilter(IDFilterEvent event) {
		if (event.getSender() == this)
			return;
		Set<?> foreignIDs = event.getIds();
		IDType foreignIDType = event.getIdType();
		Set<PathwayGraph> mappedPathways = new HashSet<>();
		for (Object foreignID : foreignIDs) {
			Set<PathwayGraph> pathways = PathwayManager.get().getPathwayGraphsByGeneID(foreignIDType,
					(Integer) foreignID);
			if (pathways != null) {
				mappedPathways.addAll(pathways);
			}
		}

		setFilteredItems(mappedPathways);
	}

	protected void setFilteredItems(Set<PathwayGraph> pathways) {
		for (Entry<PathwayGraph, MinSizeTextElement> entry : itemMap.entrySet()) {

			MinSizeTextElement item = entry.getValue();
			// item.setHighlight(false);
			boolean visible = false;

			if (pathways.contains(entry.getKey())) {
				visible = true;
				// item.setHighlight(true);
				// item.setHighlightColor(SelectionType.SELECTION.getColor());
				entityColumn.getItemList().show(item);
				entityColumn.getItemList().asGLElement().relayout();
			}

			if (!visible) {
				entityColumn.getItemList().hide(item);
				entityColumn.getItemList().asGLElement().relayout();
			}

		}
	}

	@Override
	public String getLabel() {
		return "Pathways";
	}

	@Override
	public void setContent(GLElementList itemList) {
		List<PathwayGraph> pathways = new ArrayList<>(PathwayManager.get().getAllItems());
		Collections.sort(pathways, new Comparator<PathwayGraph>() {

			@Override
			public int compare(PathwayGraph arg0, PathwayGraph arg1) {
				return arg0.getLabel().toLowerCase().compareTo(arg1.getLabel().toLowerCase());
			}
		});

		for (final PathwayGraph pathway : pathways) {
			MinSizeTextElement item = addItem(pathway.getLabel());
			itemMap.put(pathway, item);

			itemList.add(item);

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
					IDFilterEvent event = new IDFilterEvent(PathwayManager.get().getPathwayGeneIDs(pathway,
							IDType.getIDType(EGeneIDTypes.DAVID.name())), davidIDType);
					event.setSender(PathwayContentProvider.this);
					EventPublisher.trigger(event);

					setFilteredItems(Sets.newHashSet(pathway));

				}
			});
			itemList.addContextMenuItem(item, contextMenuItem);
		}

	}

}
