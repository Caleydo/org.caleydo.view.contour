/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.factory;

import org.caleydo.datadomain.image.ImageDataDomain;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IDCollection;
import org.caleydo.view.relationshipexplorer.ui.column.IDColumn;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.HTIImageAreaFactory;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;

/**
 * @author Christian
 *
 */
public class ImageAreaColumnFactory implements IColumnFactory {

	protected final IDCollection collection;
	protected final ImageDataDomain dataDomain;
	protected final ConTourElement relationshipExplorer;

	public ImageAreaColumnFactory(IDCollection collection, ImageDataDomain dataDomain,
			ConTourElement relationshipExplorer) {
		this.collection = collection;
		this.relationshipExplorer = relationshipExplorer;
		this.dataDomain = dataDomain;
	}

	@Override
	public IColumnModel create() {
		IDColumn column = new IDColumn(collection, relationshipExplorer);
		HTIImageAreaFactory f = new HTIImageAreaFactory(dataDomain, column);
		column.setItemFactory(f);
		// column.
		// column.setSummaryItemFactory(f);
		column.init();
		return column;
	}

}
