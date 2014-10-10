/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.filter;

import java.util.Set;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.operation.ESetOperation;

/**
 * @author Christian
 *
 */
public interface IFilterCommand extends IHistoryCommand {

	/**
	 * @return The source the filter is based on.
	 */
	public ILabeled getSource();

	/**
	 * @return The collections this filter shall be applied on.
	 */
	public Set<IEntityCollection> getTargetCollections();

	public ESetOperation getSetOperation();

}
