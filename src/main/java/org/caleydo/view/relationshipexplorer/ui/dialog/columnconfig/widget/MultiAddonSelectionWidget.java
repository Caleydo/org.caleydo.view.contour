/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig.widget;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.base.Runnables;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IConfigurationAddon;
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
public class MultiAddonSelectionWidget<CreatorType, AddonType extends IConfigurationAddon<CreatorType>> extends
		Composite implements ICallback<CreatorType> {

	// private final ConfigureColumnTypeWizard wizard;

	private List availableRenderersList;
	private List selectedRenderersList;
	private Button addRendererButton;
	private Button removeRendererButton;
	private Map<Integer, AddonType> availableAddonsMap = new HashMap<>();
	private Map<Integer, AddonType> selectedAddonsMap = new HashMap<>();
	private Map<AddonType, CreatorType> addonToCreatorMap = new LinkedHashMap<>();
	private AddonType currentAddon;
	private final ICallback<MultiAddonSelectionWidget<CreatorType, AddonType>> widgetUpdateCallback;

	/**
	 * @param parent
	 * @param style
	 */
	public MultiAddonSelectionWidget(Composite parent,
			final ICallback<MultiAddonSelectionWidget<CreatorType, AddonType>> widgetUpdateCallback, String addonType) {
		super(parent, SWT.NONE);
		// this.wizard = wizard;
		this.widgetUpdateCallback = widgetUpdateCallback;

		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setLayout(new GridLayout(2, false));

		Group availableRenderersGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
		availableRenderersGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		availableRenderersGroup.setText("Available " + addonType);
		availableRenderersGroup.setLayout(new GridLayout(1, false));

		availableRenderersList = new List(availableRenderersGroup, SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 100;
		availableRenderersList.setLayoutData(gd);

		availableRenderersList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AddonType addon = availableAddonsMap.get(availableRenderersList.getSelectionIndex());
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
				AddonType addon = availableAddonsMap.get(availableRenderersList.getSelectionIndex());
				if (!addonToCreatorMap.containsKey(addon)) {
					currentAddon = addon;
					addon.configure(MultiAddonSelectionWidget.this);
				}
			}
		});

		Group selectedRenderersGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
		selectedRenderersGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		selectedRenderersGroup.setText("Selected " + addonType);
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
				AddonType addon = selectedAddonsMap.get(selectedRenderersList.getSelectionIndex());
				addonToCreatorMap.remove(addon);
				updateSelectedRenderersList();
				widgetUpdateCallback.on(MultiAddonSelectionWidget.this);
			}
		});

	}

	public void updateWidgets(java.util.List<AddonType> addons, java.util.List<CreatorType> existingCreators) {
		availableRenderersList.removeAll();
		availableAddonsMap.clear();
		addonToCreatorMap.clear();
		int index = 0;
		for (AddonType addon : addons) {
			availableAddonsMap.put(index, addon);
			availableRenderersList.add(addon.getLabel());
			index++;

			for (CreatorType creator : existingCreators) {
				if (addon.getConfigObjectClass() == creator.getClass()) {
					addonToCreatorMap.put(addon, creator);
				}
			}
		}

		updateSelectedRenderersList();
		addRendererButton.setEnabled(false);
		removeRendererButton.setEnabled(false);
		widgetUpdateCallback.on(this);
	}

	private void updateSelectedRenderersList() {
		selectedRenderersList.removeAll();
		selectedAddonsMap.clear();
		int index = 0;
		for (AddonType addon : addonToCreatorMap.keySet()) {
			selectedRenderersList.add(addon.getLabel());
			selectedAddonsMap.put(index, addon);
			index++;
		}
	}

	@Override
	public void on(final CreatorType creator) {
		Runnables.withinSWTThread(new Runnable() {

			@Override
			public void run() {
				addonToCreatorMap.put(currentAddon, creator);
				updateSelectedRenderersList();
				currentAddon = null;

				widgetUpdateCallback.on(MultiAddonSelectionWidget.this);
			}

		}).run();
	}

	public Collection<CreatorType> getCreators() {
		return addonToCreatorMap.values();
	}

}
