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
import org.caleydo.datadomain.image.ImageDataDomain;
import org.caleydo.view.relationshipexplorer.ui.collection.IDCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.detail.IDetailViewConfigurationAddon;
import org.caleydo.view.relationshipexplorer.ui.detail.IDetailViewFactory;

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
	public void configure(ICallback<IDetailViewFactory> callback) {
		// TODO Auto-generated method stub

	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<? extends IDetailViewFactory> getConfigObjectClass() {
		// TODO Auto-generated method stub
		return null;
	}

}
