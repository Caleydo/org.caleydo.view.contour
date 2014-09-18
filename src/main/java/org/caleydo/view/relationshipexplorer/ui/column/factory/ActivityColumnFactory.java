/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.factory;

import org.caleydo.view.relationshipexplorer.ui.column.factory.ColumnFactories.TabularDataColumnFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.impl.ActivitySummaryItemFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.impl.HTSActivityItemFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.impl.MappingSummaryItemFactoryCreator;

/**
 * @author Christian
 *
 */
public class ActivityColumnFactory extends TabularDataColumnFactory {

	public ActivityColumnFactory() {
		addItemFactoryCreator(new HTSActivityItemFactoryCreator(), true);
		addSummaryItemFactoryCreator(new ActivitySummaryItemFactoryCreator(), true);
		addSummaryItemFactoryCreator(new MappingSummaryItemFactoryCreator(), false);
	}

}
