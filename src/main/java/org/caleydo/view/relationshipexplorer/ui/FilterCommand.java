/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import java.util.Set;

import org.caleydo.core.util.base.IProvider;
import org.caleydo.view.relationshipexplorer.ui.ASetBasedColumnOperation.ESetOperation;

/**
 * @author Christian
 *
 */
public class FilterCommand implements IContextMenuCommand {

	protected final IProvider<Set<Object>> provider;
	protected final ESetOperation setOperation;
	protected final AEntityColumn column;

	public FilterCommand(ESetOperation setOperation, IProvider<Set<Object>> selectedElementIDProvider,
			AEntityColumn column) {
		this.setOperation = setOperation;
		this.provider = selectedElementIDProvider;
		this.column = column;
	}

	@Override
	public void execute() {
		Set<Object> elementIDs = provider.get();
		Set<Object> broadcastIDs = column.getBroadcastingIDsFromElementIDs(elementIDs);

		SelectionBasedFilterOperation o = new SelectionBasedFilterOperation(elementIDs, broadcastIDs, setOperation);
		o.execute(column);
		column.getRelationshipExplorer().getHistory().addColumnOperation(column, o);
	}

}
