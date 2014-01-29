/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.view.relationshipexplorer.ui.EntityColumn.IEntityColumnContentProvider;

import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public class TabularDatasetContentProvider implements IEntityColumnContentProvider, IEventBasedSelectionManagerUser {

	protected final ATableBasedDataDomain dataDomain;
	protected final IDCategory itemIDCategory;
	protected final TablePerspective tablePerspective;
	protected final IDType itemIDType;
	protected final VirtualArray va;
	protected final Perspective perspective;

	// protected EventBasedSelectionManager selectionManager;

	protected Map<Object, SimpleDataRenderer> itemMap = new HashMap<>();
	protected List<SimpleDataRenderer> items = new ArrayList<>();
	protected EntityColumn entityColumn;

	// protected ColumnBody columnBody;

	public TabularDatasetContentProvider(TablePerspective tablePerspective, IDCategory itemIDCategory) {
		dataDomain = tablePerspective.getDataDomain();
		this.itemIDCategory = itemIDCategory;
		this.tablePerspective = tablePerspective;

		// if (dataDomain.getTable().isDataHomogeneous()) {

		if (dataDomain.getDimensionIDCategory() == itemIDCategory) {
			va = tablePerspective.getDimensionPerspective().getVirtualArray();
			itemIDType = tablePerspective.getDimensionPerspective().getIdType();
			perspective = tablePerspective.getRecordPerspective();

		} else {
			va = tablePerspective.getRecordPerspective().getVirtualArray();
			itemIDType = tablePerspective.getRecordPerspective().getIdType();
			perspective = tablePerspective.getDimensionPerspective();
		}

		// selectionManager = new EventBasedSelectionManager(this, itemIDType);
		// selectionManager.registerEventListeners();
		// } else {
		// if (dataDomain.getDimensionIDCategory() == itemIDCategory) {
		// for (int id : tablePerspective.getDimensionPerspective().getVirtualArray()) {
		// addInhomogeneousRenderer(dataDomain, tablePerspective.getDimensionPerspective().getIdType(), id,
		// tablePerspective.getRecordPerspective());
		// }
		// } else {
		// for (int id : tablePerspective.getRecordPerspective().getVirtualArray()) {
		// addInhomogeneousRenderer(dataDomain, tablePerspective.getRecordPerspective().getIdType(), id,
		// tablePerspective.getDimensionPerspective());
		// }
		// }
		// }
	}

	// protected void addBarChartRenderer(ATableBasedDataDomain dd, IDType recordIDType, int recordID,
	// Perspective dimensionPerspective) {
	// BarChartRenderer renderer = new BarChartRenderer(dd, recordIDType, recordID, dimensionPerspective);
	// renderer.setSize(Float.NaN, BarChartRenderer.MIN_HEIGHT);
	// items.add(renderer);
	// }

	protected void addItem(ATableBasedDataDomain dd, final IDType recordIDType, final int recordID,
			Perspective dimensionPerspective, GLElementList itemList) {
		SimpleDataRenderer renderer = new SimpleDataRenderer(dd, recordIDType, recordID, dimensionPerspective);
		// EntityColumnItem<SimpleDataRenderer> item = new EntityColumnItem<>();
		// item.setContent(renderer);
		// item.setSize(Float.NaN, SimpleDataRenderer.MIN_HEIGHT);
		// items.add(renderer);
		IDMappingManager m = IDMappingManagerRegistry.get().getIDMappingManager(recordIDType);
		IDType origIDType;
		IDSpecification spec = dd.getDataSetDescription().getColumnIDSpecification();
		if (spec.getIdCategory().equalsIgnoreCase(recordIDType.getIDCategory().getCategoryName())) {
			origIDType = IDType.getIDType(spec.getIdType());
		} else {
			origIDType = IDType.getIDType(dd.getDataSetDescription().getRowIDSpecification().getIdType());
		}

		itemList.add(renderer);
		Object origID = m.getID(recordIDType, origIDType, recordID);

		IDFilterEvent event = new IDFilterEvent(Sets.newHashSet(recordID), recordIDType);
		event.setSender(this);
		itemList.addContextMenuItem(renderer, new GenericContextMenuItem("Apply Filter", event));
		itemList.setElementTooltip(renderer, origID.toString());

		itemMap.put(recordID, renderer);
		//
		// item.onPick(new IPickingListener() {
		//
		// @Override
		// public void pick(Pick pick) {
		// if (pick.getPickingMode() == PickingMode.CLICKED) {
		// SelectionCommands.clearSelections();
		// // selectionManager.clearSelection(SelectionType.SELECTION);
		// selectionManager.triggerSelectionUpdateEvent();
		// selectionManager.addToType(SelectionType.SELECTION, recordID);
		//
		// selectionManager.triggerSelectionUpdateEvent();
		// updateHighlights();
		// }
		// }
		// });

	}

	// @Override
	// public void setColumnBody(ColumnBody body) {
	// // body.setMinSize(getMinSize());
	// columnBody = body;
	// }

	// @Override
	// public List<EntityColumnItem<?>> getContent() {
	// return items;
	// }

	@Override
	public String getLabel() {
		return dataDomain.getLabel();
	}

	@Override
	public void notifyOfSelectionChange(EventBasedSelectionManager selectionManager) {
		// if (selectionManager == this.selectionManager) {
		// updateHighlights();
		// }

	}

	@ListenTo
	public void onApplyIDFilter(IDFilterEvent event) {
		Set<?> foreignIDs = event.getIds();
		IDType foreignIDType = event.getIdType();
		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(itemIDType);
		Set<Object> mappedIDs = new HashSet<>();
		for (Object id : foreignIDs) {
			Set<Object> ids = mappingManager.getIDAsSet(foreignIDType, itemIDType, id);
			if (ids != null) {
				mappedIDs.addAll(ids);
			}
		}

		setFilteredItems(mappedIDs);
	}

	protected void setFilteredItems(Set<Object> ids) {
		for (Entry<Object, SimpleDataRenderer> entry : itemMap.entrySet()) {

			SimpleDataRenderer item = entry.getValue();
			// item.setHighlight(false);
			boolean visible = false;

			if (ids.contains(entry.getKey())) {
				visible = true;
				// item.setHighlight(true);
				// item.setHighlightColor(SelectionType.SELECTION.getColor());
				entityColumn.getItemList().show(item);
				// item.setVisibility(EVisibility.PICKABLE);
				entityColumn.getItemList().asGLElement().relayout();
			}

			if (!visible) {
				entityColumn.getItemList().hide(item);
				entityColumn.getItemList().asGLElement().relayout();
			}

		}
	}

	protected void updateHighlights() {
		// for (Entry<Object, EntityColumnItem<?>> entry : itemMap.entrySet()) {
		//
		// EntityColumnItem<?> item = entry.getValue();
		// item.setHighlight(false);
		//
		// Set<Integer> selectionIDs = selectionManager.getElements(SelectionType.MOUSE_OVER);
		// if (selectionIDs.contains(entry.getKey())) {
		// item.setHighlight(true);
		// item.setHighlightColor(SelectionType.MOUSE_OVER.getColor());
		// // item.setVisibility(EVisibility.PICKABLE);
		// columnBody.getParent().relayout();
		// }
		//
		// selectionIDs = selectionManager.getElements(SelectionType.SELECTION);
		// if (selectionIDs.contains(entry.getKey())) {
		// item.setHighlight(true);
		// item.setHighlightColor(SelectionType.SELECTION.getColor());
		// // item.setVisibility(EVisibility.PICKABLE);
		// columnBody.getParent().relayout();
		// }
		//
		// // if (!item.isHighlight()) {
		// // item.setVisibility(EVisibility.NONE);
		// // columnBody.getParent().relayout();
		// // }
		//
		// }

	}

	@Override
	public void takeDown() {
		// selectionManager.unregisterEventListeners();
		// selectionManager = null;
	}

	// @Override
	// public void updateContent(MinSizeElementList<?> itemList) {
	// // for (EntityColumnItem<?> item : items) {
	// // itemList.add(item.getContent());
	// // }
	// itemList.add(new SimpleDataRenderer(null, null, 1, null));
	// }

	@Override
	public void setEntityColumn(EntityColumn column) {
		this.entityColumn = column;

	}

	// @Override
	// public List<GLElement> getContent() {
	// List<GLElement> content = new ArrayList<>(items.size());
	// content.addAll(items);
	// return content;
	// }

	@Override
	public void setContent(GLElementList itemList) {
		for (int id : va) {
			addItem(dataDomain, itemIDType, id, perspective, itemList);
		}
	}

}
