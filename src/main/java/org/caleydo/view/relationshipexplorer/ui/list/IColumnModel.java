/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.list;

import java.util.Set;

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.view.relationshipexplorer.ui.list.ColumnTree.Column;
import org.caleydo.view.relationshipexplorer.ui.list.ColumnTree.NestableItem;

/**
 * @author Christian
 *
 */
public interface IColumnModel extends ILabeled {

	public void fill(Column column, Column parentColumn);

	public GLElement getSummaryElement(Set<NestableItem> items);

}
