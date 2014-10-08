/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.compound;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.base.Runnables;
import org.caleydo.view.relationshipexplorer.ui.collection.IDCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.detail.IDetailViewConfigurationAddon;
import org.caleydo.view.relationshipexplorer.ui.detail.IDetailViewFactory;
import org.caleydo.view.relationshipexplorer.ui.dialog.SelectImageDataDomainDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

/**
 * @author Christian
 *
 */
public class CompoundDetailViewAddon implements IDetailViewConfigurationAddon {

	@Override
	public boolean accepts(IEntityCollection collection) {
		return collection instanceof IDCollection;
	}

	@Override
	public void configure(final ICallback<IDetailViewFactory> callback) {
		Runnables.withinSWTThread(new Runnable() {

			@Override
			public void run() {
				SelectImageDataDomainDialog<ATableBasedDataDomain> dialog = new SelectImageDataDomainDialog<ATableBasedDataDomain>(
						Display.getDefault().getActiveShell(), "Select Smiles String Dataset",
						ATableBasedDataDomain.class);
				if (dialog.open() == Window.OK) {
					callback.on(new CompoundDetailViewFactory(dialog.getDataDomain()));
				}
			}
		}).run();
	}

	@Override
	public Class<? extends IDetailViewFactory> getConfigObjectClass() {
		return CompoundDetailViewFactory.class;
	}

	@Override
	public String getLabel() {
		return "Chemical Structure";
	}

}
