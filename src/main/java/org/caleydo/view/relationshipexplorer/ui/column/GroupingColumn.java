/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.GroupCollection;
import org.caleydo.view.relationshipexplorer.ui.column.ItemComparators.TextComparator;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;

/**
 * @author Christian
 *
 */
public class GroupingColumn extends AEntityColumn {

	protected final ATableBasedDataDomain dataDomain;
	protected final Perspective perspective;
	protected final GroupList groupList;
	protected final GroupCollection groupCollection;

	public GroupingColumn(GroupCollection groupCollection, ConTourElement relationshipExplorer) {
		super(groupCollection, relationshipExplorer);
		this.groupCollection = groupCollection;
		this.perspective = groupCollection.getPerspective();
		this.dataDomain = (ATableBasedDataDomain) perspective.getDataDomain();
		this.groupList = perspective.getVirtualArray().getGroupList();
		currentComparator = new CompositeComparator<>(ItemComparators.SELECTED_ITEMS_COMPARATOR, getDefaultComparator());
	}

	@Override
	public IInvertibleComparator<NestableItem> getDefaultComparator() {
		return new TextComparator(groupCollection);
	}


}
