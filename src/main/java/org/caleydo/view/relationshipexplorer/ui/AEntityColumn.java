/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import java.util.Comparator;
import java.util.Map.Entry;
import java.util.Set;

import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.data.selection.SelectionCommands;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.view.relationshipexplorer.ui.GLElementList.IElementSelectionListener;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

/**
 * @author Christian
 *
 */
public abstract class AEntityColumn extends AnimatedGLElementContainer implements IElementSelectionListener,
		IEventBasedSelectionManagerUser, ILabeled {
	protected static final int HEADER_HEIGHT = 20;
	protected static final int HEADER_BODY_SPACING = 5;

	protected GLElement header;
	protected GLElementList itemList = new GLElementList();
	protected BiMap<Object, GLElement> mapIDToElement = HashBiMap.create();

	protected EventBasedSelectionManager selectionManager;

	private boolean handleSelectionUpdate = true;

	public AEntityColumn() {
		super(GLLayouts.flowVertical(HEADER_BODY_SPACING));
		header = new GLElement();
		header.setSize(Float.NaN, HEADER_HEIGHT);
		add(header);
		add(itemList.asGLElement());
	}

	@Override
	protected void init(IGLElementContext context) {
		super.init(context);
		selectionManager = new EventBasedSelectionManager(this, getBroadcastingIDType());
		selectionManager.registerEventListeners();

		setContent();
		header.setRenderer(GLRenderers.drawText(getLabel(), VAlign.CENTER));
		itemList.addElementSelectionListener(this);
		Comparator<GLElement> c = getDefaultElementComparator();
		itemList.sortBy(c);
	}

	protected abstract void setContent();

	protected abstract Comparator<GLElement> getDefaultElementComparator();

	protected void addElement(GLElement element, Object elementID) {
		mapIDToElement.put(elementID, element);
		itemList.add(element);
	}

	protected void setFilteredItems(Set<Object> ids) {
		for (Entry<Object, GLElement> entry : mapIDToElement.entrySet()) {

			GLElement element = entry.getValue();
			boolean visible = false;

			if (ids.contains(entry.getKey())) {
				visible = true;
				itemList.show(element);
				itemList.asGLElement().relayout();
			}

			if (!visible) {
				itemList.hide(element);
				itemList.asGLElement().relayout();
			}

		}
	}

	@Override
	public String getLabel() {
		return "Column";
	}

	@Override
	public Vec2f getMinSize() {
		return itemList.getMinSize();
	}

	@Override
	public void notifyOfSelectionChange(EventBasedSelectionManager selectionManager) {
		if (selectionManager == this.selectionManager && handleSelectionUpdate) {
			updateHighlights();
		}

	}

	protected void updateHighlights() {
		itemList.clearSelection();

		Set<Integer> selectionIDs = selectionManager.getElements(SelectionType.SELECTION);
		for (Integer id : selectionIDs) {
			Set<Object> elementIDs = getElementIDsFromBroadcastingID(id);
			for (Object elementID : elementIDs) {
				GLElement element = mapIDToElement.get(elementID);
				if (element != null) {
					itemList.addToSelection(element);
				}
			}
		}

	}

	@Override
	public void onElementSelected(GLElement element, Pick pick) {
		if (pick.getPickingMode() == PickingMode.CLICKED || pick.getPickingMode() == PickingMode.RIGHT_CLICKED) {

			// Save selected elements before clearing
			Set<GLElement> selectedElements = itemList.getSelectedElements();
			// FIXME: bad hack to prevent this column to be affected from clearing
			handleSelectionUpdate = false;
			SelectionCommands.clearSelections();
			handleSelectionUpdate = true;
			// Trigger update after clearing to only have added selections in va delta. In case of multimappings it
			// could otherwise happen that it is not clear whether to select an element or remove it from selection.
			selectionManager.triggerSelectionUpdateEvent();

			for (GLElement el : selectedElements) {
				selectionManager.addToType(SelectionType.SELECTION, getBroadcastingIDsFromElementID(mapIDToElement
						.inverse().get(el)));
			}

			selectionManager.triggerSelectionUpdateEvent();
			// updateHighlights();
		}

	}

	@Override
	protected void takeDown() {
		selectionManager.unregisterEventListeners();
		super.takeDown();
	}

	protected abstract IDType getBroadcastingIDType();

	protected abstract Set<Integer> getBroadcastingIDsFromElementID(Object elementID);

	protected abstract Set<Object> getElementIDsFromBroadcastingID(Integer broadcastingID);
}
