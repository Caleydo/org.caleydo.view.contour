/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.pathway;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
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
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.MappingHighlightUpdateOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.SelectionBasedHighlightOperation;

/**
 * @author Alexander Lex
 *
 */

public class CompoundAugmentation extends GLElement implements IEntityRepresentation {

	private class GroupData {
		Set<Object> allCompounds = new HashSet<>();
		List<Pair<Object, List<Integer>>> containedCompounds = new ArrayList<>();

		Object groupID;

		private GroupData(Set<Object> fingerPrints) {

			// Group group = (Group) groupID;
			this.groupID = fingerPrints;
			for (Object fingerPrint : fingerPrints) {
				Set<Object> compounds = idManager.getIDAsSet(groupIDType, compoundIDType, fingerPrint);
				if (compounds != null)
					allCompounds.addAll(compounds);
			}

			if (allCompounds == null) {
				System.out.println("blu");
			}
			for (Object compoundID : allCompounds) {
				if (compoundIDs.contains(compoundID)) {
					Set<Integer> mappedDavids = idManager.getIDAsSet(compoundIDType, davidIDType, compoundID);
					List<Integer> inPathwayDavidsForCompound = new ArrayList<>();
					for (Integer david : mappedDavids) {
						if (davidIDs.contains(david)) {
							inPathwayDavidsForCompound.add(david);
						}
					}
					containedCompounds.add(new Pair<Object, List<Integer>>(compoundID, inPathwayDavidsForCompound));
				}
			}
		}

	}

	protected IPathwayRepresentation pathwayRepresentation;

	protected GroupCollection groupCollection;
	private final int historyID;

	private int padding = 50;

	private IDCategory geneIDCategory = IDCategory.getIDCategory(EGeneIDTypes.GENE.name());
	private IDType davidIDType = IDType.getIDType(EGeneIDTypes.DAVID.name());
	private IDType compoundIDType = IDType.getIDType("COMPOUND_ID");
	private IDType groupIDType;
	private Set<Object> compoundIDs;

	private IDMappingManager idManager = IDMappingManagerRegistry.get().getIDMappingManager(geneIDCategory);

	private RelationshipExplorerElement filteredMapping;

	private List<GroupData> clusterData;

	private Set<Integer> davidIDs;

	private GLElementContainer container;

	private int overallCompoundSize = 0;

	public CompoundAugmentation(IPathwayRepresentation pathwayRepresentation,
			RelationshipExplorerElement filteredMapping) {
		this.pathwayRepresentation = pathwayRepresentation;
		((PathwayTextureRepresentation) pathwayRepresentation).setPadding(new GLPadding(padding, padding, padding,
				padding));
		this.filteredMapping = filteredMapping;
		this.historyID = filteredMapping.getHistory().registerHistoryObject(this);

		updateMapping();
		setUpLayout();
	}

	private void updateMapping() {
		// System.out.println("Vertices + " + );

		davidIDs = new HashSet<>();
		for (PathwayVertexRep vertex : pathwayRepresentation.getPathway().vertexSet()) {
			davidIDs.addAll(vertex.getDavidIDs());
		}

		compoundIDs = new HashSet<>();
		for (Integer davidID : davidIDs) {
			Set<Object> currentCompounds = idManager.getIDAsSet(davidIDType, compoundIDType, davidID);
			if (currentCompounds != null)
				compoundIDs.addAll(currentCompounds);
		}

		for (IEntityCollection collection : filteredMapping.getEntityCollections()) {
			if (collection instanceof GroupCollection)
				groupCollection = (GroupCollection) collection;
		}
		if (groupCollection == null)
			return;

		groupCollection.addEntityRepresentation(this);
		Set<Object> groups = groupCollection.getElementIDsFromForeignIDs(compoundIDs, compoundIDType);
		groupIDType = groupCollection.getBroadcastingIDType();

		clusterData = new ArrayList<>(groups.size());

		for (Object group : groups) {
			GroupData gd = new GroupData(groupCollection.getBroadcastingIDsFromElementID(group));
			clusterData.add(gd);
			overallCompoundSize += gd.containedCompounds.size();

		}

	}

	protected void propagateGroupSelection(Set<Object> groups) {
		SelectionBasedHighlightOperation c = new SelectionBasedHighlightOperation(getHistoryID(), groups,
				groupCollection.getBroadcastingIDsFromElementIDs(groups), filteredMapping);
		c.execute();
		filteredMapping.getHistory().addHistoryCommand(c, Color.SELECTION_ORANGE);
	}

	protected void propagateGroupHighlight(Set<Object> groups) {
		groupCollection.setHighlightItems(groups);

		filteredMapping.applyIDMappingUpdate(new MappingHighlightUpdateOperation(groupCollection
				.getBroadcastingIDsFromElementIDs(groups), this));
	}

	private void setUpLayout() {

		// GLFlowLayout layout = new GLFlowLayout(false, 2, GLPadding.ONE);
		// container = new GLElementContainer(layout);
		//
		// Rect pathwayBounds = pathwayRepresentation.getPathwayBounds();
		//
		// container.setSize(padding, pathwayBounds.height());
		// container.setLocation(0, 0);
		//

		Comparator<GroupData> comparator = new Comparator<GroupData>() {
			@Override
			public int compare(GroupData c1, GroupData c2) {
				return c2.containedCompounds.size() - c1.containedCompounds.size();
			}
		};

		Collections.sort(clusterData, comparator);
		// for (GroupData gd : clusterData) {
		// // container.add(new GLElement(new CompundGroupRenderer()));
		//
		// }

		// for

	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {

		Rect pathwayBounds = pathwayRepresentation.getPathwayBounds();
		g.color(Color.RED)
				.drawRect(pathwayBounds.x(), pathwayBounds.y(), pathwayBounds.width(), pathwayBounds.height());

		float currentY = 0;

		float heightPerCompound = (pathwayBounds.height() - clusterData.size() * 2) / overallCompoundSize;
		for (GroupData gd : clusterData) {
			float height = gd.containedCompounds.size() * heightPerCompound;
			g.color(Color.BLUE).drawRect(0, currentY, 20, height);
			currentY += height + 2;
		}
		// container.render(g);

	}

	@Override
	public int getHistoryID() {
		return historyID;
	}

	@Override
	public void selectionChanged(Set<Object> selectedElementIDs, IEntityRepresentation srcRep) {
		// TODO Auto-generated method stub

	}

	@Override
	public void highlightChanged(Set<Object> highlightElementIDs, IEntityRepresentation srcRep) {
		// TODO Auto-generated method stub

	}

	@Override
	public void filterChanged(Set<Object> filteredElementIDs, IEntityRepresentation srcRep) {
		// TODO Auto-generated method stub

	}

	@Override
	public IEntityCollection getCollection() {
		return groupCollection;
	}

	@Override
	protected void takeDown() {
		filteredMapping.getHistory().unregisterHistoryObject(historyID);
		groupCollection.removeEntityRepresentation(this);
		super.takeDown();
	}
}
