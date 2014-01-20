/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import java.util.List;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.ScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;

/**
 * @author Christian
 *
 */
public class EntityColumn extends GLElementContainer {

	private final GLElementContainer body;

	public static interface IEntityColumnContentProvider extends ScrollingDecorator.IHasMinSize {
		public List<GLElement> getContent();
	}

	public EntityColumn(GLElement header, IEntityColumnContentProvider contentProvider) {
		super(GLLayouts.flowVertical(5));
		this.body = new GLElementContainer(GLLayouts.flowVertical(2));
		add(header);
		ScrollingDecorator scrollingDecorator = new ScrollingDecorator(body, new ScrollBar(true), new ScrollBar(false),
				8, EDimension.RECORD);
		scrollingDecorator.setMinSizeProvider(contentProvider);
		add(scrollingDecorator);
		for (GLElement el : contentProvider.getContent()) {
			body.add(el);
		}
	}
}
