/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail;

import org.caleydo.view.relationshipexplorer.ui.History.IHistoryIDOwner;

/**
 * @author Christian
 *
 */
public interface IShowSelectedItemsListener extends IHistoryIDOwner {

	public void showSelectedItems(boolean showSelectedItems);

}
