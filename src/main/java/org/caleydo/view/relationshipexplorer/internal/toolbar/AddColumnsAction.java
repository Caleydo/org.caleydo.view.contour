/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.internal.toolbar;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.gui.SimpleAction;
import org.caleydo.data.loader.ResourceLoader;
import org.caleydo.view.relationshipexplorer.internal.GLRelationshipExplorerView;
import org.caleydo.view.relationshipexplorer.ui.dialog.AddColumnDialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Display;

/**
 * @author Christian
 *
 */
public class AddColumnsAction extends SimpleAction {
	public static final String LABEL = "Add Columns";
	public static final String ICON = "resources/icons/column_double_add.png";

	public final GLRelationshipExplorerView view;

	/**
	 * @param label
	 * @param iconResource
	 */
	public AddColumnsAction(GLRelationshipExplorerView view) {
		super(LABEL, ICON, new ResourceLoader(
				org.caleydo.view.relationshipexplorer.internal.Activator.getResourceLocator()));
		this.view = view;
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

		AddColumnDialog dialog = new AddColumnDialog(Display.getDefault().getActiveShell(),
				view.getRelationshipExplorer());
		if (dialog.open() == Window.OK) {
			AddColumnsEvent event = new AddColumnsEvent(dialog.getCollections());
			EventPublisher.trigger(event.to(view.getRelationshipExplorer()));

		}
	}

}
