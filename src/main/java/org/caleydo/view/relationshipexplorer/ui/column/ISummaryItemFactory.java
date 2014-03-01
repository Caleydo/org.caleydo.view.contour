/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.util.Set;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;

/**
 * @author Christian
 *
 */
public interface ISummaryItemFactory {

	public GLElement createSummaryItem(Set<NestableItem> items);

}