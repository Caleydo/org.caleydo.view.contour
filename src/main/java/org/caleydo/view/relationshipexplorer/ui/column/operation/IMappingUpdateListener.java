/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.util.Set;

import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.ILabeled;

/**
 * Listener that is notified about highlight, selection, and filter updates.
 *
 * @author Christian
 *
 */
public interface IMappingUpdateListener {

	/**
	 * Called when elements are highlighted.
	 *
	 * @param ids
	 *            IDs of all highlighted elements.
	 * @param idType
	 *            {@link IDType} of the ids.
	 * @param updateSource
	 *            Source that triggered the highlight.
	 */
	public void highlightChanged(Set<Object> ids, IDType idType, ILabeled updateSource);

	/**
	 * Called when elements are selected.
	 *
	 * @param ids
	 *            IDs of all selected elements.
	 * @param idType
	 *            {@link IDType} of the ids.
	 * @param updateSource
	 *            Source that triggered the selection.
	 */
	public void selectionChanged(Set<Object> ids, IDType idType, ILabeled updateSource);

	/**
	 * Called when elements are filtered.
	 *
	 * @param ids
	 *            IDs of all filtered elements (elements that remain after filtering).
	 * @param idType
	 *            {@link IDType} of the ids.
	 * @param updateSource
	 *            Source that triggered the filter.
	 */
	public void filterChanged(Set<Object> ids, IDType idType, ILabeled updateSource);

}
