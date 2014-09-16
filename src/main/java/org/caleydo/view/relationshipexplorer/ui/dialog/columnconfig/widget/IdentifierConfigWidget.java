/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig.widget;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Christian
 *
 */
public class IdentifierConfigWidget extends Composite {

	private Combo idCategoryCombo;
	private Combo idTypeCombo;

	/**
	 * @param parent
	 * @param style
	 */
	public IdentifierConfigWidget(Composite parent) {
		super(parent, SWT.NONE);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setLayout(new GridLayout(2, false));

		Label idCategoryLabel = new Label(this, SWT.NONE);
		idCategoryLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		idCategoryLabel.setText("Type");

		idCategoryCombo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		idCategoryCombo.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		idCategoryCombo.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				idTypeCombo.setEnabled(true);
				idTypeCombo.clearSelection();
				idTypeCombo.removeAll();
				for (IDType idType : IDCategory.getIDCategory(idCategoryCombo.getText()).getIdTypes()) {
					if (!idType.isInternalType()) {
						idTypeCombo.add(idType.getTypeName());
					}
				}
			}
		});

		for (IDCategory idCategory : IDCategory.getAllRegisteredIDCategories()) {
			if (!idCategory.isInternaltCategory()) {
				idCategoryCombo.add(idCategory.getCategoryName());
			}
		}

		Label idTypeLabel = new Label(this, SWT.NONE);
		idTypeLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		idTypeLabel.setText("Identifier");

		idTypeCombo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		idTypeCombo.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		idTypeCombo.setEnabled(false);
	}

	public IDType getSelectedIDType() {
		if (idTypeCombo.getSelectionIndex() == -1)
			return null;
		return IDType.getIDType(idTypeCombo.getText());
	}

}
