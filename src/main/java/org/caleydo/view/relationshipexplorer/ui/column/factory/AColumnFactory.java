/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.factory;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.ISummaryItemFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.ISummaryItemFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;

/**
 *
 *
 * @author Christian
 *
 */
public abstract class AColumnFactory implements IColumnFactory {

	private List<IItemFactoryCreator> itemFactoryCreators = new ArrayList<>();
	private List<ISummaryItemFactoryCreator> summaryItemFactoryCreators = new ArrayList<>();

	private int defaultItemFactoryIndex = 0;
	private int defaultSummaryFactoryIndex = 0;

	/**
	 * Adds an {@link IItemFactoryCreator}, which will add a certain representation type for items in the column.
	 *
	 * @param creator
	 * @param isDefault
	 *            Determines whether the corresponding {@link IItemFactory} is used by default to show items in the
	 *            column.
	 */
	public void addItemFactoryCreator(IItemFactoryCreator creator, boolean isDefault) {
		itemFactoryCreators.add(creator);
		if (isDefault)
			defaultItemFactoryIndex = itemFactoryCreators.size() - 1;
	}

	/**
	 * Adds an {@link ISummaryItemFactoryCreator}, which will add a certain representation type for summary items in the
	 * column.
	 *
	 * @param creator
	 * @param isDefault
	 *            Determines whether the corresponding {@link ISummaryItemFactory} is used by default to show summary
	 *            items in the column.
	 */
	public void addSummaryItemFactoryCreator(ISummaryItemFactoryCreator creator, boolean isDefault) {
		summaryItemFactoryCreators.add(creator);
		if (isDefault)
			defaultSummaryFactoryIndex = summaryItemFactoryCreators.size() - 1;
	}

	@Override
	public final IColumnModel create(IEntityCollection collection, ConTourElement contour) {
		AEntityColumn column = createColumnInstance(collection, contour);
		initColumn(column);
		return column;
	}

	protected void initColumn(AEntityColumn column) {
		for (int i = 0; i < itemFactoryCreators.size(); i++) {
			IItemFactoryCreator creator = itemFactoryCreators.get(i);
			column.addItemFactoryCreator(creator);
			if (defaultItemFactoryIndex == i)
				column.setItemFactoryCreator(creator);
		}

		for (int i = 0; i < summaryItemFactoryCreators.size(); i++) {
			ISummaryItemFactoryCreator creator = summaryItemFactoryCreators.get(i);
			column.addSummaryItemFactoryCreator(creator);
			if (defaultSummaryFactoryIndex == i)
				column.setSummaryItemFactoryCreator(creator);
		}
		column.init();
	}

	/**
	 * Updates an existing column according to this factory.
	 *
	 * @param column
	 */
	public void updateColumn(AEntityColumn column) {
		column.reset();
		initColumn(column);
		column.getColumn().getHeader().updateButtonBar();
	}

	/**
	 * @return the itemFactoryCreators, see {@link #itemFactoryCreators}
	 */
	public List<IItemFactoryCreator> getItemFactoryCreators() {
		return itemFactoryCreators;
	}

	/**
	 * @return the summaryItemFactoryCreators, see {@link #summaryItemFactoryCreators}
	 */
	public List<ISummaryItemFactoryCreator> getSummaryItemFactoryCreators() {
		return summaryItemFactoryCreators;
	}

	public void clearItemFactoryCreators() {
		itemFactoryCreators.clear();
	}

	public void clearSummaryItemFactoryCreators() {
		summaryItemFactoryCreators.clear();
	}

	/**
	 * @return A new instance of the column. Used by {@link #create(IEntityCollection, ConTourElement)}.
	 */
	protected abstract AEntityColumn createColumnInstance(IEntityCollection collection, ConTourElement contour);

}
