/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.MappingType;

/**
 * @author Christian
 *
 */
public class GroupingColumn extends ATextColumn {

	protected final ATableBasedDataDomain dataDomain;
	protected final Perspective perspective;
	protected final GroupList groupList;

	public GroupingColumn(Perspective perspective, RelationshipExplorerElement relationshipExplorer) {
		super(relationshipExplorer);
		this.perspective = perspective;
		this.dataDomain = (ATableBasedDataDomain) perspective.getDataDomain();
		this.groupList = perspective.getVirtualArray().getGroupList();
	}


	@Override
	public String getLabel() {
		return perspective.getLabel();
	}

	@Override
	protected void setContent() {
		for (final Group group : groupList) {
			addTextElement(group.getLabel(), group);
		}

	}

	@Override
	protected IDType getBroadcastingIDType() {
		return perspective.getIdType();
	}

	@Override
	protected Set<Object> getBroadcastingIDsFromElementID(Object elementID) {
		Group g = (Group) elementID;
		Set<Object> bcIDs = new HashSet<>();
		bcIDs.addAll(perspective.getVirtualArray().getIDsOfGroup(g.getGroupIndex()));
		return bcIDs;
	}

	@Override
	protected Set<Object> getElementIDsFromBroadcastingID(Integer broadcastingID) {

		List<Group> groups = perspective.getVirtualArray().getGroupOf(broadcastingID);

		Set<Object> elementIDs = new HashSet<>(groups.size());
		for (Group group : groups) {
			elementIDs.add(group);
		}
		return elementIDs;
	}

	@Override
	protected AEntityColumn getNearestMappingColumn(List<MappingType> path) {

		List<AEntityColumn> foreignColumns = relationshipExplorer
				.getColumnsWithBroadcastIDType(getBroadcastingIDType());
		foreignColumns.remove(this);
		for (AEntityColumn column : foreignColumns) {
			if (column instanceof TabularDataColumn) {
				return column;
			}
		}
		AEntityColumn foreignColumn = this;

		if (path != null) {
			for (int i = path.size() - 1; i >= 0; i--) {
				foreignColumn = getForeignColumnWithMappingIDType(path.get(i).getFromIDType());
				if (foreignColumn != null)
					break;
			}
		}
		return foreignColumn;
	}

	@Override
	protected IDType getMappingIDType() {
		return getBroadcastingIDType();
	}

}
