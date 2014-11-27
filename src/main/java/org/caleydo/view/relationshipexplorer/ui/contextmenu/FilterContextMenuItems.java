/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.contextmenu;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.contextmenu.GroupContextMenuItem;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryIDOwner;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.operation.ESetOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.SelectionBasedFilterOperation;
import org.caleydo.view.relationshipexplorer.ui.dialog.SelectColumnsToFilterDialog;
import org.caleydo.view.relationshipexplorer.ui.util.AddHistoryCommandRunnable;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * @author Christian
 *
 */
public final class FilterContextMenuItems {

	private FilterContextMenuItems() {

	}

	public static List<AContextMenuItem> getDefaultFilterItems(ConTourElement contour, IHistoryIDOwner source,
			Set<Object> broadcastIDs, IDType broadcastIDType) {

		List<AContextMenuItem> items = new ArrayList<>();
		// items.add(createFilterItemGroup(ESetOperation.REPLACE, "Replace items with those related to the selected "
		// + representation.getCollection().getLabel() + " in ...", contour, representation));

		items.add(createFilterItemGroup(ESetOperation.INTERSECTION, "Filter items to those related to the selected "
				+ source.getLabel() + " in ...", contour, source, null, broadcastIDs, broadcastIDs, broadcastIDType));

		items.add(createFilterItemGroup(ESetOperation.UNION,
				"Add all items related to the selected " + source.getLabel() + " in ...", contour, source, null,
				broadcastIDs, broadcastIDs, broadcastIDType));

		items.add(createFilterItemGroup(ESetOperation.REMOVE,
				"Remove all items related to the selected " + source.getLabel() + " in ...", contour, source, null,
				broadcastIDs, broadcastIDs, broadcastIDType));

		return items;
	}

	public static List<AContextMenuItem> getDefaultFilterItems(ConTourElement contour, IHistoryIDOwner source,
			IEntityCollection collection) {

		List<AContextMenuItem> items = new ArrayList<>();
		// items.add(createFilterItemGroup(ESetOperation.REPLACE, "Replace items with those related to the selected "
		// + representation.getCollection().getLabel() + " in ...", contour, representation));
		Set<Object> elementIDs = collection.getSelectedElementIDs();
		Set<Object> broadcastIDs = collection.getBroadcastingIDsFromElementIDs(elementIDs);

		items.add(createFilterItemGroup(ESetOperation.INTERSECTION, "Filter items to those related to the selected "
				+ source.getLabel() + " in ...", contour, source, collection, elementIDs, broadcastIDs,
				collection.getBroadcastingIDType()));

		items.add(createFilterItemGroup(ESetOperation.UNION,
				"Add all items related to the selected " + source.getLabel() + " in ...", contour, source, collection,
				elementIDs, broadcastIDs, collection.getBroadcastingIDType()));

		items.add(createFilterItemGroup(ESetOperation.REMOVE,
				"Remove all items related to the selected " + source.getLabel() + " in ...", contour, source,
				collection, elementIDs, broadcastIDs, collection.getBroadcastingIDType()));

		return items;
	}

	private static AContextMenuItem createFilterItemGroup(ESetOperation operation, String groupItemLabel,
			ConTourElement contour, IHistoryIDOwner source, IEntityCollection collection, Set<Object> elementIDs,
			Set<Object> broadcastIDs, IDType broadcastIDType) {
		GroupContextMenuItem groupItem = new GroupContextMenuItem(groupItemLabel);

		SelectionBasedFilterOperation c = new SelectionBasedFilterOperation(collection, source.getHistoryID(),
				elementIDs, broadcastIDs, broadcastIDType, operation, contour);

		AContextMenuItem filterAllItem = new GenericContextMenuItem("All columns", new ThreadSyncEvent(
				new AddHistoryCommandRunnable(c, contour.getHistory())).to(contour));

		AContextMenuItem filterSpecifiedItem = new GenericContextMenuItem("Specified columns", new ThreadSyncEvent(
				new SelectColumnsRunnable(contour, source, collection, elementIDs, broadcastIDs, broadcastIDType,
						operation)).to(contour));

		groupItem.add(filterAllItem);
		groupItem.add(filterSpecifiedItem);

		return groupItem;
	}

	private static class SelectColumnsRunnable implements Runnable {

		private final ConTourElement contour;
		private final IHistoryIDOwner source;
		private final IEntityCollection collection;
		private final Set<Object> elementIDs;
		private final Set<Object> broadcastIDs;
		private final IDType broadcastIDType;
		private final ESetOperation setOperation;

		/**
		 *
		 */
		public SelectColumnsRunnable(ConTourElement contour, IHistoryIDOwner source, IEntityCollection collection,
				Set<Object> elementIDs, Set<Object> broadcastIDs, IDType broadcastIDType, ESetOperation setOperation) {
			this.contour = contour;
			this.source = source;
			this.collection = collection;
			this.elementIDs = elementIDs;
			this.broadcastIDs = broadcastIDs;
			this.broadcastIDType = broadcastIDType;
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

						SelectionBasedFilterOperation c = new SelectionBasedFilterOperation(collection, source
								.getHistoryID(), elementIDs, broadcastIDs, broadcastIDType, setOperation, contour);
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
