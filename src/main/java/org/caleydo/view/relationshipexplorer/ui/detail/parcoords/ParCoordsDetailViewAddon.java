/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.parcoords;

import org.caleydo.core.util.base.ICallback;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.detail.IDetailViewConfigurationAddon;
import org.caleydo.view.relationshipexplorer.ui.detail.IDetailViewFactory;

/**
 * @author Christian
 *
 */
public class ParCoordsDetailViewAddon implements IDetailViewConfigurationAddon {

	@Override
	public boolean accepts(IEntityCollection collection) {
		return collection instanceof TabularDataCollection;
	}

	@Override
	public void configure(ICallback<IDetailViewFactory> callback) {
		callback.on(new ParallelCoordinatesDetailViewFactory());

	}

	@Override
	public Class<? extends IDetailViewFactory> getConfigObjectClass() {
		return ParallelCoordinatesDetailViewFactory.class;
	}

	@Override
	public String getLabel() {
		return "Parallel Coordinates";
	}

}
