/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.item.factory.impl;

import java.net.URL;

import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactoryConfigurationAddon;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.list.EUpdateCause;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;
import org.caleydo.view.relationshipexplorer.ui.util.SimpleDataRenderer;

/**
 * @author Christian
 *
 */
public class SimpleTabularDataConfigurationAddon implements IItemFactoryConfigurationAddon {

	public static class SimpleTabularDataItemFactoryCreator implements IItemFactoryCreator {

		protected static final URL PLOT_ICON = SimpleTabularDataItemFactory.class
				.getResource("/org/caleydo/view/relationshipexplorer/icons/hbar.png");

		public static class SimpleTabularDataItemFactory implements IItemFactory {

			protected final TabularDataCollection collection;

			public SimpleTabularDataItemFactory(TabularDataCollection collection) {
				this.collection = collection;
			}

			@Override
			public GLElement createItem(Object elementID) {
				return new SimpleDataRenderer(collection.getDataDomain(), collection.getItemIDType(),
						(Integer) elementID, collection.getDimensionPerspective());
			}

			@Override
			public GLElement createHeaderExtension() {
				// TODO Auto-generated method stub
				return null;
			}

			@Override
			public boolean needsUpdate(EUpdateCause cause) {
				return false;
			}

			@Override
			public void update() {

			}

		}

		@Override
		public IItemFactory create(IEntityCollection collection, IColumnModel column, ConTourElement contour) {
			return new SimpleTabularDataItemFactory((TabularDataCollection) collection);
		}

		@Override
		public URL getIconURL() {
			return PLOT_ICON;
		}



	}

	@Override
	public boolean accepts(IEntityCollection collection) {
		return collection instanceof TabularDataCollection;
	}

	@Override
	public void configure(ICallback<IItemFactoryCreator> callback) {
		callback.on(new SimpleTabularDataItemFactoryCreator());
	}

	@Override
	public String getLabel() {
		return "Default Tabular";
	}
}
