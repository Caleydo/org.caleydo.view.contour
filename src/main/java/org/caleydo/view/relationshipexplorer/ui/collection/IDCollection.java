/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.collection;

import java.util.Set;

import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.column.factory.ColumnFactories;
import org.caleydo.view.relationshipexplorer.ui.column.factory.IColumnFactory;

import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public class IDCollection extends AEntityCollection {

	protected final IDType idType;
	protected final IDType displayedIDType;

	public IDCollection(IDType idType, IDType displayedIDType, RelationshipExplorerElement relationshipExplorer) {
		super(relationshipExplorer);
		this.idType = idType;
		this.displayedIDType = displayedIDType;
		this.label = idType.getIDCategory().getDenominationPlural(true);

		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idType.getIDCategory());
		// elementIDToDisplayedIDMapper = mappingManager.getIDTypeMapper(idType, displayedIDType);
		allElementIDs.addAll(mappingManager.getAllMappedIDs(idType));
		filteredElementIDs.addAll(allElementIDs);
	}

	@Override
	public IDType getBroadcastingIDType() {
		return idType;
	}

	@Override
	public Set<Object> getBroadcastingIDsFromElementID(Object elementID) {
		return Sets.newHashSet(elementID);
	}

	@Override
	public Set<Object> getElementIDsFromBroadcastingID(Integer broadcastingID) {
		return Sets.newHashSet((Object) broadcastingID);
	}

	@Override
	protected IColumnFactory getDefaultColumnFactory() {
		return ColumnFactories.createDefaultIDColumnFactory(this, relationshipExplorer);
	}

	public IDType getMappingIDType() {
		return getBroadcastingIDType();
	}

	/**
	 * @return the idType, see {@link #idType}
	 */
	public IDType getIdType() {
		return idType;
	}

	/**
	 * @return the displayedIDType, see {@link #displayedIDType}
	 */
	public IDType getDisplayedIDType() {
		return displayedIDType;
	}

}
