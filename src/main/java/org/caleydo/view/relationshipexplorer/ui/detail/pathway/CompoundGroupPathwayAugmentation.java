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
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.pathway.v2.ui.PathwayTextureRepresentation;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.collection.GroupCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.MappingHighlightUpdateOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.SelectionBasedHighlightOperation;
import org.caleydo.view.relationshipexplorer.ui.detail.IShowFilteredItemsListener;

/**
 * Visualizes compounds and compound cluster on the sides of a pathway.
 *
 * @author Alexander Lex
 * @author Christian Partl
 *
 */

public class CompoundGroupPathwayAugmentation extends GLElementContainer implements IEntityRepresentation,
		IShowFilteredItemsListener {

	public enum ESelectionMode {
		SELECTED, HIGHLGHTED, FILTERED
	}

	class GroupData {
		CompoundGroupVis glRepresentation;
		Set<Object> allCompounds = new HashSet<>();
		List<Pair<Object, List<Integer>>> containedCompounds = new ArrayList<>();

		Object fingerPrintIDs;
		Object group;

		private GroupData(Set<Object> fingerPrints, Object group) {

			this.group = group;
			// Group group = (Group) groupID;
			this.fingerPrintIDs = fingerPrints;
			for (Object fingerPrint : fingerPrints) {
				Set<Object> compounds = idManager.getIDAsSet(groupIDType, compoundIDType, fingerPrint);
				if (compounds != null)
					allCompounds.addAll(compounds);
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
					if (inPathwayDavidsForCompound.size() > maxMappingGenes) {
						maxMappingGenes = inPathwayDavidsForCompound.size();
					}
					containedCompounds.add(new Pair<Object, List<Integer>>(compoundID, inPathwayDavidsForCompound));
				}
			}
		}

		/**
		 * @param element
		 *            setter, see {@link element}
		 */
		public void setGLRepresentation(CompoundGroupVis glRepresentation) {
			this.glRepresentation = glRepresentation;
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

	private List<GroupData> containedGroups;
	private List<GroupData> filteredGroupData = new ArrayList<>();

	private Set<Integer> davidIDs;

	private int overallCompoundSize = 0;
	/** The highest number of genes a compound maps to */
	private int maxMappingGenes = 0;

	private GLSizeRestrictiveFlowLayout layout = new GLSizeRestrictiveFlowLayout(false, 4, new GLPadding(0, 1));
	private GLElementContainer leftClusterContainer = new GLElementContainer(layout);
	private GLElementContainer rightClusterContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout(false, 4,
			new GLPadding(0, 1)));

	private GLElement centerSpacing = new GLElement();

	private CompoundRepresentation compoundRepresentation;

	/** If set only the compounds/clusters that are not filtered are shown. Else, all */
	private boolean respectFilter = false;

	protected class CompoundRepresentation implements IEntityRepresentation {

		protected final int historyId;
		protected final IEntityCollection compoundCollection;

		public CompoundRepresentation() {
			historyId = filteredMapping.getHistory().registerHistoryObject(this);
			Set<IEntityCollection> collections = filteredMapping.getCollectionsWithBroadcastIDType(IDType
					.getIDType("COMPOUND_ID"));
			compoundCollection = collections.iterator().next();
			compoundCollection.addEntityRepresentation(this);
		}

		@Override
		public int getHistoryID() {
			return historyId;
		}

		public void propagateGroupSelection(Set<Object> compoundIDs) {
			SelectionBasedHighlightOperation c = new SelectionBasedHighlightOperation(getHistoryID(), compoundIDs,
					compoundCollection.getBroadcastingIDsFromElementIDs(compoundIDs), filteredMapping);
			c.execute();
			filteredMapping.getHistory().addHistoryCommand(c);
		}

		public void propagateGroupHighlight(Set<Object> compoundIDs) {
			compoundCollection.setHighlightItems(compoundIDs);

			filteredMapping.applyIDMappingUpdate(new MappingHighlightUpdateOperation(compoundCollection
					.getBroadcastingIDsFromElementIDs(compoundIDs), this));
		}

		@Override
		public void selectionChanged(Set<Object> selectedElementIDs, IEntityRepresentation srcRep) {
			updateSelection(ESelectionMode.SELECTED, selectedElementIDs);
		}

		@Override
		public void highlightChanged(Set<Object> highlightElementIDs, IEntityRepresentation srcRep) {
			updateSelection(ESelectionMode.HIGHLGHTED, highlightElementIDs);

		}

		@Override
		public void filterChanged(Set<Object> filteredElementIDs, IEntityRepresentation srcRep) {
			updateSelection(ESelectionMode.FILTERED, filteredElementIDs);

		}

		private void updateSelection(ESelectionMode selectionMode, Set<Object> highlightElementIDs) {
			for (GroupData group : containedGroups) {
				group.glRepresentation.setCompoundHighlighted(selectionMode, highlightElementIDs);

			}
		}

		@Override
		public IEntityCollection getCollection() {

			return compoundCollection;
		}

	}

	public CompoundGroupPathwayAugmentation(IPathwayRepresentation pathwayRepresentation,
			RelationshipExplorerElement filteredMapping) {
		this.pathwayRepresentation = pathwayRepresentation;
		((PathwayTextureRepresentation) pathwayRepresentation).setPadding(new GLPadding(padding, 0, padding, 0));
		this.filteredMapping = filteredMapping;
		this.compoundRepresentation = new CompoundRepresentation();
		this.historyID = filteredMapping.getHistory().registerHistoryObject(this);

		// setRenderer(GLRenderers.drawRect(Color.RED));

		updateMapping();

		this.setLayout(GLLayouts.flowHorizontal(3));
		leftClusterContainer.setSize(padding, Float.NaN);
		rightClusterContainer.setSize(padding, Float.NaN);

		add(new GLElement());
		add(rightClusterContainer);
		// Spacing
		add(centerSpacing);
		add(leftClusterContainer);

		add(new GLElement());

		updateGroups();
	}

	/**
	 * @return the compoundRepresentation, see {@link #compoundRepresentation}
	 */
	public CompoundRepresentation getCompoundRepresentation() {
		return compoundRepresentation;
	}

	private void updateMapping() {
		// System.out.println("Vertices + " + );

		maxMappingGenes = 0;

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

		containedGroups = new ArrayList<>(groups.size());

		for (Object group : groups) {
			GroupData gd = new GroupData(groupCollection.getBroadcastingIDsFromElementID(group), group);
			containedGroups.add(gd);
			overallCompoundSize += gd.containedCompounds.size();
		}

	}

	protected void propagateGroupSelection(Set<Object> groups) {
		SelectionBasedHighlightOperation c = new SelectionBasedHighlightOperation(getHistoryID(), groups,
				groupCollection.getBroadcastingIDsFromElementIDs(groups), filteredMapping);
		c.execute();
		filteredMapping.getHistory().addHistoryCommand(c);
	}

	protected void propagateGroupHighlight(Set<Object> groups) {
		groupCollection.setHighlightItems(groups);

		filteredMapping.applyIDMappingUpdate(new MappingHighlightUpdateOperation(groupCollection
				.getBroadcastingIDsFromElementIDs(groups), this));
	}

	private void updateGroups() {
		List<GroupData> groupList;
		if (respectFilter && filteredGroupData != null) {
			groupList = filteredGroupData;
		} else {
			groupList = containedGroups;
		}

		int gap = 3;

		Comparator<GroupData> comparator = new Comparator<GroupData>() {
			@Override
			public int compare(GroupData c1, GroupData c2) {
				return c2.containedCompounds.size() - c1.containedCompounds.size();
			}
		};

		// assuming a heigth of 1000 pixels:
		double height = 1000;
		double pixelPerCompound = (height - gap * groupList.size()) / overallCompoundSize * 2;
		double pixelStatus = 0;
		Collections.sort(groupList, comparator);
		// leftClusterContainer.setSize(30, 100);
		leftClusterContainer.clear();
		rightClusterContainer.clear();
		for (final GroupData data : groupList) {
			final CompoundGroupVis element = new CompoundGroupVis(this, data, maxMappingGenes, padding);
			data.setGLRepresentation(element);

			pixelStatus += data.containedCompounds.size() * pixelPerCompound + gap;

			if (pixelStatus > 1000) {

				leftClusterContainer.add(element);
			} else {
				rightClusterContainer.add(element);
			}
		}

		// leftClusterContainer.setSize(30, 100);

	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);

	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		super.renderImpl(g, w, h);
		centerSpacing.setSize(pathwayRepresentation.getPathwayBounds().width(), h);
	}

	@Override
	public int getHistoryID() {
		return historyID;
	}

	@Override
	public void selectionChanged(Set<Object> selectedElementIDs, IEntityRepresentation srcRep) {
		updateSelection(ESelectionMode.SELECTED, selectedElementIDs, srcRep);

	}

	@Override
	public void highlightChanged(Set<Object> highlightElementIDs, IEntityRepresentation srcRep) {
		updateSelection(ESelectionMode.HIGHLGHTED, highlightElementIDs, srcRep);

	}

	private void updateSelection(ESelectionMode selected, Set<Object> highlightElementIDs, IEntityRepresentation srcRep) {
		for (GroupData group : containedGroups) {
			group.glRepresentation.setClusterHighlighted(selected, highlightElementIDs);

		}
	}

	@Override
	public void filterChanged(Set<Object> filteredElementIDs, IEntityRepresentation srcRep) {
		respectFilter = true;
		filteredGroupData.clear();
		for (GroupData group : containedGroups) {
			if (filteredElementIDs.contains(group.group)) {
				filteredGroupData.add(group);
			}

		}
		updateGroups();

	}

	@Override
	public IEntityCollection getCollection() {
		return groupCollection;
	}

	@Override
	protected void takeDown() {
		compoundRepresentation.getCollection().removeEntityRepresentation(compoundRepresentation);
		groupCollection.removeEntityRepresentation(this);
		super.takeDown();
	}

	@Override
	public void showFilteredItems(boolean showFilteredItems) {
		respectFilter = showFilteredItems;
		leftClusterContainer.clear();
		rightClusterContainer.clear();
		containedGroups.clear();
		updateGroups();
		updateMapping();
		repaint();
		// TODO: show only filtered items/all items
	}
}
