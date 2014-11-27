/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.collection;

import org.caleydo.core.id.IDType;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.idprovider.IElementIDProvider;
import org.caleydo.view.relationshipexplorer.ui.column.factory.ColumnFactories.IDColumnFactory;

/**
 * @author Christian
 *
 */
public class IDCollectionBuilder extends AEntityCollectionBuilder {

	protected final IDType idType;
	protected final IDType displayedIDType;

	/**
	 * @param columnFactory
	 */
	public IDCollectionBuilder(IDType idType, IDType displayedIDType, IElementIDProvider elementIDProvider,
			ConTourElement contour) {
		super(new IDColumnFactory(), elementIDProvider, contour);
		this.idType = idType;
		this.displayedIDType = displayedIDType;
	}

	@Override
	protected AEntityCollection createInstance() {
		return new IDCollection(idType, displayedIDType, elementIDProvider, contour);
	}
}
