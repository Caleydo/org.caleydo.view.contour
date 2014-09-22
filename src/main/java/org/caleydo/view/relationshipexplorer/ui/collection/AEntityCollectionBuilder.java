/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.collection;

import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.idprovider.IElementIDProvider;
import org.caleydo.view.relationshipexplorer.ui.column.factory.AColumnFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.ISummaryItemFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.detail.IDetailViewFactory;
import org.caleydo.view.relationshipexplorer.ui.detail.IDetailViewWindowFactory;

/**
 * @author Christian
 *
 */
public abstract class AEntityCollectionBuilder {

	protected final IElementIDProvider elementIDProvider;
	protected final ConTourElement contour;
	protected final AColumnFactory columnFactory;

	protected IDetailViewWindowFactory detailViewWindowFactory;
	protected IDetailViewFactory detailViewFactory;
	protected String label = "Column";

	public AEntityCollectionBuilder(AColumnFactory columnFactory, IElementIDProvider elementIDProvider,
			ConTourElement contour) {
		this.columnFactory = columnFactory;
		this.elementIDProvider = elementIDProvider;
		this.contour = contour;
	}

	public void addItemFactoryCreator(IItemFactoryCreator creator, boolean isDefault) {
		columnFactory.addItemFactoryCreator(creator, isDefault);
	}

	public void addSummaryItemFactoryCreator(ISummaryItemFactoryCreator creator, boolean isDefault) {
		columnFactory.addSummaryItemFactoryCreator(creator, isDefault);
	}

	/**
	 * @param label
	 *            setter, see {@link label}
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	/**
	 * @return a newly created {@link AEntityCollection}.
	 */
	public AEntityCollection build() {
		AEntityCollection collection = createInstance();
		collection.setLabel(label);
		collection.setColumnFactory(columnFactory);
		collection.setDetailViewWindowFactory(detailViewWindowFactory);
		collection.setDetailViewFactory(detailViewFactory);
		return collection;
	}

	protected abstract AEntityCollection createInstance();

}
