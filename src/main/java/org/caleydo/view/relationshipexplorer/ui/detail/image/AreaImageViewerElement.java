/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.image;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.system.BrowserUtils;
import org.caleydo.core.view.contextmenu.ActionBasedContextMenuItem;
import org.caleydo.core.view.opengl.layout2.GLImageElement;
import org.caleydo.core.view.opengl.layout2.GLImageViewer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.datadomain.image.LayeredImage;
import org.caleydo.datadomain.image.LayeredImage.Layer;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.MappingHighlightUpdateOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.SelectionBasedHighlightOperation;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.FilterContextMenuItems;
import org.caleydo.view.relationshipexplorer.ui.util.MultiSelectionUtil;
import org.caleydo.view.relationshipexplorer.ui.util.MultiSelectionUtil.IMultiSelectionHandler;

/**
 * @author Christian
 *
 */
public class AreaImageViewerElement extends GLImageViewer implements IEntityRepresentation,
		IMultiSelectionHandler<Object> {

	protected final ConTourElement contour;
	protected final int historyID;
	protected final IEntityCollection collection;
	protected Map<String, GLImageElement> layers = new HashMap<>();

	// protected ContextMenuCreator contextMenuCreator = new ContextMenuCreator();
	//
	// private class ShowContextMenuEvent extends ADirectedEvent {
	// }

	public AreaImageViewerElement(ConTourElement contour, IEntityCollection collection, LayeredImage img) {
		this.contour = contour;
		this.historyID = contour.getHistory().registerHistoryObject(this);
		this.collection = collection;
		setVisibility(EVisibility.PICKABLE);
		scaleToFit();
		final String url = img.getConfig().getProperty("URL");

		setBackgroundColor(Color.TRANSPARENT);
		setBaseImage(img.getBaseImage().image.getAbsolutePath());
		for (final String layerID : img.getLayers().keySet()) {
			Layer layer = img.getLayer(layerID);
			if (layer.area != null && layer.border != null) {

				final GLImageElement el = addLayer(layer.border.image.getAbsolutePath(),
						layer.area.image.getAbsolutePath());
				layers.put(layerID, el);

				el.onPick(new IPickingListener() {

					@Override
					public void pick(Pick pick) {
						if (MultiSelectionUtil.handleSelection(pick, layerID, AreaImageViewerElement.this)) {
							propagateSelection();
							updateAreaColors();
						}
						if (MultiSelectionUtil.handleHighlight(pick, layerID, AreaImageViewerElement.this)) {
							propagateHighlight();
							updateAreaColors();
						}

						if (pick.getPickingMode() == PickingMode.RIGHT_CLICKED) {

							AreaImageViewerElement.this.contour.addContextMenuItems(FilterContextMenuItems
									.getDefaultFilterItems(
									AreaImageViewerElement.this.contour, AreaImageViewerElement.this));
							//
							// contextMenuCreator.addAll(FilterContextMenuItems.getDefaultFilterItems(
							// AreaImageViewerElement.this.contour, AreaImageViewerElement.this));
							//
							// EventPublisher.trigger(new ShowContextMenuEvent().to(AreaImageViewerElement.this));
						}

					}
				});
			}
		}

		if (url != null) {
			onPick(new APickingListener() {
				@Override
				protected void rightClicked(Pick pick) {

					AreaImageViewerElement.this.contour.addContextMenuItem(new ActionBasedContextMenuItem(
							"Open Image in Browser", new Runnable() {

						@Override
						public void run() {
							BrowserUtils.openURL(url);
						}
					}));
					// contextMenuCreator.add(new ActionBasedContextMenuItem("Open Image in Browser", new Runnable() {
					//
					// @Override
					// public void run() {
					// BrowserUtils.openURL(url);
					// }
					// }));
					//
					// EventPublisher.trigger(new ShowContextMenuEvent().to(AreaImageViewerElement.this));
				}
			});
		}

		collection.addEntityRepresentation(this);
	}

	// @ListenTo(sendToMe = true)
	// protected void onShowContextMenu(ShowContextMenuEvent event) {
	// if (contextMenuCreator.hasMenuItems()) {
	// context.getSWTLayer().showContextMenu(contextMenuCreator);
	// contextMenuCreator.clear();
	// }
	// }

	protected void updateAreaColors() {
		for (Entry<String, GLImageElement> entry : layers.entrySet()) {
			boolean highlighted = collection.getHighlightElementIDs().contains(entry.getKey());
			if (highlighted) {
				entry.getValue().setColor(SelectionType.MOUSE_OVER.getColor());
			} else {
				boolean selected = collection.getSelectedElementIDs().contains(entry.getKey());
				if (selected) {
					entry.getValue().setColor(SelectionType.SELECTION.getColor());
				} else {
					entry.getValue().setColor(Color.WHITE);
				}
			}
		}
	}

	public void propagateSelection() {

		SelectionBasedHighlightOperation c = new SelectionBasedHighlightOperation(getHistoryID(),
				collection.getSelectedElementIDs(), collection.getBroadcastingIDsFromElementIDs(collection
						.getSelectedElementIDs()), contour);

		c.execute();

		contour.getHistory().addHistoryCommand(c);

	}

	public void propagateHighlight() {

		// collection.setHighlightItems(highlightElementIDs);

		contour.applyIDMappingUpdate(new MappingHighlightUpdateOperation(collection
				.getBroadcastingIDsFromElementIDs(collection.getHighlightElementIDs()), this, contour
				.getMultiItemSelectionSetOperation()));
	}

	@Override
	public int getHistoryID() {
		return historyID;
	}

	@Override
	public void selectionChanged(Set<Object> selectedElementIDs, IEntityRepresentation srcRep) {
		if (srcRep != this)
			updateAreaColors();

	}

	@Override
	public void highlightChanged(Set<Object> highlightElementIDs, IEntityRepresentation srcRep) {
		if (srcRep != this)
			updateAreaColors();
	}

	@Override
	public void filterChanged(Set<Object> filteredElementIDs, IEntityRepresentation srcRep) {
		// nothing to do
	}

	@Override
	public IEntityCollection getCollection() {
		return collection;
	}

	@Override
	public boolean isSelected(Object object) {

		return collection.getSelectedElementIDs().contains(object);
	}

	@Override
	public void removeFromSelection(Object object) {
		collection.getSelectedElementIDs().remove(object);
	}

	@Override
	public void addToSelection(Object object) {
		collection.getSelectedElementIDs().add(object);
	}

	@Override
	public void setSelection(Object object) {
		Set<Object> selectedElementIDs = collection.getSelectedElementIDs();
		selectedElementIDs.clear();
		selectedElementIDs.add(object);

	}

	@Override
	public boolean isHighlight(Object object) {
		return collection.getHighlightElementIDs().contains(object);
	}

	@Override
	public void setHighlight(Object object) {
		Set<Object> highlightElementIDs = collection.getHighlightElementIDs();
		highlightElementIDs.clear();
		highlightElementIDs.add(object);
	}

	@Override
	public void removeHighlight(Object object) {
		collection.getHighlightElementIDs().remove(object);
	}

	@Override
	protected void takeDown() {
		collection.removeEntityRepresentation(this);
		super.takeDown();
	}

}
