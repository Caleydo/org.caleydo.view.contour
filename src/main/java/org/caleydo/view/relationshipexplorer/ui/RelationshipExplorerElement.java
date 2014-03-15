/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
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
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.ScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator;
import org.caleydo.core.view.opengl.layout2.dnd.EDnDType;
import org.caleydo.core.view.opengl.layout2.dnd.IDnDItem;
import org.caleydo.core.view.opengl.layout2.dnd.IDragInfo;
import org.caleydo.core.view.opengl.layout2.dnd.IDropGLTarget;
import org.caleydo.core.view.opengl.layout2.layout.GLLayoutDatas;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.core.view.opengl.layout2.util.GLElementWindow;
import org.caleydo.core.view.opengl.layout2.util.GLElementWindow.ICloseWindowListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.relationshipexplorer.internal.Activator;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryIDOwner;
import org.caleydo.view.relationshipexplorer.ui.collection.EnrichmentScores;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.operation.AMappingUpdateOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.AddColumnTreeCommand;
import org.caleydo.view.relationshipexplorer.ui.column.operation.HideDetailCommand;
import org.caleydo.view.relationshipexplorer.ui.column.operation.RemoveColumnCommand;
import org.caleydo.view.relationshipexplorer.ui.filter.FilterPipeline;
import org.caleydo.view.relationshipexplorer.ui.list.ColumnTree;
import org.caleydo.view.relationshipexplorer.ui.list.DragAndDropHeader.ColumnDragInfo;
import org.caleydo.view.relationshipexplorer.ui.list.EUpdateCause;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;

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
	protected final static int MIN_FILTER_PIPELINE_HEIGHT = 16;
	protected final static int MIN_COLUMN_HEIGHT = 200;

	// ------------
	protected List<ColumnTree> cols = new ArrayList<>();

	// ------------
	protected EnrichmentScores enrichmentScores = new EnrichmentScores(this);

	// protected List<AEntityColumn> columns = new ArrayList<>();
	protected AnimatedGLElementContainer columnContainer;
	protected AnimatedGLElementContainer detailContainer;
	protected History history;
	protected FilterPipeline filterPipeline;
	protected BiMap<IEntityCollection, GLElementWindow> detailMap = HashBiMap.create(2);
	protected Queue<GLElementWindow> detailWindowQueue = new LinkedList<>();

	protected Set<IEntityCollection> entityCollections = new LinkedHashSet<>();

	public interface IIDMappingUpdateHandler {
		public void handleIDMappingUpdate(AMappingUpdateOperation operation);
	}

	// public interface ISelectionMappingUpdateListener {
	// public void updateSelectionMappings(AEntityColumn srcColumn);
	// }

	protected IIDMappingUpdateHandler idMappingUpdateHandler = new IIDMappingUpdateHandler() {

		@Override
		public void handleIDMappingUpdate(AMappingUpdateOperation operation) {
			executeMappingUpdateOperation(operation);
			updateMappings(operation);
		}

	};

	protected class ReorderColumnTreesCommand implements IHistoryCommand {

		protected final int columnModelHistoryID;
		protected final int separatorHistoryID;

		public ReorderColumnTreesCommand(IColumnModel model, ColumnSeparator separator) {
			this.columnModelHistoryID = model.getHistoryID();
			this.separatorHistoryID = separator.getHistoryID();
		}

		@Override
		public Object execute() {
			final List<ColumnTree> l = new ArrayList<>();
			List<ColumnSeparator> separatorsToRemove = new ArrayList<>();
			IColumnModel model = history.getHistoryObjectAs(IColumnModel.class, columnModelHistoryID);
			ColumnSeparator separator = history.getHistoryObjectAs(ColumnSeparator.class, separatorHistoryID);

			ColumnTree columnTree = model.getColumn().getColumnTree();
			for (GLElement e : columnContainer) {
				if (e instanceof ColumnTree && e != columnTree) {
					l.add((ColumnTree) e);
				} else {
					if (e == separator) {
						l.add(columnTree);
					}
				}
				if (e instanceof ColumnSeparator) {
					separatorsToRemove.add((ColumnSeparator) e);
				}
			}

			for (ColumnSeparator cs : separatorsToRemove) {
				columnContainer.remove(cs, 0);
			}

			columnContainer.sortBy(new Comparator<GLElement>() {
				@Override
				public int compare(GLElement o1, GLElement o2) {
					return l.indexOf(o1) - l.indexOf(o2);
				}
			});

			for (int j = 0; j < l.size(); j++) {
				ColumnTree ct = l.get(j);

				columnContainer.add(columnContainer.indexOf(ct), new ColumnSeparator(), 0);

			}
			columnContainer.add(new ColumnSeparator(), 0);

			return null;
		}

		@Override
		public String getDescription() {
			IColumnModel model = history.getHistoryObjectAs(IColumnModel.class, columnModelHistoryID);
			return "Moved column " + model.getLabel();
		}
	}

	protected class ColumnSeparator extends PickableGLElement implements IDropGLTarget, IHistoryIDOwner {

		protected boolean isDraggedOver = false;
		protected boolean isOver = false;
		protected int historyID;

		public ColumnSeparator() {
			setSize(12, Float.NaN);
			historyID = history.registerHistoryObject(this);
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {
			g.color(isDraggedOver ? Color.DARK_GRAY : Color.LIGHT_GRAY);
			g.lineWidth(isDraggedOver ? 4 : 2);
			g.drawLine(w / 2.0f, 0, w / 2.0f, h);
			g.lineWidth(1);
		}

		@Override
		protected void onMouseOver(Pick pick) {
			context.getMouseLayer().addDropTarget(this);
			isOver = true;
		}

		@Override
		protected void onMouseOut(Pick pick) {
			context.getMouseLayer().removeDropTarget(this);
			if (isDraggedOver) {
				isDraggedOver = false;
				isOver = false;
				repaint();
			}
		}

		@Override
		public boolean canSWTDrop(IDnDItem item) {
			return item.getInfo() instanceof ColumnDragInfo;
		}

		@Override
		public void onDrop(IDnDItem item) {
			IDragInfo i = item.getInfo();
			if (!(i instanceof ColumnDragInfo))
				return;
			ColumnDragInfo info = (ColumnDragInfo) i;

			if (item.getType() == EDnDType.COPY) {

				AddColumnTreeCommand c = new AddColumnTreeCommand(info.getModel().getCollection(),
						RelationshipExplorerElement.this);
				c.setIndex(columnContainer.indexOf(this));
				c.execute();
				history.addHistoryCommand(c, Color.DARK_BLUE);

			} else {
				if (info.getModel().getColumn().isRoot()) {
					ReorderColumnTreesCommand c = new ReorderColumnTreesCommand(info.getModel(), this);
					c.execute();
					history.addHistoryCommand(c, Color.DARK_BLUE);
				} else {
					AddColumnTreeCommand c = new AddColumnTreeCommand(info.getModel().getCollection(),
							RelationshipExplorerElement.this);
					c.setIndex(columnContainer.indexOf(this));
					c.execute();
					history.addHistoryCommand(c, Color.DARK_BLUE);

					RemoveColumnCommand rc = new RemoveColumnCommand(info.getModel(), RelationshipExplorerElement.this);
					rc.execute();
					history.addHistoryCommand(rc, Color.DARK_BLUE);
				}
			}
		}

		@Override
		public void onItemChanged(IDnDItem item) {
			if (!isDraggedOver && isOver) {
				isDraggedOver = true;
				repaint();
			}

		}

		@Override
		public EDnDType defaultSWTDnDType(IDnDItem item) {
			return EDnDType.MOVE;
		}

		@Override
		protected void takeDown() {
			context.getMouseLayer().removeDropTarget(this);
			super.takeDown();
		}

		@Override
		public int getHistoryID() {
			return historyID;
		}

	}

	// private class DetailConnector extends GLElement {
	//
	// @Override
	// protected void renderImpl(GLGraphics g, float w, float h) {
	// for (GLElement detailWindow : detailContainer) {
	// AEntityColumn column = detailMap.inverse().get(detailWindow);
	// Vec2f winLoc1 = new Vec2f(detailWindow.getLocation().x(), 0);
	// Vec2f winLoc2 = new Vec2f(winLoc1.x() + detailWindow.getSize().x(), 0);
	// Vec2f colLoc1 = new Vec2f(column.getLocation().x(), h);
	// Vec2f colLoc2 = new Vec2f(colLoc1.x() + column.getSize().x(), h);
	// g.color(Color.RED).drawPath(true, winLoc1, winLoc2, colLoc2, colLoc1);
	// }
	// }
	// }

	public RelationshipExplorerElement() {
		super(new GLSizeRestrictiveFlowLayout(false, 5, GLPadding.ZERO));
		columnContainer = new AnimatedGLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 0, GLPadding.ZERO));
		detailContainer = new AnimatedGLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 5, GLPadding.ZERO));
		detailContainer.setSize(Float.NaN, 0);
		add(detailContainer);
		// columnContainer.setLayoutData(0.9f);
		add(columnContainer);
		history = new History(this);

		columnContainer.add(new ColumnSeparator(), 0);
		columnContainer.add(new ColumnSeparator(), 0);

		filterPipeline = new FilterPipeline(this);
		ScrollingDecorator scrollingDecorator = new ScrollingDecorator(filterPipeline, new ScrollBar(true),
				new ScrollBar(false), 8, EDimension.RECORD);
		scrollingDecorator.setMinSizeProvider(filterPipeline);
		scrollingDecorator.setSize(Float.NaN, MIN_FILTER_PIPELINE_HEIGHT);
		add(scrollingDecorator);


		scrollingDecorator = new ScrollingDecorator(history, new ScrollBar(true), new ScrollBar(false), 8,
				EDimension.RECORD);
		scrollingDecorator.setMinSizeProvider(history);
		scrollingDecorator.setSize(Float.NaN, MIN_HISTORY_HEIGHT);
		add(scrollingDecorator);

	}

	/**
	 * @return the filterPipeline, see {@link #filterPipeline}
	 */
	public FilterPipeline getFilterPipeline() {
		return filterPipeline;
	}

	@Override
	public void relayout() {
		super.relayout();
		columnContainer.relayout();
	}

	// public void addEntityColumn(AEntityColumn column) {
	// if (columns.size() > 0) {
	// GLElement columnSpacer = new GLElement(GLRenderers.fillRect(Color.LIGHT_GRAY));
	// columnSpacer.setSize(2, Float.NaN);
	// columnContainer.add(columnSpacer);
	// }
	// // columnContainer.add(column);
	// columns.add(column);
	// registerEntityCollection(column);
	// }

	public void addColumn(ColumnTree column) {
		if (cols.size() > 0) {
			ColumnSeparator columnSeparator = new ColumnSeparator();
			columnContainer.add(columnContainer.size() - 1, columnSeparator);
		}
		columnContainer.add(columnContainer.size() - 1, column);
		cols.add(column);
		// registerEntityCollection(column);
	}

	public void addColumn(int index, ColumnTree column) {
		if (cols.isEmpty())
			addColumn(column);
		if (index > columnContainer.size())
			index = columnContainer.size() - 1;
		if (index < 0)
			index = 0;

		GLElement element = columnContainer.get(index);
		if (element instanceof ColumnSeparator) {

			columnContainer.add(index, column);
			cols.add(column);
			ColumnSeparator columnSeparator = new ColumnSeparator();
			columnContainer.add(index, columnSeparator);
		} else {

			ColumnSeparator columnSeparator = new ColumnSeparator();
			columnContainer.add(index, columnSeparator);
			columnContainer.add(index, column);
			cols.add(column);
		}
	}

	public void removeColumn(ColumnTree column) {
		int index = columnContainer.indexOf(column);
		if (index == -1)
			return;

		if (index == 0) {
			columnContainer.remove(0);
			if (columnContainer.size() > 0)
				columnContainer.remove(0);
		} else {
			columnContainer.remove(index - 1);
			columnContainer.remove(column);
		}

		cols.remove(column);
	}

	public void clearColumns() {
		columnContainer.clear();
		columnContainer.add(new ColumnSeparator());
		columnContainer.add(new ColumnSeparator());
		cols.clear();
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
		// columns.clear();
		cols.clear();
		entityCollections.clear();
		detailMap.clear();
		detailWindowQueue.clear();
		super.takeDown();
	}

	// /**
	// * @return the columns, see {@link #columns}
	// */
	// public List<AEntityColumn> getColumns() {
	// return columns;
	// }
	//

	public Set<IEntityCollection> getCollectionsWithMappingIDType(IDType idType) {
		Set<IEntityCollection> collections = new HashSet<>();
		for (IEntityCollection collection : entityCollections) {
			if (collection.getMappingIDType() == idType) {
				collections.add(collection);
			}
		}
		return collections;
	}

	public Set<IEntityCollection> getCollectionsWithBroadcastIDType(IDType idType) {
		Set<IEntityCollection> collections = new HashSet<>();
		for (IEntityCollection collection : entityCollections) {
			if (collection.getBroadcastingIDType() == idType) {
				collections.add(collection);
			}
		}
		return collections;
	}

	public void applyIDMappingUpdate(AMappingUpdateOperation operation) {
		idMappingUpdateHandler.handleIDMappingUpdate(operation);
	}

	public void executeMappingUpdateOperation(AMappingUpdateOperation operation) {
		for (IEntityCollection collection : entityCollections) {
			// if (operation.getSrcRepresentation().getCollection() != collection)
			operation.execute(collection);
		}
	}

	public void updateMappings(AMappingUpdateOperation operation) {
		if (operation.getUpdateCause() == EUpdateCause.FILTER) {
			enrichmentScores.updateScores();
		}
		for (IEntityCollection collection : entityCollections) {
			// if (operation.getSrcRepresentation().getCollection() != collection)
			operation.triggerUpdate(collection);
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

	public void showDetailView(IEntityCollection srcCollection, final GLElement detailView, ILabeled labeled) {

		GLElementWindow window = detailMap.get(srcCollection);
		if (window == null) {
			window = new GLElementWindow(labeled);
			window.onClose(new ICloseWindowListener() {

				@Override
				public void onWindowClosed(GLElementWindow window) {
					IEntityCollection collection = detailMap.inverse().get(window);
					HideDetailCommand o = new HideDetailCommand(collection, RelationshipExplorerElement.this);

					o.execute();
					getHistory().addHistoryCommand(o, ColorBrewer.Greens.getColors(3).get(2));
				}
			});

			detailMap.put(srcCollection, window);
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
				int index1 = getIndexOfFirstColumn(detailMap.inverse().get(arg0));
				int index2 = getIndexOfFirstColumn(detailMap.inverse().get(arg1));
				return index1 - index2;
			}

			private int getIndexOfFirstColumn(IEntityCollection collection) {
				// TODO: Probably save the column where the detail view was triggered
				for (int i = 0; i < cols.size(); i++) {
					GLElement element = cols.get(i);
					if (element instanceof ColumnTree) {
						if (((ColumnTree) element).getRootColumn().getColumnModel().getCollection() == collection)
							return i;
					}
				}
				return -1;
			}
		});

		updateDetailHeight();
	}

	public void reset() {
		removeAllDetailViews();
		clearColumns();
		getFilterPipeline().clearFilterCommands();
		for (IEntityCollection collection : getEntityCollections()) {
			collection.reset();
		}
	}

	public void removeAllDetailViews() {
		for (GLElementWindow window : detailMap.values()) {
			detailContainer.remove(window);
			detailWindowQueue.remove(window);
		}
		detailMap.clear();
		updateDetailHeight();
	}

	public void removeDetailViewOfColumn(IEntityCollection column) {
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

	/**
	 * @return the entityCollections, see {@link #entityCollections}
	 */
	public Set<IEntityCollection> getEntityCollections() {
		return entityCollections;
	}

	public void restoreAllEntities() {
		for (IEntityCollection collection : entityCollections) {
			collection.restoreAllEntities();
		}
	}

	/**
	 * @return the enrichmentScores, see {@link #enrichmentScores}
	 */
	public EnrichmentScores getEnrichmentScores() {
		return enrichmentScores;
	}

}
