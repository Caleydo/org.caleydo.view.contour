/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.relationshipexplorer.ui.EntityColumn.IEntityColumnContentProvider;

/**
 * @author Christian
 *
 */
public class PathwayContentProvider implements IEntityColumnContentProvider {

	protected List<PathwayGraph> pathways = new ArrayList<>();
	protected List<GLElement> items = new ArrayList<>();

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
			PathwayTitleRenderer renderer = new PathwayTitleRenderer(pathway);
			renderer.setSize(200, 16);
			items.add(renderer);
		}
	}

	@Override
	public Vec2f getMinSize() {
		return new Vec2f(200, items.size() * 16 + (items.size() - 1) * 2);
	}

	@Override
	public List<GLElement> getContent() {

		return items;
	}

}
