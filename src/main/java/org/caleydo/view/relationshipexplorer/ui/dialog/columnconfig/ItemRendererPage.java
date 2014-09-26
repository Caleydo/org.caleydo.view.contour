/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig;

import org.caleydo.view.relationshipexplorer.ui.Addons;
import org.caleydo.view.relationshipexplorer.ui.collection.AEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.factory.AColumnFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactoryConfigurationAddon;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig.widget.MultiAddonSelectionWidget;
import org.eclipse.jface.dialogs.IPageChangedListener;
import org.eclipse.jface.dialogs.PageChangedEvent;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Christian
 *
 */
public class ItemRendererPage extends WizardPage implements IPageChangedListener {

	private AEntityCollection collection;
	private MultiAddonSelectionWidget<IItemFactoryCreator, IItemFactoryConfigurationAddon> addonWidget;

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
		addonWidget = new MultiAddonSelectionWidget<IItemFactoryCreator, IItemFactoryConfigurationAddon>(parent,
				(ConfigureColumnTypeWizard) getWizard(), "Item Representations");
		setControl(addonWidget);
	}

	@Override
	public void pageChanged(PageChangedEvent event) {
		ConfigureColumnTypeWizard wizard = (ConfigureColumnTypeWizard) getWizard();
		if (event.getSelectedPage() == this) {

			if (collection != wizard.getCollection()) {
				this.collection = wizard.getCollection();
				addonWidget.updateWidgets(Addons.getItemFactoryAddonsFor(collection));
			}
		} else if (event.getSelectedPage() == getNextPage()) {

		}

	}

	public void updateCollection() {
		ConfigureColumnTypeWizard wizard = (ConfigureColumnTypeWizard) getWizard();
		AColumnFactory factory = (AColumnFactory) wizard.getCollection().getColumnFactory();
		boolean first = true;
		for (IItemFactoryCreator creator : addonWidget.getCreators()) {
			factory.addItemFactoryCreator(creator, first);
			first = false;
		}
	}

	@Override
	public boolean isPageComplete() {
		return !addonWidget.getCreators().isEmpty();
	}

}
