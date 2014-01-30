/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.util.base.ILabelHolder;
import org.caleydo.core.view.contextmenu.ActionBasedContextMenuItem;
import org.caleydo.core.view.opengl.layout2.GLElement;

import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public class IDColumn extends ATextColumn implements ILabelHolder {

	protected final IDType idType;
	protected final IDType displayedIDType;

	protected String label;

	public IDColumn(IDType idType, IDType displayedIDType) {
		this.idType = idType;
		this.displayedIDType = displayedIDType;
		this.label = idType.getIDCategory().getDenominationPlural(true);
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

	@Override
	protected void setContent() {
		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idType.getIDCategory());
		IIDTypeMapper<Object, Object> mapper = mappingManager.getIDTypeMapper(idType, displayedIDType);

		for (final Object id : mappingManager.getAllMappedIDs(idType)) {
			Set<Object> idsToDisplay = mapper.apply(id);
			if (idsToDisplay != null) {
				for (Object name : idsToDisplay) {
					final MinSizeTextElement item = addTextElement(name.toString(), id);

					ActionBasedContextMenuItem contextMenuItem = new ActionBasedContextMenuItem("Apply Filter",
							new Runnable() {
								@Override
								public void run() {
									Set<Object> ids = new HashSet<>();
									for (GLElement element : itemList.getSelectedElements()) {
										ids.add(mapIDToElement.inverse().get(element));
									}

									IDFilterEvent event = new IDFilterEvent(ids, idType);
									event.setSender(IDColumn.this);
									EventPublisher.trigger(event);

								}
							});

					itemList.addContextMenuItem(item, contextMenuItem);
					// Only add first one
					break;
				}
			}
		}

	}

	@Override
	public String getProviderName() {
		return "ID Column";
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	protected IDType getBroadcastingIDType() {
		return idType;
	}

	@Override
	protected Set<Integer> getBroadcastingIDsFromElementID(Object elementID) {
		return Sets.newHashSet((Integer) elementID);
	}

	@Override
	protected Set<Object> getElementIDsFromBroadcastingID(Integer broadcastingID) {
		return Sets.newHashSet((Object) broadcastingID);
	}

}
