/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.command;

import java.lang.reflect.InvocationTargetException;

import org.caleydo.view.relationshipexplorer.ui.History;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.column.TabularDataColumn;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.ISummaryItemFactory;

/**
 * @author Christian
 *
 */
public class SetSummaryItemFactoryCommand implements IHistoryCommand {

	protected final int tabularColumnHistoryID;
	protected final History history;
	protected final Class<? extends AEntityColumn> columnClass;
	protected final Class<? extends ISummaryItemFactory> factoryClass;
	protected final boolean addFactory;

	public SetSummaryItemFactoryCommand(AEntityColumn column, Class<? extends ISummaryItemFactory> factoryClass,
			History history, boolean addFactory) {
		this.tabularColumnHistoryID = column.getHistoryID();
		this.columnClass = column.getClass();
		this.factoryClass = factoryClass;
		this.history = history;
		this.addFactory = addFactory;
	}

	@Override
	public Object execute() {

		AEntityColumn col = history.getHistoryObjectAs(columnClass, tabularColumnHistoryID);
		ISummaryItemFactory f;
		try {
			f = factoryClass.getConstructor(columnClass).newInstance(col);
			if (addFactory)
				col.addSummaryItemFactory(f);
			col.setSummaryItemFactory(f);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			try {
				f = factoryClass.getConstructor(AEntityColumn.class).newInstance(col);
				if (addFactory)
					col.addSummaryItemFactory(f);
				col.setSummaryItemFactory(f);
			} catch (InstantiationException | IllegalAccessException | IllegalArgumentException
					| InvocationTargetException | NoSuchMethodException | SecurityException e1) {

				e1.printStackTrace();
			}

		}

		return null;
	}

	@Override
	public String getDescription() {
		TabularDataColumn col = history.getHistoryObjectAs(TabularDataColumn.class, tabularColumnHistoryID);
		return "Changed Summary Item View for " + col.getLabel();
	}

}
