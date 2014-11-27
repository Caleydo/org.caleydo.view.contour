/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.collection.idprovider;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.id.IDType;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;

/**
 * Gets all pathways that have a mapping to a specified idtype.
 *
 * @author Christian
 *
 */
public class PathwayMappingBasedIDProvider implements IElementIDProvider {

	private final IDType targetIDType;

	public PathwayMappingBasedIDProvider(IDType targetIDType) {
		this.targetIDType = targetIDType;
	}

	@Override
	public Set<Object> getElementIDs() {

		Set<PathwayGraph> allPathways = new HashSet<>(PathwayManager.get().getAllItems());

		Set<Object> filteredPathways = new HashSet<>();
		for (PathwayGraph pathway : allPathways) {
			Set<Object> targetIDs = PathwayManager.get().getPathwayGeneIDs(pathway, targetIDType);
			if (targetIDs != null && !targetIDs.isEmpty())
				filteredPathways.add(pathway);
		}

		return filteredPathways;
	}

}
