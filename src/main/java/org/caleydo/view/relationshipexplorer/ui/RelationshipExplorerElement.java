/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.AGLElementView;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.ScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator;
import org.caleydo.core.view.opengl.layout2.layout.GLLayoutDatas;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.util.GLElementWindow;
import org.caleydo.core.view.opengl.layout2.util.GLElementWindow.ICloseWindowListener;
import org.caleydo.view.relationshipexplorer.internal.Activator;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 *
 *
 * @author Christian
 *
 */
public class RelationshipExplorerElement extends AnimatedGLElementContainer {

	protected final static int MIN_HISTORY_HEIGHT = 30;
	protected final static int MIN_COLUMN_HEIGHT = 150;

	protected List<AEntityColumn> columns = new ArrayList<>();
	protected AnimatedGLElementContainer columnContainer;
	protected AnimatedGLElementContainer detailContainer;
	protected History history;
	protected Map<AEntityColumn, GLElementWindow> detailMap = new HashMap<>();

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
		detailContainer = new AnimatedGLElementContainer(new GLSizeRestrictiveFlowLayout(true, 5, GLPadding.ZERO));
		detailContainer.setSize(Float.NaN, 0);
		add(detailContainer);
		// columnContainer.setLayoutData(0.9f);
		add(columnContainer);
		history = new History(this);
		ScrollingDecorator scrollingDecorator = new ScrollingDecorator(history, new ScrollBar(true), new ScrollBar(
				false), 8, EDimension.RECORD);
		scrollingDecorator.setMinSizeProvider(history);
		scrollingDecorator.setSize(Float.NaN, MIN_HISTORY_HEIGHT);
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

	public void showDetailView(AEntityColumn srcColumn, final GLElement detailView, ILabeled labeled) {

		GLElementWindow window = detailMap.get(srcColumn);
		if (window == null) {
			window = new GLElementWindow(labeled);
			window.onClose(new ICloseWindowListener() {

				@Override
				public void onWindowClosed(GLElementWindow window) {
					detailContainer.remove(window);
					if (detailContainer.size() <= 0) {
						resizeChild(detailContainer, Float.NaN, 0);
					}
				}
			});

			detailMap.put(srcColumn, window);
		}

		if (detailContainer.indexOf(window) == -1)
			detailContainer.add(window);

		window.setContent(detailView);
		window.getTitleBar().setLabelProvider(labeled);
		window.setShowCloseButton(true);

		Vec2f detailContainerMinSize = GLMinSizeProviders.getHorizontalFlowMinSize(detailContainer, 5, GLPadding.ZERO);
		IGLCanvas canvas = findParent(AGLElementView.class).getParentGLCanvas();
		if (canvas == null)
			return;

		while (detailContainerMinSize.x() > canvas.getDIPWidth() && detailContainer.size() != 1) {
			detailContainer.remove(0);
			detailContainerMinSize = GLMinSizeProviders.getHorizontalFlowMinSize(detailContainer, 5, GLPadding.ZERO);
		}
		float detailHeight = Math.max(0,
				Math.min(canvas.getDIPHeight() - (MIN_COLUMN_HEIGHT + MIN_HISTORY_HEIGHT), window.getMinSize().y()));

		resizeChild(detailContainer, Float.NaN, detailHeight);
	}
}
