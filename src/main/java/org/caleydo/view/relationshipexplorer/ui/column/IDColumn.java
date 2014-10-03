/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.id.IDType;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IDCollection;
import org.caleydo.view.relationshipexplorer.ui.column.ItemComparators.TextComparator;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;

/**
 * @author Christian
 *
 */
public class IDColumn extends AEntityColumn {

	protected final IDType idType;
	protected final IDType displayedIDType;

	protected final IDCollection idCollection;



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

		currentComparator = new CompositeComparator<>(ItemComparators.SELECTED_ITEMS_COMPARATOR, getDefaultComparator());
	}

	@Override
	public IInvertibleComparator<NestableItem> getDefaultComparator() {
		if (displayedIDType.getDataType() == EDataType.INTEGER)
			return ID_NUMBER_ITEM_COMPARATOR;
		return new TextComparator(idCollection);
	}

}
