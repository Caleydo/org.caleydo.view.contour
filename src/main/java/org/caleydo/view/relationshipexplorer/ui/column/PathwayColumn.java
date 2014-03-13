/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.util.Set;

import org.caleydo.core.id.IDType;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.view.pathway.v2.ui.PathwayElement;
import org.caleydo.view.pathway.v2.ui.PathwayTextureRepresentation;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.PathwayCollection;
import org.caleydo.view.relationshipexplorer.ui.detail.pathway.CompoundAugmentation;
import org.caleydo.view.relationshipexplorer.ui.detail.pathway.MultiVertexHighlightAugmentation;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;

/**
 * @author Christian
 *
 */
public class PathwayColumn extends ATextColumn {

	protected final PathwayCollection pathwayCollection;

	/**
	 * @param relationshipExplorer
	 */
	public PathwayColumn(PathwayCollection pathwayCollection, RelationshipExplorerElement relationshipExplorer) {
		super(pathwayCollection, relationshipExplorer);
		this.pathwayCollection = pathwayCollection;
		currentComparator = new CompositeComparator<>(ItemComparators.SELECTED_ITEMS_COMPARATOR, getDefaultComparator());
	}

	@Override
	public void showDetailView() {
		Set<NestableItem> selectedItems = column.getSelectedItems();

		PathwayGraph pathway = (PathwayGraph) selectedItems.iterator().next().getElementData().iterator().next();
		if (selectedItems.size() > 1) {
			pathway = (PathwayGraph) column.getHighlightedItems().iterator().next().getElementData().iterator().next();
		}

		PathwayElement pathwayElement = new PathwayElement("dummy_eventspace");
		PathwayTextureRepresentation representation = new PathwayTextureRepresentation(pathway);
		pathwayElement.setPathwayRepresentation(representation);
		pathwayElement.addForegroundAugmentation(new CompoundAugmentation(representation, getRelationshipExplorer()));

		// FIXME: hacky, we do not know what id type the gene column has...
		Set<IEntityCollection> geneCollections = relationshipExplorer.getCollectionsWithBroadcastIDType(IDType
				.getIDType(EGeneIDTypes.ENTREZ_GENE_ID.name()));
		if (geneCollections.isEmpty())
			return;

		pathwayElement.addForegroundAugmentation(new MultiVertexHighlightAugmentation(representation, geneCollections
				.iterator().next(), relationshipExplorer));

		relationshipExplorer.showDetailView(entityCollection, pathwayElement, pathway);
	}

	// @Override
	// protected GLElement createElement(Object elementID) {
	// return createTextItem(((PathwayGraph) elementID).getLabel());
	// }

	@Override
	public String getText(Object elementID) {
		return ((PathwayGraph) elementID).getLabel();
	}
}
