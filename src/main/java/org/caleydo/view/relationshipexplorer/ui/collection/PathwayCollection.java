/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.collection;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.id.IDType;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.column.factory.ColumnFactories;
import org.caleydo.view.relationshipexplorer.ui.column.factory.IColumnFactory;
import org.caleydo.view.relationshipexplorer.ui.detail.pathway.PathwayDetailViewFactory;

/**
 * @author Christian
 *
 */
public class PathwayCollection extends AEntityCollection {

	public PathwayCollection(IElementIDProvider elementIDProvider, ConTourElement relationshipExplorer) {
		super(relationshipExplorer);
		if (elementIDProvider == null)
			elementIDProvider = getDefaultElementIDProvider();
		this.allElementIDs.addAll(elementIDProvider.getElementIDs());
		filteredElementIDs.addAll(allElementIDs);
		setLabel("Pathways");
		detailViewFactory = new PathwayDetailViewFactory(relationshipExplorer);
	}

	@Override
	public IDType getBroadcastingIDType() {

		return IDType.getIDType(EGeneIDTypes.DAVID.name());
	}

	@Override
	protected Set<Object> getBroadcastIDsFromElementID(Object elementID) {
		Set<Object> ids = PathwayManager.get().getPathwayGeneIDs((PathwayGraph) elementID,
				IDType.getIDType(EGeneIDTypes.DAVID.name()));
		return ids;
	}

	@Override
	protected Set<Object> getElementIDsFromBroadcastID(Object broadcastingID) {
		Set<PathwayGraph> pathways = PathwayManager.get().getPathwayGraphsByGeneID(getBroadcastingIDType(),
				(Integer) broadcastingID);

		Set<Object> elementIDs = new HashSet<>(pathways != null ? pathways.size() : 0);
		if (pathways != null) {
			for (PathwayGraph pathway : pathways) {
				elementIDs.add(pathway);
			}
		}
		return elementIDs;
	}

	@Override
	protected IColumnFactory getDefaultColumnFactory() {
		return ColumnFactories.createDefaultPathwayColumnFactory(this, relationshipExplorer);
	}

	@Override
	public IDType getMappingIDType() {
		return getBroadcastingIDType();
	}

	public static IElementIDProvider getDefaultElementIDProvider() {
		return new IElementIDProvider() {
			@Override
			public Set<Object> getElementIDs() {
				return new HashSet<Object>(PathwayManager.get().getAllItems());
			}
		};
	}

}
