/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.factory;

import org.caleydo.datadomain.image.ImageDataDomain;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IDCollection;
import org.caleydo.view.relationshipexplorer.ui.column.factory.ColumnFactories.IDColumnFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.impl.HTIImageAreaConfigurationAddon.HTIImageAreaFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.impl.MappingSummaryConfigurationAddon.MappingSummaryItemFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.impl.TextConfigurationAddon.TextItemFactoryCreator;

/**
 * @author Christian
 *
 */
public class ImageAreaColumnFactory extends IDColumnFactory {

	public ImageAreaColumnFactory(IDCollection collection, ImageDataDomain dataDomain,
			ConTourElement relationshipExplorer) {
		addItemFactoryCreator(new HTIImageAreaFactoryCreator(dataDomain), true);
		addItemFactoryCreator(new TextItemFactoryCreator(), false);
		addSummaryItemFactoryCreator(new MappingSummaryItemFactoryCreator(), true);
	}

}
