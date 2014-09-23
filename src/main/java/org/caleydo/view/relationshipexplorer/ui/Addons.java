/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.util.ExtensionUtils;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactoryConfigurationAddon;

/**
 * @author Christian
 *
 */
public final class Addons {

	public static final List<IItemFactoryConfigurationAddon> ITEM_FACTORY_ADDONS = ExtensionUtils.findImplementation(
			"org.caleydo.view.contour.item", "class", IItemFactoryConfigurationAddon.class);

	private Addons() {

	}

	public static List<IItemFactoryConfigurationAddon> getItemFactoryAddonsFor(IEntityCollection collection) {
		List<IItemFactoryConfigurationAddon> addons = new ArrayList<>();
		for (IItemFactoryConfigurationAddon addon : ITEM_FACTORY_ADDONS) {
			if (addon.canCreate(collection))
				addons.add(addon);
		}
		return addons;
	}
}
