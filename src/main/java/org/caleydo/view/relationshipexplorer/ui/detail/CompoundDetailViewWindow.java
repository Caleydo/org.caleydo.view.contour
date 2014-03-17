/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail;

import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;

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
			RelationshipExplorerElement relationshipExplorer) {
		super(collection, relationshipExplorer);
		collection.addEntityRepresentation(this);
	}

	@Override
	public void selectionChanged(Set<Object> selectedElementIDs, IEntityRepresentation srcRep) {
		if (showSelectedItems /* && srcRep.getCollection() == collection */&& srcRep != this) {
			setContent(collection.createDetailView(this));
			relationshipExplorer.updateDetailHeight();
		}
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

	@Override
	protected void takeDown() {
		collection.removeEntityRepresentation(this);
		super.takeDown();
	}

	@Override
	public void showSelectedItems(boolean showSelectedItems) {
		showSelectedItems = showSelectedItems;

	}

}
