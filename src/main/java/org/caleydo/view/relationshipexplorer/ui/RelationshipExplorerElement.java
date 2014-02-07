/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.ScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator;
import org.caleydo.core.view.opengl.layout2.layout.GLLayoutDatas;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.internal.Activator;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 *
 *
 * @author Christian
 *
 */
public class RelationshipExplorerElement extends GLElementContainer {

	protected List<AEntityColumn> columns = new ArrayList<>();
	protected AnimatedGLElementContainer columnContainer;
	protected History history;

	public interface IIDMappingUpdateHandler {
		public void handleIDMappingUpdate(AMappingUpdateOperation operation);
	}

	protected IIDMappingUpdateHandler idMappingUpdateHandler = new IIDMappingUpdateHandler() {

		@Override
		public void handleIDMappingUpdate(AMappingUpdateOperation operation) {
			executeMappingUpdateOperation(operation);
			updateSelectionMappings(operation.getSrcColumn());
		}

	};

	public RelationshipExplorerElement() {
		super(new GLSizeRestrictiveFlowLayout(false, 5, GLPadding.ZERO));
		columnContainer = new AnimatedGLElementContainer(new GLSizeRestrictiveFlowLayout(true, 5, GLPadding.ZERO));
		// columnContainer.setLayoutData(0.9f);
		add(columnContainer);
		history = new History(this);
		ScrollingDecorator scrollingDecorator = new ScrollingDecorator(history, new ScrollBar(true), new ScrollBar(
				false), 8, EDimension.RECORD);
		scrollingDecorator.setMinSizeProvider(history);
		scrollingDecorator.setSize(Float.NaN, 30);
		add(scrollingDecorator);
	}

	public void addEntityColumn(AEntityColumn column) {
		columnContainer.add(column);
		if (columns.size() > 0) {
			GLElement columnSpacer = new GLElement(GLRenderers.fillRect(Color.LIGHT_GRAY));
			columnSpacer.setSize(2, Float.NaN);
			columnContainer.add(columnSpacer);
		}
		columns.add(column);
	}

	public Iterable<TablePerspective> getTablePerspecives() {
		return Iterables.filter(Iterables.transform(this, GLLayoutDatas.toLayoutData(TablePerspective.class, null)),
				Predicates.notNull());
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.pushResourceLocator(Activator.getResourceLocator());
		super.renderImpl(g, w, h);
		g.popResourceLocator();
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		g.pushResourceLocator(Activator.getResourceLocator());
		super.renderPickImpl(g, w, h);
		g.popResourceLocator();
	}

	@Override
	protected void takeDown() {
		columns.clear();
		super.takeDown();
	}

	/**
	 * @return the columns, see {@link #columns}
	 */
	public List<AEntityColumn> getColumns() {
		return columns;
	}

	public List<AEntityColumn> getColumnsWithBroadcastIDType(IDType idType) {
		List<AEntityColumn> list = new ArrayList<>();
		for (AEntityColumn column : columns) {
			if (column.getBroadcastingIDType() == idType) {
				list.add(column);
			}
		}
		return list;
	}

	public List<AEntityColumn> getColumnsWithMappingIDType(IDType idType) {
		List<AEntityColumn> list = new ArrayList<>();
		for (AEntityColumn column : columns) {
			if (column.getMappingIDType() == idType) {
				list.add(column);
			}
		}
		return list;
	}

	public void applyIDMappingUpdate(AMappingUpdateOperation operation) {
		idMappingUpdateHandler.handleIDMappingUpdate(operation);
	}

	public void executeMappingUpdateOperation(AMappingUpdateOperation operation) {
		for (AEntityColumn column : columns) {
			if (operation.getSrcColumn() != column)
				operation.execute(column);
		}
	}

	public void updateSelectionMappings(AEntityColumn srcColumn) {
		for (AEntityColumn column : columns) {
			column.updateSelectionMappings(srcColumn);
		}
	}

	/**
	 * @return the history, see {@link #history}
	 */
	public History getHistory() {
		return history;
	}

	public void setIdMappingUpdateHandler(IIDMappingUpdateHandler handler) {
		this.idMappingUpdateHandler = handler;
	}

	/**
	 * @return the idMappingUpdateHandler, see {@link #idMappingUpdateHandler}
	 */
	public IIDMappingUpdateHandler getIdMappingUpdateHandler() {
		return idMappingUpdateHandler;
	}

}
