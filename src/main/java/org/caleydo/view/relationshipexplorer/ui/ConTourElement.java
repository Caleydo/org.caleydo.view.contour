/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.Set;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.event.ADirectedEvent;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.dnd.EDnDType;
import org.caleydo.core.view.opengl.layout2.dnd.IDnDItem;
import org.caleydo.core.view.opengl.layout2.dnd.IDragInfo;
import org.caleydo.core.view.opengl.layout2.dnd.IDropGLTarget;
import org.caleydo.core.view.opengl.layout2.layout.GLLayoutDatas;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.util.GLElementWindow;
import org.caleydo.core.view.opengl.layout2.util.GLElementWindow.ICloseWindowListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.relationshipexplorer.internal.Activator;
import org.caleydo.view.relationshipexplorer.internal.toolbar.AddColumnsEvent;
import org.caleydo.view.relationshipexplorer.internal.toolbar.SelectionOperationEvent;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryIDOwner;
import org.caleydo.view.relationshipexplorer.ui.collection.EnrichmentScores;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.ResetAttributeFilterEvent;
import org.caleydo.view.relationshipexplorer.ui.column.operation.AMappingUpdateOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.ESetOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.IMappingUpdateListener;
import org.caleydo.view.relationshipexplorer.ui.command.AddColumnTreeCommand;
import org.caleydo.view.relationshipexplorer.ui.command.CompositeHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.command.HideDetailCommand;
import org.caleydo.view.relationshipexplorer.ui.command.RemoveColumnCommand;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.ThreadSyncEvent;
import org.caleydo.view.relationshipexplorer.ui.detail.DetailViewWindow;
import org.caleydo.view.relationshipexplorer.ui.dialog.AddColumnDialog;
import org.caleydo.view.relationshipexplorer.ui.filter.FilterPipeline;
import org.caleydo.view.relationshipexplorer.ui.list.ColumnTree;
import org.caleydo.view.relationshipexplorer.ui.list.DragAndDropHeader.ColumnDragInfo;
import org.caleydo.view.relationshipexplorer.ui.list.EUpdateCause;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

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
public class ConTourElement extends AnimatedGLElementContainer {

	protected static final URL UP_ARROW_ICON = ConTourElement.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/bullet_arrow_top_small.png");
	protected static final URL DOWN_ARROW_ICON = ConTourElement.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/bullet_arrow_bottom_small.png");
	protected static final URL ADD_COLUMN_ICON = ConTourElement.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/add.png");

	protected final static int MIN_HISTORY_HEIGHT = 30;
	protected final static int MIN_FILTER_PIPELINE_HEIGHT = 16;
	protected final static int MIN_COLUMN_HEIGHT = 200;

	// ------------
	protected List<ColumnTree> cols = new ArrayList<>();

	// ------------
	protected EnrichmentScores enrichmentScores = new EnrichmentScores(this);

	protected enum EViewSplit {
		TOP, MIDDLE, BOTTOM;

		public EViewSplit next() {
			switch (this) {
			case TOP:
				return TOP;

			case MIDDLE:
				return TOP;

			case BOTTOM:
				return MIDDLE;
			default:
				return TOP;

			}
		}

		public EViewSplit prev() {
			switch (this) {
			case TOP:
				return MIDDLE;

			case MIDDLE:
				return BOTTOM;

			case BOTTOM:
				return BOTTOM;
			default:
				return BOTTOM;

			}
		}
	}

	// protected List<AEntityColumn> columns = new ArrayList<>();
	protected AnimatedGLElementContainer columnContainer;
	protected AnimatedGLElementContainer columnContainerRow;
	protected AnimatedGLElementContainer detailContainer;
	protected History history;
	protected Snapshots snapshots;
	protected FilterPipeline filterPipeline;
	protected BiMap<IEntityCollection, DetailViewWindow> detailMap = HashBiMap.create(2);
	protected Queue<GLElementWindow> detailWindowQueue = new LinkedList<>();
	protected ESetOperation multiItemSelectionSetOperation = ESetOperation.UNION;

	protected Set<IEntityCollection> entityCollections = new LinkedHashSet<>();
	protected Set<IMappingUpdateListener> mappingUpdateListeners = new HashSet<IMappingUpdateListener>();

	protected EViewSplit split = EViewSplit.MIDDLE;
	protected GLButton moveUpButton;
	protected GLButton moveDownButton;

	protected ContextMenuCreator contextMenuCreator = new ContextMenuCreator();

	private class ShowContextMenuEvent extends ADirectedEvent {
	}

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

				AddColumnTreeCommand c = new AddColumnTreeCommand(info.getModel().getCollection(), ConTourElement.this);
				c.setIndex(columnContainer.indexOf(this));
				c.execute();
				history.addHistoryCommand(c);

			} else {
				if (info.getModel().getColumn().isRoot()) {
					ReorderColumnTreesCommand c = new ReorderColumnTreesCommand(info.getModel(), this);
					c.execute();
					history.addHistoryCommand(c);
				} else {

					CompositeHistoryCommand comp = new CompositeHistoryCommand();
					comp.setDescription("Moved Column " + info.getModel().getLabel());

					AddColumnTreeCommand c = new AddColumnTreeCommand(info.getModel().getCollection(),
							ConTourElement.this);
					c.setIndex(columnContainer.indexOf(this));
					comp.add(c);
					// c.execute();
					// history.addHistoryCommand(c);

					RemoveColumnCommand rc = new RemoveColumnCommand(info.getModel(), ConTourElement.this);
					comp.add(rc);
					comp.execute();
					// rc.execute();
					history.addHistoryCommand(comp);
				}
			}
		}

		@Override
		public void onDropLeave() {

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

		@Override
		public String getLabel() {
			return "Column Separator";
		}

	}

	public ConTourElement() {
		super(new GLSizeRestrictiveFlowLayout(true, 5, GLPadding.ZERO));

		AnimatedGLElementContainer supportViewContainer = new AnimatedGLElementContainer(
				(new GLSizeRestrictiveFlowLayout2(false, 2, new GLPadding(0, 2, 2, 2))));
		supportViewContainer.setSize(100, Float.NaN);

		history = new History(this);
		// ScrollingDecorator scrollingDecorator = new ScrollingDecorator(history, new ScrollBar(true), new ScrollBar(
		// false), 8, EDimension.RECORD);
		// scrollingDecorator.setMinSizeProvider(history);
		// scrollingDecorator.setSize(80, Float.NaN);
		history.setLayoutData(0.4f);
		supportViewContainer.add(history);

		supportViewContainer.add(createVSeparator());

		snapshots = new Snapshots(this);
		// scrollingDecorator = new ScrollingDecorator(filterPipeline, new ScrollBar(true),
		// new ScrollBar(false), 8, EDimension.RECORD);
		// filterPipeline.setMinSizeProvider(filterPipeline);
		// scrollingDecorator.setSize(80, Float.NaN);
		snapshots.setLayoutData(0.2f);
		supportViewContainer.add(snapshots);

		supportViewContainer.add(createVSeparator());

		filterPipeline = new FilterPipeline(this);
		// scrollingDecorator = new ScrollingDecorator(filterPipeline, new ScrollBar(true),
		// new ScrollBar(false), 8, EDimension.RECORD);
		// filterPipeline.setMinSizeProvider(filterPipeline);
		// scrollingDecorator.setSize(80, Float.NaN);
		filterPipeline.setLayoutData(0.4f);
		supportViewContainer.add(filterPipeline);

		AnimatedGLElementContainer container = new AnimatedGLElementContainer((new GLSizeRestrictiveFlowLayout2(false,
				2, GLPadding.ZERO)));

		container.setLayoutData(1f);
		add(container);
		columnContainerRow = new AnimatedGLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 2, new GLPadding(0,
				0, 2, 0)));
		columnContainer = new AnimatedGLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 0, GLPadding.ZERO));
		detailContainer = new AnimatedGLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 5, GLPadding.ZERO));
		detailContainer.setSize(Float.NaN, 0);
		container.add(detailContainer);
		container.add(createMoveUpButton(), 0);
		container.add(createMoveDownButton(), 0);
		container.add(columnContainerRow);

		columnContainerRow.add(columnContainer);
		// columnContainerRow.add(createAddColumnButton());
		// add(detailContainer);
		// columnContainer.setLayoutData(0.9f);
		// add(columnContainer);

		columnContainer.add(new ColumnSeparator(), 0);
		columnContainer.add(new ColumnSeparator(), 0);

		add(supportViewContainer);
	}

	protected GLElement createVSeparator() {
		GLElement separator = new GLElement(GLRenderers.fillRect(Color.LIGHT_GRAY));
		separator.setSize(Float.NaN, 2);
		return separator;
	}

	protected GLButton createMoveUpButton() {
		moveUpButton = new GLButton();
		moveUpButton.setSize(16, 7);
		moveUpButton.setRenderer(GLRenderers.fillImage(UP_ARROW_ICON));
		moveUpButton.setVisibility(EVisibility.NONE);
		moveUpButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				if (split == EViewSplit.TOP)
					return;
				split = split.next();
				updateDetailHeight();

			}
		});
		return moveUpButton;
	}

	protected GLButton createMoveDownButton() {
		moveDownButton = new GLButton();
		moveDownButton.setSize(16, 7);
		moveDownButton.setRenderer(GLRenderers.fillImage(DOWN_ARROW_ICON));
		moveDownButton.setVisibility(EVisibility.NONE);
		moveDownButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				if (split == EViewSplit.BOTTOM)
					return;
				split = split.prev();
				updateDetailHeight();

			}
		});
		return moveDownButton;
	}

	protected GLButton createAddColumnButton() {
		GLButton button = new GLButton();
		button.setSize(32, 32);
		button.setRenderer(GLRenderers.fillImage(ADD_COLUMN_ICON));
		button.setVisibility(EVisibility.PICKABLE);
		button.setTooltip("Add Columns");
		button.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				context.getSWTLayer().run(new ISWTLayerRunnable() {
					@Override
					public void run(Display display, Composite canvas) {
						AddColumnDialog dialog = new AddColumnDialog(canvas.getShell(), ConTourElement.this);
						if (dialog.open() == Window.OK) {
							Set<IEntityCollection> collections = dialog.getCollections();
							for (IEntityCollection collection : collections) {
								AddColumnTreeCommand c = new AddColumnTreeCommand(collection, ConTourElement.this);
								c.execute();
								history.addHistoryCommand(c);
							}
						}
					}
				});

			}
		});
		return button;
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

	public void addColumn(ColumnTree column) {
		if (cols.size() > 0) {
			ColumnSeparator columnSeparator = new ColumnSeparator();
			columnContainer.add(columnContainer.size() - 1, columnSeparator, 0);
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
		addMappingUpdateListener(collection);
	}

	public void unregisterEntityCollection(IEntityCollection collection) {
		entityCollections.remove(collection);
		removeMappingUpdateListener(collection);
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
		mappingUpdateListeners.clear();
		super.takeDown();
	}

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

		for (IMappingUpdateListener listener : mappingUpdateListeners) {
			operation.notify(listener);
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

	public boolean isDetailViewShown(IEntityCollection collection) {
		return detailMap.get(collection) != null;
	}

	public void showDetailView(IEntityCollection srcCollection) {

		DetailViewWindow window = detailMap.get(srcCollection);

		if (window == null) {
			window = srcCollection.createDetailViewWindow();
			window.onClose(new ICloseWindowListener() {

				@Override
				public void onWindowClosed(GLElementWindow window) {
					IEntityCollection collection = detailMap.inverse().get(window);
					HideDetailCommand o = new HideDetailCommand(collection, ConTourElement.this);

					o.execute();
					getHistory().addHistoryCommand(o);
				}
			});
			detailMap.put(srcCollection, window);
		}

		if (detailContainer.indexOf(window) == -1) {
			detailContainer.add(window);
			detailWindowQueue.add(window);
		}

		window.setContent(srcCollection.createDetailView(window));
		window.getTitleBar().setLabelProvider(srcCollection);
		window.setShowCloseButton(true);

		// Vec2f detailContainerMinSize = GLMinSizeProviders.getHorizontalFlowMinSize(detailContainer, 5,
		// GLPadding.ZERO);
		// IGLCanvas canvas = findParent(AGLElementView.class).getParentGLCanvas();
		// if (canvas == null)
		// return;

		// while (detailContainerMinSize.x() > canvas.getDIPWidth() && detailContainer.size() != 1) {
		// GLElementWindow w = detailWindowQueue.poll();
		// detailContainer.remove(w);
		// detailMap.inverse().remove(w);
		// detailContainerMinSize = GLMinSizeProviders.getHorizontalFlowMinSize(detailContainer, 5, GLPadding.ZERO);
		// }
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

	public void hideDetailView(IEntityCollection column) {
		GLElementWindow window = detailMap.remove(column);
		if (window != null) {
			detailContainer.remove(window);
			detailWindowQueue.remove(window);
			updateDetailHeight();
		}
	}

	public void updateDetailHeight() {
		if (detailContainer.size() <= 0) {
			detailContainer.setSize(Float.NaN, 0);
			// resizeChild(detailContainer, Float.NaN, 0);
			columnContainerRow.setLayoutData(1f);
			moveDownButton.setVisibility(EVisibility.NONE);
			moveUpButton.setVisibility(EVisibility.NONE);
		} else {
			detailContainer.setSize(Float.NaN, Float.NaN);
			switch (split) {
			case BOTTOM:
				detailContainer.setLayoutData(0.725f);
				columnContainerRow.setLayoutData(0.275f);
				// detailContainer.setLayoutData(0.6f);
				// columnContainerRow.setLayoutData(0.4f);
				moveDownButton.setVisibility(EVisibility.PICKABLE);
				moveUpButton.setVisibility(EVisibility.PICKABLE);
				break;
			case MIDDLE:
				detailContainer.setLayoutData(0.5);
				columnContainerRow.setLayoutData(0.5);
				moveDownButton.setVisibility(EVisibility.PICKABLE);
				moveUpButton.setVisibility(EVisibility.PICKABLE);
				break;
			case TOP:
				detailContainer.setLayoutData(0.275);
				columnContainerRow.setLayoutData(0.725);
				moveDownButton.setVisibility(EVisibility.PICKABLE);
				moveUpButton.setVisibility(EVisibility.PICKABLE);
				break;
			default:
				break;

			}

		}

		// IGLCanvas canvas = findParent(AGLElementView.class).getParentGLCanvas();
		// Vec2f detailContainerMinSize = GLMinSizeProviders.getHorizontalFlowMinSize(detailContainer, 5,
		// GLPadding.ZERO);
		// float detailHeight = Math.max(0,
		// Math.min(canvas.getDIPHeight() - (MIN_COLUMN_HEIGHT + MIN_HISTORY_HEIGHT), detailContainerMinSize.y()));
		//
		// resizeChild(detailContainer, Float.NaN, detailHeight);
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

	/**
	 * @return the multiItemSelectionSetOperation, see {@link #multiItemSelectionSetOperation}
	 */
	public ESetOperation getMultiItemSelectionSetOperation() {
		return multiItemSelectionSetOperation;
	}

	@ListenTo(sendToMe = true)
	public void onAddColumns(AddColumnsEvent event) {
		Set<IEntityCollection> collections = event.getCollections();
		for (IEntityCollection collection : collections) {
			AddColumnTreeCommand c = new AddColumnTreeCommand(collection, ConTourElement.this);
			c.execute();
			history.addHistoryCommand(c);
		}
	}

	@ListenTo
	public void onSelectionOperationChanged(final SelectionOperationEvent event) {

		IHistoryCommand c = new IHistoryCommand() {

			@Override
			public String getDescription() {
				return "Set selection mode to " + (event.isIntersection() ? "intersection" : "union");
			}

			@Override
			public Object execute() {
				if (event.isIntersection()) {
					multiItemSelectionSetOperation = ESetOperation.INTERSECTION;
				} else {
					multiItemSelectionSetOperation = ESetOperation.UNION;
				}
				return null;
			}
		};

		c.execute();
		history.addHistoryCommand(c);

	}

	@ListenTo(sendToMe = true)
	public void onResetAttributeFilter(ResetAttributeFilterEvent event) {
		Map<IEntityCollection, Set<Object>> originalFilteredItemIDs = event.getOriginalFilteredItemIDs();
		for (Entry<IEntityCollection, Set<Object>> entry : originalFilteredItemIDs.entrySet()) {
			entry.getKey().setFilteredItems(entry.getValue());
			entry.getKey().filterChanged(null, null, null);
		}
	}

	/**
	 * @return the snapshots, see {@link #snapshots}
	 */
	public Snapshots getSnapshots() {
		return snapshots;
	}

	public void addContextMenuItem(AContextMenuItem item) {
		contextMenuCreator.add(item);
		EventPublisher.trigger(new ShowContextMenuEvent().to(this));
	}

	public void addContextMenuItems(Collection<? extends AContextMenuItem> items) {
		contextMenuCreator.addAll(items);
		EventPublisher.trigger(new ShowContextMenuEvent().to(this));
	}

	@ListenTo(sendToMe = true)
	public void onExecWithinGL(ThreadSyncEvent event) {
		event.getRunnable().run();
	}

	@ListenTo(sendToMe = true)
	protected void onShowContextMenu(ShowContextMenuEvent event) {
		if (contextMenuCreator.hasMenuItems()) {
			context.getSWTLayer().showContextMenu(contextMenuCreator);
			contextMenuCreator.clear();
		}
	}

	public IGLElementContext getContext() {
		return context;
	}

	public void addMappingUpdateListener(IMappingUpdateListener listener) {
		mappingUpdateListeners.add(listener);
	}

	public void removeMappingUpdateListener(IMappingUpdateListener listener) {
		mappingUpdateListeners.remove(listener);
	}
}
