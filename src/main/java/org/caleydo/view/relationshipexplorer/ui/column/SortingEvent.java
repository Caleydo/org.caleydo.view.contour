/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.util.Comparator;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;

/**
 * @author Christian
 *
 */
public class SortingEvent extends ADirectedEvent {

	protected Comparator<NestableItem> comparator;

	public SortingEvent(Comparator<NestableItem> comparator) {
		this.comparator = comparator;
	}

	/**
	 * @return the comparator, see {@link #comparator}
	 */
	public Comparator<NestableItem> getComparator() {
		return comparator;
	}

	/**
	 * @param comparator
	 *            setter, see {@link comparator}
	 */
	public void setComparator(Comparator<NestableItem> comparator) {
		this.comparator = comparator;
	}

}
