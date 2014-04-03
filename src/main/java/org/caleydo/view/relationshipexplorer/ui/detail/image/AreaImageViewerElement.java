/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.image;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLImageElement;
import org.caleydo.core.view.opengl.layout2.GLImageViewer;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.image.LayeredImage;
import org.caleydo.datadomain.image.LayeredImage.Layer;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.MappingHighlightUpdateOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.SelectionBasedHighlightOperation;

import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public class AreaImageViewerElement extends GLImageViewer implements IEntityRepresentation {

	protected final ConTourElement contour;
	protected final int historyID;
	protected IEntityCollection collection;

	public AreaImageViewerElement(ConTourElement contour, IEntityCollection collection, LayeredImage img) {
		this.contour = contour;
		this.historyID = contour.getHistory().registerHistoryObject(this);
		this.collection = collection;

		scaleToFit();
		setBackgroundColor(Color.TRANSPARENT);
		setBaseImage(img.getBaseImage().image.getAbsolutePath());
		for (final String layerID : img.getLayers().keySet()) {
			Layer layer = img.getLayer(layerID);
			if (layer.area != null && layer.border != null) {

				final GLImageElement el = addLayer(layer.border.image.getAbsolutePath(),
						layer.area.image.getAbsolutePath());
				el.onPick(new APickingListener() {
					@Override
					protected void clicked(Pick pick) {
						el.setColor(SelectionType.SELECTION.getColor());
						propagateSelection(Sets.<Object> newHashSet(layerID));
					}

					@Override
					protected void mouseOver(Pick pick) {
						el.setColor(SelectionType.MOUSE_OVER.getColor());
						propagateHighlight(Sets.<Object> newHashSet(layerID));
					}

					@Override
					protected void mouseOut(Pick pick) {
						propagateHighlight(new HashSet<>());
						el.setColor(Color.WHITE);
					}
				});
			}
		}
	}

	public void propagateSelection(Set<Object> selectedElementIDs) {

		SelectionBasedHighlightOperation c = new SelectionBasedHighlightOperation(getHistoryID(), selectedElementIDs,
				collection.getBroadcastingIDsFromElementIDs(selectedElementIDs), contour);

		c.execute();

		contour.getHistory().addHistoryCommand(c);

	}

	public void propagateHighlight(Set<Object> highlightElementIDs) {

		collection.setHighlightItems(highlightElementIDs);

		contour.applyIDMappingUpdate(new MappingHighlightUpdateOperation(collection
				.getBroadcastingIDsFromElementIDs(highlightElementIDs), this, contour
				.getMultiItemSelectionSetOperation()));
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
		return collection;
	}

}
