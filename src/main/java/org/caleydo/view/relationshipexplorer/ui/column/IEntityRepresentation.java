/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.History.IHistoryIDOwner;

/**
 * @author Christian
 *
 */
public interface IEntityRepresentation extends IHistoryIDOwner {

	public void selectionChanged(Set<Object> selectedElementIDs, IEntityRepresentation srcRep);

	public void highlightChanged(Set<Object> highlightElementIDs, IEntityRepresentation srcRep);

	public void filterChanged(Set<Object> filteredElementIDs, IEntityRepresentation srcRep);

	public IEntityCollection getCollection();

}
