/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.base.Runnables;
import org.caleydo.view.relationshipexplorer.ui.Addons;
import org.caleydo.view.relationshipexplorer.ui.collection.AEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.factory.AColumnFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactoryConfigurationAddon;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactoryCreator;
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
public class ItemRendererPage extends WizardPage implements IPageChangedListener, ICallback<IItemFactoryCreator> {

	private AEntityCollection collection;
	private List availableRenderersList;
	private List selectedRenderersList;
	private Button addRendererButton;
	private Button removeRendererButton;
	private Map<Integer, IItemFactoryConfigurationAddon> availableAddonsMap = new HashMap<>();
	private Map<Integer, IItemFactoryConfigurationAddon> selectedAddonsMap = new HashMap<>();
	private Map<IItemFactoryConfigurationAddon, IItemFactoryCreator> addonToCreatorMap = new LinkedHashMap<>();
	private IItemFactoryConfigurationAddon currentAddon;

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
				IItemFactoryConfigurationAddon addon = availableAddonsMap.get(availableRenderersList
						.getSelectionIndex());
				addRendererButton.setEnabled(!addonToCreatorMap.containsKey(addon));
			}
		});

		addRendererButton = new Button(availableRenderersGroup, SWT.PUSH);
		addRendererButton.setText("Add");
		addRendererButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		addRendererButton.setEnabled(false);
		addRendererButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				IItemFactoryConfigurationAddon addon = availableAddonsMap.get(availableRenderersList
						.getSelectionIndex());
				if (!addonToCreatorMap.containsKey(addon)) {
					currentAddon = addon;
					addon.configure(ItemRendererPage.this);
				}
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
				IItemFactoryConfigurationAddon addon = selectedAddonsMap.get(selectedRenderersList.getSelectionIndex());
				addonToCreatorMap.remove(addon);
				updateSelectedRenderersList();
				getWizard().getContainer().updateButtons();
			}
		});

		setControl(parentComposite);
	}

	protected void updateWidgets() {
		ConfigureColumnTypeWizard wizard = (ConfigureColumnTypeWizard) getWizard();
		java.util.List<IItemFactoryConfigurationAddon> addons = Addons.getItemFactoryAddonsFor(wizard.getCollection());
		availableRenderersList.removeAll();
		availableAddonsMap.clear();
		addonToCreatorMap.clear();
		int index = 0;
		for (IItemFactoryConfigurationAddon addon : addons) {
			availableAddonsMap.put(index, addon);
			availableRenderersList.add(addon.getLabel());
			index++;
		}
		selectedRenderersList.removeAll();
		addRendererButton.setEnabled(false);
		removeRendererButton.setEnabled(false);
	}

	@Override
	public void pageChanged(PageChangedEvent event) {
		ConfigureColumnTypeWizard wizard = (ConfigureColumnTypeWizard) getWizard();
		if (event.getSelectedPage() == this) {

			if (collection != wizard.getCollection()) {
				this.collection = wizard.getCollection();
				updateWidgets();
			}
		} else if (event.getSelectedPage() == getNextPage()) {
			AColumnFactory factory = (AColumnFactory) wizard.getCollection().getColumnFactory();
			boolean first = true;
			for (IItemFactoryCreator creator : addonToCreatorMap.values()) {
				factory.addItemFactoryCreator(creator, first);
				first = false;
			}
		}

	}

	private void updateSelectedRenderersList() {
		selectedRenderersList.removeAll();
		selectedAddonsMap.clear();
		int index = 0;
		for (IItemFactoryConfigurationAddon addon : addonToCreatorMap.keySet()) {
			selectedRenderersList.add(addon.getLabel());
			selectedAddonsMap.put(index, addon);
			index++;
		}
	}

	@Override
	public void on(final IItemFactoryCreator creator) {
		Runnables.withinSWTThread(new Runnable() {

			@Override
			public void run() {
				addonToCreatorMap.put(currentAddon, creator);
				updateSelectedRenderersList();
				currentAddon = null;

				getWizard().getContainer().updateButtons();
			}

		}).run();
	}

	@Override
	public boolean isPageComplete() {
		return selectedRenderersList.getItemCount() > 0;
	}

}
