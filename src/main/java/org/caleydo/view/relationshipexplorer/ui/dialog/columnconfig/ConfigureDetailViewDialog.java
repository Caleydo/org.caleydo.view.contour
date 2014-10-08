/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.base.ICallback;
import org.caleydo.view.relationshipexplorer.ui.Addons;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.AEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.ThreadSyncEvent;
import org.caleydo.view.relationshipexplorer.ui.detail.IDetailViewConfigurationAddon;
import org.caleydo.view.relationshipexplorer.ui.detail.IDetailViewFactory;
import org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig.widget.SingleAddonSelectionWidget;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Christian
 *
 */
public class ConfigureDetailViewDialog extends Dialog implements
		ICallback<SingleAddonSelectionWidget<IDetailViewFactory, IDetailViewConfigurationAddon>> {

	private final AEntityCollection collection;
	private final ConTourElement contour;

	private SingleAddonSelectionWidget<IDetailViewFactory, IDetailViewConfigurationAddon> addonWidget;

	/**
	 * @param parentShell
	 */
	public ConfigureDetailViewDialog(Shell parentShell, AEntityCollection collection, ConTourElement contour) {
		super(parentShell);
		this.collection = collection;
		this.contour = contour;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Configure Detail View for " + collection.getLabel());
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		addonWidget = new SingleAddonSelectionWidget<IDetailViewFactory, IDetailViewConfigurationAddon>(parent, this,
				"Available Detail Views", "Selected Detail View");
		addonWidget.updateWidgets(Addons.getDetailViewFactoryAddonsFor(collection), collection.getDetailViewFactory());

		return super.createDialogArea(parent);
	}

	@Override
	public void on(SingleAddonSelectionWidget<IDetailViewFactory, IDetailViewConfigurationAddon> data) {
		Button button = getButton(Window.OK);
		if (button != null)
			button.setEnabled(addonWidget.getSelectedCreator() != null);

	}

	@Override
	protected void okPressed() {

		EventPublisher.trigger(new ThreadSyncEvent(new Runnable() {
			@Override
			public void run() {
				collection.setDetailViewFactory(addonWidget.getSelectedCreator());
				if (contour.isDetailViewShown(collection)) {
					// Re-Show with new detail view
					contour.hideDetailView(collection);
					contour.showDetailView(collection);
				}
			}
		}).to(contour));

		super.okPressed();
	}

}
