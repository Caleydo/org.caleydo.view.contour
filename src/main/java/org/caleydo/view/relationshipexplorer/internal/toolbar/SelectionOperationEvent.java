/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.internal.toolbar;

import org.caleydo.core.event.AEvent;

/**
 * @author Christian
 *
 */
public class SelectionOperationEvent extends AEvent {

	protected final boolean isIntersection;

	public SelectionOperationEvent(boolean isIntersection) {
		this.isIntersection = isIntersection;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @return the isIntersection, see {@link #isIntersection}
	 */
	public boolean isIntersection() {
		return isIntersection;
	}

}
