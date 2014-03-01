/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.pathway;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.pathway.v2.ui.PathwayTextureRepresentation;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.column.GroupCollection;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityCollection;

/**
 * @author Christian
 *
 */
public class CompoundAugmentation extends GLElement {
	protected IPathwayRepresentation pathwayRepresentation;

	private int padding = 50;

	private IDCategory geneIDCategory = IDCategory.getIDCategory(EGeneIDTypes.GENE.name());
	private IDType davidIDType = IDType.getIDType(EGeneIDTypes.DAVID.name());
	private IDType compoundIDType = IDType.getIDType("COMPOUND_ID");

	private IDMappingManager idManager = IDMappingManagerRegistry.get().getIDMappingManager(geneIDCategory);

	private RelationshipExplorerElement filteredMapping;

	public CompoundAugmentation(IPathwayRepresentation pathwayRepresentation,
			RelationshipExplorerElement filteredMapping) {
		this.pathwayRepresentation = pathwayRepresentation;
		((PathwayTextureRepresentation) pathwayRepresentation).setPadding(new GLPadding(padding, padding, padding,
				padding));
		this.filteredMapping = filteredMapping;
	}

	private void updateMapping() {
		// System.out.println("Vertices + " + );

		Set<Integer> davidIDs = new HashSet<>();
		for (PathwayVertexRep vertex : pathwayRepresentation.getPathway().vertexSet()) {
			davidIDs.addAll(vertex.getDavidIDs());
		}

		Set<Object> compoundIDs = new HashSet<>();
		for (Integer davidID : davidIDs) {
			Set<Object> currentCompounds = idManager.getIDAsSet(davidIDType, compoundIDType, davidID);
			if (currentCompounds != null)
				compoundIDs.addAll(currentCompounds);
		}

		System.out.println(compoundIDs.toString() + compoundIDs.size());

		GroupCollection groupCollection = null;

		for (IEntityCollection collection : filteredMapping.getEntityCollections()) {
			if (collection instanceof GroupCollection)
				groupCollection = (GroupCollection) collection;
		}
		if (groupCollection == null)
			return;

		Set<Object> groups = groupCollection.getElementIDsFromForeignIDs(compoundIDs, compoundIDType);


		System.out.println(groups.size());

	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {


		Rect pathwayBounds = pathwayRepresentation.getPathwayBounds();
		g.color(Color.RED)
				.drawRect(pathwayBounds.x(), pathwayBounds.y(), pathwayBounds.width(), pathwayBounds.height());

		g.color(Color.BLUE).drawRect(0, 0, 20, 20);

		// for (PathwayGraph pathway : pathwayRepresentation.getPathways()) {
		// for (PathwayVertexRep vertexRep : pathway.vertexSet()) {
		// List<Rect> boundsList = pathwayRepresentation.getVertexRepsBounds(vertexRep);
		// for (Rect bounds : boundsList) {
		// g.fillRect(bounds);
		// }
		// }
		// }
	}
}
