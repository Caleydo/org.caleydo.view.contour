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
public class SnapshotEvent extends AEvent {

	protected final String label;

	public SnapshotEvent(String label) {
		this.label = label;
	}

	@Override
	public boolean checkIntegrity() {
		return true;
	}

	/**
	 * @return the label, see {@link #label}
	 */
	public String getLabel() {
		return label;
	}

}
