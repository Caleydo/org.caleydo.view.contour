/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.internal.toolbar;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.gui.SimpleAction;
import org.caleydo.data.loader.ResourceLoader;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

/**
 * @author Christian
 *
 */
public class SnapshotAction extends SimpleAction {
	public static final String LABEL = "Take Snapshot";
	public static final String ICON = "resources/icons/camera.png";

	/**
	 * Constructor.
	 */
	public SnapshotAction() {
		super(LABEL, ICON, new ResourceLoader(
				org.caleydo.view.relationshipexplorer.internal.Activator.getResourceLocator()));
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();
		setChecked(false);
		// PathwayPathSelectionEvent pathEvent = new PathwayPathSelectionEvent();
		// pathEvent.setPathSegments(new ArrayList<PathwayPath>());
		// pathEvent.setSender(this);
		// pathEvent.setEventSpace(entourage.getPathEventSpace());
		// GeneralManager.get().getEventPublisher().triggerEvent(pathEvent);

		InputDialog dialog = new InputDialog(Display.getDefault().getActiveShell(), "Take Snapshot",
				"Take a snapshot of the current analysis state.", "Snapshot", null);
		if (dialog.open() == Window.OK) {
			SnapshotEvent event = new SnapshotEvent(dialog.getValue());
			EventPublisher.trigger(event);
		}

	}
}
