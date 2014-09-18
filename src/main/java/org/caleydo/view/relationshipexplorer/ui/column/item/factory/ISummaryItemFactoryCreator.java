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
 * Base interface of creators for {@link ISummaryItemFactory}s. This creator is needed to flexibly define
 * SummaryItemFactories via plug in mechanism.
 *
 * @author Christian
 *
 */
public interface ISummaryItemFactoryCreator extends IIconProvider {
	/**
	 * @param collection
	 * @param column
	 * @param contour
	 * @return
	 */
	public ISummaryItemFactory create(IEntityCollection collection, IColumnModel column, ConTourElement contour);
}
