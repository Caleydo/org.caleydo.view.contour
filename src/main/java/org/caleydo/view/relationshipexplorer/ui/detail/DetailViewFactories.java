/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail;

import gleem.linalg.Vec2f;

import java.net.URL;

import org.caleydo.core.util.base.Runnables;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.AEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig.ConfigureDetailViewDialog;
import org.eclipse.swt.widgets.Display;

/**
 * @author Christian
 *
 */
public final class DetailViewFactories {

	protected static final URL CONFIG_ICON = DetailViewFactories.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/gear_in.png");

	private DetailViewFactories() {
	}

	public static IDetailViewFactory createDefaultDetailViewFactory() {
		return new IDetailViewFactory() {

			@Override
			public GLElement createDetailView(final IEntityCollection collection, DetailViewWindow window,
					final ConTourElement contour) {
				GLElementContainer dummy = new GLElementContainer() {
					@Override
					public Vec2f getMinSize() {
						return new Vec2f(300, 300);
					}
				};
				dummy.setLayout(GLLayouts.flowVertical(5));
				GLElementContainer firstLine = new GLElementContainer(GLLayouts.flowHorizontal(1));
				firstLine.setSize(Float.NaN, 32);


				GLElement textElement = new GLElement(GLRenderers.drawText("Configure Detail View...", VAlign.CENTER));
				textElement.setSize(350, 32);



				GLButton configButton = new GLButton(EButtonMode.BUTTON);
				configButton.setCallback(new ISelectionCallback() {

					@Override
					public void onSelectionChanged(GLButton button, boolean selected) {
						Runnables.withinSWTThread(new Runnable() {
							@Override
							public void run() {
								ConfigureDetailViewDialog dialog = new ConfigureDetailViewDialog(Display.getDefault()
										.getActiveShell(), (AEntityCollection) collection, contour);
								dialog.open();
							}
						}).run();
					}
				});
				configButton.setSize(32, 32);
				configButton.setRenderer(GLRenderers.fillImage(CONFIG_ICON));
				configButton.setTooltip("Configure...");

				firstLine.add(new GLElement());
				firstLine.add(textElement);
				firstLine.add(configButton);
				firstLine.add(new GLElement());


				dummy.add(new GLElement());
				dummy.add(firstLine);
				dummy.add(new GLElement());

				return dummy;
			}

			@Override
			public DetailViewWindow createWindow(IEntityCollection collection, ConTourElement contour) {
				return new DetailViewWindow(collection, contour);
			}

		};
	}
}
