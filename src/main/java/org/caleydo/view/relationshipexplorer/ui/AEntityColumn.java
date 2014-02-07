/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.id.MappingType;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.view.relationshipexplorer.ui.ASetBasedColumnOperation.ESetOperation;
import org.caleydo.view.relationshipexplorer.ui.GLElementList.IElementSelectionListener;
import org.eclipse.nebula.widgets.nattable.util.ComparatorChain;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public abstract class AEntityColumn extends AnimatedGLElementContainer implements IElementSelectionListener, ILabeled {
	protected static final int HEADER_HEIGHT = 20;
	protected static final int HEADER_BODY_SPACING = 5;

	protected static final Integer DATA_KEY = Integer.valueOf(0);
	protected static final Integer MAPPING_KEY = Integer.valueOf(1);

	protected static final Integer SELECTED_ELEMENTS_KEY = Integer.valueOf(2);
	protected static final Integer FILTERED_ELEMENTS_KEY = Integer.valueOf(3);
	protected static final Integer ALL_ELEMENTS_KEY = Integer.valueOf(4);

	protected KeyBasedGLElementContainer<GLElement> header;
	protected GLElementList itemList = new GLElementList();
	protected BiMap<Object, GLElement> mapIDToElement = HashBiMap.create();

	protected Set<Object> selectedElementIDs = new HashSet<>();
	protected Map<Object, GLElement> mapFilteredElements = new HashMap<>();
	protected RelationshipExplorerElement relationshipExplorer;

	// protected static class FilterEvent extends ADirectedEvent {
	//
	// protected IColumnFilter filter;
	//
	// public FilterEvent(IColumnFilter filter) {
	// this.filter = filter;
	// }
	// }

	protected static class ContextMenuOperationEvent extends ADirectedEvent {

		protected final ESetOperation setOperation;

		public ContextMenuOperationEvent(ESetOperation setOperation) {
			this.setOperation = setOperation;
		}
	}

	protected static class MappingBarComparator implements Comparator<GLElement> {

		private final Object key;

		public MappingBarComparator(Object key) {
			this.key = key;
		}

		@Override
		public int compare(GLElement el1, GLElement el2) {
			@SuppressWarnings("unchecked")
			KeyBasedGLElementContainer<SimpleBarRenderer> layeredBars1 = (KeyBasedGLElementContainer<SimpleBarRenderer>) ((KeyBasedGLElementContainer<GLElement>) el1)
					.getElement(MAPPING_KEY);
			@SuppressWarnings("unchecked")
			KeyBasedGLElementContainer<SimpleBarRenderer> layeredBars2 = (KeyBasedGLElementContainer<SimpleBarRenderer>) ((KeyBasedGLElementContainer<GLElement>) el2)
					.getElement(MAPPING_KEY);

			return Float.compare(layeredBars2.getElement(key).getNormalizedValue(), layeredBars1.getElement(key)
					.getNormalizedValue());
		}
	}

	protected static final MappingBarComparator SELECTED_ELEMENTS_COMPARATOR = new MappingBarComparator(
			SELECTED_ELEMENTS_KEY);
	protected static final MappingBarComparator FILTERED_ELEMENTS_COMPARATOR = new MappingBarComparator(
			FILTERED_ELEMENTS_KEY);
	protected static final MappingBarComparator ALL_ELEMENTS_COMPARATOR = new MappingBarComparator(ALL_ELEMENTS_KEY);

	public AEntityColumn(RelationshipExplorerElement relationshipExplorer) {
		super(GLLayouts.flowVertical(HEADER_BODY_SPACING));
		this.relationshipExplorer = relationshipExplorer;
		header = new KeyBasedGLElementContainer<>(GLLayouts.sizeRestrictiveFlowHorizontal(2));
		header.setHorizontalFlowMinSizeProvider(2);
		header.setSize(Float.NaN, HEADER_HEIGHT);
		add(header);
		add(itemList.asGLElement());
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);

		setContent();

		itemList.addContextMenuItems(getFilterContextMenuItems());

		header.setElement(DATA_KEY, new GLElement(GLRenderers.drawText(getLabel(), VAlign.CENTER)));
		itemList.addElementSelectionListener(this);
		Comparator<GLElement> c = getDefaultElementComparator();
		itemList.sortBy(c);
		mapFilteredElements.putAll(mapIDToElement);
	}

	protected List<AContextMenuItem> getFilterContextMenuItems() {

		// Runnable filterRunnable = new Runnable() {
		// @Override
		// public void run() {
		// Set<Object> broadcastIDs = new HashSet<>();
		// Set<Object> elementIDs = new HashSet<>();
		// for (GLElement element : itemList.getSelectedElements()) {
		// Object elementID = mapIDToElement.inverse().get(element);
		// elementIDs.add(elementID);
		// broadcastIDs.addAll(getBroadcastingIDsFromElementID(elementID));
		// }
		// // Avoid direct calling of setFilteredItems due to synchronization issues
		// EventPublisher.trigger(new FilterEvent(elementIDs).to(AEntityColumn.this));
		//
		// triggerIDUpdate(broadcastIDs, EUpdateType.FILTER);
		//
		// }
		// };

		// AContextMenuItem replaceFilterItem = new GenericContextMenuItem("Replace", new FilterEvent(
		// new SelectionBasedFilter(ESetOperation.REPLACE)).to(this));
		// AContextMenuItem andFilterITem = new GenericContextMenuItem("Reduce", new FilterEvent(new
		// SelectionBasedFilter(
		// ESetOperation.INTERSECTION)).to(this));
		// AContextMenuItem orFilterITem = new GenericContextMenuItem("Add", new FilterEvent(new SelectionBasedFilter(
		// ESetOperation.UNION)).to(this));

		AContextMenuItem replaceFilterItem = new GenericContextMenuItem("Replace", new ContextMenuOperationEvent(
				ESetOperation.REPLACE).to(this));
		AContextMenuItem andFilterITem = new GenericContextMenuItem("Reduce", new ContextMenuOperationEvent(
				ESetOperation.INTERSECTION).to(this));
		AContextMenuItem orFilterITem = new GenericContextMenuItem("Add", new ContextMenuOperationEvent(
				ESetOperation.UNION).to(this));
		return Lists.newArrayList(replaceFilterItem, andFilterITem, orFilterITem);
	}

	protected void addElement(GLElement element, Object elementID) {

		KeyBasedGLElementContainer<GLElement> row = new KeyBasedGLElementContainer<>(
				GLLayouts.sizeRestrictiveFlowHorizontal(2));
		row.setHorizontalFlowMinSizeProvider(2);
		row.setElement(DATA_KEY, element);
		mapIDToElement.put(elementID, row);
		itemList.add(row);
	}

	protected KeyBasedGLElementContainer<SimpleBarRenderer> createLayeredBarRenderer() {
		KeyBasedGLElementContainer<SimpleBarRenderer> barLayerRenderer = new KeyBasedGLElementContainer<>(
				GLLayouts.LAYERS);
		barLayerRenderer.setSize(80, Float.NaN);
		barLayerRenderer.setLayeredMinSizeProvider();
		barLayerRenderer.setElement(ALL_ELEMENTS_KEY, createDefaultBarRenderer(Color.LIGHT_GRAY));
		barLayerRenderer.setElement(FILTERED_ELEMENTS_KEY, createDefaultBarRenderer(Color.GRAY));
		barLayerRenderer
				.setElement(SELECTED_ELEMENTS_KEY, createDefaultBarRenderer(SelectionType.SELECTION.getColor()));
		return barLayerRenderer;
	}

	protected SimpleBarRenderer createDefaultBarRenderer(Color color) {
		SimpleBarRenderer renderer = new SimpleBarRenderer(0, true);
		renderer.setMinSize(new Vec2f(80, 0));
		renderer.setSize(80, Float.NaN);
		renderer.setColor(color);
		renderer.setBarWidth(12);
		return renderer;
	}

	@Override
	public String getLabel() {
		return "Column";
	}

	@Override
	public Vec2f getMinSize() {
		return itemList.getMinSize();
	}

	// public void applyIDUpdate(IDMappingUpdateEvent event) {
	// if (event.getSender() == this)
	// return;
	// event.getColumnOperation().execute(this);
	// //
	// // Set<Object> elementIDs = getElementIDsFromForeignIDs(event.getIds(), event.getIdType());
	// //
	// // if (event.getUpdateType() == EUpdateType.REPLACE_FILTER || event.getUpdateType() == EUpdateType.AND_FILTER
	// // || event.getUpdateType() == EUpdateType.OR_FILTER) {
	// // applyFilter(event.getUpdateType(), elementIDs);
	// // } else if (event.getUpdateType() == EUpdateType.SELECTION) {
	// // setSelectedItems(Sets.intersection(mapFilteredElements.keySet(), elementIDs));
	// // }
	// }

	protected AEntityColumn getForeignColumnWithMappingIDType(IDType idType) {

		return getFirstForeignColumn(relationshipExplorer.getColumnsWithMappingIDType(idType));
	}

	protected AEntityColumn getForeignColumnWithBroadcastIDType(IDType idType) {

		return getFirstForeignColumn(relationshipExplorer.getColumnsWithBroadcastIDType(idType));
	}

	protected AEntityColumn getFirstForeignColumn(List<AEntityColumn> foreignColumns) {
		AEntityColumn foreignColumn = null;
		for (AEntityColumn col : foreignColumns) {
			if (col != this) {
				return col;
			}
		}
		return foreignColumn;
	}

	protected Set<Object> getElementIDsFromForeignIDs(Set<Object> foreignIDs, IDType foreignIDType) {
		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(getBroadcastingIDType());

		Set<Object> elementIDs = new HashSet<>();
		Set<Object> broadcastIDs = mappingManager.getIDTypeMapper(foreignIDType, getBroadcastingIDType()).apply(
				foreignIDs);
		for (Object bcID : broadcastIDs) {
			elementIDs.addAll(getElementIDsFromBroadcastingID((Integer) bcID));
		}

		return elementIDs;
	}

	/**
	 * @return the selectedElementIDs, see {@link #selectedElementIDs}
	 */
	protected Set<Object> getSelectedElementIDs() {
		return selectedElementIDs;
	}

	@ListenTo(sendToMe = true)
	public void onFilter(ContextMenuOperationEvent event) {
		Set<Object> broadcastIDs = new HashSet<>();
		Set<Object> elementIDs = new HashSet<>();
		fillSelectedElementAndBroadcastIDs(elementIDs, broadcastIDs);

		SelectionBasedFilterOperation o = new SelectionBasedFilterOperation(elementIDs, broadcastIDs,
				event.setOperation);
		o.execute(this);
		relationshipExplorer.getHistory().addColumnOperation(this, o);
		// applyFilter(event.type, elementIDs);
		// triggerIDUpdate(broadcastIDs, event.type);
		// triggerIDUpdate(broadcastIDs, EUpdateType.SELECTION);
	}

	@Override
	public void onElementSelected(GLElement element, Pick pick) {
		if (pick.getPickingMode() == PickingMode.CLICKED || pick.getPickingMode() == PickingMode.RIGHT_CLICKED) {

			Set<Object> broadcastIDs = new HashSet<>();
			Set<Object> elementIDs = new HashSet<>();
			fillSelectedElementAndBroadcastIDs(elementIDs, broadcastIDs);

			SelectionBasedHighlightOperation o = new SelectionBasedHighlightOperation(elementIDs, broadcastIDs);
			o.execute(this);
			relationshipExplorer.getHistory().addColumnOperation(this, o);
		}

	}

	private void fillSelectedElementAndBroadcastIDs(Set<Object> selectedElementIDs, Set<Object> selectedBroadcastIDs) {
		for (GLElement el : itemList.getSelectedElements()) {
			Object elementID = mapIDToElement.inverse().get(el);
			selectedElementIDs.add(elementID);
			selectedBroadcastIDs.addAll(getBroadcastingIDsFromElementID(elementID));
		}
	}

	// protected void applyFilter(EUpdateType filterType, Set<Object> elementIDs) {
	// if (filterType == EUpdateType.REPLACE_FILTER) {
	// setFilteredItems(elementIDs);
	// } else if (filterType == EUpdateType.AND_FILTER) {
	// setFilteredItems(Sets.intersection(elementIDs, mapFilteredElements.keySet()));
	// } else if (filterType == EUpdateType.OR_FILTER) {
	// setFilteredItems(Sets.union(elementIDs, mapFilteredElements.keySet()));
	// }
	// }

	protected void setFilteredItems(Set<Object> elementIDs) {
		// itemList.clear();

		mapFilteredElements = new HashMap<>(elementIDs.size());
		for (Entry<Object, GLElement> entry : mapIDToElement.entrySet()) {

			GLElement element = entry.getValue();
			boolean visible = false;

			if (elementIDs.contains(entry.getKey())) {
				visible = true;
				// itemList.show(element);
				if (!itemList.hasElement(element)) {
					itemList.add(element);
					itemList.asGLElement().relayout();
				}
				mapFilteredElements.put(entry.getKey(), entry.getValue());
			}

			if (!visible) {
				itemList.removeElement(element);
				// itemList.hide(element);
				itemList.asGLElement().relayout();
			}

		}

		setSelectedItems(Sets.intersection(selectedElementIDs, elementIDs));
	}

	public void showAllItems() {
		mapFilteredElements.clear();
		for (Entry<Object, GLElement> entry : mapIDToElement.entrySet()) {

			GLElement element = entry.getValue();
			// itemList.show(element);
			if (!itemList.hasElement(element)) {
				itemList.add(element);
				itemList.asGLElement().relayout();
			}
			mapFilteredElements.put(entry.getKey(), entry.getValue());
		}
	}

	protected void setSelectedItems(Set<Object> elementIDs) {
		selectedElementIDs = elementIDs;
		itemList.clearSelection();

		for (Object elementID : elementIDs) {
			GLElement element = mapIDToElement.get(elementID);
			if (element != null) {
				itemList.addToSelection(element);
			}
		}
	}

	@SuppressWarnings("null")
	public void updateSelectionMappings(AEntityColumn srcColumn) {

		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(getBroadcastingIDType());
		IIDTypeMapper<Object, Object> mapper = mappingManager.getIDTypeMapper(srcColumn.getBroadcastingIDType(),
				getBroadcastingIDType());
		List<MappingType> path = mapper.getPath();

		AEntityColumn foreignColumn = getNearestMappingColumn(path);

		int maxMappedElements = Integer.MIN_VALUE;
		for (Entry<Object, GLElement> entry : mapFilteredElements.entrySet()) {

			@SuppressWarnings("unchecked")
			KeyBasedGLElementContainer<GLElement> row = (KeyBasedGLElementContainer<GLElement>) entry.getValue();
			@SuppressWarnings("unchecked")
			KeyBasedGLElementContainer<SimpleBarRenderer> mappingRenderer = (KeyBasedGLElementContainer<SimpleBarRenderer>) row
					.getElement(MAPPING_KEY);

			if (srcColumn == this) {
				if (mappingRenderer != null)
					mappingRenderer.setVisibility(EVisibility.NONE);
			} else {
				if (mappingRenderer == null) {
					mappingRenderer = createLayeredBarRenderer();
					row.setElement(MAPPING_KEY, mappingRenderer);
				}
				mappingRenderer.setVisibility(EVisibility.PICKABLE);
				fillMappedElementCounts(row, foreignColumn, mappingRenderer);
				int numMappedElements = (int) (mappingRenderer.getElement(ALL_ELEMENTS_KEY)).getValue();
				if (numMappedElements > maxMappedElements)
					maxMappedElements = numMappedElements;
			}
		}
		boolean mappingHeaderExists = header.hasElement(MAPPING_KEY);

		if (srcColumn == this) {
			itemList.setHighlightSelections(true);
			if (mappingHeaderExists) {
				header.getElement(MAPPING_KEY).setVisibility(EVisibility.NONE);
			}
			return;
		}
		if (!mappingHeaderExists) {
			GLElement mappingHeader = new GLElement(GLRenderers.drawText(foreignColumn.getLabel(), VAlign.CENTER));
			mappingHeader.setSize(80, 12);
			header.setElement(MAPPING_KEY, mappingHeader);
		} else {
			GLElement mappingHeader = header.getElement(MAPPING_KEY);
			mappingHeader.setVisibility(EVisibility.PICKABLE);
			mappingHeader.setRenderer(GLRenderers.drawText(foreignColumn.getLabel(), VAlign.CENTER));
		}

		itemList.setHighlightSelections(false);

		for (Entry<Object, GLElement> entry : mapFilteredElements.entrySet()) {

			@SuppressWarnings("unchecked")
			KeyBasedGLElementContainer<GLElement> row = (KeyBasedGLElementContainer<GLElement>) entry.getValue();
			@SuppressWarnings("unchecked")
			KeyBasedGLElementContainer<SimpleBarRenderer> mappingRenderer = (KeyBasedGLElementContainer<SimpleBarRenderer>) row
					.getElement(MAPPING_KEY);
			SimpleBarRenderer barRenderer = mappingRenderer.getElement(ALL_ELEMENTS_KEY);
			barRenderer.setNormalizedValue(barRenderer.getValue() / maxMappedElements);
			barRenderer = mappingRenderer.getElement(FILTERED_ELEMENTS_KEY);
			barRenderer.setNormalizedValue(barRenderer.getValue() / maxMappedElements);
			barRenderer = mappingRenderer.getElement(SELECTED_ELEMENTS_KEY);
			barRenderer.setNormalizedValue(barRenderer.getValue() / maxMappedElements);
		}

		@SuppressWarnings("unchecked")
		ComparatorChain<GLElement> chain = new ComparatorChain<>(Lists.newArrayList(SELECTED_ELEMENTS_COMPARATOR,
				FILTERED_ELEMENTS_COMPARATOR, ALL_ELEMENTS_COMPARATOR, getDefaultElementComparator()));
		itemList.sortBy(chain);
	}

	public void hideMappings() {
		boolean mappingHeaderExists = header.hasElement(MAPPING_KEY);

		itemList.setHighlightSelections(true);
		if (mappingHeaderExists) {
			header.getElement(MAPPING_KEY).setVisibility(EVisibility.NONE);
		}

		for (Entry<Object, GLElement> entry : mapFilteredElements.entrySet()) {
			@SuppressWarnings("unchecked")
			KeyBasedGLElementContainer<GLElement> row = (KeyBasedGLElementContainer<GLElement>) entry.getValue();
			@SuppressWarnings("unchecked")
			KeyBasedGLElementContainer<SimpleBarRenderer> mappingRenderer = (KeyBasedGLElementContainer<SimpleBarRenderer>) row
					.getElement(MAPPING_KEY);
			if (mappingRenderer != null)
				mappingRenderer.setVisibility(EVisibility.NONE);
		}

	}

	public void sort(Comparator<GLElement> comparator) {
		itemList.sortBy(comparator);
	}

	protected void fillMappedElementCounts(GLElement element, AEntityColumn foreignColumn,
			KeyBasedGLElementContainer<SimpleBarRenderer> layeredBars) {
		Set<Object> broadcastIDs = getBroadcastingIDsFromElementID(mapIDToElement.inverse().get(element));

		int numSelectedElements = 0;
		int numFilteredElements = 0;
		Set<Object> foreignMappedElementIDs = foreignColumn.getElementIDsFromForeignIDs(broadcastIDs,
				getBroadcastingIDType());
		Set<Object> foreignFilteredElements = foreignColumn.getFilteredElementIDs();
		Set<Object> foreignSelectedElements = foreignColumn.getSelectedElementIDs();
		for (Object elementID : foreignMappedElementIDs) {
			if (foreignFilteredElements.contains(elementID)) {
				numFilteredElements++;
				// Only filtered elements can be selected
				if (foreignSelectedElements.contains(elementID))
					numSelectedElements++;
			}
		}
		layeredBars.getElement(ALL_ELEMENTS_KEY).setValue(foreignMappedElementIDs.size());
		layeredBars.getElement(FILTERED_ELEMENTS_KEY).setValue(numFilteredElements);
		layeredBars.getElement(SELECTED_ELEMENTS_KEY).setValue(numSelectedElements);
	}

	public Set<Object> getFilteredElementIDs() {
		return mapFilteredElements.keySet();
	}

	protected AEntityColumn getNearestMappingColumn(List<MappingType> path) {
		if (path == null) {
			AEntityColumn foreignColumn = getForeignColumnWithMappingIDType(getMappingIDType());
			if (foreignColumn == null)
				foreignColumn = getForeignColumnWithBroadcastIDType(getBroadcastingIDType());
			if (foreignColumn != null)
				return foreignColumn;
		} else {
			for (int i = path.size() - 1; i >= 0; i--) {
				AEntityColumn foreignColumn = getForeignColumnWithMappingIDType(path.get(i).getFromIDType());
				if (foreignColumn != null)
					return foreignColumn;
			}

		}
		return this;
	}

	public Set<Object> getAllElementIDs() {
		return Collections.unmodifiableSet(mapIDToElement.keySet());
	}

	protected abstract void setContent();

	public abstract Comparator<GLElement> getDefaultElementComparator();

	protected abstract IDType getBroadcastingIDType();

	protected abstract Set<Object> getBroadcastingIDsFromElementID(Object elementID);

	protected abstract Set<Object> getElementIDsFromBroadcastingID(Integer broadcastingID);

	protected abstract IDType getMappingIDType();
}
