/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.PathwayCollection;
import org.caleydo.view.relationshipexplorer.ui.column.ItemComparators.TextComparator;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;

/**
 * @author Christian
 *
 */
public class PathwayColumn extends AEntityColumn {

	protected final PathwayCollection pathwayCollection;

	/**
	 * @param relationshipExplorer
	 */
	public PathwayColumn(PathwayCollection pathwayCollection, ConTourElement relationshipExplorer) {
		super(pathwayCollection, relationshipExplorer);
		this.pathwayCollection = pathwayCollection;
		currentComparator = new CompositeComparator<>(ItemComparators.SELECTED_ITEMS_COMPARATOR, getDefaultComparator());
	}

	@Override
	public IInvertibleComparator<NestableItem> getDefaultComparator() {
		return new TextComparator(pathwayCollection);
	}

}
