/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.compound;

import java.util.Set;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;
import org.caleydo.view.relationshipexplorer.ui.detail.DetailViewWindow;
import org.caleydo.view.relationshipexplorer.ui.detail.IShowSelectedItemsListener;

/**
 * @author Christian
 *
 */
public class CompoundDetailViewWindow extends DetailViewWindow implements IEntityRepresentation,
		IShowSelectedItemsListener {

	protected boolean showSelectedItems = true;

	/**
	 * @param titleLabelProvider
	 * @param relationshipExplorer
	 */
	public CompoundDetailViewWindow(IEntityCollection collection,
			ConTourElement relationshipExplorer) {
		super(collection, relationshipExplorer);
		collection.addEntityRepresentation(this);
	}

	@Override
	public void selectionChanged(Set<Object> selectedElementIDs, ILabeled updateSource) {
		if (showSelectedItems /* && srcRep.getCollection() == collection */&& updateSource != this) {
			setContent(collection.createDetailView(this));
			contour.updateDetailHeight();
		}
	}

	@Override
	public void highlightChanged(Set<Object> highlightElementIDs, ILabeled updateSource) {
		// TODO Auto-generated method stub

	}

	@Override
	public void filterChanged(Set<Object> filteredElementIDs, ILabeled updateSource) {
		// TODO Auto-generated method stub

	}

	@Override
	public IEntityCollection getCollection() {
		return collection;
	}

	@Override
	protected void takeDown() {
		collection.removeEntityRepresentation(this);
		super.takeDown();
	}

	@Override
	public void showSelectedItems(boolean showSelectedItems) {
		this.showSelectedItems = showSelectedItems;

	}

	@Override
	public String getLabel() {
		return collection.getLabel();
	}

}
