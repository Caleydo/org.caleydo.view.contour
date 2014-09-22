/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.collection;

import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.idprovider.IElementIDProvider;
import org.caleydo.view.relationshipexplorer.ui.column.factory.ColumnFactories.GroupColumnFactory;

/**
 * @author Christian
 *
 */
public class GroupCollectionBuilder extends AEntityCollectionBuilder {

	protected final Perspective perspective;

	/**
	 * @param columnFactory
	 */
	public GroupCollectionBuilder(Perspective perspective, IElementIDProvider elementIDProvider, ConTourElement contour) {
		super(new GroupColumnFactory(), elementIDProvider, contour);
		this.perspective = perspective;
	}

	@Override
	protected AEntityCollection createInstance() {
		return new GroupCollection(perspective, elementIDProvider, contour);
	}

}
