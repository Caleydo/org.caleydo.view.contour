/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.collection;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.id.IDType;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.column.factory.ColumnFactories;
import org.caleydo.view.relationshipexplorer.ui.column.factory.IColumnFactory;

/**
 * @author Christian
 *
 */
public class GroupCollection extends AEntityCollection {

	protected final ATableBasedDataDomain dataDomain;
	protected final Perspective perspective;
	protected final GroupList groupList;

	public GroupCollection(Perspective perspective, IElementIDProvider elementIDProvider,
			RelationshipExplorerElement relationshipExplorer) {
		super(relationshipExplorer);
		this.perspective = perspective;
		this.dataDomain = (ATableBasedDataDomain) perspective.getDataDomain();
		this.groupList = perspective.getVirtualArray().getGroupList();
		if (elementIDProvider == null)
			elementIDProvider = getDefaultElementIDProvider(perspective);

		allElementIDs.addAll(elementIDProvider.getElementIDs());
		filteredElementIDs.addAll(allElementIDs);
		setLabel(perspective.getLabel());
	}

	@Override
	public IDType getBroadcastingIDType() {
		return perspective.getIdType();
	}

	@Override
	protected Set<Object> getBroadcastIDsFromElementID(Object elementID) {
		Group g = (Group) elementID;
		Set<Object> bcIDs = new HashSet<>();
		bcIDs.addAll(perspective.getVirtualArray().getIDsOfGroup(g.getGroupIndex()));
		return bcIDs;
	}

	@Override
	public Set<Object> getElementIDsFromBroadcastID(Object broadcastingID) {

		List<Group> groups = perspective.getVirtualArray().getGroupOf((Integer) broadcastingID);

		Set<Object> elementIDs = new HashSet<>(groups.size());
		for (Group group : groups) {
			elementIDs.add(group);
		}
		return elementIDs;
	}

	@Override
	protected IColumnFactory getDefaultColumnFactory() {
		return ColumnFactories.createDefaultGroupColumnFactory(this, relationshipExplorer);
	}

	@Override
	public IDType getMappingIDType() {
		return getBroadcastingIDType();
	}

	/**
	 * @return the dataDomain, see {@link #dataDomain}
	 */
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	/**
	 * @return the perspective, see {@link #perspective}
	 */
	public Perspective getPerspective() {
		return perspective;
	}

	/**
	 * @return the groupList, see {@link #groupList}
	 */
	public GroupList getGroupList() {
		return groupList;
	}

	public static IElementIDProvider getDefaultElementIDProvider(final Perspective perspective) {
		return new IElementIDProvider() {
			@Override
			public Set<Object> getElementIDs() {
				return new HashSet<Object>(perspective.getVirtualArray().getGroupList().getGroups());
			}
		};
	}

}
