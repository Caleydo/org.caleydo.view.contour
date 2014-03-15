/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail;

import java.net.URL;

import javax.media.opengl.GL;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElement.EVisibility;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.column.operation.UpdateDetailContentWithSelectionCommand;

/**
 * @author Christian
 *
 */
public class CompoundDetailViewWindowFactory implements IDetailViewWindowFactory {

	protected static final URL UPDATE_ON_SELECTION_ICON = AEntityColumn.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/update_selection.png");

	protected final RelationshipExplorerElement relationshipExplorer;

	public CompoundDetailViewWindowFactory(RelationshipExplorerElement relationshipExplorer) {
		this.relationshipExplorer = relationshipExplorer;
	}

	@Override
	public DetailViewWindow createWindow(IEntityCollection collection) {
		final CompoundDetailViewWindow window = new CompoundDetailViewWindow(collection, relationshipExplorer,
				collection);
		boolean changeOnSelection = true;
		window.changeViewOnSelection(changeOnSelection);
		GLButton button = new GLButton(EButtonMode.CHECKBOX);
		button.setSize(16, 16);
		button.setVisibility(EVisibility.PICKABLE);
		button.setRenderer(GLRenderers.fillImage(UPDATE_ON_SELECTION_ICON));
		button.setSelectedRenderer(new IGLRenderer() {
			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				g.fillImage(UPDATE_ON_SELECTION_ICON, 0, 0, w, h);
				g.gl.glEnable(GL.GL_BLEND);
				g.gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				g.gl.glEnable(GL.GL_LINE_SMOOTH);
				g.color(new Color(1, 1, 1, 0.5f)).fillRoundedRect(0, 0, w, h, Math.min(w, h) * 0.25f);
				// g.gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
			}
		});
		button.setTooltip("Update when selection changes");
		button.setSelected(changeOnSelection);
		button.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				UpdateDetailContentWithSelectionCommand c = new UpdateDetailContentWithSelectionCommand(window,
						selected, relationshipExplorer.getHistory());
				c.execute();
				relationshipExplorer.getHistory().addHistoryCommand(c, Color.YELLOW);
			}
		});

		window.addTitleElement(button, false);

		return window;
	}
}
