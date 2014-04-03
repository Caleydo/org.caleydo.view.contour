/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail;

import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;

/**
 * @author Christian
 *
 */
public final class DetailViewWindowFactories {

	private DetailViewWindowFactories() {
	}

	public static IDetailViewWindowFactory createDefaultDetailViewWindowFactory(
			final ConTourElement relationshipExplorer) {
		return new IDetailViewWindowFactory() {

			@Override
			public DetailViewWindow createWindow(IEntityCollection collection) {
				return new DetailViewWindow(collection, relationshipExplorer);
			}
		};
	}
}
