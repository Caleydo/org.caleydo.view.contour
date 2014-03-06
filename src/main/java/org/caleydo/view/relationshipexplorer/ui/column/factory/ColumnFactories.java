/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.factory;

import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.collection.GroupCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.IDCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.PathwayCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.column.GroupingColumn;
import org.caleydo.view.relationshipexplorer.ui.column.IDColumn;
import org.caleydo.view.relationshipexplorer.ui.column.PathwayColumn;
import org.caleydo.view.relationshipexplorer.ui.column.TabularDataColumn;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;

/**
 * @author Christian
 *
 */
public final class ColumnFactories {

	private ColumnFactories() {

	}

	public static IColumnFactory createDefaultGroupColumnFactory(final GroupCollection collection,
			final RelationshipExplorerElement relationshipExplorer) {
		return new IColumnFactory() {

			@Override
			public IColumnModel create() {
				GroupingColumn column = new GroupingColumn(collection, relationshipExplorer);
				column.init();
				return column;
			}
		};
	}

	public static IColumnFactory createDefaultIDColumnFactory(final IDCollection collection,
			final RelationshipExplorerElement relationshipExplorer) {
		return new IColumnFactory() {

			@Override
			public IColumnModel create() {
				IDColumn column = new IDColumn(collection, relationshipExplorer);
				column.init();
				return column;
			}
		};
	}

	public static IColumnFactory createDefaultPathwayColumnFactory(final PathwayCollection collection,
			final RelationshipExplorerElement relationshipExplorer) {
		return new IColumnFactory() {

			@Override
			public IColumnModel create() {
				PathwayColumn column = new PathwayColumn(collection, relationshipExplorer);
				column.init();
				return column;
			}
		};
	}

	public static IColumnFactory createDefaultTabularDataColumnFactory(final TabularDataCollection collection,
			final RelationshipExplorerElement relationshipExplorer) {
		return new IColumnFactory() {

			@Override
			public IColumnModel create() {
				TabularDataColumn column = new TabularDataColumn(collection, relationshipExplorer);
				column.init();
				return column;
			}
		};
	}

}
