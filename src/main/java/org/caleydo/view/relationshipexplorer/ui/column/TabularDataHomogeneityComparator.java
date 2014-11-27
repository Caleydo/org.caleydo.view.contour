/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.view.relationshipexplorer.ui.list.NestableColumn;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;

/**
 * Compares the homogeneity of children, which represent numerical tabular data using pearson correlation.
 *
 * @author Christian
 *
 */
public class TabularDataHomogeneityComparator extends AInvertibleComparator<NestableItem> {

	protected NestableColumn childColumn;

	@Override
	public int compare(NestableItem item1, NestableItem item2) {
		List<NestableItem> items = item1.getChildItems(childColumn);
		Set<Object> elementIDs = new HashSet<>(items.size());
		for (NestableItem item : items) {
			elementIDs.addAll(item.getElementData());
		}

		return 0;
	}

}
