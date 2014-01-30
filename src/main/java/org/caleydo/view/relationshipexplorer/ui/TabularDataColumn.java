/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.IDSpecification;
import org.caleydo.core.view.contextmenu.ActionBasedContextMenuItem;
import org.caleydo.core.view.opengl.layout2.GLElement;

/**
 * @author Christian
 *
 */
public class TabularDataColumn extends AEntityColumn {

	protected final ATableBasedDataDomain dataDomain;
	protected final IDCategory itemIDCategory;
	protected final TablePerspective tablePerspective;
	protected final IDType itemIDType;
	protected final VirtualArray va;
	protected final Perspective perspective;

	// protected EventBasedSelectionManager selectionManager;

	// protected Map<Object, SimpleDataRenderer> itemMap = new HashMap<>();
	// protected List<SimpleDataRenderer> items = new ArrayList<>();
	// protected EntityColumn entityColumn;

	// protected ColumnBody columnBody;

	public TabularDataColumn(TablePerspective tablePerspective, IDCategory itemIDCategory) {
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
			Perspective dimensionPerspective) {
		SimpleDataRenderer renderer = new SimpleDataRenderer(dd, recordIDType, recordID, dimensionPerspective);

		addElement(renderer, recordID);
		IDMappingManager m = IDMappingManagerRegistry.get().getIDMappingManager(recordIDType);
		IDType origIDType;
		IDSpecification spec = dd.getDataSetDescription().getColumnIDSpecification();
		if (spec.getIdCategory().equalsIgnoreCase(recordIDType.getIDCategory().getCategoryName())) {
			origIDType = IDType.getIDType(spec.getIdType());
		} else {
			origIDType = IDType.getIDType(dd.getDataSetDescription().getRowIDSpecification().getIdType());
		}

		// itemList.add(renderer);
		Object origID = m.getID(recordIDType, origIDType, recordID);

		ActionBasedContextMenuItem contextMenuItem = new ActionBasedContextMenuItem("Apply Filter", new Runnable() {
			@Override
			public void run() {
				Set<Object> ids = new HashSet<>();
				for (GLElement element : itemList.getSelectedElements()) {
					ids.add(mapIDToElement.inverse().get(element));
				}

				IDFilterEvent event = new IDFilterEvent(ids, recordIDType);
				event.setSender(TabularDataColumn.this);
				EventPublisher.trigger(event);

			}
		});

		itemList.addContextMenuItem(renderer, contextMenuItem);
		itemList.setElementTooltip(renderer, origID.toString());

		// itemMap.put(recordID, renderer);
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

	// @Override
	// public void notifyOfSelectionChange(EventBasedSelectionManager selectionManager) {
		// if (selectionManager == this.selectionManager) {
		// updateHighlights();
		// }

	// }

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
	protected void setContent() {
		for (int id : va) {
			addItem(dataDomain, itemIDType, id, perspective);
		}

	}

}
