/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.id.IDType;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;

/**
 * @author Christian
 *
 */
public class GroupCollection extends AEntityCollection {

	protected final ATableBasedDataDomain dataDomain;
	protected final Perspective perspective;
	protected final GroupList groupList;

	public GroupCollection(Perspective perspective, RelationshipExplorerElement relationshipExplorer) {
		super(relationshipExplorer);
		this.perspective = perspective;
		this.dataDomain = (ATableBasedDataDomain) perspective.getDataDomain();
		this.groupList = perspective.getVirtualArray().getGroupList();
		allElementIDs.addAll(groupList.getGroups());
		filteredElementIDs.addAll(allElementIDs);
		setLabel(perspective.getLabel());
	}

	@Override
	public IDType getBroadcastingIDType() {
		return perspective.getIdType();
	}

	@Override
	public Set<Object> getBroadcastingIDsFromElementID(Object elementID) {
		Group g = (Group) elementID;
		Set<Object> bcIDs = new HashSet<>();
		bcIDs.addAll(perspective.getVirtualArray().getIDsOfGroup(g.getGroupIndex()));
		return bcIDs;
	}

	@Override
	public Set<Object> getElementIDsFromBroadcastingID(Integer broadcastingID) {

		List<Group> groups = perspective.getVirtualArray().getGroupOf(broadcastingID);

		Set<Object> elementIDs = new HashSet<>(groups.size());
		for (Group group : groups) {
			elementIDs.add(group);
		}
		return elementIDs;
	}

	@Override
	public IColumnModel createColumnModel() {
		GroupingColumn column = new GroupingColumn(this, relationshipExplorer);
		column.init();
		return column;
	}

	public IDType getMappingIDType() {
		return getBroadcastingIDType();
	}

}
