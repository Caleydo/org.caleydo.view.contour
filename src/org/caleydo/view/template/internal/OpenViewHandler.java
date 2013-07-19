/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.template.internal;

import org.caleydo.core.gui.command.AOpenViewHandler;

/**
 * simple command handler for opening this view
 *
 * @author AUTHOR
 *
 */
public class OpenViewHandler extends AOpenViewHandler {
	public OpenViewHandler() {
		super(GLTemplateSingleView.VIEW_TYPE, SINGLE);
	}
}
