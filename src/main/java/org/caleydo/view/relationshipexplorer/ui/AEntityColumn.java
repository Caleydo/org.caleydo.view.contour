/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

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

/**
 * @author Christian
 *
 */
public abstract class AEntityColumn extends AnimatedGLElementContainer implements IElementSelectionListener, ILabeled {
	protected static final int HEADER_HEIGHT = 20;
	protected static final int HEADER_BODY_SPACING = 5;

	protected GLElement header;
	protected GLElementList itemList = new GLElementList();
	protected BiMap<Object, GLElement> mapIDToElement = HashBiMap.create();

	protected Set<Object> selectedElementIDs = new HashSet<>();
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

	protected SelectionComparator selectionComparator;

	protected class SelectionComparator implements Comparator<GLElement> {

		protected final AEntityColumn foreignColumn;

		public SelectionComparator(AEntityColumn foreignColumn) {
			this.foreignColumn = foreignColumn;
		}

		@Override
		public int compare(GLElement el1, GLElement el2) {
			Set<GLElement> selectedElements = itemList.getSelectedElements();
			boolean el1Selected = selectedElements.contains(el1);
			boolean el2Selected = selectedElements.contains(el2);
			if (el1Selected && !el2Selected)
				return -1;
			if (!el1Selected && el2Selected)
				return 1;
			if (!el1Selected && !el2Selected)
				return 0;

			return getNumMappedSelectedElements(el2) - getNumMappedSelectedElements(el1);

		}

		protected int getNumMappedSelectedElements(GLElement element) {
			Set<Object> broadcastIDs = getBroadcastingIDsFromElementID(mapIDToElement.inverse().get(element));

			int numSelections = 0;
			for (Object elementID : foreignColumn.getElementIDsFromForeignIDs(broadcastIDs, getBroadcastingIDType())) {
				if (foreignColumn.getSelectedElementIDs().contains(elementID))
					numSelections++;
			}
			return numSelections;
		}
	}

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
				// Avoid direct calling of setFilteredItems due to synchrinization issues
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
		mapIDToElement.put(elementID, element);
		itemList.add(element);
	}

	@Override
	public String getLabel() {
		return "Column";
	}

	@Override
	public Vec2f getMinSize() {
		return itemList.getMinSize();
	}

	@ListenTo
	public void onApplyIDUpdate(IDUpdateEvent event) {
		if (event.getSender() == this)
			return;

		Set<Object> elementIDs = getElementIDsFromForeignIDs(event.getIds(), event.getIdType());
		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(getBroadcastingIDType());
		IIDTypeMapper<Object, Object> mapper = mappingManager.getIDTypeMapper(event.getIdType(),
				getBroadcastingIDType());
		List<MappingType> path = mapper.getPath();

		// No path or same id type as broadcasting id
		if (path == null) {
			selectionComparator = new SelectionComparator(getForeignColumnWithBroadcastIDType(getBroadcastingIDType()));
		} else {
			AEntityColumn column = null;
			for (int i = path.size() - 1; i >= 0; i--) {
				column = getForeignColumnWithBroadcastIDType(path.get(i).getFromIDType());
				if (column != null)
					break;
			}
			if (column == null)
				column = this;
			selectionComparator = new SelectionComparator(column);
		}

		if (event.getUpdateType() == EUpdateType.FILTER) {
			setFilteredItems(elementIDs);
		} else if (event.getUpdateType() == EUpdateType.SELECTION) {
			setSelectedItems(elementIDs, true);
		}
	}


	protected AEntityColumn getForeignColumnWithBroadcastIDType(IDType idType) {
		List<AEntityColumn> foreignColumns = relationshipExplorer.getColumnsWithBroadcastIDType(idType);
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
	}

	protected void setFilteredItems(Set<Object> elementIDs) {
		// itemList.clear();
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
			}

			if (!visible) {
				itemList.removeElement(element);
				// itemList.hide(element);
				itemList.asGLElement().relayout();
			}

		}
		setSelectedItems(selectedElementIDs, true);
	}

	protected void setSelectedItems(Set<Object> elementIDs, boolean updateSorting) {
		selectedElementIDs = elementIDs;
		itemList.clearSelection();
		for (Object elementID : elementIDs) {
			GLElement element = mapIDToElement.get(elementID);
			if (element != null) {
				itemList.addToSelection(element);
			}
		}
		if (updateSorting) {
			@SuppressWarnings("unchecked")
			ComparatorChain<GLElement> chain = new ComparatorChain<>(Lists.newArrayList(selectionComparator,
					getDefaultElementComparator()));
			itemList.sortBy(chain);
		}
	}

	// @Override
	// public void notifyOfSelectionChange(EventBasedSelectionManager selectionManager) {
	// if (selectionManager == this.selectionManager && handleSelectionUpdate) {
	// updateHighlights();
	// }
	//
	// }

	// protected void updateHighlights() {
	// itemList.clearSelection();
	//
	// Set<Integer> selectionIDs = selectionManager.getElements(SelectionType.SELECTION);
	// for (Integer id : selectionIDs) {
	// Set<Object> elementIDs = getElementIDsFromBroadcastingID(id);
	// for (Object elementID : elementIDs) {
	// GLElement element = mapIDToElement.get(elementID);
	// if (element != null) {
	// itemList.addToSelection(element);
	// }
	// }
	// }
	// @SuppressWarnings("unchecked")
	// ComparatorChain<GLElement> chain = new ComparatorChain<>(Lists.newArrayList(SELECTION_COMPARATOR,
	// getDefaultElementComparator()));
	// itemList.sortBy(chain);
	// }

	@Override
	public void onElementSelected(GLElement element, Pick pick) {
		if (pick.getPickingMode() == PickingMode.CLICKED || pick.getPickingMode() == PickingMode.RIGHT_CLICKED) {

			// Save selected elements before clearing
			// Set<GLElement> selectedElements = itemList.getSelectedElements();
			// FIXME: bad hack to prevent this column to be affected from clearing
			// handleSelectionUpdate = false;
			// SelectionCommands.clearSelections();
			// handleSelectionUpdate = true;
			// Trigger update after clearing to only have added selections in va delta. In case of multimappings it
			// could otherwise happen that it is not clear whether to select an element or remove it from selection.
			// selectionManager.triggerSelectionUpdateEvent();

			Set<Object> broadcastIDs = new HashSet<>();
			Set<Object> elementIDs = new HashSet<>();
			for (GLElement el : itemList.getSelectedElements()) {
				Object elementID = mapIDToElement.inverse().get(el);
				elementIDs.add(elementID);
				broadcastIDs.addAll(getBroadcastingIDsFromElementID(elementID));
			}

			triggerIDUpdate(broadcastIDs, EUpdateType.SELECTION);

			setSelectedItems(elementIDs, false);
			// for (GLElement el : selectedElements) {
			//
			// selectionManager.addToType(SelectionType.SELECTION, getBroadcastingIDsFromElementID(mapIDToElement
			// .inverse().get(el)));
			// }

			// selectionManager.triggerSelectionUpdateEvent();
			// updateHighlights();
		}

	}

	// @Override
	// protected void takeDown() {
	// selectionManager.unregisterEventListeners();
	// super.takeDown();
	// }

	protected abstract IDType getBroadcastingIDType();

	protected abstract Set<Object> getBroadcastingIDsFromElementID(Object elementID);

	protected abstract Set<Object> getElementIDsFromBroadcastingID(Integer broadcastingID);
}
