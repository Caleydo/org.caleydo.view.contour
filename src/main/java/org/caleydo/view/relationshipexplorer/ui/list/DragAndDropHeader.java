/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.list;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.dnd.EDnDType;
import org.caleydo.core.view.opengl.layout2.dnd.IDnDItem;
import org.caleydo.core.view.opengl.layout2.dnd.IDragGLSource;
import org.caleydo.core.view.opengl.layout2.dnd.IDragInfo;
import org.caleydo.core.view.opengl.layout2.dnd.IDropGLTarget;
import org.caleydo.core.view.opengl.layout2.dnd.IUIDragInfo;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.History;
import org.caleydo.view.relationshipexplorer.ui.column.operation.AddChildColumnCommand;
import org.caleydo.view.relationshipexplorer.ui.column.operation.RemoveColumnCommand;

/**
 * @author Christian
 *
 */
public class DragAndDropHeader implements IDragGLSource, IDropGLTarget {

	protected NestableColumn column;

	public static class ColumnDragInfo implements IUIDragInfo {

		protected final IColumnModel model;

		public ColumnDragInfo(IColumnModel model) {
			this.model = model;
		}

		@Override
		public String getLabel() {
			return model.getLabel();
		}

		@Override
		public GLElement createUI() {
			// return new GLElement(GLRenderers.fillRect(new Color(Color.NEUTRAL_GREY.r, Color.NEUTRAL_GREY.g,
			// Color.NEUTRAL_GREY.b, 0.5f))).setSize(20, 20);
			return new GLElement(GLRenderers.drawText(model.getLabel())).setSize(200, 20);
		}

		/**
		 * @return the model, see {@link #model}
		 */
		public IColumnModel getModel() {
			return model;
		}

	}

	public DragAndDropHeader(NestableColumn column) {
		this.column = column;
	}

	@Override
	public IDragInfo startSWTDrag(IDragEvent event) {
		return new ColumnDragInfo(column.getColumnModel());
	}

	@Override
	public void onDropped(IDnDItem info) {
		// TODO Auto-generated method stub

	}

	@Override
	public GLElement createUI(IDragInfo info) {
		return ((ColumnDragInfo) info).createUI();
	}

	@Override
	public boolean canSWTDrop(IDnDItem item) {
		return (item.getInfo() instanceof ColumnDragInfo && ((ColumnDragInfo) item.getInfo()).getModel() != column
				.getColumnModel());
	}

	@Override
	public void onDrop(IDnDItem item) {
		IDragInfo i = item.getInfo();
		if (!(i instanceof ColumnDragInfo))
			return;
		History history = column.getColumnTree().getRelationshipExplorer().getHistory();
		ColumnDragInfo info = (ColumnDragInfo) i;
		if (item.getType() == EDnDType.COPY) {
			AddChildColumnCommand c = new AddChildColumnCommand(info.getModel().getCollection(), column
					.getColumnModel().getHistoryID(), history);
			c.execute();
			history.addHistoryCommand(c, Color.DARK_BLUE);
		} else {
			AddChildColumnCommand c = new AddChildColumnCommand(info.getModel().getCollection(), column
					.getColumnModel().getHistoryID(), history);
			c.execute();
			history.addHistoryCommand(c, Color.DARK_BLUE);

			RemoveColumnCommand rc = new RemoveColumnCommand(info.getModel(), history);
			rc.execute();
			history.addHistoryCommand(rc, Color.DARK_BLUE);
		}

	}

	@Override
	public void onItemChanged(IDnDItem item) {
		// System.out.println("changed for" + column.getColumnModel().getLabel());

	}

	@Override
	public EDnDType defaultSWTDnDType(IDnDItem item) {
		return EDnDType.MOVE;
	}

}
