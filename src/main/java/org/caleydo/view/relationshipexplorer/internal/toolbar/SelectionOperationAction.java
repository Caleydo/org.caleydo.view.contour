/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.internal.toolbar;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.gui.SimpleAction;
import org.caleydo.data.loader.ResourceLoader;

/**
 * @author Christian
 *
 */
public class SelectionOperationAction extends SimpleAction {
	public static final String LABEL = "Toggle Selection Mode";
	public static final String ICON = "resources/icons/intersection.png";

	/**
	 * Constructor.
	 */
	public SelectionOperationAction() {
		super(LABEL, ICON, new ResourceLoader(
				org.caleydo.view.relationshipexplorer.internal.Activator.getResourceLocator()));
		setChecked(false);
	}

	@Override
	public void run() {
		super.run();
		// setChecked(false);
		// PathwayPathSelectionEvent pathEvent = new PathwayPathSelectionEvent();
		// pathEvent.setPathSegments(new ArrayList<PathwayPath>());
		// pathEvent.setSender(this);
		// pathEvent.setEventSpace(entourage.getPathEventSpace());
		// GeneralManager.get().getEventPublisher().triggerEvent(pathEvent);

		SelectionOperationEvent event = new SelectionOperationEvent(isChecked());
		EventPublisher.trigger(event);

	}
}
