/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.contextmenu;

import org.caleydo.core.event.ADirectedEvent;

/**
 * @author Christian
 *
 */
public class ThreadSyncEvent extends ADirectedEvent {

	protected final Runnable runnable;

	public ThreadSyncEvent(Runnable runnable) {
		this.runnable = runnable;
	}

	/**
	 * @return the command, see {@link #command}
	 */
	public Runnable getRunnable() {
		return runnable;
	}
}
