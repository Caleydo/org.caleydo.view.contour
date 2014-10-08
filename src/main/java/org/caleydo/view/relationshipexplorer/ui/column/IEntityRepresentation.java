/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.util.Set;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryIDOwner;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;

/**
 * @author Christian
 *
 */
public interface IEntityRepresentation extends IHistoryIDOwner, ILabeled {

	public void selectionChanged(Set<Object> selectedElementIDs, ILabeled updateSource);

	public void highlightChanged(Set<Object> highlightElementIDs, ILabeled updateSource);

	public void filterChanged(Set<Object> filteredElementIDs, ILabeled updateSource);

	public IEntityCollection getCollection();

}
