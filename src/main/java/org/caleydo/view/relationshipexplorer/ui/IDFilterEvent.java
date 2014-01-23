/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import java.util.Set;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.id.IDType;

/**
 * @author Christian
 *
 */
public class IDFilterEvent extends AEvent {

	private Set<?> ids;
	private IDType idType;

	/**
	 *
	 */
	public IDFilterEvent() {
	}

	public IDFilterEvent(Set<?> ids, IDType idType) {
		this.ids = ids;
		this.idType = idType;
	}

	@Override
	public boolean checkIntegrity() {
		return ids != null && idType != null;
	}

	/**
	 * @param id
	 *            setter, see {@link id}
	 */
	public void setIds(Set<?> ids) {
		this.ids = ids;
	}

	/**
	 * @return the id, see {@link #id}
	 */
	public Set<?> getIds() {
		return ids;
	}

	/**
	 * @param idType
	 *            setter, see {@link idType}
	 */
	public void setIdType(IDType idType) {
		this.idType = idType;
	}

	/**
	 * @return the idType, see {@link #idType}
	 */
	public IDType getIdType() {
		return idType;
	}

}
