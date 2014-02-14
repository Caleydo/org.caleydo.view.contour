/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.pathway;

import java.util.Collection;
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
import org.caleydo.view.relationshipexplorer.ui.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.column.operation.ASetBasedColumnOperation.ESetOperation;
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
		IMultiSelectionHandler<PathwayVertexRep>, IEntityCollection {

	protected Set<PathwayVertexRep> selectedVertexReps = new HashSet<>();
	protected Set<PathwayVertexRep> highlightedVertexReps = new HashSet<>();
	protected AEntityColumn referenceColumn;
	protected boolean updateIsFromMe = false;

	/**
	 * @param pathwayRepresentation
	 */
	public MultiVertexHighlightAugmentation(IPathwayRepresentation pathwayRepresentation, AEntityColumn referenceColumn) {
		super(pathwayRepresentation);
		this.referenceColumn = referenceColumn;
		referenceColumn.getRelationshipExplorer().registerEntityCollection(this);
		pathwayRepresentation.addVertexRepSelectionListener(this);
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
			IContextMenuCommand replaceCommand = new FilterCommand(ESetOperation.REPLACE, this,
					referenceColumn.getRelationshipExplorer());
			IContextMenuCommand intersectionCommand = new FilterCommand(ESetOperation.INTERSECTION, this,
					referenceColumn.getRelationshipExplorer());
			IContextMenuCommand unionCommand = new FilterCommand(ESetOperation.UNION, this,
					referenceColumn.getRelationshipExplorer());

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
		Set<Object> selectedElementIDs = getReferenceColumnElementIDs(selectedVertexReps);
		// Set<Object> filteredElementIDs = referenceColumn.getFilteredElementIDs();

		// Set<Object> selectedFilteredElementIDs = Sets.intersection(selectedElementIDs, filteredElementIDs);
		// Set<Object> selectedFilteredbroadcastIDs = referenceColumn
		// .getBroadcastingIDsFromElementIDs(selectedFilteredElementIDs);
		Set<Object> selectedBroadcastIDs = referenceColumn.getBroadcastingIDsFromElementIDs(selectedElementIDs);
		updateIsFromMe = true;
		SelectionBasedHighlightOperation o = new SelectionBasedHighlightOperation(selectedElementIDs,
				selectedBroadcastIDs, referenceColumn.getRelationshipExplorer());
		o.execute(referenceColumn);
		referenceColumn.getRelationshipExplorer().getHistory().addColumnOperation(referenceColumn, o);
	}

	public void propagateHighlight() {
		updateIsFromMe = true;
		Set<Object> highlightElementIDs = getReferenceColumnElementIDs(highlightedVertexReps);
		Set<Object> highlightBroadcastIDs = referenceColumn.getBroadcastingIDsFromElementIDs(highlightElementIDs);
		referenceColumn.getRelationshipExplorer().applyIDMappingUpdate(
				new MappingHighlightUpdateOperation(highlightBroadcastIDs, referenceColumn), false);
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

	public Set<Object> getReferenceColumnElementIDs(Set<PathwayVertexRep> vertexReps) {
		Set<Object> davidIDs = new HashSet<>();
		for (PathwayVertexRep v : vertexReps) {
			davidIDs.addAll(v.getDavidIDs());
		}

		return referenceColumn.getElementIDsFromForeignIDs(davidIDs, IDType.getIDType(EGeneIDTypes.DAVID.name()));
	}

	@ListenTo(sendToMe = true)
	public void onHandleContextMenuOperation(ContextMenuCommandEvent event) {
		event.getCommand().execute();
	}

	@Override
	public void updateSelectionMappings(IEntityCollection srcCollection) {
		// nothing to do
	}

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
		referenceColumn.getRelationshipExplorer().unregisterEntityCollection(this);
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
	public Set<Object> getAllElementIDs() {
		return referenceColumn.getAllElementIDs();
	}

	@Override
	public Set<Object> getFilteredElementIDs() {
		// Get all because we do not want to reduce the set of genes for selections to that of the column
		return referenceColumn.getAllElementIDs();
	}

	@Override
	public Set<Object> getSelectedElementIDs() {
		return getReferenceColumnElementIDs(selectedVertexReps);
	}

	@Override
	public Set<Object> getHighlightElementIDs() {
		return getReferenceColumnElementIDs(highlightedVertexReps);
	}

	@Override
	public void setFilteredItems(Set<Object> elementIDs) {
		// nothing to do

	}

	@Override
	public void setHighlightItems(Set<Object> elementIDs) {
		// FIXME: Hack to prevent filtered elements of own selection to be received
		if (updateIsFromMe) {
			updateIsFromMe = false;
			return;
		}
		selectVerticesFromForeignIDs(getBroadcastingIDsFromElementIDs(elementIDs), getBroadcastingIDType(),
				highlightedVertexReps);
	}

	@Override
	public void setSelectedItems(Set<Object> elementIDs) {
		// FIXME: Hack to prevent filtered elements of own selection to be received
		if (updateIsFromMe) {
			updateIsFromMe = false;
			return;
		}

		selectVerticesFromForeignIDs(getBroadcastingIDsFromElementIDs(elementIDs), getBroadcastingIDType(),
				selectedVertexReps);
	}

	@Override
	public IDType getBroadcastingIDType() {
		return referenceColumn.getBroadcastingIDType();
	}

	@Override
	public Set<Object> getBroadcastingIDsFromElementID(Object elementID) {
		return referenceColumn.getBroadcastingIDsFromElementID(elementID);
	}

	@Override
	public Set<Object> getBroadcastingIDsFromElementIDs(Collection<Object> elementIDs) {
		return referenceColumn.getBroadcastingIDsFromElementIDs(elementIDs);
	}

	@Override
	public Set<Object> getElementIDsFromBroadcastingID(Integer broadcastingID) {
		return referenceColumn.getElementIDsFromBroadcastingID(broadcastingID);
	}

	@Override
	public Set<Object> getElementIDsFromForeignIDs(Set<Object> foreignIDs, IDType foreignIDType) {
		return referenceColumn.getElementIDsFromForeignIDs(foreignIDs, foreignIDType);
	}

}
