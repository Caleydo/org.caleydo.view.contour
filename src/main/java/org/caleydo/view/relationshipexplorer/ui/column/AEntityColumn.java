/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorBrewer;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElement.EVisibility;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.ISummaryItemFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.MappingSummaryItemFactory;
import org.caleydo.view.relationshipexplorer.ui.column.operation.AttributeFilterCommand;
import org.caleydo.view.relationshipexplorer.ui.column.operation.ColumnSortingCommand;
import org.caleydo.view.relationshipexplorer.ui.column.operation.ESetOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.MappingHighlightUpdateOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.SelectionBasedHighlightOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.ShowDetailCommand;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.ContextMenuCommandEvent;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.FilterContextMenuItems;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.IContextMenuCommand;
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
	// protected static final int HEADER_HEIGHT = 20;
	// protected static final int HEADER_BODY_SPACING = 5;
	//
	// protected static final Integer DATA_KEY = Integer.valueOf(0);
	// protected static final Integer MAPPING_KEY = Integer.valueOf(1);
	//
	// protected static final Integer SELECTED_ELEMENTS_KEY = Integer.valueOf(2);
	// protected static final Integer FILTERED_ELEMENTS_KEY = Integer.valueOf(3);
	// protected static final Integer ALL_ELEMENTS_KEY = Integer.valueOf(4);

	protected static final URL FILTER_ICON = AEntityColumn.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/filter.png");
	protected static final URL SORT_ICON = AEntityColumn.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/sort_descending.png");

	// -----------------

	protected final IEntityCollection entityCollection;
	protected Map<Object, Set<NestableItem>> mapIDToFilteredItems = new HashMap<>();
	protected NestableColumn column;
	protected NestableColumn parentColumn;

	protected int maxParentMappings = 0;

	protected List<GLElement> headerButtons = new ArrayList<>();

	protected Set<ISummaryItemFactory> summaryItemFactories = new HashSet<>();
	protected ISummaryItemFactory summaryItemFactory;

	protected int historyID;

	protected Set<Comparator<NestableItem>> baseComparators = new HashSet<>();
	protected Comparator<NestableItem> currentComparator;

	protected IScoreProvider scoreProvider;

	protected boolean initialized = false;

	// -----------------
	protected RelationshipExplorerElement relationshipExplorer;

	public AEntityColumn(IEntityCollection entityCollection, RelationshipExplorerElement relationshipExplorer) {
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

				column.getColumnTree().getContext().getSWTLayer().run(new ISWTLayerRunnable() {
					@Override
					public void run(Display display, Composite canvas) {
						// Point loc = canvas.toDisplay((int) location.x(), (int) location.y());
						SortingDialog dialog = new SortingDialog(canvas.getShell(), AEntityColumn.this);
						if (dialog.open() == Window.OK) {
							Comparator<NestableItem> comparator = dialog.getComparator();
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
	}

	protected GLButton addHeaderButton(URL iconURL) {
		GLButton button = new GLButton(EButtonMode.BUTTON);
		button.setVisibility(EVisibility.PICKABLE);
		button.setRenderer(GLRenderers.fillImage(iconURL));
		button.setSize(16, 16);
		headerButtons.add(button);
		return button;
	}

	// @Override
	// protected void init(IGLElementContext context) {
	// super.init(context);
	//
	// // setContent();
	//
	// // itemList.addContextMenuItems(getContextMenuItems());
	//
	// // header.setElement(DATA_KEY, new GLElement(GLRenderers.drawText(getLabel(), VAlign.CENTER)));
	// // itemList.addElementSelectionListener(this);
	// // sort(getDefaultElementComparator());
	// // mapFilteredElements.putAll(mapIDToElement);
	// }

	protected List<AContextMenuItem> getContextMenuItems() {
		// AContextMenuItem replaceFilterItem = new GenericContextMenuItem(
		// "Replace current Set by Relationships for selected " + entityCollection.getLabel(),
		// new ContextMenuCommandEvent(new FilterCommand(ESetOperation.REPLACE, this, relationshipExplorer))
		// .to(this));
		// AContextMenuItem andFilterITem = new
		// GenericContextMenuItem("Filter current Set by Relationships for selected "
		// + entityCollection.getLabel(), new ContextMenuCommandEvent(new FilterCommand(
		// ESetOperation.INTERSECTION, this, relationshipExplorer)).to(this));
		// AContextMenuItem orFilterITem = new GenericContextMenuItem("Add all Relationships for selected "
		// + entityCollection.getLabel(), new ContextMenuCommandEvent(new FilterCommand(ESetOperation.UNION, this,
		// relationshipExplorer)).to(this));
		List<AContextMenuItem> items = FilterContextMenuItems.getDefaultFilterItems(relationshipExplorer, this, this);
		AContextMenuItem detailItem = new GenericContextMenuItem("Show in Detail", new ContextMenuCommandEvent(
				new IContextMenuCommand() {
					@Override
					public void execute() {
						ShowDetailCommand o = new ShowDetailCommand(AEntityColumn.this,
								relationshipExplorer.getHistory());
						o.execute();
						relationshipExplorer.getHistory().addHistoryCommand(o, ColorBrewer.Greens.getColors(3).get(1));
					}
				}).to(this));

		items.add(detailItem);

		return items;
	}

	// protected void addElement(GLElement element, Object elementID) {
	//
	// KeyBasedGLElementContainer<GLElement> row = new KeyBasedGLElementContainer<>(
	// GLLayouts.sizeRestrictiveFlowHorizontal(2));
	// row.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(row, 2, GLPadding.ZERO));
	// row.setElement(DATA_KEY, element);
	// mapIDToElement.put(elementID, row);
	// itemList.add(row);
	// }

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

	// protected KeyBasedGLElementContainer<SimpleBarRenderer> createLayeredBarRenderer() {
	// KeyBasedGLElementContainer<SimpleBarRenderer> barLayerRenderer = new KeyBasedGLElementContainer<>(
	// GLLayouts.LAYERS);
	//
	// // barLayerRenderer.setSize(80, Float.NaN);
	// barLayerRenderer.setMinSizeProvider(GLMinSizeProviders.createLayeredMinSizeProvider(barLayerRenderer));
	// barLayerRenderer.setElement(ALL_ELEMENTS_KEY, createDefaultBarRenderer(Color.LIGHT_GRAY, 0.1f));
	// barLayerRenderer.setElement(FILTERED_ELEMENTS_KEY, createDefaultBarRenderer(Color.GRAY, 0.2f));
	// barLayerRenderer.setElement(SELECTED_ELEMENTS_KEY,
	// createDefaultBarRenderer(SelectionType.SELECTION.getColor(), 0.3f));
	// // barLayerRenderer.setRenderer(GLRenderers.drawRect(Color.BLUE));
	// return barLayerRenderer;
	// }
	//
	// protected SimpleBarRenderer createDefaultBarRenderer(Color color, float zDelta) {
	// SimpleBarRenderer renderer = new SimpleBarRenderer(0, true);
	// renderer.setMinSize(new Vec2f(80, 16));
	// // renderer.setSize(80, Float.NaN);
	// renderer.setColor(color);
	// renderer.setBarWidth(12);
	// renderer.setzDelta(zDelta);
	// return renderer;
	// }

	@Override
	public String getLabel() {
		return entityCollection.getLabel();
	}

	// @Override
	// public Vec2f getMinSize() {
	// return itemList.getMinSize();
	// }

	protected AEntityColumn getForeignColumnWithMappingIDType(IDType idType) {

		return getFirstForeignColumn(relationshipExplorer.getColumnsWithMappingIDType(idType));
	}

	protected AEntityColumn getForeignColumnWithBroadcastIDType(IDType idType) {

		return getFirstForeignColumn(relationshipExplorer.getColumnsWithBroadcastIDType(idType));
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
	public Set<Object> getElementIDsFromForeignIDs(Set<Object> foreignIDs, IDType foreignIDType) {
		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(getBroadcastingIDType());

		Set<Object> elementIDs = new HashSet<>();
		Set<Object> broadcastIDs = mappingManager.getIDTypeMapper(foreignIDType, getBroadcastingIDType()).apply(
				foreignIDs);
		for (Object bcID : broadcastIDs) {
			elementIDs.addAll(getElementIDsFromBroadcastingID((Integer) bcID));
		}

		return elementIDs;
	}

	// /**
	// * @return the selectedElementIDs, see {@link #selectedElementIDs}
	// */
	// @Override
	// public Set<Object> getSelectedElementIDs() {
	// return selectedElementIDs;
	// }
	//
	// @Override
	// public Set<Object> getHighlightElementIDs() {
	// return highlightElementIDs;
	// }

	// @ListenTo(sendToMe = true)
	@Override
	public void onHandleContextMenuOperation(ContextMenuCommandEvent event) {
		event.getCommand().execute();

		// applyFilter(event.type, elementIDs);
		// triggerIDUpdate(broadcastIDs, event.type);
		// triggerIDUpdate(broadcastIDs, EUpdateType.SELECTION);
	}

	@Override
	public void onAttributeFilter(AttributeFilterEvent event) {
		// Set<Object> newFilteredItems = FilterUtil.filter(entityCollection.getFilteredElementIDs(),
		// event.getFilter());

		AttributeFilterCommand c = new AttributeFilterCommand(this, event.getFilter(), ESetOperation.INTERSECTION,
				relationshipExplorer.getHistory());
		c.execute();
		relationshipExplorer.getHistory().addHistoryCommand(c, Color.LIGHT_BLUE);
	}

	// @Override
	// public void onSelectionChanged(GLElementList list) {
	// Set<Object> broadcastIDs = new HashSet<>();
	// Set<Object> elementIDs = new HashSet<>();
	// fillSelectedElementAndBroadcastIDs(elementIDs, broadcastIDs, itemList.getSelectedElements());
	//
	// // SelectionBasedHighlightOperation o = new SelectionBasedHighlightOperation(elementIDs, broadcastIDs,
	// // relationshipExplorer);
	// // o.execute(this);
	// // relationshipExplorer.getHistory().addColumnOperation(this, o);
	// }

	// @Override
	// public void onHighlightChanged(GLElementList list) {
	// Set<Object> broadcastIDs = new HashSet<>();
	// Set<Object> elementIDs = new HashSet<>();
	// fillSelectedElementAndBroadcastIDs(elementIDs, broadcastIDs, itemList.getHighlightElements());
	// highlightElementIDs = elementIDs;
	//
	// relationshipExplorer.applyIDMappingUpdate(new MappingHighlightUpdateOperation(broadcastIDs, this), false);
	// }

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

		relationshipExplorer.getHistory().addHistoryCommand(c, Color.SELECTION_ORANGE);

		// entityCollection.setSelectedItems(elementIDs, this);
		//
		// relationshipExplorer.applyIDMappingUpdate(new MappingSelectionUpdateOperation(
		// getBroadcastingIDsFromElementIDs(elementIDs), this), true);
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

		relationshipExplorer.applyIDMappingUpdate(new MappingHighlightUpdateOperation(
				getBroadcastingIDsFromElementIDs(elementIDs), this));

	}

	// private void fillSelectedElementAndBroadcastIDs(Set<Object> selectedElementIDs, Set<Object> selectedBroadcastIDs,
	// Set<GLElement> selectedElements) {
	// for (GLElement el : selectedElements) {
	// Object elementID = mapIDToElement.inverse().get(el);
	// selectedElementIDs.add(elementID);
	// selectedBroadcastIDs.addAll(getBroadcastingIDsFromElementID(elementID));
	// }
	// }

	// @Override
	// public void setFilteredItems(Set<Object> elementIDs, IEntityRepresentation srcRep) {
	// mapFilteredElements = new HashMap<>(elementIDs.size());
	// for (Entry<Object, GLElement> entry : mapIDToElement.entrySet()) {
	//
	// GLElement element = entry.getValue();
	// boolean visible = false;
	//
	// if (elementIDs.contains(entry.getKey())) {
	// visible = true;
	// // itemList.show(element);
	// if (!itemList.hasElement(element)) {
	// itemList.add(element);
	// itemList.asGLElement().relayout();
	// }
	// mapFilteredElements.put(entry.getKey(), entry.getValue());
	// }
	//
	// if (!visible) {
	// itemList.removeElement(element);
	// // itemList.hide(element);
	// itemList.asGLElement().relayout();
	// }
	//
	// }
	// updateSelections();
	// }

	// public void showAllItems() {
	// mapFilteredElements.clear();
	// for (Entry<Object, GLElement> entry : mapIDToElement.entrySet()) {
	//
	// GLElement element = entry.getValue();
	// // itemList.show(element);
	// if (!itemList.hasElement(element)) {
	// itemList.add(element);
	// itemList.asGLElement().relayout();
	// }
	// mapFilteredElements.put(entry.getKey(), entry.getValue());
	// }
	// }

	// @Override
	// public void setSelectedItems(Set<Object> elementIDs, IEntityRepresentation srcRep) {
	// selectedElementIDs = elementIDs;
	// updateSelections();
	// }

	// protected void updateSelections() {
	// itemList.clearSelection();
	//
	// for (Object elementID : selectedElementIDs) {
	// GLElement element = mapIDToElement.get(elementID);
	// if (element != null) {
	// itemList.addToSelection(element);
	// }
	// }
	// }

	// @Override
	// public void setHighlightItems(Set<Object> elementIDs, IEntityRepresentation srcRep) {
	// if (srcRep == this)
	// highlightElementIDs = elementIDs;
	// itemList.clearHighlight();
	//
	// for (Object elementID : elementIDs) {
	// GLElement element = mapIDToElement.get(elementID);
	// if (element != null) {
	// itemList.addToHighlight(element);
	// }
	// }
	// }
	//
	// @Override
	// @SuppressWarnings("null")
	// public void updateSelectionMappings(IEntityRepresentation srcRep) {
	//
	// IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(getBroadcastingIDType());
	// IIDTypeMapper<Object, Object> mapper = mappingManager.getIDTypeMapper(srcRep.getCollection()
	// .getBroadcastingIDType(), getBroadcastingIDType());
	// List<MappingType> path = mapper.getPath();
	//
	// AEntityColumn foreignColumn = getNearestMappingColumn(path);
	//
	// int maxMappedElements = Integer.MIN_VALUE;
	// for (Entry<Object, GLElement> entry : mapFilteredElements.entrySet()) {
	//
	// @SuppressWarnings("unchecked")
	// KeyBasedGLElementContainer<GLElement> row = (KeyBasedGLElementContainer<GLElement>) entry.getValue();
	// @SuppressWarnings("unchecked")
	// KeyBasedGLElementContainer<SimpleBarRenderer> mappingRenderer = (KeyBasedGLElementContainer<SimpleBarRenderer>)
	// row
	// .getElement(MAPPING_KEY);
	//
	// if (srcRep == this) {
	// if (mappingRenderer != null)
	// mappingRenderer.setVisibility(EVisibility.NONE);
	// } else {
	// if (mappingRenderer == null) {
	// mappingRenderer = createLayeredBarRenderer();
	// row.setElement(MAPPING_KEY, mappingRenderer);
	// }
	// mappingRenderer.setVisibility(EVisibility.PICKABLE);
	// fillMappedElementCounts(row, foreignColumn, mappingRenderer);
	// int numMappedElements = (int) (mappingRenderer.getElement(ALL_ELEMENTS_KEY)).getValue();
	// if (numMappedElements > maxMappedElements)
	// maxMappedElements = numMappedElements;
	// }
	// }
	// boolean mappingHeaderExists = header.hasElement(MAPPING_KEY);
	//
	// if (srcRep == this) {
	// // itemList.setHighlightSelections(true);
	// if (mappingHeaderExists) {
	// header.getElement(MAPPING_KEY).setVisibility(EVisibility.NONE);
	// }
	// return;
	// }
	// if (!mappingHeaderExists) {
	// GLElement mappingHeader = new GLElement(GLRenderers.drawText(foreignColumn.getLabel(), VAlign.CENTER));
	// mappingHeader.setSize(80, 12);
	// header.setElement(MAPPING_KEY, mappingHeader);
	// } else {
	// GLElement mappingHeader = header.getElement(MAPPING_KEY);
	// mappingHeader.setVisibility(EVisibility.PICKABLE);
	// mappingHeader.setRenderer(GLRenderers.drawText(foreignColumn.getLabel(), VAlign.CENTER));
	// }
	//
	// // itemList.setHighlightSelections(false);
	//
	// for (Entry<Object, GLElement> entry : mapFilteredElements.entrySet()) {
	//
	// @SuppressWarnings("unchecked")
	// KeyBasedGLElementContainer<GLElement> row = (KeyBasedGLElementContainer<GLElement>) entry.getValue();
	// @SuppressWarnings("unchecked")
	// KeyBasedGLElementContainer<SimpleBarRenderer> mappingRenderer = (KeyBasedGLElementContainer<SimpleBarRenderer>)
	// row
	// .getElement(MAPPING_KEY);
	// SimpleBarRenderer barRenderer = mappingRenderer.getElement(ALL_ELEMENTS_KEY);
	// barRenderer.setNormalizedValue(barRenderer.getValue() / maxMappedElements);
	// barRenderer = mappingRenderer.getElement(FILTERED_ELEMENTS_KEY);
	// barRenderer.setNormalizedValue(barRenderer.getValue() / maxMappedElements);
	// barRenderer = mappingRenderer.getElement(SELECTED_ELEMENTS_KEY);
	// barRenderer.setNormalizedValue(barRenderer.getValue() / maxMappedElements);
	// }
	//
	// @SuppressWarnings("unchecked")
	// ComparatorChain<GLElement> chain = new ComparatorChain<>(Lists.newArrayList(SELECTED_ELEMENTS_COMPARATOR,
	// SELECTED_FOREIGN_ELEMENTS_COMPARATOR, FILTERED_FOREIGN_ELEMENTS_COMPARATOR,
	// ALL_FOREIGN_ELEMENTS_COMPARATOR, getDefaultElementComparator()));
	// sort(chain);
	// }

	// public void hideMappings() {
	// boolean mappingHeaderExists = header.hasElement(MAPPING_KEY);
	//
	// // itemList.setHighlightSelections(true);
	// if (mappingHeaderExists) {
	// header.getElement(MAPPING_KEY).setVisibility(EVisibility.NONE);
	// }
	//
	// for (Entry<Object, GLElement> entry : mapFilteredElements.entrySet()) {
	// @SuppressWarnings("unchecked")
	// KeyBasedGLElementContainer<GLElement> row = (KeyBasedGLElementContainer<GLElement>) entry.getValue();
	// @SuppressWarnings("unchecked")
	// KeyBasedGLElementContainer<SimpleBarRenderer> mappingRenderer = (KeyBasedGLElementContainer<SimpleBarRenderer>)
	// row
	// .getElement(MAPPING_KEY);
	// if (mappingRenderer != null)
	// mappingRenderer.setVisibility(EVisibility.NONE);
	// }
	//
	// }
	//
	// public void sort(Comparator<GLElement> comparator) {
	// if (comparator == null)
	// return;
	// this.currentComparator = comparator;
	// itemList.sortBy(comparator);
	// }

	// protected void fillMappedElementCounts(GLElement element, AEntityColumn foreignColumn,
	// KeyBasedGLElementContainer<SimpleBarRenderer> layeredBars) {
	// Set<Object> broadcastIDs = getBroadcastingIDsFromElementID(mapIDToElement.inverse().get(element));
	//
	// int numSelectedElements = 0;
	// int numFilteredElements = 0;
	// Set<Object> foreignMappedElementIDs = foreignColumn.getElementIDsFromForeignIDs(broadcastIDs,
	// getBroadcastingIDType());
	// Set<Object> foreignFilteredElements = foreignColumn.getFilteredElementIDs();
	// Set<Object> foreignSelectedElements = foreignColumn.getSelectedElementIDs();
	// for (Object elementID : foreignMappedElementIDs) {
	// if (foreignFilteredElements.contains(elementID)) {
	// numFilteredElements++;
	// // Only filtered elements can be selected
	// if (foreignSelectedElements.contains(elementID))
	// numSelectedElements++;
	// }
	// }
	// layeredBars.getElement(ALL_ELEMENTS_KEY).setValue(foreignMappedElementIDs.size());
	// layeredBars.getElement(FILTERED_ELEMENTS_KEY).setValue(numFilteredElements);
	// layeredBars.getElement(SELECTED_ELEMENTS_KEY).setValue(numSelectedElements);
	// }

	@Override
	public Set<Object> getFilteredElementIDs() {
		return mapIDToFilteredItems.keySet();
	}

	// protected AEntityColumn getNearestMappingColumn(List<MappingType> path) {
	// if (path == null) {
	// AEntityColumn foreignColumn = getForeignColumnWithMappingIDType(getMappingIDType());
	// if (foreignColumn == null)
	// foreignColumn = getForeignColumnWithBroadcastIDType(getBroadcastingIDType());
	// if (foreignColumn != null)
	// return foreignColumn;
	// } else {
	// for (int i = path.size() - 1; i >= 0; i--) {
	// AEntityColumn foreignColumn = getForeignColumnWithMappingIDType(path.get(i).getFromIDType());
	// if (foreignColumn != null)
	// return foreignColumn;
	// }
	//
	// }
	// return this;
	// }

	// @Override
	// public Set<Object> getAllElementIDs() {
	// return Collections.unmodifiableSet(mapIDToElement.keySet());
	// }

	/**
	 * @return the relationshipExplorer, see {@link #relationshipExplorer}
	 */
	public RelationshipExplorerElement getRelationshipExplorer() {
		return relationshipExplorer;
	}

	@Override
	public Set<Object> getBroadcastingIDsFromElementIDs(Collection<Object> elementIDs) {
		Set<Object> broadcastIDs = new HashSet<>();
		for (Object elementID : elementIDs) {
			broadcastIDs.addAll(getBroadcastingIDsFromElementID(elementID));
		}

		return broadcastIDs;
	}

	// public void updateSorting() {
	// sort(currentComparator);
	// }

	@Override
	public GLElement getSummaryElement(NestableItem parentItem, Set<NestableItem> items, NestableItem summaryItem,
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
		column.addContextMenuItems(getContextMenuItems());

		if (parentColumn == null) {
			for (Object id : entityCollection.getFilteredElementIDs()) {
				ScoreElement element = createElement(id, null);
				if (element != null) {
					addItem(element, id, column, null);
				}
			}
		} else {
			for (Object id : entityCollection.getFilteredElementIDs()) {
				Set<Object> foreignElementIDs = parentColumn.getColumnModel().getElementIDsFromForeignIDs(
						getBroadcastingIDsFromElementID(id), getBroadcastingIDType());
				Set<NestableItem> parentItems = parentColumn.getColumnModel().getItems(foreignElementIDs);

				boolean add = true;
				for (NestableItem parentItem : parentItems) {
					if (parentItem.getParentItem() != null) {
						add = hasParentItemElementMapping(parentItem.getParentItem(), foreignElementIDs);
					}

					if (add) {
						ScoreElement element = createElement(id, parentItem);
						if (element != null) {
							addItem(element, id, column, parentItem);
						}
					}
				}
			}
		}

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
		if (srcRep == this)
			return;
		if (srcRep instanceof IColumnModel) {
			if (!column.isChild((IColumnModel) srcRep)) {
				column.updateSummaryItems(EUpdateCause.SELECTION);
				updateSorting();
			}
		} else {
			column.updateSummaryItems(EUpdateCause.SELECTION);
			updateSorting();
		}
	}

	@Override
	public void highlightChanged(Set<Object> highlightElementIDs, IEntityRepresentation srcRep) {
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
				ScoreElement element = createElement(elementID, null);
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
				Set<Object> foreignElementIDs = parentColumn.getColumnModel().getElementIDsFromForeignIDs(
						getBroadcastingIDsFromElementID(id), getBroadcastingIDType());
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
						ScoreElement element = createElement(id, parentItem);
						if (element != null) {
							addItem(element, id, column, parentItem);
						}
					}
				}
			}

		}

		updateChildColumnFilters(srcRep);
		column.updateSummaryItems(EUpdateCause.FILTER);
		column.getHeader().updateItemCounts();
		updateScores();

		if (srcRep == this)
			return;
		if (srcRep instanceof IColumnModel) {
			if (!column.isChild((IColumnModel) srcRep)) {
				column.updateSummaryItems(EUpdateCause.FILTER);
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
			Set<Object> parentBCIDs = parentColumn.getColumnModel().getBroadcastingIDsFromElementIDs(
					parentItem.getElementData());
			Set<Object> mappedElementIDs = getElementIDsFromForeignIDs(parentBCIDs, parentColumn.getColumnModel()
					.getBroadcastingIDType());
			if (mappedElementIDs.size() > maxParentMappings)
				maxParentMappings = mappedElementIDs.size();
		}
	}

	public void addSummaryItemFactory(ISummaryItemFactory factory) {
		summaryItemFactories.add(factory);
	}

	public void setSummaryItemFactory(ISummaryItemFactory factory) {
		summaryItemFactory = factory;
		if (column != null)
			column.updateSummaryItems(EUpdateCause.OTHER);
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
	public void sortBy(Comparator<NestableItem> comparator) {
		currentComparator = comparator;
		if (initialized)
			column.sortBy(comparator);
	}

	@Override
	public Comparator<NestableItem> getCurrentComparator() {
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
	public Set<Comparator<NestableItem>> getComparators() {
		return baseComparators;
	}

	public void addComparator(Comparator<NestableItem> comparator) {
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
		relationshipExplorer.getHistory().addHistoryCommand(c, Color.MAGENTA);

	}

	protected ScoreElement createElement(Object elementID, NestableItem parentItem) {
		GLElement element = newElement(elementID);
		ScoreElement scoreElement = new ScoreElement(element);
		if (scoreProvider != null) {
			scoreElement.showScore();
			// if (parentItem == null) {
			// scoreElement.setScore(scoreProvider.getScore(elementID, entityCollection, null, null));
			// } else {
			// scoreElement.setScore(scoreProvider.getScore(elementID, entityCollection, parentItem.getElementData()
			// .iterator().next(), parentColumn.getColumnModel().getCollection()));
			// }
		}
		return scoreElement;
	}

	protected abstract GLElement newElement(Object elementID);

	public abstract void showDetailView();

}
