/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.internal;

import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;

/**
 * @author Christian
 *
 */
public class EmptyViewFactory implements IGLElementFactory {

	@Override
	public String getId() {
		return "relationship explorer";
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {

		final ConTourElement contour = new ConTourElement();

		// -----
		// contour.getHistory().setInitCommand(initCommand);

		return contour;
	}

	@Override
	public boolean apply(GLElementFactoryContext context) {
		return true;
	}

}
