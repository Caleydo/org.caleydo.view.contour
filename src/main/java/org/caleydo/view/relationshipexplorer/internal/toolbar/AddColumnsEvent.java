/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.internal.toolbar;

import java.util.Set;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;

/**
 * @author Christian
 *
 */
public class AddColumnsEvent extends ADirectedEvent {

	protected final Set<IEntityCollection> collections;

	public AddColumnsEvent(Set<IEntityCollection> collections) {
		this.collections = collections;
	}

	/**
	 * @return the collections, see {@link #collections}
	 */
	public Set<IEntityCollection> getCollections() {
		return collections;
	}

}
