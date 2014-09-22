/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig.widget;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.ICallback;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.AEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.IDCollection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;

/**
 * @author Christian
 *
 */
public class IdentifierConfigWidget extends ADataConfigWidget {

	private Combo idCategoryCombo;
	private Combo idTypeCombo;

	/**
	 * @param parent
	 * @param style
	 */
	public IdentifierConfigWidget(Composite parent, final ICallback<ADataConfigWidget> callback) {
		super(parent, callback);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setLayout(new GridLayout(2, false));

		Label idCategoryLabel = new Label(this, SWT.NONE);
		idCategoryLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		idCategoryLabel.setText("Type");

		idCategoryCombo = new Combo(this, SWT.DROP_DOWN | SWT.READ_ONLY);
		idCategoryCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
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
				callback.on(IdentifierConfigWidget.this);
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
		idTypeCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		idTypeCombo.setEnabled(false);
		idTypeCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				callback.on(IdentifierConfigWidget.this);
			}
		});
	}

	public IDType getSelectedIDType() {
		if (idTypeCombo.getSelectionIndex() == -1)
			return null;
		return IDType.getIDType(idTypeCombo.getText());
	}

	@Override
	public boolean isConfigValid() {
		return getSelectedIDType() != null;
	}


	@Override
	public AEntityCollection getCollection(ConTourElement contour) {
		return new IDCollection(getSelectedIDType(), getSelectedIDType(), null, contour);
	}

}
