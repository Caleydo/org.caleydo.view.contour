/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.image;

import java.util.List;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.base.Runnables;
import org.caleydo.datadomain.image.ImageDataDomain;
import org.caleydo.view.relationshipexplorer.ui.collection.IDCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.detail.IDetailViewConfigurationAddon;
import org.caleydo.view.relationshipexplorer.ui.detail.IDetailViewFactory;
import org.caleydo.view.relationshipexplorer.ui.dialog.SelectDataDomainDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

/**
 * @author Christian
 *
 */
public class HTIImageDetailViewAddon implements IDetailViewConfigurationAddon {

	@Override
	public boolean accepts(IEntityCollection collection) {
		if (!(collection instanceof IDCollection))
			return false;

		List<IDataDomain> dds = DataDomainManager.get().getDataDomainsByType(ImageDataDomain.DATA_DOMAIN_TYPE);

		return !dds.isEmpty();
	}

	@Override
	public void configure(final ICallback<IDetailViewFactory> callback) {
		Runnables.withinSWTThread(new Runnable() {

			@Override
			public void run() {
				SelectDataDomainDialog<ImageDataDomain> dialog = new SelectDataDomainDialog<ImageDataDomain>(
						Display.getDefault().getActiveShell(), "Select Image Dataset", ImageDataDomain.class);
				if (dialog.open() == Window.OK) {
					callback.on(new HTIImageDetailViewFactory(dialog.getDataDomain()));
				}
			}
		}).run();

	}

	@Override
	public String getLabel() {
		return "HTI Image Areas";
	}

	@Override
	public Class<? extends IDetailViewFactory> getConfigObjectClass() {
		return HTIImageDetailViewFactory.class;
	}

}
