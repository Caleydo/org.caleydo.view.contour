/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.relationshipexplorer.internal;

import org.caleydo.core.view.ARcpGLElementViewPart;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.AGLElementView;
import org.caleydo.view.relationshipexplorer.internal.serial.SerializedRelationshipExplorerView;
import org.caleydo.view.relationshipexplorer.internal.toolbar.SnapshotAction;
import org.eclipse.jface.action.IToolBarManager;

/**
 *
 * @author Christian
 *
 */
public class RcpGLRelationshipExplorerView extends ARcpGLElementViewPart {

	public RcpGLRelationshipExplorerView() {
		super(SerializedRelationshipExplorerView.class);
	}

	@Override
	protected AGLElementView createView(IGLCanvas canvas) {
		return new GLRelationshipExplorerView(glCanvas);
	}

	@Override
	protected void addToolBarContent(IToolBarManager toolBarManager) {
		toolBarManager.add(new SnapshotAction());
	}
}
