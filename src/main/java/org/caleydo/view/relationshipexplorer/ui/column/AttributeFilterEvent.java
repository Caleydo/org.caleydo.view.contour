/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.util.Set;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.view.relationshipexplorer.ui.filter.IEntityFilter;

/**
 * @author Christian
 *
 */
public class AttributeFilterEvent extends ADirectedEvent {

	protected IEntityFilter filter;
	protected boolean save;
	protected Set<Object> filterElementIDPool;

	public AttributeFilterEvent(IEntityFilter filter, Set<Object> filterElementIDPool, boolean save) {
		this.filter = filter;
		this.save = save;
		this.filterElementIDPool = filterElementIDPool;
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

	/**
	 * @return the save, see {@link #save}
	 */
	public boolean isSave() {
		return save;
	}

	/**
	 * @param save
	 *            setter, see {@link save}
	 */
	public void setSave(boolean save) {
		this.save = save;
	}

	/**
	 * @return the filterElementIDPool, see {@link #filterElementIDPool}
	 */
	public Set<Object> getFilterElementIDPool() {
		return filterElementIDPool;
	}

	/**
	 * @param filterElementIDPool
	 *            setter, see {@link filterElementIDPool}
	 */
	public void setFilterElementIDPool(Set<Object> filterElementIDPool) {
		this.filterElementIDPool = filterElementIDPool;
	}

}
