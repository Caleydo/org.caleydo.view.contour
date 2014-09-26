/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.factory;

import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.GroupCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.IDCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.PathwayCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.column.GroupingColumn;
import org.caleydo.view.relationshipexplorer.ui.column.IDColumn;
import org.caleydo.view.relationshipexplorer.ui.column.PathwayColumn;
import org.caleydo.view.relationshipexplorer.ui.column.TabularDataColumn;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.impl.MappingSummaryConfigurationAddon.MappingSummaryItemFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.impl.SimpleTabularDataConfigurationAddon.SimpleTabularDataItemFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.impl.TextConfigurationAddon.TextItemFactoryCreator;

/**
 * @author Christian
 *
 */
public final class ColumnFactories {

	public static class IDColumnFactory extends AColumnFactory {

		@Override
		protected AEntityColumn createColumnInstance(IEntityCollection collection, ConTourElement contour) {
			return new IDColumn((IDCollection) collection, contour);
		}

	}

	public static class GroupColumnFactory extends AColumnFactory {

		@Override
		protected AEntityColumn createColumnInstance(IEntityCollection collection, ConTourElement contour) {
			return new GroupingColumn((GroupCollection) collection, contour);
		}

	}

	public static class PathwayColumnFactory extends AColumnFactory {

		@Override
		protected AEntityColumn createColumnInstance(IEntityCollection collection, ConTourElement contour) {
			return new PathwayColumn((PathwayCollection) collection, contour);
		}

	}

	public static class TabularDataColumnFactory extends AColumnFactory {

		@Override
		protected AEntityColumn createColumnInstance(IEntityCollection collection, ConTourElement contour) {
			return new TabularDataColumn((TabularDataCollection) collection, contour);
		}

	}

	private ColumnFactories() {

	}

	public static GroupColumnFactory createDefaultGroupColumnFactory() {
		GroupColumnFactory factory = new GroupColumnFactory();

		factory.addItemFactoryCreator(new TextItemFactoryCreator(), true);
		factory.addSummaryItemFactoryCreator(new MappingSummaryItemFactoryCreator(), true);

		return factory;
	}

	public static IDColumnFactory createDefaultIDColumnFactory() {
		IDColumnFactory factory = new IDColumnFactory();

		factory.addItemFactoryCreator(new TextItemFactoryCreator(), true);
		factory.addSummaryItemFactoryCreator(new MappingSummaryItemFactoryCreator(), true);

		return factory;
	}

	public static PathwayColumnFactory createDefaultPathwayColumnFactory() {
		PathwayColumnFactory factory = new PathwayColumnFactory();
		factory.addItemFactoryCreator(new TextItemFactoryCreator(), true);
		factory.addSummaryItemFactoryCreator(new MappingSummaryItemFactoryCreator(), true);

		return factory;
	}

	public static TabularDataColumnFactory createDefaultTabularDataColumnFactory() {
		TabularDataColumnFactory factory = new TabularDataColumnFactory();
		factory.addItemFactoryCreator(new SimpleTabularDataItemFactoryCreator(), true);
		factory.addSummaryItemFactoryCreator(new MappingSummaryItemFactoryCreator(), true);

		return factory;
	}

}
