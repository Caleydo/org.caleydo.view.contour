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
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IConfigurationAddon;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactoryConfigurationAddon;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.ISummaryItemFactoryConfigurationAddon;
import org.caleydo.view.relationshipexplorer.ui.detail.IDetailViewConfigurationAddon;

/**
 * @author Christian
 *
 */
public final class Addons {

	public static final List<IItemFactoryConfigurationAddon> ITEM_FACTORY_ADDONS = ExtensionUtils.findImplementation(
			"org.caleydo.view.contour.item", "class", IItemFactoryConfigurationAddon.class);

	public static final List<ISummaryItemFactoryConfigurationAddon> SUMMARY_ITEM_FACTORY_ADDONS = ExtensionUtils
			.findImplementation("org.caleydo.view.contour.summaryitem", "class",
					ISummaryItemFactoryConfigurationAddon.class);

	public static final List<IDetailViewConfigurationAddon> DETAIL_VIEW_FACTORY_ADDONS = ExtensionUtils
			.findImplementation("org.caleydo.view.contour.detailview", "class",
 IDetailViewConfigurationAddon.class);

	private Addons() {

	}

	public static List<IItemFactoryConfigurationAddon> getItemFactoryAddonsFor(IEntityCollection collection) {
		return getAddonsFor(ITEM_FACTORY_ADDONS, collection);
	}

	public static List<ISummaryItemFactoryConfigurationAddon> getSummaryItemFactoryAddonsFor(
			IEntityCollection collection) {
		return getAddonsFor(SUMMARY_ITEM_FACTORY_ADDONS, collection);
	}

	public static List<IDetailViewConfigurationAddon> getDetailViewFactoryAddonsFor(IEntityCollection collection) {
		return getAddonsFor(DETAIL_VIEW_FACTORY_ADDONS, collection);
	}

	private static <T extends IConfigurationAddon<?>> List<T> getAddonsFor(List<T> addons, IEntityCollection collection) {
		List<T> res = new ArrayList<>();
		for (T addon : addons) {
			if (addon.accepts(collection))
				res.add(addon);
		}
		return res;
	}
}
