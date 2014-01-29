/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;

import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public class GroupingContentProvider extends ATextualContentProvider {

	protected final ATableBasedDataDomain dataDomain;
	protected final Perspective perspective;
	protected final GroupList groupList;

	protected Map<Group, MinSizeTextElement> itemMap = new HashMap<>();

	public GroupingContentProvider(Perspective perspective) {

		this.perspective = perspective;
		this.dataDomain = (ATableBasedDataDomain) perspective.getDataDomain();
		this.groupList = perspective.getVirtualArray().getGroupList();



		// for (IDataDomain dataDomain : DataDomainManager.get().getAllDataDomains()) {
		// if (dataDomain instanceof ATableBasedDataDomain) {
		// ATableBasedDataDomain dd = (ATableBasedDataDomain) dataDomain;
		// DataSetDescription desc = dd.getDataSetDescription();
		// if (desc.getColumnIDSpecification().getIdType().equals("COMPOUND_ID")
		// || desc.getRowIDSpecification().getIdType().equals("COMPOUND_ID")) {
		//
		// IDType compoundIDType = IDType.getIDType("COMPOUND_ID");
		// if (dd.getDimensionIDCategory() == compoundIDType.getIDCategory()) {
		// Set<String> perspectiveIDs = dd.getDimensionPerspectiveIDs();
		// String defaultPerspectiveID = dd.getDefaultTablePerspective().getDimensionPerspective()
		// .getPerspectiveID();
		// for (String perspectiveID : perspectiveIDs) {
		// if (!perspectiveID.equals(defaultPerspectiveID)) {
		// GroupList groupList = dd.getDimensionVA(perspectiveID).getGroupList();
		//
		// for (Group group : groupList) {
		// GLElement el = new GLElement(GLRenderers.drawText(group.getLabel()));
		// el.setSize(Float.NaN, ITEM_HEIGHT);
		// items.add(el);
		// }
		// return;
		// }
		// }
		//
		// } else {
		// Set<String> perspectiveIDs = dd.getRecordPerspectiveIDs();
		// String defaultPerspectiveID = dd.getDefaultTablePerspective().getRecordPerspective()
		// .getPerspectiveID();
		// for (String perspectiveID : perspectiveIDs) {
		// if (!perspectiveID.equals(defaultPerspectiveID)) {
		// GroupList groupList = dd.getRecordVA(perspectiveID).getGroupList();
		// for (Group group : groupList) {
		// GLElement el = new GLElement(GLRenderers.drawText(group.getLabel()));
		// el.setSize(Float.NaN, ITEM_HEIGHT);
		// items.add(el);
		// }
		// return;
		// }
		// }
		// }
		// return;
		// }
		// }

	}

	@ListenTo
	public void onApplyIDFilter(IDFilterEvent event) {
		Set<?> foreignIDs = event.getIds();
		IDType foreignIDType = event.getIdType();
		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(perspective.getIdType());
		Set<Object> mappedIDs = new HashSet<>();
		for (Object id : foreignIDs) {
			Set<Object> ids = mappingManager.getIDAsSet(foreignIDType, perspective.getIdType(), id);
			if (ids != null) {
				mappedIDs.addAll(ids);
			}
		}

		setFilteredItems(mappedIDs);
		//
		// Set<Group> filteredGroups = new HashSet<>();
		// for (Object id : mappedIDs) {
		// filteredGroups.addAll(perspective.getVirtualArray().getGroupOf((Integer) id));
		// }
		// for (Entry<Group, EntityColumnItem<?>> entry : itemMap.entrySet()) {
		// Group group = entry.getKey();
		// EntityColumnItem<?> item = entry.getValue();
		//
		// if (filteredGroups.contains(group)) {
		// item.setHighlight(true);
		// item.setHighlightColor(SelectionType.SELECTION.getColor());
		// if (item.getVisibility() == EVisibility.NONE) {
		// item.setVisibility(EVisibility.PICKABLE);
		// columnBody.getParent().relayout();
		// }
		// } else {
		// item.setHighlight(false);
		// if (item.getVisibility() != EVisibility.NONE) {
		// item.setVisibility(EVisibility.NONE);
		// columnBody.getParent().relayout();
		// }
		// }
		// }
	}

	protected void setFilteredItems(Set<Object> ids) {
		for (Entry<Group, MinSizeTextElement> entry : itemMap.entrySet()) {

			MinSizeTextElement item = entry.getValue();
			Group group = entry.getKey();

			boolean visible = false;
			// item.setHighlight(false);

			for (int index = group.getStartIndex(); index <= group.getEndIndex(); index++) {
				if (ids.contains(perspective.getVirtualArray().get(index))) {
					// item.setHighlight(true);
					// item.setHighlightColor(SelectionType.SELECTION.getColor());
					entityColumn.getItemList().show(item);
					visible = true;
					entityColumn.getItemList().asGLElement().relayout();
					break;
				}
			}

			if (!visible) {
				entityColumn.getItemList().hide(item);
				entityColumn.getItemList().asGLElement().relayout();
			}

		}
	}

	@Override
	public String getLabel() {
		return perspective.getLabel();
	}

	@Override
	public void setContent(GLElementList itemList) {
		for (final Group group : groupList) {
			MinSizeTextElement item = addItem(group.getLabel());
			itemMap.put(group, item);
			item.onPick(new IPickingListener() {

				@Override
				public void pick(Pick pick) {

					if (pick.getPickingMode() == PickingMode.CLICKED) {
						// Perspective p = GroupingContentProvider.this.perspective;
						// IDFilterEvent event = new IDFilterEvent(Sets.newHashSet(p.getVirtualArray().getIDsOfGroup(
						// group.getGroupIndex())), p.getIdType());
						// event.setSender(GroupingContentProvider.this);
						// EventPublisher.trigger(event);
						// selectionManager.clearSelection(SelectionType.SELECTION);
						// selectionManager.addToType(SelectionType.SELECTION, (Integer) id);
						// selectionManager.triggerSelectionUpdateEvent();
						// updateHighlights();
					}
				}
			});
			itemList.add(item);
			IDFilterEvent event = new IDFilterEvent(Sets.newHashSet(perspective.getVirtualArray().getIDsOfGroup(
					group.getGroupIndex())), perspective.getIdType());
			event.setSender(this);
			itemList.addContextMenuItem(item, new GenericContextMenuItem("Apply Filter", event));
		}
	}

}
