/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.filter;

import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.operation.ESetOperation;

/**
 * @author Christian
 *
 */
public interface IFilterCommand extends IHistoryCommand {

	public IEntityCollection getCollection();

	public ESetOperation getSetOperation();

}
