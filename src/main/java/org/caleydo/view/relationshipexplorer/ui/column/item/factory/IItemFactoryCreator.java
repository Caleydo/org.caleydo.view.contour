/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.item.factory;

import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;

/**
 * Base interface of creators for {@link IItemFactory}s. This creator is needed to flexibly define ItemFactories via
 * plug in mechanism.
 *
 * @author Christian
 *
 */
public interface IItemFactoryCreator extends IIconProvider {

	/**
	 * Creates a new {@link IItemFactory}.
	 *
	 * @param collection
	 * @param column
	 * @param contour
	 * @return
	 */
	public IItemFactory create(IEntityCollection collection, IColumnModel column, ConTourElement contour);

}
