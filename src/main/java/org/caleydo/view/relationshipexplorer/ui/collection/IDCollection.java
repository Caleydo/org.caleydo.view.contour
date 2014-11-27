/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.collection;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.idprovider.IElementIDProvider;
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

	protected IIDTypeMapper<Object, Object> elementIDToDisplayedIDMapper;

	public IDCollection(IDType idType, IDType displayedIDType, IElementIDProvider elementIDProvider,
			ConTourElement relationshipExplorer) {
		super(relationshipExplorer);
		this.idType = idType;
		this.displayedIDType = displayedIDType;
		this.label = idType.getIDCategory().getDenominationPlural(true);
		if (elementIDProvider == null)
			elementIDProvider = getDefaultElementIDProvider(idType);
		allElementIDs.addAll(elementIDProvider.getElementIDs());
		filteredElementIDs.addAll(allElementIDs);

		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idType.getIDCategory());

		elementIDToDisplayedIDMapper = mappingManager.getIDTypeMapper(idType, displayedIDType);
	}

	@Override
	public IDType getBroadcastingIDType() {
		return idType;
	}

	@Override
	protected Set<Object> getBroadcastIDsFromElementID(Object elementID) {
		return Sets.newHashSet(elementID);
	}

	@Override
	protected Set<Object> getElementIDsFromBroadcastID(Object broadcastingID) {
		return Sets.newHashSet(broadcastingID);
	}

	@Override
	protected IColumnFactory getDefaultColumnFactory() {
		return ColumnFactories.createDefaultIDColumnFactory();
	}

	@Override
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

	public static IElementIDProvider getDefaultElementIDProvider(final IDType idType) {
		return new IElementIDProvider() {
			@Override
			public Set<Object> getElementIDs() {
				IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
						idType.getIDCategory());
				return new HashSet<Object>(mappingManager.getAllMappedIDs(idType));
			}
		};
	}

	protected String getDisplayedID(Object id) {
		Set<Object> idsToDisplay = elementIDToDisplayedIDMapper.apply(id);
		if (idsToDisplay != null) {
			for (Object name : idsToDisplay) {
				return name.toString();
			}
		}
		return id.toString();
	}

	@Override
	public String getText(Object elementID) {
		return getDisplayedID(elementID);
	}

}
