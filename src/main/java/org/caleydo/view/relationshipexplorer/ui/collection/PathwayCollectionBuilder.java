/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.collection;

import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.idprovider.IElementIDProvider;
import org.caleydo.view.relationshipexplorer.ui.column.factory.ColumnFactories.PathwayColumnFactory;

/**
 * @author Christian
 *
 */
public class PathwayCollectionBuilder extends AEntityCollectionBuilder {

	/**
	 * @param columnFactory
	 * @param elementIDProvider
	 * @param contour
	 */
	public PathwayCollectionBuilder(IElementIDProvider elementIDProvider, ConTourElement contour) {
		super(new PathwayColumnFactory(), elementIDProvider, contour);
	}

	@Override
	protected AEntityCollection createInstance() {
		return new PathwayCollection(elementIDProvider, contour);
	}

}
