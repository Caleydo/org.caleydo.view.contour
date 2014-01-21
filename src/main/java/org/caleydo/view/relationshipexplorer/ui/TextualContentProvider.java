/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.view.relationshipexplorer.ui.EntityColumn.ColumnBody;
import org.caleydo.view.relationshipexplorer.ui.EntityColumn.IEntityColumnContentProvider;

/**
 * @author Christian
 *
 */
public class TextualContentProvider implements IEntityColumnContentProvider {

	protected static final int MIN_TEXT_WIDTH = 200;
	protected static final int ITEM_HEIGHT = 16;

	protected List<GLElement> items = new ArrayList<>();

	@Override
	public void setColumnBody(ColumnBody body) {
		body.setMinSize(new Vec2f(MIN_TEXT_WIDTH, items.size() * ITEM_HEIGHT + (items.size() - 1)
				* EntityColumn.ROW_GAP));

	}

	@Override
	public List<GLElement> getContent() {
		return items;
	}

}
