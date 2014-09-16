/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig;

import org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig.widget.IdentifierConfigWidget;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;

/**
 * @author Christian
 *
 */
public class DataTypePage extends WizardPage {

	private Composite dataTypeSpecificComposite;

	private Text columnNameText;

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	protected DataTypePage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	@Override
	public void createControl(Composite parent) {
		final Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(2, false));

		Group columnNameGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		columnNameGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true, 2, 1));
		columnNameGroup.setLayout(new GridLayout(1, false));
		columnNameGroup.setText("Column Name");

		columnNameText = new Text(columnNameGroup, SWT.BORDER);
		columnNameText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		columnNameText.setText("New Column");

		Group selectColumnTypeGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		selectColumnTypeGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		selectColumnTypeGroup.setLayout(new GridLayout(1, false));
		selectColumnTypeGroup.setText("Data Type");

		addDataTypeButton(selectColumnTypeGroup, "Identifier", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				if (dataTypeSpecificComposite != null)
					dataTypeSpecificComposite.dispose();
				dataTypeSpecificComposite = new IdentifierConfigWidget(parentComposite);
				getShell().layout(true, true);
				getShell().pack(true);
			}
		});
		addDataTypeButton(selectColumnTypeGroup, "Dataset", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

			}
		});
		addDataTypeButton(selectColumnTypeGroup, "Pathways", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

			}
		});
		addDataTypeButton(selectColumnTypeGroup, "Groupings", new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

			}
		});

		setControl(parentComposite);
	}

	private void addDataTypeButton(Composite parent, String label, SelectionListener listener) {
		Button button = new Button(parent, SWT.RADIO);
		button.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		button.setText(label);
		button.addSelectionListener(listener);

	}

}
