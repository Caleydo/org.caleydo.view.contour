/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.id.IDType;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;

/**
 * @author Christian
 *
 */
public class PathwayCollection extends AEntityCollection {

	public PathwayCollection(RelationshipExplorerElement relationshipExplorer) {
		super(relationshipExplorer);
		this.allElementIDs.addAll(PathwayManager.get().getAllItems());
		filteredElementIDs.addAll(allElementIDs);
		setLabel("Pathways");
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
	public IColumnModel createColumnModel() {
		return new PathwayColumn(this, relationshipExplorer);
	}

}
