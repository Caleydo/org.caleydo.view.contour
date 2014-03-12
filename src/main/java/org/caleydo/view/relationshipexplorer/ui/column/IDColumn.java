/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import gleem.linalg.Vec2f;

import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactories;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactories.GLElementSupplier;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IDCollection;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;

import com.google.common.base.Predicate;

/**
 * @author Christian
 *
 */
public class IDColumn extends ATextColumn implements IColumnModel {

	protected final IDType idType;
	protected final IDType displayedIDType;

	protected final IDCollection idCollection;

	protected IIDTypeMapper<Object, Object> elementIDToDisplayedIDMapper;
	protected IDMappingManager mappingManager;

	// public static final Comparator<GLElement> ID_NUMBER_COMPARATOR = new Comparator<GLElement>() {
	//
	// @Override
	// public int compare(GLElement arg0, GLElement arg1) {
	// MinSizeTextElement r1 = (MinSizeTextElement) ((KeyBasedGLElementContainer) arg0).getElement(DATA_KEY);
	// MinSizeTextElement r2 = (MinSizeTextElement) ((KeyBasedGLElementContainer) arg1).getElement(DATA_KEY);
	// return Integer.valueOf(r1.getLabel()).compareTo(Integer.valueOf(r2.getLabel()));
	// }
	// };

	public static final Comparator<NestableItem> ID_NUMBER_ITEM_COMPARATOR = new Comparator<NestableItem>() {

		@Override
		public int compare(NestableItem arg0, NestableItem arg1) {
			MinSizeTextElement r1 = (MinSizeTextElement) ((ScoreElement) arg0.getElement()).getElement();
			MinSizeTextElement r2 = (MinSizeTextElement) ((ScoreElement) arg1.getElement()).getElement();
			return Integer.valueOf(r1.getLabel()).compareTo(Integer.valueOf(r2.getLabel()));
		}

		@Override
		public String toString() {
			return "Item ID";
		}
	};

	public IDColumn(IDCollection idCollection, RelationshipExplorerElement relationshipExplorer) {
		super(idCollection, relationshipExplorer);
		this.idCollection = idCollection;
		this.idType = idCollection.getIdType();
		this.displayedIDType = idCollection.getDisplayedIDType();

		mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idType.getIDCategory());
		elementIDToDisplayedIDMapper = mappingManager.getIDTypeMapper(idType, displayedIDType);
		currentComparator = new CompositeComparator<>(ItemComparators.SELECTED_ITEMS_COMPARATOR, getDefaultComparator());
	}

	// @ListenTo
	// public void onApplyIDFilter(IDUpdateEvent event) {
	// Set<?> foreignIDs = event.getIds();
	// IDType foreignIDType = event.getIdType();
	// IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(this.idType);
	// Set<Object> mappedIDs = new HashSet<>();
	// for (Object id : foreignIDs) {
	// Set<Object> ids = mappingManager.getIDAsSet(foreignIDType, this.idType, id);
	// if (ids != null) {
	// mappedIDs.addAll(ids);
	// }
	// }
	//
	// setFilteredItems(mappedIDs);
	// }

	// @Override
	// protected void setContent() {
	// IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idType.getIDCategory());
	// IIDTypeMapper<Object, Object> mapper = mappingManager.getIDTypeMapper(idType, displayedIDType);
	//
	// for (final Object id : mappingManager.getAllMappedIDs(idType)) {
	// Set<Object> idsToDisplay = mapper.apply(id);
	// if (idsToDisplay != null) {
	// for (Object name : idsToDisplay) {
	// addTextElement(name.toString(), id);
	// break;
	// }
	// } else {
	// addTextElement(id.toString(), id);
	// }
	// }
	//
	// }

	protected String getDisplayedID(Object id) {
		Set<Object> idsToDisplay = elementIDToDisplayedIDMapper.apply(id);
		if (idsToDisplay != null) {
			for (Object name : idsToDisplay) {
				return name.toString();
			}
		}
		return id.toString();
	}

	// @Override
	// public IDType getBroadcastingIDType() {
	// return idType;
	// }
	//
	// @Override
	// public Set<Object> getBroadcastingIDsFromElementID(Object elementID) {
	// return Sets.newHashSet(elementID);
	// }
	//
	// @Override
	// public Set<Object> getElementIDsFromBroadcastingID(Integer broadcastingID) {
	// return Sets.newHashSet((Object) broadcastingID);
	// }

	// @Override
	// public Comparator<GLElement> getDefaultElementComparator() {
	// if (displayedIDType.getDataType() == EDataType.INTEGER)
	// return ID_NUMBER_COMPARATOR;
	// return super.getDefaultElementComparator();
	// }

	@Override
	public Comparator<NestableItem> getDefaultComparator() {
		if (displayedIDType.getDataType() == EDataType.INTEGER)
			return ID_NUMBER_ITEM_COMPARATOR;
		return super.getDefaultComparator();
	}

	@Override
	public void showDetailView() {
		// FIXME: Hack to get right column and dataset
		if (getLabel().toLowerCase().contains("compounds")) {
			Set<NestableItem> selectedItems = column.getSelectedItems();

			if (selectedItems.size() != 1)
				return;

			Object elementID = selectedItems.iterator().next().getElementData().iterator().next();

			int recordID = (int) entityCollection.getBroadcastingIDsFromElementID(elementID).iterator().next();

			ATableBasedDataDomain dataDomain = null;

			// Not the most elegant way but it does the job to get the smiles dataset
			for (IDataDomain dd : DataDomainManager.get().getAllDataDomains()) {
				if (dd instanceof ATableBasedDataDomain && dd.getLabel().contains("Smiles")) {
					dataDomain = (ATableBasedDataDomain) dd;
					break;
				}
			}

			Set<Integer> smileIDs = mappingManager.getIDAsSet(entityCollection.getBroadcastingIDType(),
					dataDomain.getRecordIDType(), recordID);

			if (smileIDs != null && !smileIDs.isEmpty()) {

				IDType dimensionIDType = dataDomain.getDimensionIDType();
				int smilesColumnID = dataDomain.getDefaultTablePerspective().getRecordPerspective().getVirtualArray()
						.get(0);

				String smileString = (String) dataDomain.getRaw(dataDomain.getRecordIDType(), smileIDs.iterator()
						.next(), dimensionIDType, smilesColumnID);

				GLElementFactoryContext context = GLElementFactoryContext.builder().put("smile", smileString).build();
				List<GLElementSupplier> suppliers = GLElementFactories.getExtensions(context, "relexplorer",
						new Predicate<String>() {

							@Override
							public boolean apply(String input) {
								return input.equals("smile");
							}
						});
				if (!suppliers.isEmpty()) {
					GLElement compoundView = suppliers.get(0).get();

					relationshipExplorer.showDetailView(entityCollection, compoundView, this);
				}
			}

		} else {
			GLElement dummy = new GLElement() {
				@Override
				public Vec2f getMinSize() {
					return new Vec2f(300, 300);
				}
			};
			dummy.setRenderer(GLRenderers.fillRect(Color.BLUE));

			relationshipExplorer.showDetailView(entityCollection, dummy, this);
		}

	}

	// @Override
	// public void fill(NestableColumn column, NestableColumn parentColumn) {
	// this.column = column;
	// this.parentColumn = parentColumn;
	//
	// if (parentColumn == null) {
	// for (Object id : filteredElementIDs) {
	// addTextItem(getDisplayedID(id), id, column, null);
	// }
	// } else {
	//
	// for (Object id : filteredElementIDs) {
	// Set<Object> foreignElementIDs = parentColumn.getColumnModel().getElementIDsFromForeignIDs(
	// getBroadcastingIDsFromElementID(id), getBroadcastingIDType());
	// Set<NestableItem> parentItems = parentColumn.getColumnModel().getItems(foreignElementIDs);
	//
	// for (NestableItem parentItem : parentItems) {
	// addTextItem(getDisplayedID(id), id, column, parentItem);
	// }
	// }
	// }
	//
	// }

	@Override
	public String getText(Object elementID) {
		return getDisplayedID(elementID);
	}

}
