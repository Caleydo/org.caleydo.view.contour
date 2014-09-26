/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.item.factory;

import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;

/**
 * @author Christian
 *
 */
public interface IConfigurationAddon<T> extends ILabeled {

	/**
	 * Determines whether this addon is applicable for the specified collection.
	 *
	 * @param collection
	 * @return
	 */
	public boolean accepts(IEntityCollection collection);

	/**
	 * Starts a custom configuration procedure for an Object T. When this procedure is finished the callback is supposed
	 * to be notified.
	 *
	 * @param callback
	 */
	public void configure(ICallback<T> callback);

}
