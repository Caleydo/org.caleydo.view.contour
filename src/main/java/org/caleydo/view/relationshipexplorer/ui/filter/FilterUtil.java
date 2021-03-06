/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.filter;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import com.google.common.base.Predicate;

/**
 * @author Christian
 *
 */
public final class FilterUtil {

	public static final IEntityFilter ALL_PASS_FILTER = new IEntityFilter() {

		@Override
		public boolean apply(Object input) {
			return true;
		}

		@Override
		public String getDescription() {
			return "All pass";
		}
	};

	public static <T> Set<T> filter(Collection<T> itemsToFilter, Predicate<T> filter) {
		Set<T> resultSet = new HashSet<>();
		for (T item : itemsToFilter) {
			if (filter.apply(item)) {
				resultSet.add(item);
			}
		}
		return resultSet;
	}

}
