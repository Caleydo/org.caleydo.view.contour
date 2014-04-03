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
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.core.view.opengl.layout2.util.GLElementWindow;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryIDOwner;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.command.ShowFilteredItemsInDetailViewCommand;
import org.caleydo.view.relationshipexplorer.ui.command.UpdateDetailContentWithSelectionCommand;

/**
 * @author Christian
 *
 */
public class DetailViewWindow extends GLElementWindow implements IHistoryIDOwner {

	protected static final URL UPDATE_ON_SELECTION_ICON = AEntityColumn.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/update_selection.png");
	protected static final URL SHOW_FILTERED_ITEMS_ICON = AEntityColumn.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/filter.png");

	protected final int historyID;
	protected final ConTourElement relationshipExplorer;
	protected final IEntityCollection collection;

	protected GLButton showSelectedItemsButton;
	protected GLButton showFilteredItemsButton;

	/**
	 * @param titleLabelProvider
	 */
	public DetailViewWindow(IEntityCollection collection, ConTourElement relationshipExplorer) {
		super(collection);
		this.collection = collection;
		this.relationshipExplorer = relationshipExplorer;
		this.historyID = relationshipExplorer.getHistory().registerHistoryObject(this);
	}

	@Override
	public int getHistoryID() {
		return historyID;
	}

	public void addChangeViewOnSelectionButton(final IShowSelectedItemsListener listener, boolean changeOnSelection) {

		showSelectedItemsButton = addButton(UPDATE_ON_SELECTION_ICON, "Update when selection changes",
				changeOnSelection);
		showSelectedItemsButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				UpdateDetailContentWithSelectionCommand c = new UpdateDetailContentWithSelectionCommand(listener,
						DetailViewWindow.this, selected, relationshipExplorer.getHistory());
				c.execute();
				relationshipExplorer.getHistory().addHistoryCommand(c);
			}
		});
	}

	public void addShowFilteredItems(final IShowFilteredItemsListener listener, boolean showFilteredItems) {
		showFilteredItemsButton = addButton(SHOW_FILTERED_ITEMS_ICON, "Show filterd items only", showFilteredItems);
		showFilteredItemsButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				ShowFilteredItemsInDetailViewCommand c = new ShowFilteredItemsInDetailViewCommand(listener,
						DetailViewWindow.this, selected, relationshipExplorer.getHistory());
				c.execute();
				relationshipExplorer.getHistory().addHistoryCommand(c);
			}
		});
	}

	public void selectShowSelectedItemsButton(boolean select) {
		if (showSelectedItemsButton != null)
			showSelectedItemsButton.setSelectedSilent(select);
	}

	public void selectShowFilteredItemsButton(boolean select) {
		if (showFilteredItemsButton != null)
			showFilteredItemsButton.setSelectedSilent(select);
	}

	protected GLButton addButton(final URL icon, String tooltip, boolean selected) {
		GLButton button = new GLButton(EButtonMode.CHECKBOX);
		button.setSize(16, 16);
		button.setVisibility(EVisibility.PICKABLE);
		button.setRenderer(GLRenderers.fillImage(icon));
		button.setSelectedRenderer(new IGLRenderer() {
			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				g.fillImage(icon, 0, 0, w, h);
				g.gl.glEnable(GL.GL_BLEND);
				g.gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
				g.gl.glEnable(GL.GL_LINE_SMOOTH);
				g.color(new Color(1, 1, 1, 0.5f)).fillRoundedRect(0, 0, w, h, Math.min(w, h) * 0.25f);
				// g.gl.glBlendFunc(GL.GL_ONE, GL.GL_ONE_MINUS_SRC_ALPHA);
			}
		});
		button.setTooltip(tooltip);
		button.setSelected(selected);

		addTitleElement(button, false);
		return button;
	}

	/**
	 * @return the collection, see {@link #collection}
	 */
	public IEntityCollection getCollection() {
		return collection;
	}

}
