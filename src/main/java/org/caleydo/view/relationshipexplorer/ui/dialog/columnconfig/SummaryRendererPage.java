/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig;

import org.caleydo.view.relationshipexplorer.ui.Addons;
import org.caleydo.view.relationshipexplorer.ui.collection.AEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.factory.AColumnFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.ISummaryItemFactoryConfigurationAddon;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.ISummaryItemFactoryCreator;
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
public class SummaryRendererPage extends WizardPage implements IPageChangedListener {

	private AEntityCollection collection;
	private MultiAddonSelectionWidget<ISummaryItemFactoryCreator, ISummaryItemFactoryConfigurationAddon> addonWidget;

	/**
	 * @param pageName
	 * @param title
	 * @param titleImage
	 */
	protected SummaryRendererPage(String pageName, String title, ImageDescriptor titleImage) {
		super(pageName, title, titleImage);
	}

	@Override
	public void createControl(Composite parent) {
		addonWidget = new MultiAddonSelectionWidget<ISummaryItemFactoryCreator, ISummaryItemFactoryConfigurationAddon>(
				parent, (ConfigureColumnTypeWizard) getWizard(), "Summary Representations");
		setControl(addonWidget);

	}

	@Override
	public void pageChanged(PageChangedEvent event) {
		ConfigureColumnTypeWizard wizard = (ConfigureColumnTypeWizard) getWizard();
		if (event.getSelectedPage() == this) {

			if (collection != wizard.getCollection()) {
				this.collection = wizard.getCollection();
				addonWidget.updateWidgets(Addons.getSummaryItemFactoryAddonsFor(collection));
			}
		} else if (event.getSelectedPage() == getNextPage()) {
			AColumnFactory factory = (AColumnFactory) wizard.getCollection().getColumnFactory();
			boolean first = true;
			for (ISummaryItemFactoryCreator creator : addonWidget.getCreators()) {
				factory.addSummaryItemFactoryCreator(creator, first);
				first = false;
			}
		}

	}

	@Override
	public boolean isPageComplete() {
		return !addonWidget.getCreators().isEmpty();
	}

}
