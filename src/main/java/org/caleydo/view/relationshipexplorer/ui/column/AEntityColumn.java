/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import gleem.linalg.Vec2f;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.base.Runnables;
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
import org.caleydo.view.relationshipexplorer.ui.collection.AEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.EnrichmentScores.EnrichmentScoreComparator;
import org.caleydo.view.relationshipexplorer.ui.collection.EnrichmentScores.MaxEnrichmentScoreComparator;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IIconProvider;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.ISummaryItemFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.ISummaryItemFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.column.operation.ESetOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.MappingHighlightUpdateOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.SelectionBasedHighlightOperation;
import org.caleydo.view.relationshipexplorer.ui.command.AttributeFilterCommand;
import org.caleydo.view.relationshipexplorer.ui.command.ColumnSortingCommand;
import org.caleydo.view.relationshipexplorer.ui.command.DuplicateColumnCommand;
import org.caleydo.view.relationshipexplorer.ui.command.RemoveColumnCommand;
import org.caleydo.view.relationshipexplorer.ui.command.SetItemFactoryCommand;
import org.caleydo.view.relationshipexplorer.ui.command.SetSummaryItemFactoryCommand;
import org.caleydo.view.relationshipexplorer.ui.command.ShowDetailCommand;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.FilterContextMenuItems;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.ThreadSyncEvent;
import org.caleydo.view.relationshipexplorer.ui.dialog.SearchDialog;
import org.caleydo.view.relationshipexplorer.ui.dialog.SortingDialog;
import org.caleydo.view.relationshipexplorer.ui.dialog.StringFilterDialog;
import org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig.ConfigureDetailViewDialog;
import org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig.ConfigureItemRendererDialog;
import org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig.ConfigureSummaryRendererDialog;
import org.caleydo.view.relationshipexplorer.ui.filter.IEntityFilter;
import org.caleydo.view.relationshipexplorer.ui.list.EUpdateCause;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;
import org.caleydo.view.relationshipexplorer.ui.list.NestableColumn;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
import org.caleydo.view.relationshipexplorer.ui.util.EntityMappingUtil;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Point;
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
	protected static final URL FIND_ICON = AEntityColumn.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/find.png");
	protected static final URL SORT_ICON = AEntityColumn.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/sort_descending.png");
	protected static final URL REMOVE_ICON = AEntityColumn.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/remove.png");
	protected static final URL DUPLICATE_ICON = AEntityColumn.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/duplicate.png");

	// -----------------

	protected final IEntityCollection collection;
	protected Map<Object, Set<NestableItem>> mapIDToFilteredItems = new HashMap<>();
	protected NestableColumn column;
	protected NestableColumn parentColumn;

	protected int maxParentMappings = 0;

	protected List<GLElement> headerButtons = new ArrayList<>();

	/**
	 * Creators of item factories for item plots
	 */
	protected List<IItemFactoryCreator> itemFactoryCreators = new ArrayList<>();
	/**
	 * Currently used {@link IItemFactoryCreator}
	 */
	protected IItemFactoryCreator itemFactoryCreator;

	/**
	 * Creators of summary item factories for summary item plots.
	 */
	protected List<ISummaryItemFactoryCreator> summaryItemFactoryCreators = new ArrayList<>();
	/**
	 * Currently used {@link ISummaryItemFactoryCreator}
	 */
	protected ISummaryItemFactoryCreator summaryItemFactoryCreator;

	protected ISummaryItemFactory summaryItemFactory;
	protected IItemFactory itemFactory;

	protected GLComboBox<IItemFactoryCreator> itemPlots;
	protected GLComboBox<ISummaryItemFactoryCreator> summaryPlots;

	protected int historyID;

	protected Set<IInvertibleComparator<NestableItem>> baseComparators = new HashSet<>();
	protected IInvertibleComparator<NestableItem> currentComparator;

	protected IScoreProvider scoreProvider;

	protected boolean initialized = false;

	// -----------------
	protected ConTourElement contour;

	public AEntityColumn(IEntityCollection entityCollection, final ConTourElement contour) {
		// super(GLLayouts.flowVertical(HEADER_BODY_SPACING));
		this.collection = entityCollection;
		entityCollection.addEntityRepresentation(this);
		this.contour = contour;
		// this.summaryItemFactory = new MappingSummaryItemFactory(this);
		// summaryItemFactoryCreators.add(summaryItemFactory);

		historyID = contour.getHistory().registerHistoryObject(this);

		final GLButton sortButton = addHeaderButton(SORT_ICON, "Sort column");

		sortButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				// final Vec2f location = filterButton.getAbsoluteLocation();

				AEntityColumn.this.contour.getContext().getSWTLayer().run(new ISWTLayerRunnable() {
					@Override
					public void run(Display display, Composite canvas) {
						// Point loc = canvas.toDisplay((int) location.x(), (int) location.y());
						SortingDialog dialog = new SortingDialog(canvas.getShell(), AEntityColumn.this);
						if (dialog.open() == Window.OK) {
							IInvertibleComparator<NestableItem> comparator = dialog.getComparator();
							EventPublisher.trigger(new SortingEvent(comparator, dialog.getScoreProvider())
									.to(AEntityColumn.this));
							if (comparator instanceof CompositeComparator<?>) {
								CompositeComparator<?> comp = (CompositeComparator<?>) comparator;
								for (IInvertibleComparator<?> c : comp) {
									if (c instanceof MaxEnrichmentScoreComparator) {
										MaxEnrichmentScoreComparator enrichComp = (MaxEnrichmentScoreComparator) c;

										for (NestableColumn childColumn : column.getChildren()) {
											if (childColumn.getColumnModel().getCollection() == enrichComp
													.getEnrichmentScore().getEnrichmentCollection()
													|| childColumn.getColumnModel().getCollection() == enrichComp
															.getEnrichmentScore().getTargetCollection()) {

												EnrichmentScoreComparator scoreComp = new EnrichmentScoreComparator(
														enrichComp.getEnrichmentScore());

												EventPublisher.trigger(new SortingEvent(scoreComp, scoreComp)
														.to(childColumn.getColumnModel()));
											}
										}
									}
								}
							}
						}
					}
				});
			}
		});

		// setItemFactory(new TextItemFactory(this));
		final GLButton filterButton = addHeaderButton(FILTER_ICON, "Filter by Name");

		filterButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				final Vec2f location = filterButton.getAbsoluteLocation();

				contour.getContext().getSWTLayer().run(new ISWTLayerRunnable() {
					@Override
					public void run(Display display, Composite canvas) {

						Point loc = canvas.toDisplay((int) location.x(), (int) location.y());
						// StringFilterDialog dialog = new StringFilterDialog(canvas.getShell(), "Filter " + getLabel(),
						// ATextColumn.this, loc, new HashMap<>(mapFilteredElements));
						StringFilterDialog dialog = new StringFilterDialog(canvas.getShell(), "Filter " + getLabel(),
								loc, AEntityColumn.this);

						if (dialog.open() == Window.OK) {
							IEntityFilter filter = dialog.getFilter();
							EventPublisher.trigger(new AttributeFilterEvent(filter, dialog.getFilterElementIDPool(),
									true).to(AEntityColumn.this));
						} else {
							// EventPublisher.trigger(new ResetAttributeFilterEvent(dialog.getOriginalFilteredItemIDs())
							// .to(ATextColumn.this.relationshipExplorer));
						}
					}
				});
			}
		});

		final GLButton findButton = addHeaderButton(FIND_ICON, "Search for Items");
		//
		// findButton.setCallback(new ISelectionCallback() {
		//
		// @Override
		// public void onSelectionChanged(GLButton button, boolean selected) {
		// Vec2f location = contour.getContext().getSWTLayer().run(new ISWTLayerRunnable() {
		//
		// @Override
		// public void run(Display display, Composite canvas) {
		//
		// }
		// })
		//
		// }
		// });

		if (initialized) {
			// Remove remove column, duplicate column and spacing elements.
			headerButtons.remove(headerButtons.size() - 1);
			headerButtons.remove(headerButtons.size() - 1);
			headerButtons.remove(headerButtons.size() - 1);

			headerButtons.remove(itemPlots);
			headerButtons.remove(summaryPlots);
		}

		initialized = false;
		baseComparators.clear();
		itemFactory = null;
		itemFactoryCreators.clear();
		itemFactoryCreator = null;
		summaryItemFactory = null;
		summaryItemFactoryCreators.clear();
		summaryItemFactoryCreator = null;

		findButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				final Vec2f location = findButton.getAbsoluteLocation();

				contour.getContext().getSWTLayer().run(new ISWTLayerRunnable() {
					@Override
					public void run(Display display, Composite canvas) {

						Point loc = canvas.toDisplay((int) location.x(), (int) location.y());
						// StringFilterDialog dialog = new StringFilterDialog(canvas.getShell(), "Filter " + getLabel(),
						// ATextColumn.this, loc, new HashMap<>(mapFilteredElements));
						SearchDialog dialog = new SearchDialog(canvas.getShell(), "Search " + getLabel(), loc,
								AEntityColumn.this);

						if (dialog.open() != Window.OK) {
							final IInvertibleComparator<NestableItem> comparator = dialog.getOriginalComparator();
							EventPublisher.trigger(new ThreadSyncEvent(new Runnable() {
								@Override
								public void run() {
									AEntityColumn.this.sortBy(comparator);
								}
							}).to(getRelationshipExplorer()));
						}
					}
				});
			}
		});

	}

	/**
	 * Removes all item and summary factories and sets the status of this column to uninitialized.
	 */
	public void reset() {
		if (initialized) {
			// Remove remove column, duplicate column and spacing elements.
			headerButtons.remove(headerButtons.size() - 1);
			headerButtons.remove(headerButtons.size() - 1);
			headerButtons.remove(headerButtons.size() - 1);

			headerButtons.remove(itemPlots);
			headerButtons.remove(summaryPlots);
		}

		initialized = false;
		baseComparators.clear();
		itemFactory = null;
		itemFactoryCreators.clear();
		itemFactoryCreator = null;
		summaryItemFactory = null;
		summaryItemFactoryCreators.clear();
		summaryItemFactoryCreator = null;

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

		itemPlots = createPlotCombo(itemFactoryCreators);
		itemPlots.setCallback(new GLComboBox.ISelectionCallback<IItemFactoryCreator>() {

			@Override
			public void onSelectionChanged(GLComboBox<? extends IItemFactoryCreator> widget, IItemFactoryCreator item) {
				setItemFactoryCreator(item);
				SetItemFactoryCommand c = new SetItemFactoryCommand(AEntityColumn.this, item, contour.getHistory(),
						false);
				// c.execute();
				contour.getHistory().addHistoryCommand(c);
			}
		});
		updateItemPlots();

		headerButtons.add(itemPlots);

		summaryPlots = createPlotCombo(summaryItemFactoryCreators);
		summaryPlots.setCallback(new GLComboBox.ISelectionCallback<ISummaryItemFactoryCreator>() {

			@Override
			public void onSelectionChanged(GLComboBox<? extends ISummaryItemFactoryCreator> widget,
					ISummaryItemFactoryCreator item) {
				setSummaryItemFactoryCreator(item);
				SetSummaryItemFactoryCommand c = new SetSummaryItemFactoryCommand(AEntityColumn.this, item, contour
						.getHistory(), false);
				// c.execute();
				contour.getHistory().addHistoryCommand(c);
			}
		});
		updateSummaryPlots();

		headerButtons.add(summaryPlots);

		headerButtons.add(new GLElement());

		final GLButton duplicateColumnButton = addHeaderButton(DUPLICATE_ICON, "Duplicate Column");

		duplicateColumnButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				DuplicateColumnCommand c = new DuplicateColumnCommand(AEntityColumn.this, contour);
				c.execute();
				contour.getHistory().addHistoryCommand(c);
			}
		});

		final GLButton removeColumnButton = addHeaderButton(REMOVE_ICON, "Remove Column");

		removeColumnButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				RemoveColumnCommand c = new RemoveColumnCommand(AEntityColumn.this, contour);
				c.execute();
				contour.getHistory().addHistoryCommand(c);
			}
		});

	}

	protected <T extends IIconProvider> GLComboBox<T> createPlotCombo(List<T> plots) {
		GLComboBox<T> combo = new GLComboBox<>(plots, new IGLRenderer() {

			@Override
			public void render(GLGraphics g, float w, float h, GLElement parent) {
				IIconProvider f = parent.getLayoutDataAs(IIconProvider.class, null);
				g.fillImage(f.getIconURL(), 0, 0, w, h);

			}
		}, GLRenderers.fillRect(Color.WHITE));
		combo.setSize(16, 16);
		combo.setVisibility(EVisibility.HIDDEN);

		return combo;
	}

	protected void updateSummaryPlots() {
		if (parentColumn == null || summaryItemFactoryCreators.size() <= 1 || !initialized)
			return;
		summaryPlots.setVisibility(EVisibility.PICKABLE);
		summaryPlots.setModel(new ArrayList<>(summaryItemFactoryCreators));
		summaryPlots.setSelectedItemSilent(summaryItemFactoryCreator);
	}

	protected void updateItemPlots() {
		if (itemFactoryCreators.size() <= 1 || !initialized)
			return;
		itemPlots.setVisibility(EVisibility.PICKABLE);
		itemPlots.setModel(new ArrayList<>(itemFactoryCreators));
		itemPlots.setSelectedItemSilent(itemFactoryCreator);

	}

	protected GLButton addHeaderButton(URL iconURL, String tooltip) {
		GLButton button = new GLButton(EButtonMode.BUTTON);
		button.setVisibility(EVisibility.PICKABLE);
		button.setRenderer(GLRenderers.fillImage(iconURL));
		button.setSize(16, 16);
		button.setTooltip(tooltip);
		headerButtons.add(button);
		return button;
	}

	@Override
	public Collection<? extends AContextMenuItem> getItemContextMenuItems() {
		List<AContextMenuItem> items = FilterContextMenuItems.getDefaultFilterItems(contour, this, collection);
		if (((AEntityCollection) collection).getDetailViewFactory() != null) {
			AContextMenuItem detailItem = new GenericContextMenuItem("Show in Detail", new ThreadSyncEvent(
					new Runnable() {
						@Override
						public void run() {
							ShowDetailCommand o = new ShowDetailCommand(collection, contour);
							o.execute();
							contour.getHistory().addHistoryCommand(o);
						}
					}).to(contour));
			items.add(detailItem);
		}

		return items;
	}

	@Override
	public Collection<? extends AContextMenuItem> getHeaderContextMenuItems() {
		List<AContextMenuItem> items = new ArrayList<>();

		AContextMenuItem configItemRep = new GenericContextMenuItem("Configure Item Representations",
				new ThreadSyncEvent(Runnables.withinSWTThread(new Runnable() {
					@Override
					public void run() {
						ConfigureItemRendererDialog dialog = new ConfigureItemRendererDialog(Display.getDefault()
								.getActiveShell(), (AEntityCollection) collection, contour);
						dialog.open();
					}
				})).to(contour));
		items.add(configItemRep);

		AContextMenuItem configSummaryItemRep = new GenericContextMenuItem("Configure Summary Item Representations",
				new ThreadSyncEvent(Runnables.withinSWTThread(new Runnable() {
					@Override
					public void run() {
						ConfigureSummaryRendererDialog dialog = new ConfigureSummaryRendererDialog(Display.getDefault()
								.getActiveShell(), (AEntityCollection) collection, contour);
						dialog.open();
					}
				})).to(contour));
		items.add(configSummaryItemRep);

		AContextMenuItem configDetailView = new GenericContextMenuItem("Configure Detail View", new ThreadSyncEvent(
				Runnables.withinSWTThread(new Runnable() {
					@Override
					public void run() {
						ConfigureDetailViewDialog dialog = new ConfigureDetailViewDialog(Display.getDefault()
								.getActiveShell(), (AEntityCollection) collection, contour);
						dialog.open();
					}
				})).to(contour));
		items.add(configDetailView);

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
		return collection.getLabel();
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
				event.getFilterElementIDPool(), event.isSave(), contour.getHistory());
		c.setTargetCollections(contour.getEntityCollections());
		c.execute();
		if (event.isSave())
			contour.getHistory().addHistoryCommand(c);
	}

	@Override
	public void onSelectionChanged(NestableColumn column) {
		Set<Object> elementIDs = new HashSet<>();
		for (NestableItem item : column.getSelectedItems()) {
			Set<Object> ids = item.getElementData();
			if (ids != null)
				elementIDs.addAll(ids);
		}

		SelectionBasedHighlightOperation c = new SelectionBasedHighlightOperation(collection, getHistoryID(),
				elementIDs, collection.getBroadcastingIDsFromElementIDs(elementIDs),
				collection.getBroadcastingIDType(), contour);

		c.execute();

		contour.getHistory().addHistoryCommand(c);
	}

	@Override
	public void onHighlightChanged(NestableColumn column) {
		Set<Object> elementIDs = new HashSet<>();
		for (NestableItem item : column.getHighlightedItems()) {
			Set<Object> ids = item.getElementData();
			if (ids != null)
				elementIDs.addAll(ids);
		}

		collection.setHighlightItems(elementIDs);

		contour.applyIDMappingUpdate(new MappingHighlightUpdateOperation(collection, collection
				.getBroadcastingIDsFromElementIDs(elementIDs), collection.getBroadcastingIDType(), this, contour
				.getMultiItemSelectionSetOperation(), contour.getEntityCollections()));

	}

	/**
	 * @return the relationshipExplorer, see {@link #contour}
	 */
	public ConTourElement getRelationshipExplorer() {
		return contour;
	}

	@Override
	public GLElement getSummaryItemElement(NestableItem parentItem, Set<NestableItem> items, NestableItem summaryItem,
			EUpdateCause cause) {

		if (summaryItem.getElement() == null || summaryItemFactory.needsUpdate(cause)
				|| cause == EUpdateCause.PLOT_TYPE_CHANGE) {
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
			for (Object id : collection.getFilteredElementIDs()) {
				ScoreElement element = createElement(id);
				if (element != null) {
					addItem(element, id, column, null);
				}
			}
		} else {
			for (Object id : collection.getFilteredElementIDs()) {
				Set<Object> foreignElementIDs = EntityMappingUtil.getAllMappedElementIDs(id, collection, parentColumn
						.getColumnModel().getCollection());
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
				.getColumn().getColumnModel().getCollection(), collection);
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
	public void selectionChanged(Set<Object> selectedElementIDs, ILabeled updateSource) {

		if (!column.isRoot())
			return;

		updateSelections(updateSource);
	}

	protected void updateSelections(ILabeled updateSource) {
		column.clearSelection();
		for (Object elementID : collection.getSelectedElementIDs()) {
			Set<NestableItem> items = mapIDToFilteredItems.get(elementID);
			if (items != null) {
				for (NestableItem item : items) {
					column.addToSelection(item);
				}
			}
		}

		updateChildColumnSelections(updateSource);

		column.getHeader().updateItemCounts();
		if (itemFactory.needsUpdate(EUpdateCause.SELECTION)) {
			itemFactory.update();
			column.updateItems(EUpdateCause.SELECTION);
		}
		if (summaryItemFactory.needsUpdate(EUpdateCause.SELECTION)) {
			column.updateSummaryItems(EUpdateCause.SELECTION);
		}

		if (updateSource == this)
			return;
		if (updateSource instanceof IColumnModel) {
			if (!column.isChild((IColumnModel) updateSource)) {
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
	public void highlightChanged(Set<Object> highlightElementIDs, ILabeled updateSource) {
		if (itemFactory.needsUpdate(EUpdateCause.HIGHLIGHT)) {
			itemFactory.update();
			column.updateItems(EUpdateCause.HIGHLIGHT);
		}
		if (summaryItemFactory.needsUpdate(EUpdateCause.HIGHLIGHT)) {
			column.updateSummaryItems(EUpdateCause.HIGHLIGHT);
		}
		if (updateSource == this)
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
	public void filterChanged(Set<Object> filteredElementIDs, ILabeled updateSource) {
		if (!column.isRoot())
			return;

		updateFilteredItems(updateSource);
	}

	@Override
	public IEntityCollection getCollection() {
		return collection;
	}

	protected void updateChildColumnSelections(ILabeled updateSource) {
		for (NestableColumn col : column.getChildren()) {
			((AEntityColumn) col.getColumnModel()).updateSelections(updateSource);
			// ((AEntityColumn) col.getColumnModel()).updateChildColumnFilters();
		}
	}

	protected void updateChildColumnFilters(ILabeled updateSource) {
		for (NestableColumn col : column.getChildren()) {
			((AEntityColumn) col.getColumnModel()).updateFilteredItems(updateSource);
			// ((AEntityColumn) col.getColumnModel()).updateChildColumnFilters();
		}
	}

	public void updateFilteredItems(ILabeled updateSource) {

		if (column.isRoot()) {
			Set<Object> elementIDs = collection.getFilteredElementIDs();

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

			Set<Object> elementIDs = collection.getFilteredElementIDs();
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
				Set<Object> foreignElementIDs = EntityMappingUtil.getAllMappedElementIDs(id, collection, parentColumn
						.getColumnModel().getCollection());
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

		updateChildColumnFilters(updateSource);
		column.getHeader().updateItemCounts();
		if (itemFactory.needsUpdate(EUpdateCause.FILTER)) {
			itemFactory.update();
			column.updateItems(EUpdateCause.FILTER);
		}
		if (summaryItemFactory.needsUpdate(EUpdateCause.FILTER)) {
			column.updateSummaryItems(EUpdateCause.FILTER);
		}
		updateScores();

		if (updateSource == this)
			return;
		if (updateSource instanceof IColumnModel) {
			if (!column.isChild((IColumnModel) updateSource)) {
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
			Set<Object> mappedElementIDs = collection.getElementIDsFromForeignIDs(parentBCIDs, parentColumn
					.getColumnModel().getCollection().getBroadcastingIDType());
			if (mappedElementIDs.size() > maxParentMappings)
				maxParentMappings = mappedElementIDs.size();
		}
	}

	public void addSummaryItemFactoryCreator(ISummaryItemFactoryCreator creator) {
		summaryItemFactoryCreators.add(creator);
		if (summaryItemFactoryCreators.size() == 1)
			setSummaryItemFactoryCreator(creator);
		updateSummaryPlots();
	}

	public void setSummaryItemFactoryCreator(ISummaryItemFactoryCreator creator) {
		summaryItemFactoryCreator = creator;
		summaryItemFactory = creator.create(collection, this, contour);
		updateSummaryPlots();
		if (column != null) {
			column.updateSummaryItems(EUpdateCause.PLOT_TYPE_CHANGE);
			column.getColumnTree().relayout();
		}
	}

	public void addItemFactoryCreator(IItemFactoryCreator creator) {
		itemFactoryCreators.add(creator);
		if (itemFactoryCreators.size() == 1)
			setItemFactoryCreator(creator);
		updateItemPlots();
	}

	/**
	 * @param itemFactory
	 *            setter, see {@link itemFactory}
	 */
	public void setItemFactoryCreator(IItemFactoryCreator creator) {
		itemFactoryCreator = creator;
		this.itemFactory = creator.create(collection, this, contour);
		updateItemPlots();
		if (column != null) {
			column.getHeader().updateHeaderExtension();
			column.updateItems(EUpdateCause.PLOT_TYPE_CHANGE);
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
						score = scoreProvider.getScore(entry.getKey(), collection, null, null);

					} else {
						score = scoreProvider.getScore(entry.getKey(), collection, item.getParentItem()
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
						scoreElement.setScore(scoreProvider.getScore(entry.getKey(), collection, null, null), maxScore);
					} else {
						scoreElement.setScore(scoreProvider.getScore(entry.getKey(), collection, item.getParentItem()
								.getElementData().iterator().next(), parentColumn.getColumnModel().getCollection()),
								maxScore);
					}
				}
			}
		}

		column.getColumnTree().relayout();
	}

	@Override
	public void onSort(SortingEvent event) {
		ColumnSortingCommand c = new ColumnSortingCommand(this, event.getComparator(), event.getScoreProvider(),
				contour.getHistory());
		c.execute();
		contour.getHistory().addHistoryCommand(c);

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
		if (itemFactory.needsUpdate(cause) || item.getElement() == null || cause == EUpdateCause.PLOT_TYPE_CHANGE) {
			return createElement(item.getElementData().iterator().next());
		}
		return item.getElement();
	}

	@Override
	public void takeDown() {
		column.removeSelectionUpdateListener(this);
		collection.removeEntityRepresentation(this);
		mapIDToFilteredItems.clear();
		baseComparators.clear();
		summaryItemFactoryCreators.clear();
	}

	/**
	 * @return the summaryItemFactory, see {@link #summaryItemFactory}
	 */
	public ISummaryItemFactory getSummaryItemFactory() {
		return summaryItemFactory;
	}

	/**
	 * @return the summaryItemFactoryCreator, see {@link #summaryItemFactoryCreator}
	 */
	public ISummaryItemFactoryCreator getSummaryItemFactoryCreator() {
		return summaryItemFactoryCreator;
	}

	/**
	 * @return the itemFactory, see {@link #itemFactory}
	 */
	public IItemFactory getItemFactory() {
		return itemFactory;
	}

	/**
	 * @return the itemFactoryCreator, see {@link #itemFactoryCreator}
	 */
	public IItemFactoryCreator getItemFactoryCreator() {
		return itemFactoryCreator;
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
