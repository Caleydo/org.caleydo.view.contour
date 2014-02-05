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
public class IDUpdateEvent extends AEvent {

	public enum EUpdateType {
		REPLACE_FILTER, AND_FILTER, OR_FILTER, SELECTION
	}

	private Set<Object> ids;
	private IDType idType;
	private EUpdateType updateType;

	/**
	 *
	 */
	public IDUpdateEvent() {
	}

	public IDUpdateEvent(Set<Object> ids, IDType idType, EUpdateType updateType) {
		this.ids = ids;
		this.idType = idType;
		this.updateType = updateType;
	}

	@Override
	public boolean checkIntegrity() {
		return ids != null && idType != null;
	}

	/**
	 * @param id
	 *            setter, see {@link id}
	 */
	public void setIds(Set<Object> ids) {
		this.ids = ids;
	}

	/**
	 * @return the id, see {@link #id}
	 */
	public Set<Object> getIds() {
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

	/**
	 * @param updateType
	 *            setter, see {@link updateType}
	 */
	public void setUpdateType(EUpdateType updateType) {
		this.updateType = updateType;
	}

	/**
	 * @return the updateType, see {@link #updateType}
	 */
	public EUpdateType getUpdateType() {
		return updateType;
	}

}
