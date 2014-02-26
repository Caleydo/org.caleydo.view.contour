/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.structure;

import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.AGLElementView;
import org.caleydo.core.view.opengl.layout2.GLElement;

/**
 * @author Christian
 *
 */
public class GLCompoundStructureView extends AGLElementView {

	public static final String VIEW_TYPE = "org.caleydo.view.compoundstructure";
	public static final String VIEW_NAME = "CompoundStructure";

	/**
	 * @param glCanvas
	 * @param viewType
	 * @param viewName
	 */
	public GLCompoundStructureView(IGLCanvas glCanvas) {
		super(glCanvas, VIEW_TYPE, VIEW_NAME);
	}

	@Override
	protected GLElement createRoot() {
		return new CompoundStructureElement();
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return new SerializedCompoundStructureView();
	}

}
