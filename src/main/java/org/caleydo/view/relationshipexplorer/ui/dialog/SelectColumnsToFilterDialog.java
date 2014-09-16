/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog;

import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Christian
 *
 */
public class SelectColumnsToFilterDialog extends ASelectCollectionsDialog {

	/**
	 * @param shell
	 * @param relationshipExplorerElement
	 * @param caption
	 * @param isDefaultChecked
	 */
	public SelectColumnsToFilterDialog(Shell shell, ConTourElement relationshipExplorerElement) {
		super(shell, relationshipExplorerElement, "Select columns to filter", true);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout());
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createCollectionList(parentComposite);

		return super.createDialogArea(parent);
	}

}
