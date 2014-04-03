/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.factory;

import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.column.TabularDataColumn;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.ActivitySummaryItemFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.HTSActivityItemFactory;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;

/**
 * @author Christian
 *
 */
public class ActivityColumnFactory implements IColumnFactory {

	protected final TabularDataCollection collection;
	protected final ConTourElement relationshipExplorer;

	public ActivityColumnFactory(TabularDataCollection collection, ConTourElement relationshipExplorer) {
		this.collection = collection;
		this.relationshipExplorer = relationshipExplorer;
	}

	@Override
	public IColumnModel create() {
		TabularDataColumn column = new TabularDataColumn(collection, relationshipExplorer);
		ActivitySummaryItemFactory f = new ActivitySummaryItemFactory(column);
		column.addSummaryItemFactory(f);
		column.setSummaryItemFactory(f);
		column.setItemFactory(new HTSActivityItemFactory(column));
		column.init();
		return column;
	}

}
