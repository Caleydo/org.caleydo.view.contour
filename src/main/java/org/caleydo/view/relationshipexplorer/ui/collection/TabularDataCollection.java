/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.collection;

import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.column.factory.ColumnFactories;
import org.caleydo.view.relationshipexplorer.ui.column.factory.IColumnFactory;

import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public class TabularDataCollection extends AEntityCollection {

	protected final ATableBasedDataDomain dataDomain;
	protected final IDCategory itemIDCategory;
	protected final TablePerspective tablePerspective;
	protected final IDType itemIDType;
	protected final VirtualArray va;
	protected final Perspective dimensionPerspective;
	protected final IDType mappingIDType;

	public TabularDataCollection(TablePerspective tablePerspective, IDCategory itemIDCategory,
			RelationshipExplorerElement relationshipExplorer) {
		super(relationshipExplorer);
		dataDomain = tablePerspective.getDataDomain();

		this.itemIDCategory = itemIDCategory;
		this.tablePerspective = tablePerspective;
		this.mappingIDType = dataDomain.getDatasetDescriptionIDType(itemIDCategory);

		if (dataDomain.getDimensionIDCategory() == itemIDCategory) {
			va = tablePerspective.getDimensionPerspective().getVirtualArray();
			itemIDType = tablePerspective.getDimensionPerspective().getIdType();
			dimensionPerspective = tablePerspective.getRecordPerspective();

		} else {
			va = tablePerspective.getRecordPerspective().getVirtualArray();
			itemIDType = tablePerspective.getRecordPerspective().getIdType();
			dimensionPerspective = tablePerspective.getDimensionPerspective();
		}
		allElementIDs.addAll(va.getIDs());
		filteredElementIDs.addAll(allElementIDs);
		setLabel(dataDomain.getLabel());
	}

	@Override
	public IDType getBroadcastingIDType() {
		return itemIDType;
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
	protected IColumnFactory getDefaultColumnFactory() {
		return ColumnFactories.createDefaultTabularDataColumnFactory(this, relationshipExplorer);
	}

	/**
	 * @return the dataDomain, see {@link #dataDomain}
	 */
	public ATableBasedDataDomain getDataDomain() {
		return dataDomain;
	}

	/**
	 * @return the perspective, see {@link #dimensionPerspective}
	 */
	public Perspective getDimensionPerspective() {
		return dimensionPerspective;
	}

	/**
	 * @return the itemIDCategory, see {@link #itemIDCategory}
	 */
	public IDCategory getItemIDCategory() {
		return itemIDCategory;
	}

	/**
	 * @return the itemIDType, see {@link #itemIDType}
	 */
	public IDType getItemIDType() {
		return itemIDType;
	}

	public IDType getMappingIDType() {
		return mappingIDType;
	}

	/**
	 * @return the tablePerspective, see {@link #tablePerspective}
	 */
	public TablePerspective getTablePerspective() {
		return tablePerspective;
	}

	/**
	 * @return the va, see {@link #va}
	 */
	public VirtualArray getVa() {
		return va;
	}

}
