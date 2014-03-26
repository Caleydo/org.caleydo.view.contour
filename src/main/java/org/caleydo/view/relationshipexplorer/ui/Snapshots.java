/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import javax.media.opengl.GL2;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.basic.ScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.relationshipexplorer.internal.toolbar.SnapshotEvent;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;

/**
 * @author Christian
 *
 */
public class Snapshots extends GLElementContainer {

	protected final RelationshipExplorerElement relationshipExplorer;
	protected GLElementContainer snapshotContainer;

	protected class SnapshotElement extends PickableGLElement {
		protected IHistoryCommand command;
		protected Color color;
		protected boolean hovered = false;

		public SnapshotElement(IHistoryCommand command) {
			this.command = command;
			color = Color.LIGHT_GRAY;
			// if (command instanceof ResetCommand)
			// setRenderer(GLRenderers.drawText("Reset", VAlign.CENTER));
			setTooltip(command.getDescription());
			setSize(Float.NaN, 16);
			// setSize(command instanceof ResetCommand ? 60 : 16, Float.NaN);
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			if (hovered) {
				renderFilled(g, w, h, 1);
				renderOutline(g, w, h);

			} else {
				renderFilled(g, w, h, 0.3f);
			}

			g.drawText(command.getDescription(), 0, 0, w, h - 2, VAlign.CENTER);
		}

		private void renderOutline(GLGraphics g, float w, float h) {
			g.gl.glPushAttrib(GL2.GL_LINE_BIT);
			g.lineWidth(2);
			g.color(color.r, color.g, color.b, 1).drawRoundedRect(0, 0, w, h, 5);
			g.gl.glPopAttrib();
		}

		private void renderFilled(GLGraphics g, float w, float h, float alpha) {
			g.color(color.r, color.g, color.b, alpha).fillRoundedRect(0, 0, w, h, 5);
		}

		@Override
		protected void renderPickImpl(GLGraphics g, float w, float h) {
			g.fillRoundedRect(0, 0, w, h, 5);
		}

		@Override
		protected void onClicked(Pick pick) {
			applySnapshot(command);
		}

		@Override
		protected void onMouseOver(Pick pick) {
			hovered = true;
			repaint();
		}

		@Override
		protected void onMouseOut(Pick pick) {
			hovered = false;
			repaint();
		}

		@Override
		public Vec2f getMinSize() {
			return new Vec2f(16, 16);
		}

		@Override
		public String getTooltip() {
			return command.getDescription();
		}
	}

	public Snapshots(RelationshipExplorerElement relationshipExplorer) {
		this.relationshipExplorer = relationshipExplorer;
		setLayout(new GLSizeRestrictiveFlowLayout(false, 4, GLPadding.ZERO));
		setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(this, 4, GLPadding.ZERO));
		snapshotContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout(false, 4, new GLPadding(2, 2, 2, 2)));
		snapshotContainer.setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(snapshotContainer, 4,
				new GLPadding(2, 2, 2, 2)));

		ScrollingDecorator scrollingDecorator = new ScrollingDecorator(snapshotContainer, new ScrollBar(true),
				new ScrollBar(false), 8, EDimension.RECORD);
		scrollingDecorator.setMinSizeProvider(snapshotContainer);

		GLElement caption = new GLElement(GLRenderers.drawText("Snapshots", VAlign.CENTER));
		caption.setSize(Float.NaN, 16);
		add(caption);
		add(scrollingDecorator);
	}

	@ListenTo
	public void onTakeSnapshot(SnapshotEvent event) {
		addSnapshot(relationshipExplorer.getHistory().createSnapshotCommand(event.getLabel()));
	}

	public void addSnapshot(IHistoryCommand command) {
		snapshotContainer.add(new SnapshotElement(command));
		snapshotContainer.getParent().relayout();
	}

	protected void applySnapshot(IHistoryCommand c) {
		c.execute();
		relationshipExplorer.getHistory().addHistoryCommand(c);
	}

}
