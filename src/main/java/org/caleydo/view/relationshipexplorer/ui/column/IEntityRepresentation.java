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

	public void selectionChanged(Set<Object> selectedElementIDs, IEntityRepresentation srcRep);

	public void highlightChanged(Set<Object> highlightElementIDs, IEntityRepresentation srcRep);

	public void filterChanged(Set<Object> filteredElementIDs, IEntityRepresentation srcRep);

	public void updateMappings(IEntityRepresentation srcRep);

	public IEntityCollection getCollection();

}
