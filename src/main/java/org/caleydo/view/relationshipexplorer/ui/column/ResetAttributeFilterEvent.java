/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.util.Map;
import java.util.Set;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;

/**
 * @author Christian
 *
 */
public class ResetAttributeFilterEvent extends ADirectedEvent {

	protected Map<IEntityCollection, Set<Object>> originalFilteredItemIDs;

	public ResetAttributeFilterEvent(Map<IEntityCollection, Set<Object>> originalFilteredItemIDs) {
		this.originalFilteredItemIDs = originalFilteredItemIDs;
	}

	/**
	 * @return the originalFilteredItemIDs, see {@link #originalFilteredItemIDs}
	 */
	public Map<IEntityCollection, Set<Object>> getOriginalFilteredItemIDs() {
		return originalFilteredItemIDs;
	}

}
