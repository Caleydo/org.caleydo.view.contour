/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.item.factory;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.list.EUpdateCause;
import org.caleydo.view.relationshipexplorer.ui.util.SimpleDataRenderer;

/**
 * @author Christian
 *
 */
public class SimpleTabularDataItemFactory implements IItemFactory {

	protected final TabularDataCollection collection;

	public SimpleTabularDataItemFactory(TabularDataCollection collection) {
		this.collection = collection;
	}

	@Override
	public GLElement createItem(Object elementID) {
		return new SimpleDataRenderer(collection.getDataDomain(), collection.getItemIDType(), (Integer) elementID,
				collection.getDimensionPerspective());
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
