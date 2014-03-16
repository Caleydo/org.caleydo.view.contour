/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail;

import java.util.Set;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;

/**
 * @author Christian
 *
 */
public class CompoundDetailViewWindow extends DetailViewWindow implements IEntityRepresentation {

	protected final IEntityCollection collection;
	protected boolean changeViewOnSelection = true;

	/**
	 * @param titleLabelProvider
	 * @param relationshipExplorer
	 */
	public CompoundDetailViewWindow(ILabeled titleLabelProvider, RelationshipExplorerElement relationshipExplorer,
			IEntityCollection collection) {
		super(titleLabelProvider, relationshipExplorer);
		this.collection = collection;
		collection.addEntityRepresentation(this);
	}

	public void changeViewOnSelection(boolean change) {
		this.changeViewOnSelection = change;
	}

	@Override
	public void selectionChanged(Set<Object> selectedElementIDs, IEntityRepresentation srcRep) {
		if (changeViewOnSelection && srcRep.getCollection() == collection && srcRep != this) {
			setContent(collection.createDetailView());
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

}
