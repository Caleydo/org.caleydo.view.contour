/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

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
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.id.MappingType;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.ActionBasedContextMenuItem;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.view.relationshipexplorer.ui.GLElementList.IElementSelectionListener;
import org.caleydo.view.relationshipexplorer.ui.IDUpdateEvent.EUpdateType;
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

	protected static final Integer DATA_KEY = new Integer(0);
	protected static final Integer MAPPING_KEY = new Integer(1);

	protected GLElement header;
	protected GLElementList itemList = new GLElementList();
	protected BiMap<Object, GLElement> mapIDToElement = HashBiMap.create();

	protected Set<Object> selectedElementIDs = new HashSet<>();
	protected Map<Object, GLElement> mapFilteredElements = new HashMap<>();
	protected RelationshipExplorerElement relationshipExplorer;

	// @DeepScan
	// protected Runnable filterRunnable;

	protected static class FilterEvent extends ADirectedEvent {
		protected Set<Object> elementIDs;

		public FilterEvent(Set<Object> elementIDs) {
			this.elementIDs = elementIDs;
		}
	}

	// protected EventBasedSelectionManager selectionManager;
	//
	// private boolean handleSelectionUpdate = true;

	protected final Comparator<GLElement> SELECTION_COMPARATOR = new Comparator<GLElement>() {

		@Override
		public int compare(GLElement el1, GLElement el2) {
			SimpleBarRenderer barRenderer1 = (SimpleBarRenderer) ((EntityRow) el1).getElement(MAPPING_KEY);
			SimpleBarRenderer barRenderer2 = (SimpleBarRenderer) ((EntityRow) el2).getElement(MAPPING_KEY);
			return Float.compare(barRenderer2.getNormalizedValue(), barRenderer1.getNormalizedValue());
		}

	};

	// protected class SelectionComparator implements Comparator<GLElement> {
	//
	// protected final AEntityColumn foreignColumn;
	//
	// public SelectionComparator(AEntityColumn foreignColumn) {
	// this.foreignColumn = foreignColumn;
	// }
	//
	// @Override
	// public int compare(GLElement el1, GLElement el2) {
	// Set<GLElement> selectedElements = itemList.getSelectedElements();
	// boolean el1Selected = selectedElements.contains(el1);
	// boolean el2Selected = selectedElements.contains(el2);
	// if (el1Selected && !el2Selected)
	// return -1;
	// if (!el1Selected && el2Selected)
	// return 1;
	// if (!el1Selected && !el2Selected)
	// return 0;
	//
	// return getNumMappedSelectedElements(el2) - getNumMappedSelectedElements(el1);
	//
	// }
	//
	// protected int getNumMappedSelectedElements(GLElement element) {
	// Set<Object> broadcastIDs = getBroadcastingIDsFromElementID(mapIDToElement.inverse().get(element));
	//
	// int numSelections = 0;
	// for (Object elementID : foreignColumn.getElementIDsFromForeignIDs(broadcastIDs, getBroadcastingIDType())) {
	// if (foreignColumn.getSelectedElementIDs().contains(elementID))
	// numSelections++;
	// }
	// return numSelections;
	// }
	// }

	public AEntityColumn(RelationshipExplorerElement relationshipExplorer) {
		super(GLLayouts.flowVertical(HEADER_BODY_SPACING));
		this.relationshipExplorer = relationshipExplorer;
		header = new GLElement();
		header.setSize(Float.NaN, HEADER_HEIGHT);
		add(header);
		add(itemList.asGLElement());
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		// selectionManager = new EventBasedSelectionManager(this, getBroadcastingIDType());
		// selectionManager.registerEventListeners();

		setContent();

		itemList.addContextMenuItem(getFilterContextMenuItem());

		header.setRenderer(GLRenderers.drawText(getLabel(), VAlign.CENTER));
		itemList.addElementSelectionListener(this);
		Comparator<GLElement> c = getDefaultElementComparator();
		itemList.sortBy(c);
		mapFilteredElements.putAll(mapIDToElement);
		// for (GLElement el : mapIDToElement.values()) {
		// ((EntityRow) el).getElement(MAPPING_KEY).setVisibility(EVisibility.NONE);
		// }
	}

	protected abstract void setContent();

	protected abstract Comparator<GLElement> getDefaultElementComparator();

	protected AContextMenuItem getFilterContextMenuItem() {

		Runnable filterRunnable = new Runnable() {
			@Override
			public void run() {
				Set<Object> broadcastIDs = new HashSet<>();
				Set<Object> elementIDs = new HashSet<>();
				for (GLElement element : itemList.getSelectedElements()) {
					Object elementID = mapIDToElement.inverse().get(element);
					elementIDs.add(elementID);
					broadcastIDs.addAll(getBroadcastingIDsFromElementID(elementID));
				}
				// Avoid direct calling of setFilteredItems due to synchronization issues
				EventPublisher.trigger(new FilterEvent(elementIDs).to(AEntityColumn.this));

				triggerIDUpdate(broadcastIDs, EUpdateType.FILTER);

			}
		};
		ActionBasedContextMenuItem contextMenuItem = new ActionBasedContextMenuItem("Apply Filter", filterRunnable);
		return contextMenuItem;
	}

	protected void triggerIDUpdate(Set<Object> ids, EUpdateType updateType) {
		IDUpdateEvent event = new IDUpdateEvent(ids, getBroadcastingIDType(), updateType);
		event.setSender(AEntityColumn.this);
		EventPublisher.trigger(event);
	}

	protected void addElement(GLElement element, Object elementID) {

		EntityRow row = new EntityRow();
		row.addElement(DATA_KEY, element);
		SimpleBarRenderer mappingRenderer = new SimpleBarRenderer(0, true);
		mappingRenderer.setMinSize(new Vec2f(80, 0));
		mappingRenderer.setSize(80, Float.NaN);
		mappingRenderer.setColor(SelectionType.SELECTION.getColor());
		mappingRenderer.setBarWidth(12);
		// mappingRenderer.setVisibility(EVisibility.NONE);
		row.addElement(MAPPING_KEY, mappingRenderer);
		mapIDToElement.put(elementID, row);
		itemList.add(row);
	}

	@Override
	public String getLabel() {
		return "Column";
	}

	@Override
	public Vec2f getMinSize() {
		return itemList.getMinSize();
	}

	public void applyIDUpdate(IDUpdateEvent event) {
		if (event.getSender() == this)
			return;

		Set<Object> elementIDs = getElementIDsFromForeignIDs(event.getIds(), event.getIdType());

		if (event.getUpdateType() == EUpdateType.FILTER) {
			setFilteredItems(elementIDs);
		} else if (event.getUpdateType() == EUpdateType.SELECTION) {
			setSelectedItems(Sets.intersection(mapFilteredElements.keySet(), elementIDs));
		}
	}

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
		for (Object foreignID : foreignIDs) {
			Set<Object> ids = mappingManager.getIDAsSet(foreignIDType, getBroadcastingIDType(), foreignID);
			if (ids != null) {
				for (Object id : ids) {
					elementIDs.addAll(getElementIDsFromBroadcastingID((Integer) id));
				}
			}
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
	public void onFilter(FilterEvent event) {
		setFilteredItems(event.elementIDs);

		Set<Object> broadcastIDs = new HashSet<>();
		for (GLElement el : itemList.getSelectedElements()) {
			Object elementID = mapIDToElement.inverse().get(el);
			broadcastIDs.addAll(getBroadcastingIDsFromElementID(elementID));
		}

		triggerIDUpdate(broadcastIDs, EUpdateType.SELECTION);
	}

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

	public void updateSelectionMappings(IDUpdateEvent event) {

		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(getBroadcastingIDType());
		IIDTypeMapper<Object, Object> mapper = mappingManager.getIDTypeMapper(event.getIdType(),
				getBroadcastingIDType());
		List<MappingType> path = mapper.getPath();

		AEntityColumn foreignColumn = getNearestMappingColumn(path);

		int maxSelectedElements = Integer.MIN_VALUE;
		for (Entry<Object, GLElement> entry : mapFilteredElements.entrySet()) {

			EntityRow row = (EntityRow) entry.getValue();
			SimpleBarRenderer mappingRenderer = (SimpleBarRenderer) row.getElement(MAPPING_KEY);
			if (event.getSender() == this) {
				mappingRenderer.setVisibility(EVisibility.NONE);
			} else {
				mappingRenderer.setVisibility(EVisibility.PICKABLE);
				int numSelectedElements = getNumMappedSelectedElements(row, foreignColumn);
				if (numSelectedElements > maxSelectedElements)
					maxSelectedElements = numSelectedElements;
				mappingRenderer.setNormalizedValue(numSelectedElements);
				mappingRenderer.setTooltip(String.valueOf(numSelectedElements));
			}
		}
		if (event.getSender() == this) {
			itemList.setHighlightSelections(true);
			header.setRenderer(GLRenderers.drawText(getLabel(), VAlign.CENTER));
			return;
		}
		itemList.setHighlightSelections(false);
		header.setRenderer(GLRenderers.drawText(getLabel() + "/" + foreignColumn.getLabel(), VAlign.CENTER));

		for (Entry<Object, GLElement> entry : mapFilteredElements.entrySet()) {

			EntityRow row = (EntityRow) entry.getValue();
			SimpleBarRenderer mappingRenderer = (SimpleBarRenderer) row.getElement(MAPPING_KEY);
			mappingRenderer.setNormalizedValue(mappingRenderer.getNormalizedValue() / maxSelectedElements);
		}

		@SuppressWarnings("unchecked")
		ComparatorChain<GLElement> chain = new ComparatorChain<>(Lists.newArrayList(SELECTION_COMPARATOR,
				getDefaultElementComparator()));
		itemList.sortBy(chain);
	}

	protected int getNumMappedSelectedElements(GLElement element, AEntityColumn foreignColumn) {
		Set<Object> broadcastIDs = getBroadcastingIDsFromElementID(mapIDToElement.inverse().get(element));

		int numSelections = 0;
		for (Object elementID : foreignColumn.getElementIDsFromForeignIDs(broadcastIDs, getBroadcastingIDType())) {
			if (foreignColumn.getSelectedElementIDs().contains(elementID))
				numSelections++;
		}
		return numSelections;
	}

	@Override
	public void onElementSelected(GLElement element, Pick pick) {
		if (pick.getPickingMode() == PickingMode.CLICKED || pick.getPickingMode() == PickingMode.RIGHT_CLICKED) {


			Set<Object> broadcastIDs = new HashSet<>();
			Set<Object> elementIDs = new HashSet<>();
			for (GLElement el : itemList.getSelectedElements()) {
				Object elementID = mapIDToElement.inverse().get(el);
				elementIDs.add(elementID);
				broadcastIDs.addAll(getBroadcastingIDsFromElementID(elementID));
			}

			triggerIDUpdate(broadcastIDs, EUpdateType.SELECTION);

			setSelectedItems(elementIDs);
		}

	}

	protected abstract IDType getBroadcastingIDType();

	protected abstract Set<Object> getBroadcastingIDsFromElementID(Object elementID);

	protected abstract Set<Object> getElementIDsFromBroadcastingID(Integer broadcastingID);

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

	protected abstract IDType getMappingIDType();
}
