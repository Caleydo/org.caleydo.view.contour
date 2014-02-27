/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import gleem.linalg.Vec2f;

import java.net.URL;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.id.MappingType;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorBrewer;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElement.EVisibility;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.column.operation.ESetOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.MappingHighlightUpdateOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.MappingSelectionUpdateOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.ShowDetailOperation;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.ContextMenuCommandEvent;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.FilterCommand;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.IContextMenuCommand;
import org.caleydo.view.relationshipexplorer.ui.list.GLElementList;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;
import org.caleydo.view.relationshipexplorer.ui.list.NestableColumn;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
import org.caleydo.view.relationshipexplorer.ui.util.KeyBasedGLElementContainer;
import org.caleydo.view.relationshipexplorer.ui.util.SimpleBarRenderer;
import org.eclipse.nebula.widgets.nattable.util.ComparatorChain;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public abstract class AEntityColumn implements ILabeled, IEntityCollection, IColumnModel, IEntityRepresentation {
	protected static final int HEADER_HEIGHT = 20;
	protected static final int HEADER_BODY_SPACING = 5;

	protected static final Integer DATA_KEY = Integer.valueOf(0);
	protected static final Integer MAPPING_KEY = Integer.valueOf(1);

	protected static final Integer SELECTED_ELEMENTS_KEY = Integer.valueOf(2);
	protected static final Integer FILTERED_ELEMENTS_KEY = Integer.valueOf(3);
	protected static final Integer ALL_ELEMENTS_KEY = Integer.valueOf(4);

	protected static final URL FILTER_ICON = AEntityColumn.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/filter.png");

	// -----------------

	protected final IEntityCollection entityCollection;
	protected Map<Object, Set<NestableItem>> mapIDToFilteredItems = new HashMap<>();
	protected NestableColumn column;
	protected NestableColumn parentColumn;

	protected int maxParentMappings = 0;

	// -----------------

	protected KeyBasedGLElementContainer<GLElement> header;
	protected GLElementContainer buttonBar;

	protected GLElementList itemList = new GLElementList();
	protected BiMap<Object, GLElement> mapIDToElement = HashBiMap.create();

	protected Set<Object> selectedElementIDs = new HashSet<>();
	protected Set<Object> highlightElementIDs = new HashSet<>();
	protected Map<Object, GLElement> mapFilteredElements = new HashMap<>();
	protected RelationshipExplorerElement relationshipExplorer;

	protected Comparator<GLElement> currentComparator;

	protected static class MappingBarComparator implements Comparator<GLElement> {

		private final Object key;

		public MappingBarComparator(Object key) {
			this.key = key;
		}

		@Override
		public int compare(GLElement el1, GLElement el2) {
			@SuppressWarnings("unchecked")
			KeyBasedGLElementContainer<SimpleBarRenderer> layeredBars1 = (KeyBasedGLElementContainer<SimpleBarRenderer>) ((KeyBasedGLElementContainer<GLElement>) el1)
					.getElement(MAPPING_KEY);
			@SuppressWarnings("unchecked")
			KeyBasedGLElementContainer<SimpleBarRenderer> layeredBars2 = (KeyBasedGLElementContainer<SimpleBarRenderer>) ((KeyBasedGLElementContainer<GLElement>) el2)
					.getElement(MAPPING_KEY);

			return Float.compare(layeredBars2.getElement(key).getNormalizedValue(), layeredBars1.getElement(key)
					.getNormalizedValue());
		}
	}

	public final Comparator<NestableItem> SELECTED_ITEMS_COMPARATOR = new Comparator<NestableItem>() {

		@Override
		public int compare(NestableItem o1, NestableItem o2) {
			if (o1.isSelected() && !o2.isSelected()) {
				return -1;
			}
			if (!o1.isSelected() && o2.isSelected()) {
				return 1;
			}

			return 0;
		}
	};

	public static final MappingBarComparator SELECTED_FOREIGN_ELEMENTS_COMPARATOR = new MappingBarComparator(
			SELECTED_ELEMENTS_KEY);
	public static final MappingBarComparator FILTERED_FOREIGN_ELEMENTS_COMPARATOR = new MappingBarComparator(
			FILTERED_ELEMENTS_KEY);
	public static final MappingBarComparator ALL_FOREIGN_ELEMENTS_COMPARATOR = new MappingBarComparator(
			ALL_ELEMENTS_KEY);

	public final Comparator<GLElement> SELECTED_ELEMENTS_COMPARATOR = new Comparator<GLElement>() {

		@Override
		public int compare(GLElement o1, GLElement o2) {
			boolean o1Selected = itemList.isSelected(o1);
			boolean o2Selected = itemList.isSelected(o2);

			if (o1Selected && !o2Selected) {
				return -1;
			}
			if (!o1Selected && o2Selected) {
				return 1;
			}

			return 0;
		}
	};

	public AEntityColumn(IEntityCollection entityCollection, RelationshipExplorerElement relationshipExplorer) {
		// super(GLLayouts.flowVertical(HEADER_BODY_SPACING));
		this.entityCollection = entityCollection;
		entityCollection.addEntityRepresentation(this);
		this.relationshipExplorer = relationshipExplorer;

		// header = new KeyBasedGLElementContainer<>(GLLayouts.sizeRestrictiveFlowHorizontal(2));
		// header.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(header, 2, GLPadding.ZERO));
		// header.setSize(Float.NaN, HEADER_HEIGHT);
		// header.setVisibility(EVisibility.PICKABLE);
		// buttonBar = new GLElementContainer(GLLayouts.sizeRestrictiveFlowHorizontal(1));
		//
		// GLElementContainer headerContainer = new GLElementContainer();
		// headerContainer.setLayout(GLLayouts.LAYERS);
		// headerContainer.add(header);
		// headerContainer.add(buttonBar);
		// buttonBar.setzDelta(0.1f);
		// buttonBar.setVisibility(EVisibility.HIDDEN);
		// headerContainer.setSize(Float.NaN, HEADER_HEIGHT);
		// headerContainer.setVisibility(EVisibility.PICKABLE);
		//
		// add(headerContainer);
		// add(itemList.asGLElement());
		//
		// headerContainer.onPick(new APickingListener() {
		// @Override
		// protected void doubleClicked(Pick pick) {
		// @SuppressWarnings("unchecked")
		// ComparatorChain<GLElement> chain = new ComparatorChain<>(Lists.newArrayList(
		// SELECTED_ELEMENTS_COMPARATOR, getDefaultElementComparator()));
		// ColumnSortingCommand c = new ColumnSortingCommand(AEntityColumn.this, chain);
		// c.execute();
		// AEntityColumn.this.relationshipExplorer.getHistory().addHistoryCommand(c,
		// ColorBrewer.Purples.getColors(3).get(1));
		// }
		//
		// @Override
		// protected void mouseOver(Pick pick) {
		// buttonBar.setVisibility(EVisibility.PICKABLE);
		// }
		//
		// @Override
		// protected void mouseOut(Pick pick) {
		// buttonBar.setVisibility(EVisibility.HIDDEN);
		// }
		// });

	}

	protected GLButton addHeaderButton(URL iconURL) {
		GLButton button = new GLButton(EButtonMode.BUTTON);
		button.setRenderer(GLRenderers.fillImage(iconURL));
		button.setSize(16, 16);
		buttonBar.add(button);
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
		AContextMenuItem replaceFilterItem = new GenericContextMenuItem("Replace", new ContextMenuCommandEvent(
				new FilterCommand(ESetOperation.REPLACE, this, relationshipExplorer)).to(this));
		AContextMenuItem andFilterITem = new GenericContextMenuItem("Reduce", new ContextMenuCommandEvent(
				new FilterCommand(ESetOperation.INTERSECTION, this, relationshipExplorer)).to(this));
		AContextMenuItem orFilterITem = new GenericContextMenuItem("Add", new ContextMenuCommandEvent(
				new FilterCommand(ESetOperation.UNION, this, relationshipExplorer)).to(this));
		AContextMenuItem detailItem = new GenericContextMenuItem("Show in Detail", new ContextMenuCommandEvent(
				new IContextMenuCommand() {
					@Override
					public void execute() {
						ShowDetailOperation o = new ShowDetailOperation(AEntityColumn.this);
						o.execute();
						relationshipExplorer.getHistory().addHistoryCommand(o, ColorBrewer.Greens.getColors(3).get(1));
					}
				}).to(this));

		return Lists.newArrayList(replaceFilterItem, andFilterITem, orFilterITem, detailItem);
	}

	protected void addElement(GLElement element, Object elementID) {

		KeyBasedGLElementContainer<GLElement> row = new KeyBasedGLElementContainer<>(
				GLLayouts.sizeRestrictiveFlowHorizontal(2));
		row.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(row, 2, GLPadding.ZERO));
		row.setElement(DATA_KEY, element);
		mapIDToElement.put(elementID, row);
		itemList.add(row);
	}

	protected void addItem(GLElement element, Object elementID, NestableColumn column, NestableItem parentItem) {

		NestableItem item = column.addElement(element, parentItem);
		Set<NestableItem> items = mapIDToFilteredItems.get(elementID);
		if (items == null) {
			items = new HashSet<>();
			mapIDToFilteredItems.put(elementID, items);
		}
		items.add(item);
		item.setElementData(Sets.newHashSet(elementID));
	}

	protected KeyBasedGLElementContainer<SimpleBarRenderer> createLayeredBarRenderer() {
		KeyBasedGLElementContainer<SimpleBarRenderer> barLayerRenderer = new KeyBasedGLElementContainer<>(
				GLLayouts.LAYERS);

		// barLayerRenderer.setSize(80, Float.NaN);
		barLayerRenderer.setMinSizeProvider(GLMinSizeProviders.createLayeredMinSizeProvider(barLayerRenderer));
		barLayerRenderer.setElement(ALL_ELEMENTS_KEY, createDefaultBarRenderer(Color.LIGHT_GRAY, 0.1f));
		barLayerRenderer.setElement(FILTERED_ELEMENTS_KEY, createDefaultBarRenderer(Color.GRAY, 0.2f));
		barLayerRenderer.setElement(SELECTED_ELEMENTS_KEY,
				createDefaultBarRenderer(SelectionType.SELECTION.getColor(), 0.3f));
		// barLayerRenderer.setRenderer(GLRenderers.drawRect(Color.BLUE));
		return barLayerRenderer;
	}

	protected SimpleBarRenderer createDefaultBarRenderer(Color color, float zDelta) {
		SimpleBarRenderer renderer = new SimpleBarRenderer(0, true);
		renderer.setMinSize(new Vec2f(80, 16));
		// renderer.setSize(80, Float.NaN);
		renderer.setColor(color);
		renderer.setBarWidth(12);
		renderer.setzDelta(zDelta);
		return renderer;
	}

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

	/**
	 * @return the selectedElementIDs, see {@link #selectedElementIDs}
	 */
	@Override
	public Set<Object> getSelectedElementIDs() {
		return selectedElementIDs;
	}

	@Override
	public Set<Object> getHighlightElementIDs() {
		return highlightElementIDs;
	}

	@ListenTo(sendToMe = true)
	public void onHandleContextMenuOperation(ContextMenuCommandEvent event) {
		event.getCommand().execute();

		// applyFilter(event.type, elementIDs);
		// triggerIDUpdate(broadcastIDs, event.type);
		// triggerIDUpdate(broadcastIDs, EUpdateType.SELECTION);
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

		entityCollection.setSelectedItems(elementIDs, this);

		relationshipExplorer.applyIDMappingUpdate(new MappingSelectionUpdateOperation(
				getBroadcastingIDsFromElementIDs(elementIDs), this), true);
	}

	@Override
	public void onHighlightChanged(NestableColumn column) {
		Set<Object> elementIDs = new HashSet<>();
		for (NestableItem item : column.getHighlightedItems()) {
			Set<Object> ids = item.getElementData();
			if (ids != null)
				elementIDs.addAll(ids);
		}

		entityCollection.setHighlightItems(elementIDs, this);

		relationshipExplorer.applyIDMappingUpdate(new MappingHighlightUpdateOperation(
				getBroadcastingIDsFromElementIDs(elementIDs), this), false);

	}

	private void fillSelectedElementAndBroadcastIDs(Set<Object> selectedElementIDs, Set<Object> selectedBroadcastIDs,
			Set<GLElement> selectedElements) {
		for (GLElement el : selectedElements) {
			Object elementID = mapIDToElement.inverse().get(el);
			selectedElementIDs.add(elementID);
			selectedBroadcastIDs.addAll(getBroadcastingIDsFromElementID(elementID));
		}
	}

	@Override
	public void setFilteredItems(Set<Object> elementIDs, IEntityRepresentation srcRep) {
		mapFilteredElements = new HashMap<>(elementIDs.size());
		for (Entry<Object, GLElement> entry : mapIDToElement.entrySet()) {

			GLElement element = entry.getValue();
			boolean visible = false;

			if (elementIDs.contains(entry.getKey())) {
				visible = true;
				// itemList.show(element);
				if (!itemList.hasElement(element)) {
					itemList.add(element);
					itemList.asGLElement().relayout();
				}
				mapFilteredElements.put(entry.getKey(), entry.getValue());
			}

			if (!visible) {
				itemList.removeElement(element);
				// itemList.hide(element);
				itemList.asGLElement().relayout();
			}

		}
		updateSelections();
	}

	public void showAllItems() {
		mapFilteredElements.clear();
		for (Entry<Object, GLElement> entry : mapIDToElement.entrySet()) {

			GLElement element = entry.getValue();
			// itemList.show(element);
			if (!itemList.hasElement(element)) {
				itemList.add(element);
				itemList.asGLElement().relayout();
			}
			mapFilteredElements.put(entry.getKey(), entry.getValue());
		}
	}

	@Override
	public void setSelectedItems(Set<Object> elementIDs, IEntityRepresentation srcRep) {
		selectedElementIDs = elementIDs;
		updateSelections();
	}

	protected void updateSelections() {
		itemList.clearSelection();

		for (Object elementID : selectedElementIDs) {
			GLElement element = mapIDToElement.get(elementID);
			if (element != null) {
				itemList.addToSelection(element);
			}
		}
	}

	@Override
	public void setHighlightItems(Set<Object> elementIDs, IEntityRepresentation srcRep) {
		highlightElementIDs = elementIDs;
		itemList.clearHighlight();

		for (Object elementID : elementIDs) {
			GLElement element = mapIDToElement.get(elementID);
			if (element != null) {
				itemList.addToHighlight(element);
			}
		}
	}

	@Override
	@SuppressWarnings("null")
	public void updateSelectionMappings(IEntityRepresentation srcRep) {

		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(getBroadcastingIDType());
		IIDTypeMapper<Object, Object> mapper = mappingManager.getIDTypeMapper(srcRep.getCollection()
				.getBroadcastingIDType(), getBroadcastingIDType());
		List<MappingType> path = mapper.getPath();

		AEntityColumn foreignColumn = getNearestMappingColumn(path);

		int maxMappedElements = Integer.MIN_VALUE;
		for (Entry<Object, GLElement> entry : mapFilteredElements.entrySet()) {

			@SuppressWarnings("unchecked")
			KeyBasedGLElementContainer<GLElement> row = (KeyBasedGLElementContainer<GLElement>) entry.getValue();
			@SuppressWarnings("unchecked")
			KeyBasedGLElementContainer<SimpleBarRenderer> mappingRenderer = (KeyBasedGLElementContainer<SimpleBarRenderer>) row
					.getElement(MAPPING_KEY);

			if (srcRep == this) {
				if (mappingRenderer != null)
					mappingRenderer.setVisibility(EVisibility.NONE);
			} else {
				if (mappingRenderer == null) {
					mappingRenderer = createLayeredBarRenderer();
					row.setElement(MAPPING_KEY, mappingRenderer);
				}
				mappingRenderer.setVisibility(EVisibility.PICKABLE);
				fillMappedElementCounts(row, foreignColumn, mappingRenderer);
				int numMappedElements = (int) (mappingRenderer.getElement(ALL_ELEMENTS_KEY)).getValue();
				if (numMappedElements > maxMappedElements)
					maxMappedElements = numMappedElements;
			}
		}
		boolean mappingHeaderExists = header.hasElement(MAPPING_KEY);

		if (srcRep == this) {
			// itemList.setHighlightSelections(true);
			if (mappingHeaderExists) {
				header.getElement(MAPPING_KEY).setVisibility(EVisibility.NONE);
			}
			return;
		}
		if (!mappingHeaderExists) {
			GLElement mappingHeader = new GLElement(GLRenderers.drawText(foreignColumn.getLabel(), VAlign.CENTER));
			mappingHeader.setSize(80, 12);
			header.setElement(MAPPING_KEY, mappingHeader);
		} else {
			GLElement mappingHeader = header.getElement(MAPPING_KEY);
			mappingHeader.setVisibility(EVisibility.PICKABLE);
			mappingHeader.setRenderer(GLRenderers.drawText(foreignColumn.getLabel(), VAlign.CENTER));
		}

		// itemList.setHighlightSelections(false);

		for (Entry<Object, GLElement> entry : mapFilteredElements.entrySet()) {

			@SuppressWarnings("unchecked")
			KeyBasedGLElementContainer<GLElement> row = (KeyBasedGLElementContainer<GLElement>) entry.getValue();
			@SuppressWarnings("unchecked")
			KeyBasedGLElementContainer<SimpleBarRenderer> mappingRenderer = (KeyBasedGLElementContainer<SimpleBarRenderer>) row
					.getElement(MAPPING_KEY);
			SimpleBarRenderer barRenderer = mappingRenderer.getElement(ALL_ELEMENTS_KEY);
			barRenderer.setNormalizedValue(barRenderer.getValue() / maxMappedElements);
			barRenderer = mappingRenderer.getElement(FILTERED_ELEMENTS_KEY);
			barRenderer.setNormalizedValue(barRenderer.getValue() / maxMappedElements);
			barRenderer = mappingRenderer.getElement(SELECTED_ELEMENTS_KEY);
			barRenderer.setNormalizedValue(barRenderer.getValue() / maxMappedElements);
		}

		@SuppressWarnings("unchecked")
		ComparatorChain<GLElement> chain = new ComparatorChain<>(Lists.newArrayList(SELECTED_ELEMENTS_COMPARATOR,
				SELECTED_FOREIGN_ELEMENTS_COMPARATOR, FILTERED_FOREIGN_ELEMENTS_COMPARATOR,
				ALL_FOREIGN_ELEMENTS_COMPARATOR, getDefaultElementComparator()));
		sort(chain);
	}

	public void hideMappings() {
		boolean mappingHeaderExists = header.hasElement(MAPPING_KEY);

		// itemList.setHighlightSelections(true);
		if (mappingHeaderExists) {
			header.getElement(MAPPING_KEY).setVisibility(EVisibility.NONE);
		}

		for (Entry<Object, GLElement> entry : mapFilteredElements.entrySet()) {
			@SuppressWarnings("unchecked")
			KeyBasedGLElementContainer<GLElement> row = (KeyBasedGLElementContainer<GLElement>) entry.getValue();
			@SuppressWarnings("unchecked")
			KeyBasedGLElementContainer<SimpleBarRenderer> mappingRenderer = (KeyBasedGLElementContainer<SimpleBarRenderer>) row
					.getElement(MAPPING_KEY);
			if (mappingRenderer != null)
				mappingRenderer.setVisibility(EVisibility.NONE);
		}

	}

	public void sort(Comparator<GLElement> comparator) {
		if (comparator == null)
			return;
		this.currentComparator = comparator;
		itemList.sortBy(comparator);
	}

	protected void fillMappedElementCounts(GLElement element, AEntityColumn foreignColumn,
			KeyBasedGLElementContainer<SimpleBarRenderer> layeredBars) {
		Set<Object> broadcastIDs = getBroadcastingIDsFromElementID(mapIDToElement.inverse().get(element));

		int numSelectedElements = 0;
		int numFilteredElements = 0;
		Set<Object> foreignMappedElementIDs = foreignColumn.getElementIDsFromForeignIDs(broadcastIDs,
				getBroadcastingIDType());
		Set<Object> foreignFilteredElements = foreignColumn.getFilteredElementIDs();
		Set<Object> foreignSelectedElements = foreignColumn.getSelectedElementIDs();
		for (Object elementID : foreignMappedElementIDs) {
			if (foreignFilteredElements.contains(elementID)) {
				numFilteredElements++;
				// Only filtered elements can be selected
				if (foreignSelectedElements.contains(elementID))
					numSelectedElements++;
			}
		}
		layeredBars.getElement(ALL_ELEMENTS_KEY).setValue(foreignMappedElementIDs.size());
		layeredBars.getElement(FILTERED_ELEMENTS_KEY).setValue(numFilteredElements);
		layeredBars.getElement(SELECTED_ELEMENTS_KEY).setValue(numSelectedElements);
	}

	@Override
	public Set<Object> getFilteredElementIDs() {
		return mapFilteredElements.keySet();
	}

	protected AEntityColumn getNearestMappingColumn(List<MappingType> path) {
		if (path == null) {
			AEntityColumn foreignColumn = getForeignColumnWithMappingIDType(getMappingIDType());
			if (foreignColumn == null)
				foreignColumn = getForeignColumnWithBroadcastIDType(getBroadcastingIDType());
			if (foreignColumn != null)
				return foreignColumn;
		} else {
			for (int i = path.size() - 1; i >= 0; i--) {
				AEntityColumn foreignColumn = getForeignColumnWithMappingIDType(path.get(i).getFromIDType());
				if (foreignColumn != null)
					return foreignColumn;
			}

		}
		return this;
	}

	@Override
	public Set<Object> getAllElementIDs() {
		return Collections.unmodifiableSet(mapIDToElement.keySet());
	}

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

	public void updateSorting() {
		sort(currentComparator);
	}

	@Override
	public GLElement getSummaryElement(Set<NestableItem> items) {
		if (parentColumn == null)
			return new GLElement(GLRenderers.drawText("Summary of " + items.size()));

		Set<Object> parentElementIDs = new HashSet<>();
		int numSelections = 0;
		for (NestableItem item : items) {
			parentElementIDs.addAll(item.getParentItem().getElementData());
			if (item.isSelected())
				numSelections++;
		}

		Set<Object> parentBCIDs = parentColumn.getColumnModel().getBroadcastingIDsFromElementIDs(parentElementIDs);
		Set<Object> mappedElementIDs = getElementIDsFromForeignIDs(parentBCIDs, parentColumn.getColumnModel()
				.getBroadcastingIDType());

		KeyBasedGLElementContainer<SimpleBarRenderer> layeredRenderer = createLayeredBarRenderer();
		// layeredRenderer.setRenderer(GLRenderers.drawRect(Color.RED));
		layeredRenderer.getElement(SELECTED_ELEMENTS_KEY).setValue(numSelections);
		layeredRenderer.getElement(SELECTED_ELEMENTS_KEY).setNormalizedValue((float) numSelections / maxParentMappings);
		layeredRenderer.getElement(FILTERED_ELEMENTS_KEY).setValue(items.size());
		layeredRenderer.getElement(FILTERED_ELEMENTS_KEY).setNormalizedValue((float) items.size() / maxParentMappings);
		layeredRenderer.getElement(ALL_ELEMENTS_KEY).setValue(mappedElementIDs.size());
		layeredRenderer.getElement(ALL_ELEMENTS_KEY).setNormalizedValue(
				(float) mappedElementIDs.size() / maxParentMappings);
		return layeredRenderer;
	}

	@Override
	public void fill(NestableColumn column, NestableColumn parentColumn) {
		this.column = column;
		this.parentColumn = parentColumn;
		column.addContextMenuItems(getContextMenuItems());

		if (parentColumn == null) {
			for (Object id : entityCollection.getFilteredElementIDs()) {
				GLElement element = createElement(id);
				if (element != null) {
					addItem(element, id, column, null);
				}
			}
		} else {
			for (Object id : entityCollection.getFilteredElementIDs()) {
				Set<Object> foreignElementIDs = parentColumn.getColumnModel().getElementIDsFromForeignIDs(
						getBroadcastingIDsFromElementID(id), getBroadcastingIDType());
				Set<NestableItem> parentItems = parentColumn.getColumnModel().getItems(foreignElementIDs);

				for (NestableItem parentItem : parentItems) {
					GLElement element = createElement(id);
					if (element != null) {
						addItem(element, id, column, parentItem);
					}
				}
			}
		}

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
		if (srcRep == this)
			return;
		column.clearSelection();
		for (Object elementID : selectedElementIDs) {
			Set<NestableItem> items = mapIDToFilteredItems.get(elementID);
			if (items != null) {
				for (NestableItem item : items) {
					column.addToSelection(item);
				}
			}
		}
		if (srcRep instanceof IColumnModel) {
			if (!column.isChild((IColumnModel) srcRep)) {
				column.sortBy(getDefaultComparator());
			}
		} else {
			column.sortBy(getDefaultComparator());
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
	public IEntityCollection getCollection() {
		return entityCollection;
	}

	@Override
	public void updateMappings(IEntityRepresentation srcRep) {

		if (!column.isRoot())
			return;

		// mapIDToFilteredItems.clear();

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
			GLElement element = createElement(elementID);
			if (element != null) {
				addItem(element, elementID, column, null);
			}
		}

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
		//
		// if (parentItem != null) {
		// List<NestableItem> childItems = parentItem.getChildItems(column);
		// for (NestableItem item : childItems) {
		// if (item.getElementData().contains(elementID)) {
		// // An item with that is already present -> no need to add
		// return;
		// }
		// }
		// }

		column.updateSummaryItems();
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

	@Override
	public void filterChanged(Set<Object> filteredElementIDs, IEntityRepresentation srcRep) {

		//
		// updateSelections();

	}

	@Override
	public void addEntityRepresentation(IEntityRepresentation rep) {
		// TODO Auto-generated method stub

	}

	@Override
	public void removeEntityRepresentation(IEntityRepresentation rep) {
		// TODO Auto-generated method stub

	}

	// @Override
	// public void setFilteredItems(Set<Object> elementIDs, IEntityRepresentation updateSource) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void setHighlightItems(Set<Object> elementIDs, IEntityRepresentation updateSource) {
	// // TODO Auto-generated method stub
	//
	// }
	//
	// @Override
	// public void setSelectedItems(Set<Object> elementIDs, IEntityRepresentation updateSource) {
	// // TODO Auto-generated method stub
	//
	// }

	protected abstract GLElement createElement(Object elementID);

	protected abstract void setContent();

	public abstract Comparator<GLElement> getDefaultElementComparator();

	// public abstract IDType getBroadcastingIDType();
	//
	// public abstract Set<Object> getBroadcastingIDsFromElementID(Object elementID);
	//
	// public abstract Set<Object> getElementIDsFromBroadcastingID(Integer broadcastingID);

	public abstract IDType getMappingIDType();

	public abstract void showDetailView();

}
