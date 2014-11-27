/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig;

import org.caleydo.core.util.base.ICallback;
import org.caleydo.view.relationshipexplorer.ui.Addons;
import org.caleydo.view.relationshipexplorer.ui.collection.AEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.detail.IDetailViewConfigurationAddon;
import org.caleydo.view.relationshipexplorer.ui.detail.IDetailViewFactory;
import org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig.widget.SingleAddonSelectionWidget;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Christian
 *
 */
public class DetailViewPage extends WizardPage implements
		ICallback<SingleAddonSelectionWidget<IDetailViewFactory, IDetailViewConfigurationAddon>>, IPageChangedListener {

	private AEntityCollection collection;
	private SingleAddonSelectionWidget<IDetailViewFactory, IDetailViewConfigurationAddon> addonWidget;

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	protected DetailViewPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	@Override
	public void createControl(Composite parent) {
		addonWidget = new SingleAddonSelectionWidget<IDetailViewFactory, IDetailViewConfigurationAddon>(parent, this,
				"Available Detail Views", "Selected Detail View");
		setControl(addonWidget);
	}

	@Override
	public void pageChanged(PageChangedEvent event) {
		ConfigureColumnTypeWizard wizard = (ConfigureColumnTypeWizard) getWizard();
		if (event.getSelectedPage() == this) {

			if (collection != wizard.getCollection()) {
				this.collection = wizard.getCollection();
				addonWidget.updateWidgets(Addons.getDetailViewFactoryAddonsFor(collection),
						collection.getDetailViewFactory());
			}
		} else if (event.getSelectedPage() == getNextPage()) {

		}

	}

	public void updateCollection() {
		ConfigureColumnTypeWizard wizard = (ConfigureColumnTypeWizard) getWizard();
		this.collection = wizard.getCollection();
		collection.setDetailViewFactory(addonWidget.getSelectedCreator());
	}

	@Override
	public boolean isPageComplete() {
		return addonWidget.getSelectedCreator() != null;
	}

	@Override
	public void on(SingleAddonSelectionWidget<IDetailViewFactory, IDetailViewConfigurationAddon> data) {
		getWizard().getContainer().updateButtons();
	}

}
