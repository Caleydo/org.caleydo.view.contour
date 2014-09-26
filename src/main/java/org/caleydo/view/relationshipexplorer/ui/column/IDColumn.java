/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

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


	public static final AInvertibleComparator<NestableItem> ID_NUMBER_ITEM_COMPARATOR = new AInvertibleComparator<NestableItem>() {

		@Override
		public int compare(NestableItem arg0, NestableItem arg1) {
			int id1 = (int) arg0.getElementData().iterator().next();
			int id2 = (int) arg1.getElementData().iterator().next();
			return Integer.compare(id1, id2);
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
	public IInvertibleComparator<NestableItem> getDefaultComparator() {
		if (displayedIDType.getDataType() == EDataType.INTEGER)
			return ID_NUMBER_ITEM_COMPARATOR;
		return super.getDefaultComparator();
	}


	@Override
	public String getText(Object elementID) {
		return getDisplayedID(elementID);
	}

}
