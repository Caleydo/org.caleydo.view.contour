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

import org.caleydo.core.data.selection.EventBasedSelectionManager;
import org.caleydo.core.data.selection.IEventBasedSelectionManagerUser;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.view.opengl.layout2.GLElement.EVisibility;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;

import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public class IDContentProvider extends ATextualContentProvider implements IEventBasedSelectionManagerUser {

	protected final IDType idType;
	protected EventBasedSelectionManager selectionManager;

	protected Map<Object, EntityColumnItem<?>> itemMap = new HashMap<>();


	public IDContentProvider(IDType idType) {
		this.idType = idType;

		selectionManager = new EventBasedSelectionManager(this, idType);
		selectionManager.registerEventListeners();

		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idType.getIDCategory());
		IIDTypeMapper<Object, String> mapper = mappingManager.getIDTypeMapper(idType, idType.getIDCategory()
				.getHumanReadableIDType());

		for (final Object id : mappingManager.getAllMappedIDs(idType)) {
			Set<String> humanReadableNames = mapper.apply(id);
			if (humanReadableNames != null) {
				for (String name : humanReadableNames) {
					final EntityColumnItem<?> item = addItem(name);
					item.onPick(new IPickingListener() {

						@Override
						public void pick(Pick pick) {
							if (pick.getPickingMode() == PickingMode.CLICKED) {
								IDFilterEvent event = new IDFilterEvent(Sets.newHashSet(id),
										IDContentProvider.this.idType);
								event.setSender(IDContentProvider.this);
								EventPublisher.trigger(event);
								// selectionManager.clearSelection(SelectionType.SELECTION);
								// selectionManager.addToType(SelectionType.SELECTION, (Integer) id);
								// selectionManager.triggerSelectionUpdateEvent();
								// updateHighlights();
							}
						}
					});

					itemMap.put(id, item);
					// Only add first one
					break;
				}
			}
		}
	}

	@Override
	public String getLabel() {
		return idType.getIDCategory().getDenominationPlural(true);
	}

	@Override
	public void notifyOfSelectionChange(EventBasedSelectionManager selectionManager) {
		if (selectionManager == this.selectionManager) {
			updateHighlights();
		}

	}

	@ListenTo
	public void onApplyIDFilter(IDFilterEvent event) {
		Set<?> foreignIDs = event.getIds();
		IDType foreignIDType = event.getIdType();
		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(this.idType);
		Set<Object> mappedIDs = new HashSet<>();
		for (Object id : foreignIDs) {
			Set<Object> ids = mappingManager.getIDAsSet(foreignIDType, this.idType, id);
			if (ids != null) {
				mappedIDs.addAll(ids);
			}
		}

		setFilteredItems(mappedIDs);
	}

	protected void setFilteredItems(Set<Object> ids) {
		for (Entry<Object, EntityColumnItem<?>> entry : itemMap.entrySet()) {

			EntityColumnItem<?> item = entry.getValue();
			item.setHighlight(false);

			if (ids.contains(entry.getKey())) {
				item.setHighlight(true);
				item.setHighlightColor(SelectionType.SELECTION.getColor());
				item.setVisibility(EVisibility.PICKABLE);
				columnBody.getParent().relayout();
			}

			if (!item.isHighlight()) {
				item.setVisibility(EVisibility.NONE);
				columnBody.getParent().relayout();
			}

		}
	}

	protected void updateHighlights() {
		for (Entry<Object, EntityColumnItem<?>> entry : itemMap.entrySet()) {

			EntityColumnItem<?> item = entry.getValue();
			item.setHighlight(false);

			Set<Integer> selectionIDs = selectionManager.getElements(SelectionType.MOUSE_OVER);
			if (selectionIDs.contains(entry.getKey())) {
				item.setHighlight(true);
				item.setHighlightColor(SelectionType.MOUSE_OVER.getColor());
				item.setVisibility(EVisibility.PICKABLE);
				columnBody.getParent().relayout();
			}

			selectionIDs = selectionManager.getElements(SelectionType.SELECTION);
			if (selectionIDs.contains(entry.getKey())) {
				item.setHighlight(true);
				item.setHighlightColor(SelectionType.SELECTION.getColor());
				item.setVisibility(EVisibility.PICKABLE);
				columnBody.getParent().relayout();
			}

			if (!item.isHighlight()) {
				item.setVisibility(EVisibility.NONE);
				columnBody.getParent().relayout();
			}
		}

	}

	@Override
	public void takeDown() {
		selectionManager.unregisterEventListeners();
		selectionManager = null;
	}

}
