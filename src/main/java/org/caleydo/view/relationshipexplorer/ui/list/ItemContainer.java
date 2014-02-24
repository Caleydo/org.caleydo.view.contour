/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.list;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;

/**
 * @author Christian
 *
 */
public class ItemContainer extends AnimatedGLElementContainer {
	public List<NestableItem> getItems() {
		return getCurrentItems();
	}

	public List<NestableItem> getCurrentItems() {
		List<NestableItem> items = new ArrayList<>(size());
		for (GLElement item : this) {
			items.add((NestableItem) item);
		}
		return items;
	}

	public void updateSummaryItems() {

	}
}
