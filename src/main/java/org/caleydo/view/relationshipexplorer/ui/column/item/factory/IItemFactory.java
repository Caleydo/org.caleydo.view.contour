/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.item.factory;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.view.relationshipexplorer.ui.list.EUpdateCause;

/**
 * @author Christian
 *
 */
public interface IItemFactory {

	public GLElement createItem(Object elementID);

	public GLElement createHeaderExtension();

	public boolean needsUpdate(EUpdateCause cause);

	public void update();

}
