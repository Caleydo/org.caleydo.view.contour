/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog;

import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Christian
 *
 */
public class AddColumnDialog extends ASelectCollectionsDialog {

	/**
	 * @param shell
	 * @param relationshipExplorerElement
	 * @param caption
	 * @param isDefaultChecked
	 */
	public AddColumnDialog(Shell shell, ConTourElement relationshipExplorerElement) {
		super(shell, relationshipExplorerElement, "Add Column", false);
	}

}
