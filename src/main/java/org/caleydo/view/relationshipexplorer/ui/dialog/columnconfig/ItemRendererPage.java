/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig;

import org.caleydo.view.relationshipexplorer.ui.collection.AEntityCollection;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.List;

/**
 * @author Christian
 *
 */
public class ItemRendererPage extends WizardPage implements IPageChangedListener {

	private AEntityCollection collection;
	private List availableRenderersList;
	private List selectedRenderersList;
	private Button addRendererButton;
	private Button removeRendererButton;

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	protected ItemRendererPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	@Override
	public void createControl(Composite parent) {
		final Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(2, false));

		Group availableRenderersGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		availableRenderersGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		availableRenderersGroup.setText("Available Item Representations");
		availableRenderersGroup.setLayout(new GridLayout(1, false));

		availableRenderersList = new List(availableRenderersGroup, SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 100;
		availableRenderersList.setLayoutData(gd);

		availableRenderersList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addRendererButton.setEnabled(availableRenderersList.getSelectionIndex() >= 0);
			}
		});

		addRendererButton = new Button(availableRenderersGroup, SWT.PUSH);
		addRendererButton.setText("Add");
		addRendererButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		addRendererButton.setEnabled(false);
		addRendererButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

			}
		});

		Group selectedRenderersGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		selectedRenderersGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		selectedRenderersGroup.setText("Selected Item Representations");
		selectedRenderersGroup.setLayout(new GridLayout(1, false));

		selectedRenderersList = new List(selectedRenderersGroup, SWT.BORDER | SWT.MULTI);
		selectedRenderersList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		selectedRenderersList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removeRendererButton.setEnabled(selectedRenderersList.getSelectionIndex() >= 0);
			}
		});
		removeRendererButton = new Button(selectedRenderersGroup, SWT.PUSH);
		removeRendererButton.setText("Remove");
		removeRendererButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		removeRendererButton.setEnabled(false);
		removeRendererButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {

			}
		});

		setControl(parentComposite);
	}

	protected void updateWidgets() {

	}

	@Override
	public void pageChanged(PageChangedEvent event) {
		if (event.getSelectedPage() == this) {
			ConfigureColumnTypeWizard wizard = (ConfigureColumnTypeWizard) getWizard();

			if (collection != wizard.getCollection()) {
				this.collection = wizard.getCollection();
				updateWidgets();
			}
		}

	}

}
