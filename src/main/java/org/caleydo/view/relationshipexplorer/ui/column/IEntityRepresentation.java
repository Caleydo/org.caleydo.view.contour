/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.util.Set;

/**
 * @author Christian
 *
 */
public interface IEntityRepresentation {

	public void selectionChanged(Set<Object> selectedElementIDs);

	public void highlightChanged(Set<Object> highlightElementIDs);

	public void filterChanged(Set<Object> filteredElementIDs);

}
