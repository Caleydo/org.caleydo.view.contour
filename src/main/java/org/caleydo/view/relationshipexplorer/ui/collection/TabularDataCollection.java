/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.collection;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.VirtualArray;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.idprovider.IElementIDProvider;
import org.caleydo.view.relationshipexplorer.ui.column.factory.ColumnFactories;
import org.caleydo.view.relationshipexplorer.ui.column.factory.IColumnFactory;
import org.caleydo.view.relationshipexplorer.ui.detail.parcoords.ParallelCoordinatesDetailViewFactory;

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
			IElementIDProvider elementIDProvider, ConTourElement relationshipExplorer) {
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
		if (elementIDProvider == null)
			elementIDProvider = getDefaultElementIDProvider(va);

		allElementIDs.addAll(elementIDProvider.getElementIDs());
		filteredElementIDs.addAll(allElementIDs);
		setLabel(dataDomain.getLabel());
		detailViewFactory = new ParallelCoordinatesDetailViewFactory();
	}

	@Override
	public IDType getBroadcastingIDType() {
		return itemIDType;
	}

	@Override
	protected Set<Object> getBroadcastIDsFromElementID(Object elementID) {
		return Sets.newHashSet(elementID);
	}

	@Override
	protected Set<Object> getElementIDsFromBroadcastID(Object broadcastingID) {
		return Sets.newHashSet(broadcastingID);
	}

	@Override
	protected IColumnFactory getDefaultColumnFactory() {
		return ColumnFactories.createDefaultTabularDataColumnFactory();
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

	@Override
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

	public static IElementIDProvider getDefaultElementIDProvider(final VirtualArray va) {
		return new IElementIDProvider() {
			@Override
			public Set<Object> getElementIDs() {
				return new HashSet<Object>(va.getIDs());
			}
		};
	}

	@Override
	public String getText(Object elementID) {
		return elementID.toString();
	}

}
