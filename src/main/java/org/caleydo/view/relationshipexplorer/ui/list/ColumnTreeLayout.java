/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.list;

import java.util.List;

import org.caleydo.core.view.opengl.layout2.layout.IGLLayout2;
import org.caleydo.core.view.opengl.layout2.layout.IGLLayoutElement;

/**
 * @author Christian
 *
 */
public class ColumnTreeLayout implements IGLLayout2 {

	protected NestableColumn2 nestableColumn;
	protected float columnSpacing;
	protected float itemSpacing;
	protected float headerBodySpacing;

	public ColumnTreeLayout(NestableColumn2 nestableColumn, float columnSpacing, float itemSpacing,
			float headerBodySpacing) {
		this.nestableColumn = nestableColumn;
		this.columnSpacing = columnSpacing;
		this.itemSpacing = itemSpacing;
		this.headerBodySpacing = headerBodySpacing;
	}

	@Override
	public boolean doLayout(List<? extends IGLLayoutElement> children, float w, float h, IGLLayoutElement parent,
			int deltaTimeMs) {

		return false;
	}

}
