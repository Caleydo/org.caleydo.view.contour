/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.collection.idprovider;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.datadomain.pathway.manager.PathwayManager;

/**
 * Provides the pathways of specified {@link EPathwayDatabaseType}s.
 *
 * @author Christian
 *
 */
public class PathwayDatabaseIDProvider implements IElementIDProvider {

	private final Set<EPathwayDatabaseType> pathwayDBs;

	public PathwayDatabaseIDProvider(Set<EPathwayDatabaseType> pathwayDBs) {
		this.pathwayDBs = pathwayDBs;
	}

	@Override
	public Set<Object> getElementIDs() {

		Set<Object> pathways = new HashSet<>();

		for (EPathwayDatabaseType dbType : pathwayDBs) {
			pathways.addAll(PathwayManager.get().getPathwaysOfDatabase(dbType));
		}

		return pathways;
	}

}
