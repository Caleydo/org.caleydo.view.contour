/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.view.relationshipexplorer.ui.filter.IEntityFilter;

/**
 * @author Christian
 *
 */
public class AttributeFilterEvent extends ADirectedEvent {

	protected IEntityFilter filter;

	public AttributeFilterEvent(IEntityFilter filter) {
		this.filter = filter;
	}

	/**
	 * @param filter
	 *            setter, see {@link filter}
	 */
	public void setFilter(IEntityFilter filter) {
		this.filter = filter;
	}

	/**
	 * @return the filter, see {@link #filter}
	 */
	public IEntityFilter getFilter() {
		return filter;
	}

}
