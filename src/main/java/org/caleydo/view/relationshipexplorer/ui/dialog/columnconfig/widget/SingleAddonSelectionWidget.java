/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig.widget;

import java.util.HashMap;
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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

/**
 * @author Christian
 *
 */
public class SingleAddonSelectionWidget<CreatorType, AddonType extends IConfigurationAddon<CreatorType>> extends
		Composite implements ICallback<CreatorType> {

	private List availableRenderersList;
	private Label selectedRendererLabel;
	private Button selectRendererButton;
	private Map<Integer, AddonType> availableAddonsMap = new HashMap<>();
	private AddonType currentAddon;
	private AddonType selectedAddon;
	private CreatorType selectedCreator;
	private final ICallback<SingleAddonSelectionWidget<CreatorType, AddonType>> widgetUpdateCallback;

	/**
	 * @param parent
	 * @param style
	 */
	public SingleAddonSelectionWidget(Composite parent,
			final ICallback<SingleAddonSelectionWidget<CreatorType, AddonType>> widgetUpdateCallback,
			String availableAddonsLabel, String selectedAddonLabel) {
		super(parent, SWT.NONE);
		// this.wizard = wizard;
		this.widgetUpdateCallback = widgetUpdateCallback;

		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setLayout(new GridLayout(2, false));

		Group availableRenderersGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
		availableRenderersGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		availableRenderersGroup.setText(availableAddonsLabel);
		availableRenderersGroup.setLayout(new GridLayout(1, false));

		availableRenderersList = new List(availableRenderersGroup, SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.heightHint = 100;
		availableRenderersList.setLayoutData(gd);

		availableRenderersList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AddonType addon = availableAddonsMap.get(availableRenderersList.getSelectionIndex());
				selectRendererButton.setEnabled(addon != selectedAddon);
			}
		});

		selectRendererButton = new Button(availableRenderersGroup, SWT.PUSH);
		selectRendererButton.setText("Select");
		selectRendererButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		selectRendererButton.setEnabled(false);
		selectRendererButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				AddonType addon = availableAddonsMap.get(availableRenderersList.getSelectionIndex());
				if (selectedAddon != addon) {
					currentAddon = addon;
					addon.configure(SingleAddonSelectionWidget.this);
				}
			}
		});

		Group selectedRenderersGroup = new Group(this, SWT.SHADOW_ETCHED_IN);
		selectedRenderersGroup.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		selectedRenderersGroup.setText(selectedAddonLabel);
		selectedRenderersGroup.setLayout(new GridLayout(1, false));

		selectedRendererLabel = new Label(selectedRenderersGroup, SWT.NONE);
		selectedRendererLabel.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
	}

	public void updateWidgets(java.util.List<AddonType> addons, CreatorType currentCreator) {
		availableRenderersList.removeAll();
		availableAddonsMap.clear();
		selectedRendererLabel.setText("");

		int index = 0;
		for (AddonType addon : addons) {
			availableAddonsMap.put(index, addon);
			availableRenderersList.add(addon.getLabel());
			index++;

			if (currentCreator != null && addon.getConfigObjectClass() == currentCreator.getClass()) {
				selectedAddon = addon;
				selectedCreator = currentCreator;
				selectedRendererLabel.setText(addon.getLabel());
			}
		}

		selectRendererButton.setEnabled(false);
		widgetUpdateCallback.on(this);
	}


	@Override
	public void on(final CreatorType creator) {
		Runnables.withinSWTThread(new Runnable() {

			@Override
			public void run() {
				selectedAddon = currentAddon;
				selectedCreator = creator;
				selectedRendererLabel.setText(selectedAddon.getLabel());
				currentAddon = null;

				widgetUpdateCallback.on(SingleAddonSelectionWidget.this);
			}

		}).run();
	}

	/**
	 * @return the selectedCreator, see {@link #selectedCreator}
	 */
	public CreatorType getSelectedCreator() {
		return selectedCreator;
	}

}
