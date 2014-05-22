/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElement.EVisibility;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.basic.GLComboBox;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.layout2.renderer.IGLRenderer;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.ISummaryItemFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.MappingSummaryItemFactory;
import org.caleydo.view.relationshipexplorer.ui.column.operation.ESetOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.MappingHighlightUpdateOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.SelectionBasedHighlightOperation;
import org.caleydo.view.relationshipexplorer.ui.command.AttributeFilterCommand;
import org.caleydo.view.relationshipexplorer.ui.command.ColumnSortingCommand;
import org.caleydo.view.relationshipexplorer.ui.command.DuplicateColumnCommand;
import org.caleydo.view.relationshipexplorer.ui.command.RemoveColumnCommand;
import org.caleydo.view.relationshipexplorer.ui.command.SetSummaryItemFactoryCommand;
import org.caleydo.view.relationshipexplorer.ui.command.ShowDetailCommand;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.FilterContextMenuItems;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.ThreadSyncEvent;
import org.caleydo.view.relationshipexplorer.ui.dialog.SortingDialog;
import org.caleydo.view.relationshipexplorer.ui.list.EUpdateCause;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;
import org.caleydo.view.relationshipexplorer.ui.list.NestableColumn;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
import org.caleydo.view.relationshipexplorer.ui.util.EntityMappingUtil;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public abstract class AEntityColumn implements ILabeled, IColumnModel {

	protected static final URL FILTER_ICON = AEntityColumn.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/filter.png");
	protected static final URL SORT_ICON = AEntityColumn.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/sort_descending.png");
	protected static final URL REMOVE_ICON = AEntityColumn.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/remove.png");
	protected static final URL DUPLICATE_ICON = AEntityColumn.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/duplicate.png");

	// -----------------

	protected final IEntityCollection entityCollection;
	protected Map<Object, Set<NestableItem>> mapIDToFilteredItems = new HashMap<>();
	protected NestableColumn column;
	protected NestableColumn parentColumn;

	protected int maxParentMappings = 0;

	protected List<GLElement> headerButtons = new ArrayList<>();

	protected Set<ISummaryItemFactory> summaryItemFactories = new LinkedHashSet<>();
	protected ISummaryItemFactory summaryItemFactory;
	protected IItemFactory itemFactory;

	protected GLComboBox<ISummaryItemFactory> summaryPlots;

	protected int historyID;

	protected Set<IInvertibleComparator<NestableItem>> baseComparators = new HashSet<>();
	protected IInvertibleComparator<NestableItem> currentComparator;

	protected IScoreProvider scoreProvider;

	protected boolean initialized = false;

	// -----------------
	protected ConTourElement relationshipExplorer;

	public AEntityColumn(IEntityCollection entityCollection, ConTourElement relationshipExplorer) {
		// super(GLLayouts.flowVertical(HEADER_BODY_SPACING));
		this.entityCollection = entityCollection;
		entityCollection.addEntityRepresentation(this);
		this.relationshipExplorer = relationshipExplorer;
		this.summaryItemFactory = new MappingSummaryItemFactory(this);
		summaryItemFactories.add(summaryItemFactory);

		historyID = relationshipExplorer.getHistory().registerHistoryObject(this);

		final GLButton sortButton = addHeaderButton(SORT_ICON);

		sortButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				// final Vec2f location = filterButton.getAbsoluteLocation();

				AEntityColumn.this.relationshipExplorer.getContext().getSWTLayer().run(new ISWTLayerRunnable() {
					@Override
					public void run(Display display, Composite canvas) {
						// Point loc = canvas.toDisplay((int) location.x(), (int) location.y());
						SortingDialog dialog = new SortingDialog(canvas.getShell(), AEntityColumn.this);
						if (dialog.open() == Window.OK) {
							IInvertibleComparator<NestableItem> comparator = dialog.getComparator();
							EventPublisher.trigger(new SortingEvent(comparator, dialog.getScoreProvider())
									.to(AEntityColumn.this));
						}
					}
				});
			}
		});

	}

	@Override
	public void init() {

		// baseComparators.add(ItemComparators.SELECTED_ITEMS_COMPARATOR);
		baseComparators.add(getDefaultComparator());
		// currentComparator = new CompositeComparator<NestableItem>(ItemComparators.SELECTED_ITEMS_COMPARATOR,
		// selectionMappingComparator, visibleMappingComparator, totalMappingComparator, getDefaultComparator());
		initialized = true;

		// if (parentColumn != null) {

		// }

		summaryPlots = new GLComboBox<>(new ArrayList<>(summaryItemFactories), new IGLRenderer() {

			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				ISummaryItemFactory f = parent.getLayoutDataAs(ISummaryItemFactory.class, null);
				g.fillImage(f.getIconURL(), 0, 0, w, h);

			}
		}, GLRenderers.fillRect(Color.WHITE));
		summaryPlots.setSize(16, 16);
		summaryPlots.setVisibility(EVisibility.HIDDEN);

		summaryPlots.setCallback(new GLComboBox.ISelectionCallback<ISummaryItemFactory>() {

			@Override
			public void onSelectionChanged(GLComboBox<? extends ISummaryItemFactory> widget, ISummaryItemFactory item) {
				setSummaryItemFactory(item);
				SetSummaryItemFactoryCommand c = new SetSummaryItemFactoryCommand(AEntityColumn.this, item.getClass(),
						relationshipExplorer.getHistory(), false);
				// c.execute();
				relationshipExplorer.getHistory().addHistoryCommand(c);
			}
		});

		headerButtons.add(summaryPlots);

		headerButtons.add(new GLElement());

		final GLButton duplicateColumnButton = addHeaderButton(DUPLICATE_ICON);

		duplicateColumnButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				DuplicateColumnCommand c = new DuplicateColumnCommand(AEntityColumn.this, relationshipExplorer);
				c.execute();
				relationshipExplorer.getHistory().addHistoryCommand(c);
			}
		});

		final GLButton removeColumnButton = addHeaderButton(REMOVE_ICON);

		removeColumnButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				RemoveColumnCommand c = new RemoveColumnCommand(AEntityColumn.this, relationshipExplorer);
				c.execute();
				relationshipExplorer.getHistory().addHistoryCommand(c);
			}
		});

	}

	protected void updateSummaryPlots() {
		if (parentColumn == null || summaryItemFactories.size() <= 1)
			return;
		summaryPlots.setVisibility(EVisibility.PICKABLE);
		summaryPlots.setModel(new ArrayList<>(summaryItemFactories));
		summaryPlots.setSelectedItemSilent(summaryItemFactory);

	}

	protected GLButton addHeaderButton(URL iconURL) {
		GLButton button = new GLButton(EButtonMode.BUTTON);
		button.setVisibility(EVisibility.PICKABLE);
		button.setRenderer(GLRenderers.fillImage(iconURL));
		button.setSize(16, 16);
		headerButtons.add(button);
		return button;
	}

	@Override
	public Collection<? extends AContextMenuItem> getContextMenuItems() {
		List<AContextMenuItem> items = FilterContextMenuItems.getDefaultFilterItems(relationshipExplorer, this);
		AContextMenuItem detailItem = new GenericContextMenuItem("Show in Detail", new ThreadSyncEvent(
				new Runnable() {
					@Override
					public void run() {
						ShowDetailCommand o = new ShowDetailCommand(entityCollection, relationshipExplorer);
						o.execute();
						relationshipExplorer.getHistory().addHistoryCommand(o);
					}
				}).to(relationshipExplorer));

		items.add(detailItem);

		return items;
	}

	protected void addItem(ScoreElement element, Object elementID, NestableColumn column, NestableItem parentItem) {

		NestableItem item = column.addElement(element, parentItem);
		Set<NestableItem> items = mapIDToFilteredItems.get(elementID);
		if (items == null) {
			items = new HashSet<>();
			mapIDToFilteredItems.put(elementID, items);
		}
		items.add(item);
		item.setElementData(Sets.newHashSet(elementID));
	}

	@Override
	public String getLabel() {
		return entityCollection.getLabel();
	}

	protected AEntityColumn getFirstForeignColumn(List<AEntityColumn> foreignColumns) {
		AEntityColumn foreignColumn = null;
		for (AEntityColumn col : foreignColumns) {
			if (col != this) {
				return col;
			}
		}
		return foreignColumn;
	}

	@Override
	public void onAttributeFilter(AttributeFilterEvent event) {
		// Set<Object> newFilteredItems = FilterUtil.filter(entityCollection.getFilteredElementIDs(),
		// event.getFilter());

		AttributeFilterCommand c = new AttributeFilterCommand(this, event.getFilter(), ESetOperation.INTERSECTION,
				event.getFilterElementIDPool(), event.isSave(), relationshipExplorer.getHistory());
		c.setTargetCollections(relationshipExplorer.getEntityCollections());
		c.execute();
		if (event.isSave())
			relationshipExplorer.getHistory().addHistoryCommand(c);
	}

	@Override
	public void onSelectionChanged(NestableColumn column) {
		Set<Object> elementIDs = new HashSet<>();
		for (NestableItem item : column.getSelectedItems()) {
			Set<Object> ids = item.getElementData();
			if (ids != null)
				elementIDs.addAll(ids);
		}

		SelectionBasedHighlightOperation c = new SelectionBasedHighlightOperation(getHistoryID(), elementIDs,
				entityCollection.getBroadcastingIDsFromElementIDs(elementIDs), relationshipExplorer);

		c.execute();

		relationshipExplorer.getHistory().addHistoryCommand(c);
	}

	@Override
	public void onHighlightChanged(NestableColumn column) {
		Set<Object> elementIDs = new HashSet<>();
		for (NestableItem item : column.getHighlightedItems()) {
			Set<Object> ids = item.getElementData();
			if (ids != null)
				elementIDs.addAll(ids);
		}

		entityCollection.setHighlightItems(elementIDs);

		relationshipExplorer.applyIDMappingUpdate(new MappingHighlightUpdateOperation(entityCollection
				.getBroadcastingIDsFromElementIDs(elementIDs), this, relationshipExplorer
				.getMultiItemSelectionSetOperation(), relationshipExplorer.getEntityCollections()));

	}

	/**
	 * @return the relationshipExplorer, see {@link #relationshipExplorer}
	 */
	public ConTourElement getRelationshipExplorer() {
		return relationshipExplorer;
	}

	@Override
	public GLElement getSummaryItemElement(NestableItem parentItem, Set<NestableItem> items, NestableItem summaryItem,
			EUpdateCause cause) {

		if (summaryItem.getElement() == null || summaryItemFactory.needsUpdate(cause)) {
			return summaryItemFactory.createSummaryItem(parentItem, items);
		}
		return summaryItem.getElement();
	}

	@Override
	public void fill(NestableColumn column, NestableColumn parentColumn) {
		this.column = column;
		this.parentColumn = parentColumn;
		// column.addContextMenuItems(getContextMenuItems());

		if (parentColumn == null) {
			for (Object id : entityCollection.getFilteredElementIDs()) {
				ScoreElement element = createElement(id);
				if (element != null) {
					addItem(element, id, column, null);
				}
			}
		} else {
			for (Object id : entityCollection.getFilteredElementIDs()) {
				Set<Object> foreignElementIDs = EntityMappingUtil.getAllMappedElementIDs(id, entityCollection,
						parentColumn.getColumnModel().getCollection());
				// = parentColumn
				// .getColumnModel()
				// .getCollection()
				// .getElementIDsFromForeignIDs(entityCollection.getBroadcastingIDsFromElementID(id),
				// entityCollection.getBroadcastingIDType());
				Set<NestableItem> parentItems = parentColumn.getColumnModel().getItems(foreignElementIDs);

				boolean add = true;
				for (NestableItem parentItem : parentItems) {
					if (parentItem.getParentItem() != null) {
						add = hasParentItemElementMapping(parentItem.getParentItem(), id);
					}

					if (add) {
						ScoreElement element = createElement(id);
						if (element != null) {
							addItem(element, id, column, parentItem);
						}
					}
				}
			}
		}

		updateSelections(null);

		updateSummaryPlots();

		if (scoreProvider != null)
			updateScores();
	}

	protected boolean hasParentItemElementMapping(NestableItem parentItem, Object id) {

		Set<Object> mappingIDs = EntityMappingUtil.getFilteredMappedElementIDs(parentItem.getElementData(), parentItem
				.getColumn().getColumnModel().getCollection(), entityCollection);
		if (!mappingIDs.contains(id))
			return false;
		if (parentItem.getParentItem() == null)
			return true;
		return hasParentItemElementMapping(parentItem.getParentItem(), id);
	}

	@Override
	public Set<NestableItem> getItems(Set<Object> elementIDs) {
		Set<NestableItem> allItems = new HashSet<>();
		for (Object elementID : elementIDs) {
			Set<NestableItem> items = mapIDToFilteredItems.get(elementID);
			if (items != null) {
				allItems.addAll(items);
			}
		}
		return allItems;
	}

	@Override
	public void selectionChanged(Set<Object> selectedElementIDs, IEntityRepresentation srcRep) {

		if (!column.isRoot())
			return;

		updateSelections(srcRep);
	}

	protected void updateSelections(IEntityRepresentation srcRep) {
		column.clearSelection();
		for (Object elementID : entityCollection.getSelectedElementIDs()) {
			Set<NestableItem> items = mapIDToFilteredItems.get(elementID);
			if (items != null) {
				for (NestableItem item : items) {
					column.addToSelection(item);
				}
			}
		}

		updateChildColumnSelections(srcRep);

		column.getHeader().updateItemCounts();
		if (itemFactory.needsUpdate(EUpdateCause.SELECTION)) {
			itemFactory.update();
			column.updateItems(EUpdateCause.SELECTION);
		}
		if (summaryItemFactory.needsUpdate(EUpdateCause.SELECTION)) {
			column.updateSummaryItems(EUpdateCause.SELECTION);
		}

		if (srcRep == this)
			return;
		if (srcRep instanceof IColumnModel) {
			if (!column.isChild((IColumnModel) srcRep)) {
				// column.updateSummaryItems(EUpdateCause.SELECTION);
				updateSorting();
			}
		} else {
			// column.updateSummaryItems(EUpdateCause.SELECTION);
			updateSorting();
		}
		column.setDirectSelectionMode(false);
	}

	@Override
	public void highlightChanged(Set<Object> highlightElementIDs, IEntityRepresentation srcRep) {
		if (itemFactory.needsUpdate(EUpdateCause.HIGHLIGHT)) {
			itemFactory.update();
			column.updateItems(EUpdateCause.HIGHLIGHT);
		}
		if (summaryItemFactory.needsUpdate(EUpdateCause.HIGHLIGHT)) {
			column.updateSummaryItems(EUpdateCause.HIGHLIGHT);
		}
		if (srcRep == this)
			return;
		column.clearHighlight();
		for (Object elementID : highlightElementIDs) {
			Set<NestableItem> items = mapIDToFilteredItems.get(elementID);
			if (items != null) {
				for (NestableItem item : items) {
					column.addToHighlight(item);
				}
			}
		}
	}

	@Override
	public void filterChanged(Set<Object> filteredElementIDs, IEntityRepresentation srcRep) {
		if (!column.isRoot())
			return;

		updateFilteredItems(srcRep);
	}

	@Override
	public IEntityCollection getCollection() {
		return entityCollection;
	}

	protected void updateChildColumnSelections(IEntityRepresentation srcRep) {
		for (NestableColumn col : column.getChildren()) {
			((AEntityColumn) col.getColumnModel()).updateSelections(srcRep);
			// ((AEntityColumn) col.getColumnModel()).updateChildColumnFilters();
		}
	}

	protected void updateChildColumnFilters(IEntityRepresentation srcRep) {
		for (NestableColumn col : column.getChildren()) {
			((AEntityColumn) col.getColumnModel()).updateFilteredItems(srcRep);
			// ((AEntityColumn) col.getColumnModel()).updateChildColumnFilters();
		}
	}

	public void updateFilteredItems(IEntityRepresentation srcRep) {

		if (column.isRoot()) {
			Set<Object> elementIDs = entityCollection.getFilteredElementIDs();

			Set<Object> elementsToAdd = new HashSet<>(Sets.difference(elementIDs,
					Sets.intersection(elementIDs, mapIDToFilteredItems.keySet())));
			Set<Object> elementsToRemove = new HashSet<>(Sets.difference(mapIDToFilteredItems.keySet(),
					Sets.intersection(elementIDs, mapIDToFilteredItems.keySet())));

			for (Object elementID : elementsToRemove) {
				Set<NestableItem> items = mapIDToFilteredItems.remove(elementID);
				for (NestableItem item : items) {
					column.removeItem(item);
				}
			}
			for (Object elementID : elementsToAdd) {
				ScoreElement element = createElement(elementID);
				if (element != null) {
					addItem(element, elementID, column, null);
				}
			}
		} else {

			Set<Object> elementIDs = entityCollection.getFilteredElementIDs();
			// TODO: when adding, some parents might have items already, but new parents might not although they would
			// need it as child
			// Set<Object> elementsToAdd = new HashSet<>(Sets.difference(elementIDs,
			// Sets.intersection(elementIDs, mapIDToFilteredItems.keySet())));
			Set<Object> elementsToRemove = new HashSet<>(Sets.difference(mapIDToFilteredItems.keySet(),
					Sets.intersection(elementIDs, mapIDToFilteredItems.keySet())));
			// First remove items that are filtered for this column
			for (Object elementID : elementsToRemove) {
				Set<NestableItem> items = mapIDToFilteredItems.remove(elementID);
				for (NestableItem item : items) {
					column.removeItem(item);
				}
			}

			// Second remove remaining items that have no parent
			Map<Object, Set<NestableItem>> tempMap = new HashMap<>(mapIDToFilteredItems);
			for (Entry<Object, Set<NestableItem>> entry : tempMap.entrySet()) {

				for (NestableItem item : new HashSet<>(entry.getValue())) {
					// FIXME: This is so ugly...
					if (item.getParentItem().isRemoved()) {
						column.removeItem(item);
						entry.getValue().remove(item);
						if (entry.getValue().isEmpty()) {
							mapIDToFilteredItems.remove(entry.getKey());
						}
					}
				}
			}

			// Third add items that are present in the filter and where there's a parent
			for (Object id : elementIDs) {
				Set<Object> foreignElementIDs = EntityMappingUtil.getAllMappedElementIDs(id, entityCollection,
						parentColumn.getColumnModel().getCollection());
				Set<NestableItem> parentItems = parentColumn.getColumnModel().getItems(foreignElementIDs);

				for (NestableItem parentItem : parentItems) {

					List<NestableItem> childItems = parentItem.getChildItems(column);
					boolean createItem = true;
					for (NestableItem item : childItems) {
						if (item.getElementData().contains(id)) {
							createItem = false;
							break;
						}
					}
					if (createItem) {
						boolean add = true;
						if (parentItem.getParentItem() != null) {
							add = hasParentItemElementMapping(parentItem.getParentItem(), id);
						}

						if (add) {
							ScoreElement element = createElement(id);
							if (element != null) {
								addItem(element, id, column, parentItem);
							}
						}
					}
				}
			}

		}

		updateChildColumnFilters(srcRep);
		column.getHeader().updateItemCounts();
		if (itemFactory.needsUpdate(EUpdateCause.FILTER)) {
			itemFactory.update();
			column.updateItems(EUpdateCause.FILTER);
		}
		if (summaryItemFactory.needsUpdate(EUpdateCause.FILTER)) {
			column.updateSummaryItems(EUpdateCause.FILTER);
		}
		updateScores();

		if (srcRep == this)
			return;
		if (srcRep instanceof IColumnModel) {
			if (!column.isChild((IColumnModel) srcRep)) {
				// column.updateSummaryItems(EUpdateCause.FILTER);
				updateSorting();
			}
		}
	}

	@Override
	public void updateMappings() {
		updateMaxParentMappings();
	}

	protected void updateMaxParentMappings() {
		maxParentMappings = 0;
		if (parentColumn == null)
			return;
		for (NestableItem parentItem : parentColumn.getVisibleItems()) {
			Set<Object> parentBCIDs = parentColumn.getColumnModel().getCollection()
					.getBroadcastingIDsFromElementIDs(parentItem.getElementData());
			Set<Object> mappedElementIDs = entityCollection.getElementIDsFromForeignIDs(parentBCIDs, parentColumn
					.getColumnModel().getCollection().getBroadcastingIDType());
			if (mappedElementIDs.size() > maxParentMappings)
				maxParentMappings = mappedElementIDs.size();
		}
	}

	public void addSummaryItemFactory(ISummaryItemFactory factory) {
		summaryItemFactories.add(factory);
		updateSummaryPlots();
	}

	public void setSummaryItemFactory(ISummaryItemFactory factory) {
		summaryItemFactory = factory;
		updateSummaryPlots();
		if (column != null) {
			column.updateSummaryItems(EUpdateCause.OTHER);
			column.getColumnTree().relayout();
		}
	}

	@Override
	public NestableColumn getColumn() {
		return column;
	}

	@Override
	public int getHistoryID() {
		return historyID;
	}

	public void updateSorting() {
		sortBy(currentComparator);
	}

	@Override
	public void sortBy(IInvertibleComparator<NestableItem> comparator) {
		currentComparator = comparator;
		if (initialized)
			column.sortBy(comparator);
	}

	@Override
	public IInvertibleComparator<NestableItem> getCurrentComparator() {
		return currentComparator;
	}

	@Override
	public List<GLElement> getHeaderOverlayElements() {
		return headerButtons;
	}

	/**
	 * @return the parentColumn, see {@link #parentColumn}
	 */
	public NestableColumn getParentColumn() {
		return parentColumn;
	}

	/**
	 * @return the maxParentMappings, see {@link #maxParentMappings}
	 */
	public int getMaxParentMappings() {
		return maxParentMappings;
	}

	/**
	 * @return the baseComparators, see {@link #baseComparators}
	 */
	public Set<IInvertibleComparator<NestableItem>> getComparators() {
		return baseComparators;
	}

	public void addComparator(IInvertibleComparator<NestableItem> comparator) {
		baseComparators.add(comparator);
	}

	@Override
	public void setScoreProvider(IScoreProvider scoreProvider) {
		if (this.scoreProvider != scoreProvider) {
			this.scoreProvider = scoreProvider;
			updateScores();
		}
	}

	protected void updateScores() {
		float maxScore = Float.NEGATIVE_INFINITY;

		for (Entry<Object, Set<NestableItem>> entry : mapIDToFilteredItems.entrySet()) {
			for (NestableItem item : entry.getValue()) {
				ScoreElement scoreElement = (ScoreElement) item.getElement();

				if (scoreProvider == null) {
					scoreElement.hideScore();
				} else {
					float score = 0;
					if (item.getParentItem() == null) {
						score = scoreProvider.getScore(entry.getKey(), entityCollection, null, null);

					} else {
						score = scoreProvider.getScore(entry.getKey(), entityCollection, item.getParentItem()
								.getElementData().iterator().next(), parentColumn.getColumnModel().getCollection());
					}

					if (score > maxScore)
						maxScore = score;
					scoreElement.showScore();
				}
				scoreElement.relayout();
			}
		}

		if (scoreProvider != null) {

			for (Entry<Object, Set<NestableItem>> entry : mapIDToFilteredItems.entrySet()) {
				for (NestableItem item : entry.getValue()) {
					ScoreElement scoreElement = (ScoreElement) item.getElement();

					if (item.getParentItem() == null) {
						scoreElement.setScore(scoreProvider.getScore(entry.getKey(), entityCollection, null, null),
								maxScore);
					} else {
						scoreElement.setScore(scoreProvider.getScore(entry.getKey(), entityCollection, item
								.getParentItem().getElementData().iterator().next(), parentColumn.getColumnModel()
								.getCollection()), maxScore);
					}
				}
			}
		}

		column.getColumnTree().relayout();
	}

	@Override
	public void onSort(SortingEvent event) {
		ColumnSortingCommand c = new ColumnSortingCommand(this, event.getComparator(), event.getScoreProvider(),
				relationshipExplorer.getHistory());
		c.execute();
		relationshipExplorer.getHistory().addHistoryCommand(c);

	}

	protected ScoreElement createElement(Object elementID) {
		GLElement element = itemFactory.createItem(elementID);
		ScoreElement scoreElement = new ScoreElement(element);
		if (scoreProvider != null) {
			scoreElement.showScore();
		}
		return scoreElement;
	}

	@Override
	public GLElement getItemElement(NestableItem item, EUpdateCause cause) {
		if (itemFactory.needsUpdate(cause) || item.getElement() == null) {
			return createElement(item.getElementData().iterator().next());
		}
		return item.getElement();
	}

	@Override
	public void takeDown() {
		column.removeSelectionUpdateListener(this);
		entityCollection.removeEntityRepresentation(this);
		mapIDToFilteredItems.clear();
		baseComparators.clear();
		summaryItemFactories.clear();
	}

	/**
	 * @return the summaryItemFactory, see {@link #summaryItemFactory}
	 */
	public ISummaryItemFactory getSummaryItemFactory() {
		return summaryItemFactory;
	}

	/**
	 * @return the summaryItemFactories, see {@link #summaryItemFactories}
	 */
	public Set<ISummaryItemFactory> getSummaryItemFactories() {
		return summaryItemFactories;
	}

	/**
	 * @param itemFactory
	 *            setter, see {@link itemFactory}
	 */
	public void setItemFactory(IItemFactory itemFactory) {
		this.itemFactory = itemFactory;
	}

	@Override
	public GLElement getHeaderExtension() {
		return itemFactory.createHeaderExtension();
	}

	@Override
	public IScoreProvider getScoreProvider() {
		return scoreProvider;
	}

}
