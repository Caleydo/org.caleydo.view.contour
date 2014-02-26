/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import gleem.linalg.Vec2f;

import java.util.Comparator;
import java.util.Set;

import org.caleydo.core.data.collection.EDataType;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.util.base.ILabelHolder;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
import org.caleydo.view.relationshipexplorer.ui.util.KeyBasedGLElementContainer;
import org.eclipse.nebula.widgets.nattable.util.ComparatorChain;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public class IDColumn extends ATextColumn implements ILabelHolder, IColumnModel {

	protected final IDType idType;
	protected final IDType displayedIDType;

	protected String label;

	protected IIDTypeMapper<Object, Object> elementIDToDisplayedIDMapper;
	protected IDMappingManager mappingManager;

	public static final Comparator<GLElement> ID_NUMBER_COMPARATOR = new Comparator<GLElement>() {

		@Override
		public int compare(GLElement arg0, GLElement arg1) {
			MinSizeTextElement r1 = (MinSizeTextElement) ((KeyBasedGLElementContainer) arg0).getElement(DATA_KEY);
			MinSizeTextElement r2 = (MinSizeTextElement) ((KeyBasedGLElementContainer) arg1).getElement(DATA_KEY);
			return Integer.valueOf(r1.getLabel()).compareTo(Integer.valueOf(r2.getLabel()));
		}
	};

	public static final Comparator<NestableItem> ID_NUMBER_ITEM_COMPARATOR = new Comparator<NestableItem>() {

		@Override
		public int compare(NestableItem arg0, NestableItem arg1) {
			MinSizeTextElement r1 = (MinSizeTextElement) arg0.getElement();
			MinSizeTextElement r2 = (MinSizeTextElement) arg1.getElement();
			return Integer.valueOf(r1.getLabel()).compareTo(Integer.valueOf(r2.getLabel()));
		}
	};



	public IDColumn(IDType idType, IDType displayedIDType, RelationshipExplorerElement relationshipExplorer) {
		super(relationshipExplorer);
		this.idType = idType;
		this.displayedIDType = displayedIDType;
		this.label = idType.getIDCategory().getDenominationPlural(true);

		mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idType.getIDCategory());
		elementIDToDisplayedIDMapper = mappingManager.getIDTypeMapper(idType, displayedIDType);
		filteredElementIDs.addAll(mappingManager.getAllMappedIDs(idType));
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

	@Override
	protected void setContent() {
		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idType.getIDCategory());
		IIDTypeMapper<Object, Object> mapper = mappingManager.getIDTypeMapper(idType, displayedIDType);

		for (final Object id : mappingManager.getAllMappedIDs(idType)) {
			Set<Object> idsToDisplay = mapper.apply(id);
			if (idsToDisplay != null) {
				for (Object name : idsToDisplay) {
					addTextElement(name.toString(), id);
					break;
				}
			} else {
				addTextElement(id.toString(), id);
			}
		}

	}

	protected String getDisplayedID(Object id) {
		Set<Object> idsToDisplay = elementIDToDisplayedIDMapper.apply(id);
		if (idsToDisplay != null) {
			for (Object name : idsToDisplay) {
				return name.toString();
			}
		}
		return id.toString();
	}

	@Override
	public String getProviderName() {
		return "ID Column";
	}

	@Override
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public String getLabel() {
		return label;
	}

	@Override
	public IDType getBroadcastingIDType() {
		return idType;
	}

	@Override
	public Set<Object> getBroadcastingIDsFromElementID(Object elementID) {
		return Sets.newHashSet(elementID);
	}

	@Override
	public Set<Object> getElementIDsFromBroadcastingID(Integer broadcastingID) {
		return Sets.newHashSet((Object) broadcastingID);
	}

	@Override
	public Comparator<GLElement> getDefaultElementComparator() {
		if (displayedIDType.getDataType() == EDataType.INTEGER)
			return ID_NUMBER_COMPARATOR;
		return super.getDefaultElementComparator();
	}

	@SuppressWarnings("unchecked")
	@Override
	public Comparator<NestableItem> getDefaultComparator() {
		if (displayedIDType.getDataType() == EDataType.INTEGER)
			return new ComparatorChain<>(Lists.<Comparator<NestableItem>> newArrayList(SELECTED_ITEMS_COMPARATOR,
					ID_NUMBER_ITEM_COMPARATOR));
		return super.getDefaultComparator();
	}

	@Override
	public IDType getMappingIDType() {
		return getBroadcastingIDType();
	}

	@Override
	public void showDetailView() {
		GLElement dummy = new GLElement() {
			@Override
			public Vec2f getMinSize() {
				return new Vec2f(300, 300);
			}
		};
		dummy.setRenderer(GLRenderers.fillRect(Color.BLUE));

		relationshipExplorer.showDetailView(this, dummy, this);

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
	protected GLElement createElement(Object elementID) {
		return createTextItem(getDisplayedID(elementID));
	}

}
