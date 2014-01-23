/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;

/**
 * @author Christian
 *
 */
public class PathwayContentProvider extends ATextualContentProvider {

	protected List<PathwayGraph> pathways = new ArrayList<>();

	public PathwayContentProvider() {
		pathways.addAll(PathwayManager.get().getAllItems());
		Collections.sort(pathways, new Comparator<PathwayGraph>() {

			@Override
			public int compare(PathwayGraph arg0, PathwayGraph arg1) {
				return arg0.getLabel().toLowerCase().compareTo(arg1.getLabel().toLowerCase());
			}
		});

		items.clear();
		for(PathwayGraph pathway : pathways) {
			addItem(pathway.getLabel());
		}
	}

	@Override
	public String getLabel() {
		return "Pathways";
	}

}
