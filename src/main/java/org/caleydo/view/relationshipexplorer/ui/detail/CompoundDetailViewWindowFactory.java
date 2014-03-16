/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail;

import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;

/**
 * @author Christian
 *
 */
public class CompoundDetailViewWindowFactory implements IDetailViewWindowFactory {

	protected final RelationshipExplorerElement relationshipExplorer;

	public CompoundDetailViewWindowFactory(RelationshipExplorerElement relationshipExplorer) {
		this.relationshipExplorer = relationshipExplorer;
	}

	@Override
	public DetailViewWindow createWindow(IEntityCollection collection) {
		final CompoundDetailViewWindow window = new CompoundDetailViewWindow(collection, relationshipExplorer);
		boolean changeOnSelection = true;
		window.showSelectedItems(changeOnSelection);
		window.addChangeViewOnSelectionButton(window, changeOnSelection);

		return window;
	}
}
