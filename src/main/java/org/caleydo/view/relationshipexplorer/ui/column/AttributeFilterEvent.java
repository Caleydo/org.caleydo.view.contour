/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import org.caleydo.core.event.ADirectedEvent;

import com.google.common.base.Predicate;

/**
 * @author Christian
 *
 */
public class AttributeFilterEvent extends ADirectedEvent {

	protected Predicate<Object> filter;

	public AttributeFilterEvent(Predicate<Object> filter) {
		this.filter = filter;
	}

	/**
	 * @param filter
	 *            setter, see {@link filter}
	 */
	public void setFilter(Predicate<Object> filter) {
		this.filter = filter;
	}

	/**
	 * @return the filter, see {@link #filter}
	 */
	public Predicate<Object> getFilter() {
		return filter;
	}

}
