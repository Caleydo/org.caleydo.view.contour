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

	// @ListenTo
	// public void onApplyIDFilter(IDUpdateEvent event) {
	// Set<?> foreignIDs = event.getIds();
	// IDType foreignIDType = event.getIdType();
	// IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(perspective.getIdType());
	// Set<Object> mappedIDs = new HashSet<>();
	// for (Object id : foreignIDs) {
	// Set<Object> ids = mappingManager.getIDAsSet(foreignIDType, perspective.getIdType(), id);
	// if (ids != null) {
	// mappedIDs.addAll(ids);
	// }
	// }
	//
	// setFilteredItems(mappedIDs);
	// }

	// @Override
	// protected void setFilteredItems(Set<Object> ids) {
	// for (Entry<Object, GLElement> entry : mapIDToElement.entrySet()) {
	//
	// GLElement item = entry.getValue();
	// Group group = (Group) entry.getKey();
	//
	// boolean visible = false;
	//
	// for (int index = group.getStartIndex(); index <= group.getEndIndex(); index++) {
	// if (ids.contains(perspective.getVirtualArray().get(index))) {
	// itemList.show(item);
	// visible = true;
	// itemList.asGLElement().relayout();
	// break;
	// }
	// }
	//
	// if (!visible) {
	// itemList.hide(item);
	// itemList.asGLElement().relayout();
	// }
	//
	// }
	// }

	@Override
	public String getLabel() {
		return perspective.getLabel();
	}

	@Override
	protected void setContent() {
		for (final Group group : groupList) {
			addTextElement(group.getLabel(), group);
			// ActionBasedContextMenuItem contextMenuItem = new ActionBasedContextMenuItem("Apply Filter", new
			// Runnable() {
			// @Override
			// public void run() {
			// Set<Object> ids = new HashSet<>();
			// for (GLElement element : itemList.getSelectedElements()) {
			// Group g = (Group) mapIDToElement.inverse().get(element);
			// ids.addAll(getBroadcastingIDsFromElementID(g));
			// }
			//
			// IDFilterEvent event = new IDFilterEvent(ids, perspective.getIdType());
			// event.setSender(GroupingColumn.this);
			// EventPublisher.trigger(event);
			//
			// }
			// });
			//
			// itemList.addContextMenuItem(item, contextMenuItem);
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
		// for (int index = group.getStartIndex(); index <= group.getEndIndex(); index++) {
		// if (ids.contains(perspective.getVirtualArray().get(index))) {
		// itemList.show(item);
		// visible = true;
		// itemList.asGLElement().relayout();
		// break;
		// }
		// }
		Set<Object> elementIDs = new HashSet<>(groups.size());
		for (Group group : groups) {
			elementIDs.add(group);
		}
		return elementIDs;
	}

}
