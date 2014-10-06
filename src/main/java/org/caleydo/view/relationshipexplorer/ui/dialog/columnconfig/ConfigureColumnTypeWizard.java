/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig;

import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.AEntityCollection;
import org.eclipse.jface.dialogs.IPageChangeProvider;
import org.eclipse.jface.wizard.IWizardContainer;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.swt.widgets.Composite;

/**
 * @author Christian
 *
 */
public class ConfigureColumnTypeWizard extends Wizard {

	private final ConTourElement contour;

	private DataTypePage dataTypePage;
	private ItemRendererPage itemRendererPage;
	private SummaryRendererPage summaryRendererPage;

	private AEntityCollection collection;

	public ConfigureColumnTypeWizard(ConTourElement contour) {
		setWindowTitle("Create A New Column Type");
		this.contour = contour;
	}

	@Override
	public void addPages() {
		dataTypePage = new DataTypePage("Data Type", "Specify the data the column is based on", null);

		itemRendererPage = new ItemRendererPage("Item Representation",
				"Select the representations for individual items in the column", null);
		summaryRendererPage = new SummaryRendererPage("Summary Representation",
				"Select the summary representations for multiple items in the column", null);

		addPage(dataTypePage);
		addPage(itemRendererPage);
		addPage(summaryRendererPage);

		IWizardContainer wizardContainer = getContainer();
		if (wizardContainer instanceof IPageChangeProvider) {
			IPageChangeProvider pageChangeProvider = (IPageChangeProvider) wizardContainer;
			pageChangeProvider.addPageChangedListener(dataTypePage);
			pageChangeProvider.addPageChangedListener(itemRendererPage);
			pageChangeProvider.addPageChangedListener(summaryRendererPage);
		}
	}

	@Override
	public void createPageControls(Composite pageContainer) {

		super.createPageControls(pageContainer);
	}

	@Override
	public boolean performFinish() {
		if (getContainer().getCurrentPage() == dataTypePage && dataTypePage.isPageComplete()) {
			collection = dataTypePage.getCollection();
		}

		if (getContainer().getCurrentPage() == itemRendererPage && itemRendererPage.isPageComplete()) {
			itemRendererPage.updateCollection();
		}
		if (getContainer().getCurrentPage() == summaryRendererPage) {
			itemRendererPage.updateCollection();
			if (summaryRendererPage.isPageComplete())
				summaryRendererPage.updateCollection();
		}
		contour.registerEntityCollection(collection);
		return true;
	}

	@Override
	public boolean canFinish() {
		return dataTypePage.isPageComplete();
	}

	@Override
	public boolean performCancel() {
		return super.performCancel();
	}

	/**
	 * @return the collection, see {@link #collection}
	 */
	public AEntityCollection getCollection() {
		return collection;
	}

	/**
	 * @param collection
	 *            setter, see {@link collection}
	 */
	public void setCollection(AEntityCollection collection) {
		this.collection = collection;
	}

	/**
	 * @return the contour, see {@link #contour}
	 */
	public ConTourElement getContour() {
		return contour;
	}

}
