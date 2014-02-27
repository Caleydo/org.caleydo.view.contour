/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorBrewer;
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
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.util.GLElementWindow;
import org.caleydo.core.view.opengl.layout2.util.GLElementWindow.ICloseWindowListener;
import org.caleydo.view.relationshipexplorer.internal.Activator;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.operation.AMappingUpdateOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.HideDetailOperation;
import org.caleydo.view.relationshipexplorer.ui.list.ColumnTree;

import com.google.common.base.Predicates;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Iterables;

/**
 *
 *
 * @author Christian
 *
 */
public class RelationshipExplorerElement extends AnimatedGLElementContainer {

	protected final static int MIN_HISTORY_HEIGHT = 30;
	protected final static int MIN_COLUMN_HEIGHT = 200;

	// ------------
	protected List<ColumnTree> cols = new ArrayList<>();

	// ------------

	protected List<AEntityColumn> columns = new ArrayList<>();
	protected AnimatedGLElementContainer columnContainer;
	protected AnimatedGLElementContainer detailContainer;
	protected History history;
	protected BiMap<AEntityColumn, GLElementWindow> detailMap = HashBiMap.create(2);
	protected Queue<GLElementWindow> detailWindowQueue = new LinkedList<>();

	protected Set<IEntityCollection> entityCollections = new LinkedHashSet<>();

	public interface IIDMappingUpdateHandler {
		public void handleIDMappingUpdate(AMappingUpdateOperation operation, boolean updateSelectionMappings);
	}

	// public interface ISelectionMappingUpdateListener {
	// public void updateSelectionMappings(AEntityColumn srcColumn);
	// }

	protected IIDMappingUpdateHandler idMappingUpdateHandler = new IIDMappingUpdateHandler() {

		@Override
		public void handleIDMappingUpdate(AMappingUpdateOperation operation, boolean updateSelectionMappings) {
			executeMappingUpdateOperation(operation);
			if (updateSelectionMappings)
				updateSelectionMappings(operation.getSrcCollection());
		}

	};

	private class DetailConnector extends GLElement {

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			for (GLElement detailWindow : detailContainer) {
				AEntityColumn column = detailMap.inverse().get(detailWindow);
				Vec2f winLoc1 = new Vec2f(detailWindow.getLocation().x(), 0);
				Vec2f winLoc2 = new Vec2f(winLoc1.x() + detailWindow.getSize().x(), 0);
				Vec2f colLoc1 = new Vec2f(column.getLocation().x(), h);
				Vec2f colLoc2 = new Vec2f(colLoc1.x() + column.getSize().x(), h);
				g.color(Color.RED).drawPath(true, winLoc1, winLoc2, colLoc2, colLoc1);
			}
		}
	}

	public RelationshipExplorerElement() {
		super(new GLSizeRestrictiveFlowLayout(false, 5, GLPadding.ZERO));
		columnContainer = new AnimatedGLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 5, GLPadding.ZERO));
		detailContainer = new AnimatedGLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 5, GLPadding.ZERO));
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
		if (columns.size() > 0) {
			GLElement columnSpacer = new GLElement(GLRenderers.fillRect(Color.LIGHT_GRAY));
			columnSpacer.setSize(2, Float.NaN);
			columnContainer.add(columnSpacer);
		}
		columnContainer.add(column);
		columns.add(column);
		registerEntityCollection(column);
	}

	public void addColumn(ColumnTree column) {
		if (cols.size() > 0) {
			GLElement columnSpacer = new GLElement(GLRenderers.fillRect(Color.LIGHT_GRAY));
			columnSpacer.setSize(2, Float.NaN);
			columnContainer.add(columnSpacer);
		}
		columnContainer.add(column);
		cols.add(column);
		// registerEntityCollection(column);
	}

	public void registerEntityCollection(IEntityCollection collection) {
		entityCollections.add(collection);
	}

	public void unregisterEntityCollection(IEntityCollection collection) {
		entityCollections.remove(collection);
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
		entityCollections.clear();
		detailMap.clear();
		detailWindowQueue.clear();
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

	public void applyIDMappingUpdate(AMappingUpdateOperation operation, boolean updateSelectionMappings) {
		idMappingUpdateHandler.handleIDMappingUpdate(operation, updateSelectionMappings);
	}

	public void executeMappingUpdateOperation(AMappingUpdateOperation operation) {
		for (IEntityCollection collection : entityCollections) {
			if (operation.getSrcCollection() != collection)
				operation.execute(collection);
		}
	}

	public void updateSelectionMappings(IEntityCollection srcCollection) {
		for (IEntityCollection collection : entityCollections) {
			collection.updateSelectionMappings(srcCollection);
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
					AEntityColumn column = detailMap.inverse().get(window);
					HideDetailOperation o = new HideDetailOperation(column);

					o.execute();
					getHistory().addHistoryCommand(o, ColorBrewer.Greens.getColors(3).get(2));
				}
			});

			detailMap.put(srcColumn, window);
		}

		if (detailContainer.indexOf(window) == -1) {
			detailContainer.add(window);
			detailWindowQueue.add(window);
		}

		window.setContent(detailView);
		window.getTitleBar().setLabelProvider(labeled);
		window.setShowCloseButton(true);

		Vec2f detailContainerMinSize = GLMinSizeProviders.getHorizontalFlowMinSize(detailContainer, 5, GLPadding.ZERO);
		IGLCanvas canvas = findParent(AGLElementView.class).getParentGLCanvas();
		if (canvas == null)
			return;

		while (detailContainerMinSize.x() > canvas.getDIPWidth() && detailContainer.size() != 1) {
			GLElementWindow w = detailWindowQueue.poll();
			detailContainer.remove(w);
			detailMap.inverse().remove(w);
			detailContainerMinSize = GLMinSizeProviders.getHorizontalFlowMinSize(detailContainer, 5, GLPadding.ZERO);
		}
		detailContainer.sortBy(new Comparator<GLElement>() {

			@Override
			public int compare(GLElement arg0, GLElement arg1) {
				int index1 = columnContainer.indexOf(detailMap.inverse().get(arg0));
				int index2 = columnContainer.indexOf(detailMap.inverse().get(arg1));
				return index1 - index2;
			}
		});

		updateDetailHeight();
	}

	public void removeAllDetailViews() {
		for (GLElementWindow window : detailMap.values()) {
			detailContainer.remove(window);
			detailWindowQueue.remove(window);
		}
		detailMap.clear();
		updateDetailHeight();
	}

	public void removeDetailViewOfColumn(AEntityColumn column) {
		GLElementWindow window = detailMap.remove(column);
		if (window != null) {
			detailContainer.remove(window);
			detailWindowQueue.remove(window);
			updateDetailHeight();
		}
	}

	protected void updateDetailHeight() {
		IGLCanvas canvas = findParent(AGLElementView.class).getParentGLCanvas();
		Vec2f detailContainerMinSize = GLMinSizeProviders.getHorizontalFlowMinSize(detailContainer, 5, GLPadding.ZERO);
		float detailHeight = Math.max(0,
				Math.min(canvas.getDIPHeight() - (MIN_COLUMN_HEIGHT + MIN_HISTORY_HEIGHT), detailContainerMinSize.y()));

		resizeChild(detailContainer, Float.NaN, detailHeight);
	}
}
