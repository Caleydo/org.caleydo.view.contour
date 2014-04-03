/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.util.Comparator;
import java.util.Set;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IDCollection;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;

/**
 * @author Christian
 *
 */
public class IDColumn extends ATextColumn implements IColumnModel {

	protected final IDType idType;
	protected final IDType displayedIDType;

	protected final IDCollection idCollection;

	protected IIDTypeMapper<Object, Object> elementIDToDisplayedIDMapper;
	protected IDMappingManager mappingManager;


	public static final Comparator<NestableItem> ID_NUMBER_ITEM_COMPARATOR = new Comparator<NestableItem>() {

		@Override
		public int compare(NestableItem arg0, NestableItem arg1) {
			MinSizeTextElement r1 = (MinSizeTextElement) ((ScoreElement) arg0.getElement()).getElement();
			MinSizeTextElement r2 = (MinSizeTextElement) ((ScoreElement) arg1.getElement()).getElement();
			return Integer.valueOf(r1.getLabel()).compareTo(Integer.valueOf(r2.getLabel()));
		}

		@Override
		public String toString() {
			return "Item ID";
		}
	};

	public IDColumn(IDCollection idCollection, ConTourElement relationshipExplorer) {
		super(idCollection, relationshipExplorer);
		this.idCollection = idCollection;
		this.idType = idCollection.getIdType();
		this.displayedIDType = idCollection.getDisplayedIDType();

		mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idType.getIDCategory());
		elementIDToDisplayedIDMapper = mappingManager.getIDTypeMapper(idType, displayedIDType);
		currentComparator = new CompositeComparator<>(ItemComparators.SELECTED_ITEMS_COMPARATOR, getDefaultComparator());
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
	public Comparator<NestableItem> getDefaultComparator() {
		if (displayedIDType.getDataType() == EDataType.INTEGER)
			return ID_NUMBER_ITEM_COMPARATOR;
		return super.getDefaultComparator();
	}


	@Override
	public String getText(Object elementID) {
		return getDisplayedID(elementID);
	}

}
