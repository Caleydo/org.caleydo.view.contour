/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import java.util.Comparator;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout2.GLElement;

import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public class TabularDataColumn extends AEntityColumn {

	protected final ATableBasedDataDomain dataDomain;
	protected final IDCategory itemIDCategory;
	protected final TablePerspective tablePerspective;
	protected final IDType itemIDType;
	protected final VirtualArray va;
	protected final Perspective perspective;
	protected final IDType mappingIDType;

	public static final Comparator<GLElement> ID_COMPARATOR = new Comparator<GLElement>() {

		@Override
		public int compare(GLElement arg0, GLElement arg1) {
			@SuppressWarnings("unchecked")
			SimpleDataRenderer r1 = (SimpleDataRenderer) ((KeyBasedGLElementContainer<GLElement>) arg0)
					.getElement(DATA_KEY);
			@SuppressWarnings("unchecked")
			SimpleDataRenderer r2 = (SimpleDataRenderer) ((KeyBasedGLElementContainer<GLElement>) arg1)
					.getElement(DATA_KEY);

			return r1.recordID - r2.recordID;
		}
	};

	public TabularDataColumn(TablePerspective tablePerspective, IDCategory itemIDCategory,
			RelationshipExplorerElement relationshipExplorer) {

		super(relationshipExplorer);
		dataDomain = tablePerspective.getDataDomain();

		this.itemIDCategory = itemIDCategory;
		this.tablePerspective = tablePerspective;
		this.mappingIDType = dataDomain.getDatasetDescriptionIDType(itemIDCategory);

		if (dataDomain.getDimensionIDCategory() == itemIDCategory) {
			va = tablePerspective.getDimensionPerspective().getVirtualArray();
			itemIDType = tablePerspective.getDimensionPerspective().getIdType();
			perspective = tablePerspective.getRecordPerspective();

		} else {
			va = tablePerspective.getRecordPerspective().getVirtualArray();
			itemIDType = tablePerspective.getRecordPerspective().getIdType();
			perspective = tablePerspective.getDimensionPerspective();
		}

	}

	protected void addItem(ATableBasedDataDomain dd, final IDType recordIDType, final int recordID,
			Perspective dimensionPerspective) {
		SimpleDataRenderer renderer = new SimpleDataRenderer(dd, recordIDType, recordID, dimensionPerspective);

		addElement(renderer, recordID);
		// IDMappingManager m = IDMappingManagerRegistry.get().getIDMappingManager(recordIDType);
		// IDType origIDType;
		// IDSpecification spec = dd.getDataSetDescription().getColumnIDSpecification();
		// if (spec.getIdCategory().equalsIgnoreCase(recordIDType.getIDCategory().getCategoryName())) {
		// origIDType = IDType.getIDType(spec.getIdType());
		// } else {
		// origIDType = IDType.getIDType(dd.getDataSetDescription().getRowIDSpecification().getIdType());
		// }

		// itemList.add(renderer);
		// Object origID = m.getID(recordIDType, origIDType, recordID);

		// itemList.setElementTooltip(renderer, origID.toString());

	}

	@Override
	public String getLabel() {
		return dataDomain.getLabel();
	}

	@Override
	protected void setContent() {
		for (int id : va) {
			addItem(dataDomain, itemIDType, id, perspective);
		}
	}

	@Override
	protected IDType getBroadcastingIDType() {
		return itemIDType;
	}

	@Override
	protected Set<Object> getBroadcastingIDsFromElementID(Object elementID) {
		return Sets.newHashSet(elementID);
	}

	@Override
	protected Set<Object> getElementIDsFromBroadcastingID(Integer broadcastingID) {
		return Sets.newHashSet((Object) broadcastingID);
	}

	@Override
	public Comparator<GLElement> getDefaultElementComparator() {
		return ID_COMPARATOR;
	}

	@Override
	protected IDType getMappingIDType() {
		return mappingIDType;
	}

}
