/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.operation;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;

import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public enum ESetOperation {
	REMOVE(AEntityColumn.class.getResource("/org/caleydo/view/relationshipexplorer/icons/delete.png")), /*
																										 * REPLACE(
																										 * AEntityColumn
																										 * .
																										 * class.getResource
																										 * (
																										 * "/org/caleydo/view/relationshipexplorer/icons/delete.png"
																										 * )),
																										 */
	INTERSECTION(
			AEntityColumn.class.getResource("/org/caleydo/view/relationshipexplorer/icons/delete.png")), UNION(
			AEntityColumn.class.getResource("/org/caleydo/view/relationshipexplorer/icons/add.png"));

	private URL icon;

	private ESetOperation(URL icon) {
		this.icon = icon;
	}

	public Set<Object> apply(Set<Object> set1, Set<Object> set2) {
		switch (this) {
		case REMOVE:
			Set<Object> result = new HashSet<>(set2);
			result.removeAll(set1);
			return result;
			// case REPLACE:
			// return set1;
		case INTERSECTION:
			return Sets.intersection(set1, set2);
		case UNION:
			return Sets.union(set1, set2);
		default:
			return null;
		}
	}

	/**
	 * @return the icon, see {@link #icon}
	 */
	public URL getIcon() {
		return icon;
	}
}
