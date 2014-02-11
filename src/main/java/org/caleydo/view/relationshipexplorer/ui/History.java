/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement.IIDMappingUpdateHandler;

/**
 * TODO: Possible performance improvements: Take snapshots of the whole setup every now and then. The reset command
 * might not need to call sorting etc if a filtering or selection update follows.
 *
 * @author Christian
 *
 */
public class History extends AnimatedGLElementContainer {

	protected RelationshipExplorerElement relationshipExplorer;
	protected List<IHistoryCommand> commands = new ArrayList<>();
	protected int currentPosition = -1;

	protected AMappingUpdateOperation lastMappingUpdateOperation;

	protected class HistoryIDMappingHandler implements IIDMappingUpdateHandler {

		@Override
		public void handleIDMappingUpdate(AMappingUpdateOperation operation) {
			relationshipExplorer.executeMappingUpdateOperation(operation);
			lastMappingUpdateOperation = operation;
		}
	}

	protected HistoryIDMappingHandler idMappingHandler = new HistoryIDMappingHandler();

	public static interface IHistoryCommand {
		public void execute();
	}

	protected static class ColumnOperationCommand implements IHistoryCommand {
		protected final AEntityColumn column;
		protected final IColumnOperation columnOperation;

		public ColumnOperationCommand(AEntityColumn column, IColumnOperation columnOperation) {
			this.column = column;
			this.columnOperation = columnOperation;
		}

		@Override
		public void execute() {
			columnOperation.execute(column);
		}

	}

	protected static class ResetCommand implements IHistoryCommand {

		protected final RelationshipExplorerElement relationshipExplorer;

		public ResetCommand(RelationshipExplorerElement relationshipExplorer) {
			this.relationshipExplorer = relationshipExplorer;
		}

		@Override
		public void execute() {
			for (AEntityColumn column : relationshipExplorer.getColumns()) {
				column.showAllItems();
				column.setSelectedItems(new HashSet<>(), false);
				column.hideMappings();
				column.sort(column.getDefaultElementComparator());
			}
		}

	}

	protected class HistoryCommandElement extends PickableGLElement {
		protected Color color;
		protected boolean hovered = false;

		public HistoryCommandElement(Color color) {
			this.color = color;
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			float index = indexOf(this);
			if (hovered || index == currentPosition) {
				renderFilled(g, w, h, 1);
				renderOutline(g, w, h);
				return;
			}

			if (index < currentPosition) {
				renderFilled(g, w, h, 0.5f);
				renderOutline(g, w, h);
			} else {
				renderFilled(g, w, h, 0.3f);
			}
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
			applyHistoryState(indexOf(this));
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
	}

	public History(RelationshipExplorerElement relationshipExplorer) {
		this.relationshipExplorer = relationshipExplorer;
		setLayout(new GLSizeRestrictiveFlowLayout(true, 2, new GLPadding(2, 2, 2, 2)));
		addHistoryCommand(new ResetCommand(relationshipExplorer), Color.GRAY);
	}

	public void addColumnOperation(AEntityColumn column, IColumnOperation columnOperation) {
		Color color = Color.GRAY;
		if (columnOperation instanceof SelectionBasedFilterOperation) {
			color = Color.LIGHT_BLUE;
		} else if (columnOperation instanceof SelectionBasedHighlightOperation) {
			color = SelectionType.SELECTION.getColor();
		}
		addHistoryCommand(new ColumnOperationCommand(column, columnOperation), color);

	}

	protected void addHistoryCommand(IHistoryCommand command, Color color) {
		if (currentPosition < commands.size() - 1) {
			int numElementsToRemove = (commands.size() - 1) - currentPosition;
			for (int i = 0; i < numElementsToRemove; i++) {
				remove(size() - 1);
			}
			commands = commands.subList(0, currentPosition + 1);
		}
		commands.add(command);
		HistoryCommandElement element = new HistoryCommandElement(color);
		element.setSize(16, Float.NaN);
		add(element);
		currentPosition++;
	}

	public void applyHistoryState(int index) {
		if (index == currentPosition || index < 0 || index >= commands.size())
			return;

		lastMappingUpdateOperation = null;

		IIDMappingUpdateHandler prevIDMappingHandler = relationshipExplorer.getIdMappingUpdateHandler();
		relationshipExplorer.setIdMappingUpdateHandler(idMappingHandler);
		for (int i = currentPosition < index ? currentPosition : 0; i <= index; i++) {
			commands.get(i).execute();
		}

		if (lastMappingUpdateOperation != null)
			relationshipExplorer.updateSelectionMappings(lastMappingUpdateOperation.getSrcColumn());
		relationshipExplorer.setIdMappingUpdateHandler(prevIDMappingHandler);

		currentPosition = index;
		repaintAll();
	}

	@Override
	public Vec2f getMinSize() {
		float maxHeight = Float.MIN_VALUE;
		float sumWidth = 0;
		int numItems = 0;
		for (GLElement child : this) {
			if (child.getVisibility() != EVisibility.NONE) {
				Vec2f minSize = child.getMinSize();
				sumWidth += minSize.x();
				if (maxHeight < minSize.y())
					maxHeight = minSize.y();

				numItems++;
			}
		}
		return new Vec2f(4 + sumWidth + (numItems - 1) * 2, 4 + maxHeight);
	}
}
