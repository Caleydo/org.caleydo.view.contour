/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.util.Map;

import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.view.opengl.layout2.GLElement;

/**
 * @author Christian
 *
 */
public class AttributeFilterEvent<T> extends ADirectedEvent {
	protected T filterDefinitionData;
	protected Map<Object, GLElement> itemPool;
	protected boolean save;

	public AttributeFilterEvent(T filterDefinitionData, Map<Object, GLElement> itemPool, boolean save) {
		this.filterDefinitionData = filterDefinitionData;
		this.itemPool = itemPool;
		this.save = save;
	}

	/**
	 * @return the filterDefinitionData, see {@link #filterDefinitionData}
	 */
	public T getFilterDefinitionData() {
		return filterDefinitionData;
	}

	/**
	 * @param filterDefinitionData
	 *            setter, see {@link filterDefinitionData}
	 */
	public void setFilterDefinitionData(T filterDefinitionData) {
		this.filterDefinitionData = filterDefinitionData;
	}

	/**
	 * @param itemPool
	 *            setter, see {@link itemPool}
	 */
	public void setItemPool(Map<Object, GLElement> itemPool) {
		this.itemPool = itemPool;
	}

	/**
	 * @return the itemPool, see {@link #itemPool}
	 */
	public Map<Object, GLElement> getItemPool() {
		return itemPool;
	}

	/**
	 * @return the save, see {@link #save}
	 */
	public boolean isSave() {
		return save;
	}

}
