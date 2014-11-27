/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog;

import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig.ConfigureColumnTypeWizard;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Christian
 *
 */
public class AddColumnDialog extends ASelectCollectionsDialog {

	/**
	 * @param shell
	 * @param contour
	 * @param caption
	 * @param isDefaultChecked
	 */
	public AddColumnDialog(Shell shell, ConTourElement contour) {
		super(shell, contour, "Add Column", false);
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout());
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createCollectionList(parentComposite);

		Button createNewColumnButton = new Button(parentComposite, SWT.PUSH);
		createNewColumnButton.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		createNewColumnButton.setText("Create New Column Type");
		createNewColumnButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ConfigureColumnTypeWizard wizard = new ConfigureColumnTypeWizard(contour);
				WizardDialog dialog = new WizardDialog(getShell(), wizard);
				dialog.open();
				updateCollectionList();
			}
		});

		return super.createDialogArea(parent);
	}

}
