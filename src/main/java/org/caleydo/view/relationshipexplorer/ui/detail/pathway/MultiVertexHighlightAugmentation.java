/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.pathway;

import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.IVertexRepSelectionListener;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.pathway.v2.ui.augmentation.APerVertexAugmentation;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.ESetOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.MappingHighlightUpdateOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.SelectionBasedHighlightOperation;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.CompositeContextMenuCommand;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.ContextMenuCommandEvent;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.FilterCommand;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.IContextMenuCommand;
import org.caleydo.view.relationshipexplorer.ui.util.MultiSelectionUtil;
import org.caleydo.view.relationshipexplorer.ui.util.MultiSelectionUtil.IMultiSelectionHandler;

/**
 * Augmentation that highlights elements
 *
 * @author Christian
 *
 */
public class MultiVertexHighlightAugmentation extends APerVertexAugmentation implements IVertexRepSelectionListener,
		IMultiSelectionHandler<PathwayVertexRep>, IEntityRepresentation {

	protected Set<PathwayVertexRep> selectedVertexReps = new HashSet<>();
	protected Set<PathwayVertexRep> highlightedVertexReps = new HashSet<>();
	// protected AEntityColumn referenceColumn;
	protected IEntityCollection geneCollection;
	protected RelationshipExplorerElement relationshipExplorer;
	protected int historyID;

	/**
	 * @param pathwayRepresentation
	 */
	public MultiVertexHighlightAugmentation(IPathwayRepresentation pathwayRepresentation,
			IEntityCollection geneCollection, RelationshipExplorerElement relationshipExplorer) {
		super(pathwayRepresentation);
		this.geneCollection = geneCollection;
		this.relationshipExplorer = relationshipExplorer;
		geneCollection.addEntityRepresentation(this);
		pathwayRepresentation.addVertexRepSelectionListener(this);
		this.historyID = relationshipExplorer.getHistory().registerHistoryObject(this);
	}

	@Override
	public void onSelect(PathwayVertexRep vertexRep, Pick pick) {
		boolean update = MultiSelectionUtil.handleSelection(pick, vertexRep, this);
		if (update) {
			repaint();
			propagateSelection();
		}
		update = MultiSelectionUtil.handleHighlight(pick, vertexRep, this);

		if (update) {
			repaint();
			propagateHighlight();
		}

		if (pick.getPickingMode() == PickingMode.RIGHT_CLICKED) {
			ContextMenuCreator contextMenuCreator = new ContextMenuCreator();
			IContextMenuCommand selectionCommand = new IContextMenuCommand() {

				@Override
				public void execute() {
					propagateSelection();
				}

			};
			IContextMenuCommand replaceCommand = new FilterCommand(ESetOperation.REPLACE, this, relationshipExplorer);
			IContextMenuCommand intersectionCommand = new FilterCommand(ESetOperation.INTERSECTION, this,
					relationshipExplorer);
			IContextMenuCommand unionCommand = new FilterCommand(ESetOperation.UNION, this, relationshipExplorer);

			contextMenuCreator.add(new GenericContextMenuItem("Replace", new ContextMenuCommandEvent(
					new CompositeContextMenuCommand(replaceCommand, selectionCommand)).to(this)));
			contextMenuCreator.add(new GenericContextMenuItem("Reduce", new ContextMenuCommandEvent(
					new CompositeContextMenuCommand(intersectionCommand, selectionCommand)).to(this)));
			contextMenuCreator.add(new GenericContextMenuItem("Add", new ContextMenuCommandEvent(
					new CompositeContextMenuCommand(unionCommand, selectionCommand)).to(this)));

			context.getSWTLayer().showContextMenu(contextMenuCreator);
		}
	}

	@Override
	protected void renderVertexAugmentation(GLGraphics g, float w, float h, PathwayVertexRep vertexRep, Rect bounds) {

		renderHighlight(g, bounds, SelectionType.SELECTION.getColor(), SelectionType.MOUSE_OVER.getColor(), vertexRep,
				selectedVertexReps.contains(vertexRep), highlightedVertexReps.contains(vertexRep));

	}

	protected void renderHighlight(GLGraphics g, Rect bounds, Color selectionColor, Color highlightColor,
			PathwayVertexRep vertexRep, boolean isSelected, boolean isHighlight) {
		if (!isSelected && !isHighlight)
			return;

		g.gl.glPushAttrib(GL2.GL_LINE_BIT);
		g.gl.glEnable(GL.GL_BLEND);
		g.gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		g.lineWidth(3);

		// g.drawRect(bounds.x(), bounds.y(), bounds.width() + 1, bounds.height());
		if (isHighlight || isSelected) {
			Color primaryColor = selectionColor;
			Color secondaryColor = selectionColor;

			if (isHighlight) {
				if (!isSelected) {
					primaryColor = highlightColor;
				}
				secondaryColor = highlightColor;
			}

			GL2 gl = g.gl;
			g.color(primaryColor);
			gl.glBegin(GL.GL_LINE_LOOP);
			gl.glVertex3f(bounds.x(), bounds.y() + bounds.height(), g.z());
			gl.glVertex3f(bounds.x() + bounds.width() + 1, bounds.y() + bounds.height(), g.z());
			g.color(secondaryColor);
			gl.glVertex3f(bounds.x() + bounds.width() + 1, bounds.y(), g.z());
			gl.glVertex3f(bounds.x(), bounds.y(), g.z());
			gl.glEnd();
			// g.gl.glPushAttrib(GL2.GL_LINE_BIT);
			// g.color(highlightColor).lineWidth(3).drawRect(0, 0, w, h);
			// g.gl.glPopAttrib();
		}

		g.gl.glPopAttrib();
	}

	public void propagateSelection() {
		Set<Object> selectedElementIDs = getGeneCollectionElementIDs(selectedVertexReps);

		SelectionBasedHighlightOperation c = new SelectionBasedHighlightOperation(getHistoryID(), selectedElementIDs,
				geneCollection.getBroadcastingIDsFromElementIDs(selectedElementIDs), relationshipExplorer);

		c.execute();

		relationshipExplorer.getHistory().addHistoryCommand(c, Color.SELECTION_ORANGE);

		// geneCollection.setSelectedItems(selectedElementIDs, this);
		//
		// relationshipExplorer.applyIDMappingUpdate(
		// new MappingSelectionUpdateOperation(
		// geneCollection.getBroadcastingIDsFromElementIDs(selectedElementIDs), this), true);
	}

	public void propagateHighlight() {
		Set<Object> highlightElementIDs = getGeneCollectionElementIDs(highlightedVertexReps);

		geneCollection.setHighlightItems(highlightElementIDs, this);

		relationshipExplorer.applyIDMappingUpdate(
				new MappingHighlightUpdateOperation(geneCollection
						.getBroadcastingIDsFromElementIDs(highlightElementIDs), this), false);
	}

	@Override
	public boolean isSelected(PathwayVertexRep object) {
		return selectedVertexReps.contains(object);
	}

	@Override
	public void removeFromSelection(PathwayVertexRep object) {
		selectedVertexReps.remove(object);

	}

	@Override
	public void addToSelection(PathwayVertexRep object) {
		selectedVertexReps.add(object);

	}

	@Override
	public void setSelection(PathwayVertexRep object) {
		selectedVertexReps.clear();
		addToSelection(object);
	}

	public Set<Object> getGeneCollectionElementIDs(Set<PathwayVertexRep> vertexReps) {
		Set<Object> davidIDs = new HashSet<>();
		for (PathwayVertexRep v : vertexReps) {
			davidIDs.addAll(v.getDavidIDs());
		}

		return geneCollection.getElementIDsFromForeignIDs(davidIDs, IDType.getIDType(EGeneIDTypes.DAVID.name()));
	}

	@ListenTo(sendToMe = true)
	public void onHandleContextMenuOperation(ContextMenuCommandEvent event) {
		event.getCommand().execute();
	}

	// @Override
	// public void updateSelectionMappings(IEntityRepresentation srcRep) {
	// // nothing to do
	// }

	private void selectVerticesFromForeignIDs(Set<Object> foreignIDs, IDType foreignIDType,
			Set<PathwayVertexRep> vertexSet) {

		IDType davidIDType = IDType.getIDType(EGeneIDTypes.DAVID.name());
		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(davidIDType);
		Set<Object> selectedDavidIDs = mappingManager.getIDTypeMapper(foreignIDType, davidIDType).apply(foreignIDs);
		vertexSet.clear();
		for (PathwayVertexRep v : pathwayRepresentation.getPathway().vertexSet()) {
			for (Integer davidID : v.getDavidIDs()) {
				if (selectedDavidIDs.contains(davidID)) {
					vertexSet.add(v);
					break;
				}
			}
		}
		repaint();
	}

	@Override
	protected void takeDown() {
		geneCollection.removeEntityRepresentation(this);
		relationshipExplorer.getHistory().unregisterHistoryObject(getHistoryID());
		super.takeDown();
	}

	@Override
	public boolean isHighlight(PathwayVertexRep vertexRep) {
		return highlightedVertexReps.contains(vertexRep);
	}

	@Override
	public void setHighlight(PathwayVertexRep vertexRep) {
		highlightedVertexReps.clear();
		highlightedVertexReps.add(vertexRep);
	}

	@Override
	public void removeHighlight(PathwayVertexRep vertexRep) {
		highlightedVertexReps.remove(vertexRep);

	}

	@Override
	public void updateMappings(IEntityRepresentation srcRep) {

	}

	@Override
	public IEntityCollection getCollection() {
		return geneCollection;
	}

	@Override
	public void selectionChanged(Set<Object> selectedElementIDs, IEntityRepresentation srcRep) {
		if (srcRep == this)
			return;

		selectVerticesFromForeignIDs(geneCollection.getBroadcastingIDsFromElementIDs(selectedElementIDs),
				geneCollection.getBroadcastingIDType(), selectedVertexReps);

	}

	@Override
	public void highlightChanged(Set<Object> highlightElementIDs, IEntityRepresentation srcRep) {
		if (srcRep == this)
			return;

		selectVerticesFromForeignIDs(geneCollection.getBroadcastingIDsFromElementIDs(highlightElementIDs),
				geneCollection.getBroadcastingIDType(), highlightedVertexReps);

	}

	@Override
	public void filterChanged(Set<Object> filteredElementIDs, IEntityRepresentation srcRep) {
		// nothing to do

	}

	@Override
	public int getHistoryID() {
		return historyID;
	}

}
