/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.collection.idprovider;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;

import com.google.common.collect.Sets;

/**
 * Provides all pathways except those with specified names. Uses case insensitive comparison
 *
 * @author Christian
 *
 */
public class ExcludingPathwayIDProvider implements IElementIDProvider {

	public static final ExcludingPathwayIDProvider NO_METABOLIC_PATHWAY_PROVIDER = new ExcludingPathwayIDProvider(
			Sets.newHashSet("metabolic pathway"));

	private final Set<String> pathwayNames;

	public ExcludingPathwayIDProvider(Set<String> pathwayNames) {
		this.pathwayNames = toLowerCaseNames(pathwayNames);
	}

	@Override
	public Set<Object> getElementIDs() {
		Set<PathwayGraph> allPathways = new HashSet<>(PathwayManager.get().getAllItems());

		Set<Object> filteredPathways = new HashSet<>();
		for (PathwayGraph pathway : allPathways) {

			boolean found = false;
			for (String name : pathwayNames) {
				if (pathway.getLabel().toLowerCase().contains(name)) {
					found = true;
					break;
				}
			}
			if (!found)
				filteredPathways.add(pathway);
		}
		return filteredPathways;
	}

	private Set<String> toLowerCaseNames(Set<String> names) {
		Set<String> lowerCase = new HashSet<>(names.size());
		for (String name : names) {
			lowerCase.add(name.toLowerCase());
		}
		return lowerCase;
	}

}
