/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.structure;

import org.caleydo.core.gui.command.AOpenViewHandler;

/**
 * @author Christian
 *
 */
public class OpenStructureViewHandler extends AOpenViewHandler {

	public OpenStructureViewHandler() {
		super(GLCompoundStructureView.VIEW_TYPE, SINGLE);
	}
}
