/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.contextmenu;

import java.util.List;
import java.util.Set;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.ESetOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.SelectionBasedFilterOperation;
import org.caleydo.view.relationshipexplorer.ui.dialog.SelectColumnsToFilterDialog;
import org.caleydo.view.relationshipexplorer.ui.util.AddHistoryCommandRunnable;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.Lists;

/**
 * @author Christian
 *
 */
public final class FilterContextMenuItems {

	private FilterContextMenuItems() {

	}

	public static List<AContextMenuItem> getDefaultFilterItems(final ConTourElement relationshipExplorer,
			final IEntityRepresentation representation) {

		final Set<Object> elementIDs = representation.getCollection().getSelectedElementIDs();
		final Set<Object> broadcastIDs = representation.getCollection().getBroadcastingIDsFromElementIDs(elementIDs);

		SelectionBasedFilterOperation c = new SelectionBasedFilterOperation(representation.getHistoryID(), elementIDs,
				broadcastIDs, ESetOperation.REPLACE, relationshipExplorer);

		// AContextMenuItem replaceFilterItem = new GenericContextMenuItem(
		// "Replace items with those related to the selected " + representation.getCollection().getLabel(),
		// new ThreadSyncCommandEvent(new FilterCommand(ESetOperation.REPLACE, representation,
		// relationshipExplorer)).to(relationshipExplorer));
		AContextMenuItem replaceFilterItem = new GenericContextMenuItem(
				"Replace items with those related to the selected " + representation.getCollection().getLabel(),
				new ThreadSyncEvent(new AddHistoryCommandRunnable(c, relationshipExplorer.getHistory()))
						.to(relationshipExplorer));

		AContextMenuItem replaceFilterInItem = new GenericContextMenuItem(
				"Replace items with those related to the selected " + representation.getCollection().getLabel()
						+ " in ...", new ThreadSyncEvent(new SelectColumnsRunnable(relationshipExplorer,
						representation,
						ESetOperation.REPLACE)).to(relationshipExplorer));

		c = new SelectionBasedFilterOperation(representation.getHistoryID(), elementIDs, broadcastIDs,
				ESetOperation.INTERSECTION, relationshipExplorer);

		AContextMenuItem andFilterITem = new GenericContextMenuItem("Filter items to those related to the selected "
				+ representation.getCollection().getLabel(), new ThreadSyncEvent(new AddHistoryCommandRunnable(c,
				relationshipExplorer.getHistory())).to(relationshipExplorer));

		AContextMenuItem andFilterInITem = new GenericContextMenuItem("Filter items to those related to the selected "
				+ representation.getCollection().getLabel() + " in ...", new ThreadSyncEvent(new SelectColumnsRunnable(
				relationshipExplorer, representation, ESetOperation.INTERSECTION)).to(relationshipExplorer));

		c = new SelectionBasedFilterOperation(representation.getHistoryID(), elementIDs, broadcastIDs,
				ESetOperation.UNION, relationshipExplorer);

		AContextMenuItem orFilterITem = new GenericContextMenuItem("Add all items related to the selected "
				+ representation.getCollection().getLabel(), new ThreadSyncEvent(new AddHistoryCommandRunnable(c,
				relationshipExplorer.getHistory())).to(relationshipExplorer));

		AContextMenuItem orFilterInITem = new GenericContextMenuItem("Add all items related to the selected "
				+ representation.getCollection().getLabel() + " in ...", new ThreadSyncEvent(new SelectColumnsRunnable(
				relationshipExplorer, representation, ESetOperation.UNION)).to(relationshipExplorer));

		c = new SelectionBasedFilterOperation(representation.getHistoryID(), elementIDs, broadcastIDs,
				ESetOperation.REMOVE, relationshipExplorer);

		AContextMenuItem removeItem = new GenericContextMenuItem("Remove all items related to the selected "
				+ representation.getCollection().getLabel(), new ThreadSyncEvent(new AddHistoryCommandRunnable(c,
				relationshipExplorer.getHistory())).to(relationshipExplorer));

		AContextMenuItem removeInItem = new GenericContextMenuItem("Remove all items related to the selected "
				+ representation.getCollection().getLabel() + " in ...", new ThreadSyncEvent(new SelectColumnsRunnable(
				relationshipExplorer, representation, ESetOperation.REMOVE)).to(relationshipExplorer));

		return Lists.newArrayList(replaceFilterItem, replaceFilterInItem, andFilterITem, andFilterInITem, orFilterITem,
				orFilterInITem, removeItem, removeInItem);
	}

	private static class SelectColumnsRunnable implements Runnable {

		private final ConTourElement contour;
		private final IEntityRepresentation representation;
		private final ESetOperation setOperation;

		/**
		 *
		 */
		public SelectColumnsRunnable(ConTourElement contour, IEntityRepresentation representation,
				ESetOperation setOperation) {
			this.contour = contour;
			this.representation = representation;
			this.setOperation = setOperation;
		}

		@Override
		public void run() {
			contour.getContext().getSWTLayer().run(new ISWTLayerRunnable() {
				@Override
				public void run(Display display, Composite canvas) {
					// Point loc = canvas.toDisplay((int) location.x(), (int) location.y());
					SelectColumnsToFilterDialog dialog = new SelectColumnsToFilterDialog(canvas.getShell(), contour);
					if (dialog.open() == Window.OK) {

						final Set<Object> elementIDs = representation.getCollection().getSelectedElementIDs();
						final Set<Object> broadcastIDs = representation.getCollection()
								.getBroadcastingIDsFromElementIDs(elementIDs);

						SelectionBasedFilterOperation c = new SelectionBasedFilterOperation(representation
								.getHistoryID(), elementIDs, broadcastIDs, setOperation, contour);
						Set<IEntityCollection> collections = dialog.getCollections();
						c.setTargetCollections(collections);
						EventPublisher.trigger(new ThreadSyncEvent(new AddHistoryCommandRunnable(c, contour
								.getHistory())).to(contour));
					}
				}
			});
		}

	}

}
