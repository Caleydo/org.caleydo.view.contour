/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.pathway;

import java.util.Set;

import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLZoomPanContainer;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.pathway.v2.ui.PathwayElement;
import org.caleydo.view.pathway.v2.ui.PathwayTextureRepresentation;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.detail.DetailViewWindow;
import org.caleydo.view.relationshipexplorer.ui.detail.IDetailViewFactory;

/**
 * @author Christian
 *
 */
public class PathwayDetailViewFactory implements IDetailViewFactory {

	protected final ConTourElement relationshipExplorer;

	public PathwayDetailViewFactory(ConTourElement relationshipExplorer) {
		this.relationshipExplorer = relationshipExplorer;
	}

	@Override
	public GLElement create(IEntityCollection collection, DetailViewWindow window) {

		Set<Object> selectedElements = collection.getSelectedElementIDs();
		Set<Object> highlightedElements = collection.getHighlightElementIDs();
		if (selectedElements.isEmpty() && highlightedElements.isEmpty())
			return null;

		PathwayGraph pathway = (PathwayGraph) collection.getSelectedElementIDs().iterator().next();
		if (selectedElements.size() > 1) {
			pathway = (PathwayGraph) highlightedElements.iterator().next();
		}

		PathwayElement pathwayElement = new PathwayElement("dummy_eventspace");
		PathwayTextureRepresentation representation = new PathwayTextureRepresentation(pathway);
		representation.setMinHeight(pathway.getHeight());
		representation.setMinWidth(pathway.getWidth());
		pathwayElement.setPathwayRepresentation(representation);
		CompoundGroupPathwayAugmentation aug = new CompoundGroupPathwayAugmentation(representation,
				relationshipExplorer);
		pathwayElement.addBackgroundAugmentation(aug);

		// FIXME: hacky, we do not know what id type the gene column has...
		Set<IEntityCollection> geneCollections = relationshipExplorer.getCollectionsWithBroadcastIDType(IDType
				.getIDType(EGeneIDTypes.ENTREZ_GENE_ID.name()));
		if (geneCollections.isEmpty())
			return null;

		pathwayElement.addForegroundAugmentation(new MultiVertexHighlightAugmentation(representation, geneCollections
				.iterator().next(), relationshipExplorer));

		window.clearTitleElements();
		window.addShowFilteredItems(aug, false);

		GLZoomPanContainer container = new GLZoomPanContainer();
		container.add(pathwayElement);
		container.setBackgroundColor(Color.TRANSPARENT);
		container.setScaleLimits(0.1f, 1f);

		// container.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(new Vec2f(300, 600)));

		return container;
	}

}
