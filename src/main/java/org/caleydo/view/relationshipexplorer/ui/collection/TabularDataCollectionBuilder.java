/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.collection;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.id.IDCategory;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.idprovider.IElementIDProvider;
import org.caleydo.view.relationshipexplorer.ui.column.factory.ColumnFactories.TabularDataColumnFactory;

/**
 * @author Christian
 *
 */
public class TabularDataCollectionBuilder extends AEntityCollectionBuilder {

	protected final TablePerspective tablePerspective;
	protected final IDCategory itemIDCategory;

	/**
	 * @param columnFactory
	 * @param elementIDProvider
	 * @param contour
	 */
	public TabularDataCollectionBuilder(TablePerspective tablePerspective, IDCategory itemIDCategory,
			IElementIDProvider elementIDProvider, ConTourElement contour) {
		super(new TabularDataColumnFactory(), elementIDProvider, contour);
		this.tablePerspective = tablePerspective;
		this.itemIDCategory = itemIDCategory;
	}

	@Override
	protected AEntityCollection createInstance() {
		return new TabularDataCollection(tablePerspective, itemIDCategory, elementIDProvider, contour);
	}

}
