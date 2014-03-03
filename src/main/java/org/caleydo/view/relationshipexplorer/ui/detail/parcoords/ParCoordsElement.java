/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.parcoords;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.parcoords.v2.ParallelCoordinateElement;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.MappingHighlightUpdateOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.SelectionBasedHighlightOperation;

/**
 * @author Christian
 *
 */
public class ParCoordsElement extends ParallelCoordinateElement implements IEntityRepresentation {

	protected final IEntityCollection collection;
	protected final RelationshipExplorerElement relationshipExplorer;
	protected final int historyID;

	/**
	 * @param tablePerspective
	 * @param detailLevel
	 */
	public ParCoordsElement(TablePerspective tablePerspective, IEntityCollection collection,
			RelationshipExplorerElement relationshipExplorer) {
		super(tablePerspective, EDetailLevel.HIGH);
		this.collection = collection;
		collection.addEntityRepresentation(this);
		this.relationshipExplorer = relationshipExplorer;
		this.historyID = relationshipExplorer.getHistory().registerHistoryObject(this);
	}

	@Override
	protected void onLinePick(Pick pick) {
		SelectionManager record = selections.getRecordSelectionManager();
		switch (pick.getPickingMode()) {
		case MOUSE_OVER:
			record.addToType(SelectionType.MOUSE_OVER, pick.getObjectID());
			propagateHighlight();
			break;
		case MOUSE_OUT:
			record.removeFromType(SelectionType.MOUSE_OVER, pick.getObjectID());
			propagateHighlight();
			break;
		case CLICKED:
			if (isBrushClick(pick))
				return;
			if (!((IMouseEvent) pick).isCtrlDown())
				record.clearSelection(SelectionType.SELECTION);
			record.addToType(SelectionType.SELECTION, pick.getObjectID());
			propagateSelection();
			break;
		default:
			return;
		}
		selections.fireRecordSelectionDelta();
		repaint();

	}

	public void propagateSelection() {
		SelectionManager selectionManager = selections.getRecordSelectionManager();
		Set<Object> selectedElementIDs = new HashSet<Object>(selectionManager.getElements(SelectionType.SELECTION));

		SelectionBasedHighlightOperation c = new SelectionBasedHighlightOperation(getHistoryID(), selectedElementIDs,
				collection.getBroadcastingIDsFromElementIDs(selectedElementIDs), relationshipExplorer);
		c.execute();

		relationshipExplorer.getHistory().addHistoryCommand(c, Color.SELECTION_ORANGE);

		// geneCollection.setSelectedItems(selectedElementIDs, this);
		//
		// relationshipExplorer.applyIDMappingUpdate(
		// new MappingSelectionUpdateOperation(
		// geneCollection.getBroadcastingIDsFromElementIDs(selectedElementIDs), this), true);
	}

	public void propagateHighlight() {
		SelectionManager selectionManager = selections.getRecordSelectionManager();
		Set<Object> highlightElementIDs = new HashSet<Object>(selectionManager.getElements(SelectionType.MOUSE_OVER));

		collection.setHighlightItems(highlightElementIDs, this);

		relationshipExplorer.applyIDMappingUpdate(
				new MappingHighlightUpdateOperation(collection.getBroadcastingIDsFromElementIDs(highlightElementIDs),
						this), false);
	}

	@Override
	public int getHistoryID() {
		return historyID;
	}

	@Override
	public void selectionChanged(Set<Object> selectedElementIDs, IEntityRepresentation srcRep) {
		updateSelection(SelectionType.SELECTION, selectedElementIDs);

	}

	@Override
	public void highlightChanged(Set<Object> highlightElementIDs, IEntityRepresentation srcRep) {
		updateSelection(SelectionType.MOUSE_OVER, highlightElementIDs);
	}

	protected void updateSelection(SelectionType selectionType, Set<Object> ids) {
		SelectionManager record = selections.getRecordSelectionManager();
		record.clearSelection(selectionType);
		for (Object id : ids) {
			record.addToType(selectionType, (Integer) id);
		}
		repaint();
	}

	@Override
	public void filterChanged(Set<Object> filteredElementIDs, IEntityRepresentation srcRep) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateMappings(IEntityRepresentation srcRep) {
		// TODO Auto-generated method stub

	}

	@Override
	public IEntityCollection getCollection() {
		return collection;
	}

}
