/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.parcoords;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.perspective.variable.PerspectiveInitializationData;
import org.caleydo.core.data.selection.SelectionManager;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.opengl.canvas.EDetailLevel;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.parcoords.v2.ParallelCoordinateElement;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.MappingHighlightUpdateOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.SelectionBasedHighlightOperation;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.ContextMenuCommandEvent;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.FilterContextMenuItems;
import org.caleydo.view.relationshipexplorer.ui.detail.IShowFilteredItemsListener;

/**
 * @author Christian
 *
 */
public class ParCoordsElement extends ParallelCoordinateElement implements IEntityRepresentation,
		IShowFilteredItemsListener {

	protected final TabularDataCollection collection;
	protected final RelationshipExplorerElement relationshipExplorer;
	protected final int historyID;
	protected boolean showFilteredItemsOnly = false;

	/**
	 * @param tablePerspective
	 * @param detailLevel
	 */
	public ParCoordsElement(TablePerspective tablePerspective, TabularDataCollection collection,
			RelationshipExplorerElement relationshipExplorer) {
		super(tablePerspective, EDetailLevel.HIGH);
		this.collection = collection;
		collection.addEntityRepresentation(this);
		this.relationshipExplorer = relationshipExplorer;
		this.historyID = relationshipExplorer.getHistory().registerHistoryObject(this);
		onVAUpdate(createTablePerspectiveFromCollectionIDs(collection.getAllElementIDs()));
	}

	protected TablePerspective createTablePerspectiveFromCollectionIDs(Set<Object> elementIDs) {
		Perspective dimensionPerspective = collection.getDimensionPerspective();

		Perspective recordPerspective = new Perspective(collection.getDataDomain(), collection.getItemIDType());
		List<Integer> indices = new ArrayList<>(elementIDs.size());
		for (Object elementID : elementIDs) {
			indices.add((Integer) elementID);
		}
		PerspectiveInitializationData data = new PerspectiveInitializationData();
		data.setData(indices);
		recordPerspective.init(data);

		TablePerspective tablePerspective = new TablePerspective(collection.getDataDomain(), recordPerspective,
				dimensionPerspective);
		return tablePerspective;
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
		case RIGHT_CLICKED:
			ContextMenuCreator contextMenuCreator = new ContextMenuCreator();
			contextMenuCreator.addAll(FilterContextMenuItems.getDefaultFilterItems(relationshipExplorer, this, this));

			context.getSWTLayer().showContextMenu(contextMenuCreator);
			break;

		default:
			return;
		}
		selections.fireRecordSelectionDelta();
		repaint();

	}

	@ListenTo(sendToMe = true)
	public void onHandleContextMenuOperation(ContextMenuCommandEvent event) {
		event.getCommand().execute();
	}

	public void propagateSelection() {
		SelectionManager selectionManager = selections.getRecordSelectionManager();
		Set<Object> selectedElementIDs = new HashSet<Object>(selectionManager.getElements(SelectionType.SELECTION));

		SelectionBasedHighlightOperation c = new SelectionBasedHighlightOperation(getHistoryID(), selectedElementIDs,
				collection.getBroadcastingIDsFromElementIDs(selectedElementIDs), relationshipExplorer);
		c.execute();

		relationshipExplorer.getHistory().addHistoryCommand(c);

		// geneCollection.setSelectedItems(selectedElementIDs, this);
		//
		// relationshipExplorer.applyIDMappingUpdate(
		// new MappingSelectionUpdateOperation(
		// geneCollection.getBroadcastingIDsFromElementIDs(selectedElementIDs), this), true);
	}

	public void propagateHighlight() {
		SelectionManager selectionManager = selections.getRecordSelectionManager();
		Set<Object> highlightElementIDs = new HashSet<Object>(selectionManager.getElements(SelectionType.MOUSE_OVER));

		collection.setHighlightItems(highlightElementIDs);

		relationshipExplorer.applyIDMappingUpdate(new MappingHighlightUpdateOperation(collection
				.getBroadcastingIDsFromElementIDs(highlightElementIDs), this));
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
		if (showFilteredItemsOnly) {
			onVAUpdate(createTablePerspectiveFromCollectionIDs(filteredElementIDs));
		}

	}

	@Override
	public IEntityCollection getCollection() {
		return collection;
	}

	@Override
	public void showFilteredItems(boolean showFilteredItems) {
		if (showFilteredItemsOnly != showFilteredItems) {
			showFilteredItemsOnly = showFilteredItems;
			if (showFilteredItems) {
				onVAUpdate(createTablePerspectiveFromCollectionIDs(collection.getFilteredElementIDs()));
			} else {
				onVAUpdate(createTablePerspectiveFromCollectionIDs(collection.getAllElementIDs()));
			}
		}

	}

}
