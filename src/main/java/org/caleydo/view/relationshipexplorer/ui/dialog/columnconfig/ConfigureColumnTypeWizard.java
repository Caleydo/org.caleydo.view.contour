/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig;

import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Christian
 *
 */
public class ConfigureColumnTypeWizard extends Wizard {

	private DataTypePage dataTypePage;

	public ConfigureColumnTypeWizard() {
		setWindowTitle("Create A New Column Type");
	}

	@Override
	public void addPages() {
		dataTypePage = new DataTypePage("Data Type", "Specify the data the column is based on", null);
		addPage(dataTypePage);
	}

	@Override
	public void createPageControls(Composite pageContainer) {

		super.createPageControls(pageContainer);
	}

	@Override
	public boolean performFinish() {
		return true;
	}

	@Override
	public boolean performCancel() {
		return super.performCancel();
	}

}
