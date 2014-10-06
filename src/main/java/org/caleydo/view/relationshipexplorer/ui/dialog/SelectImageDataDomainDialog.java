/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog;

import java.util.HashMap;
import java.util.Map;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.datadomain.image.ImageDataDomain;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Christian
 *
 */
public class SelectImageDataDomainDialog extends Dialog {

	private ImageDataDomain dataDomain;
	private org.eclipse.swt.widgets.List dataDomainList;
	private Map<Integer, ImageDataDomain> dataDomainMap = new HashMap<>();

	/**
	 * @param shell
	 * @param contour
	 * @param caption
	 * @param isDefaultChecked
	 */
	public SelectImageDataDomainDialog(Shell shell) {
		super(shell);
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Select Image Dataset");
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout());
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		createDataDomainList(parentComposite);

		return super.createDialogArea(parent);
	}

	protected void createDataDomainList(Composite parentComposite) {
		dataDomainList = new org.eclipse.swt.widgets.List(parentComposite, SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 50;
		dataDomainList.setLayoutData(gd);
		dataDomainList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				setOKEnabled(true);
			}
		});
		int index = 0;
		for (IDataDomain dd : DataDomainManager.get().getDataDomainsByType(ImageDataDomain.DATA_DOMAIN_TYPE)) {
			dataDomainList.add(dd.getLabel());
			dataDomainMap.put(index, (ImageDataDomain) dd);
			index++;
		}
	}

	@Override
	protected void createButtonsForButtonBar(Composite parent) {
		super.createButtonsForButtonBar(parent);
		setOKEnabled(false);
	}

	private void setOKEnabled(boolean enabled) {
		getButton(IDialogConstants.OK_ID).setEnabled(enabled);
	}

	@Override
	protected void okPressed() {
		if (dataDomainList.getSelectionIndex() >= 0) {
			dataDomain = dataDomainMap.get(dataDomainList.getSelectionIndex());
		}

		super.okPressed();
	}

	/**
	 * @return the dataDomain, see {@link #dataDomain}
	 */
	public ImageDataDomain getDataDomain() {
		return dataDomain;
	}

}