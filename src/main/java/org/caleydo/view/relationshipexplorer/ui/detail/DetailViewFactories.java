/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail;

import gleem.linalg.Vec2f;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;

/**
 * @author Christian
 *
 */
public final class DetailViewFactories {
	private DetailViewFactories() {
	}

	public static IDetailViewFactory createDefaultDetailViewFactory() {
		return new IDetailViewFactory() {

			@Override
			public GLElement createDetailView(IEntityCollection collection, DetailViewWindow window,
					ConTourElement contour) {
				GLElement dummy = new GLElement() {
					@Override
					public Vec2f getMinSize() {
						return new Vec2f(300, 300);
					}
				};
				dummy.setRenderer(GLRenderers.fillRect(Color.BLUE));
				return dummy;
			}

			@Override
			public DetailViewWindow createWindow(IEntityCollection collection, ConTourElement contour) {
				return new DetailViewWindow(collection, contour);
			}

		};
	}
}
