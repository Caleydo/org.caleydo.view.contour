/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.layout2.util.GLElementWindow;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryIDOwner;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;

/**
 * @author Christian
 *
 */
public class DetailViewWindow extends GLElementWindow implements IHistoryIDOwner {

	protected final int historyID;
	protected final RelationshipExplorerElement relationshipExplorer;

	/**
	 * @param titleLabelProvider
	 */
	public DetailViewWindow(ILabeled titleLabelProvider, RelationshipExplorerElement relationshipExplorer) {
		super(titleLabelProvider);
		this.relationshipExplorer = relationshipExplorer;
		this.historyID = relationshipExplorer.getHistory().registerHistoryObject(this);
	}

	@Override
	public int getHistoryID() {
		return historyID;
	}

}
