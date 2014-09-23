/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.collection;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;
import org.caleydo.view.relationshipexplorer.ui.column.factory.IColumnFactory;
import org.caleydo.view.relationshipexplorer.ui.detail.DetailViewFactories;
import org.caleydo.view.relationshipexplorer.ui.detail.DetailViewWindow;
import org.caleydo.view.relationshipexplorer.ui.detail.DetailViewWindowFactories;
import org.caleydo.view.relationshipexplorer.ui.detail.IDetailViewFactory;
import org.caleydo.view.relationshipexplorer.ui.detail.IDetailViewWindowFactory;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;

import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public abstract class AEntityCollection implements IEntityCollection {

	protected final ConTourElement contour;
	protected String label = "";
	protected Set<Object> allElementIDs = new HashSet<>();
	protected Set<Object> filteredElementIDs = new HashSet<>();
	protected Set<Object> selectedElementIDs = new HashSet<>();
	protected Set<Object> highlightElementIDs = new HashSet<>();

	protected Set<IEntityRepresentation> representations = new HashSet<>();

	protected IColumnFactory columnFactory;

	protected IDetailViewWindowFactory detailViewWindowFactory;
	protected IDetailViewFactory detailViewFactory;

	public AEntityCollection(ConTourElement relationshipExplorer) {
		this.contour = relationshipExplorer;
	}

	@Override
	public Set<Object> getAllElementIDs() {
		return allElementIDs;
	}

	@Override
	public Set<Object> getFilteredElementIDs() {
		return filteredElementIDs;
	}

	@Override
	public Set<Object> getSelectedElementIDs() {
		return selectedElementIDs;
	}

	@Override
	public Set<Object> getHighlightElementIDs() {
		return highlightElementIDs;
	}

	@Override
	public void setFilteredItems(Set<Object> elementIDs) {
		this.filteredElementIDs = new HashSet<>(Sets.intersection(elementIDs, allElementIDs));
		// notifyFilterUpdate(updateSource);
	}

	@Override
	public void notifyFilterUpdate(IEntityRepresentation updateSource) {

		for (IEntityRepresentation rep : representations) {
			rep.filterChanged(filteredElementIDs, updateSource);
		}

	}

	@Override
	public void setHighlightItems(Set<Object> elementIDs) {
		this.highlightElementIDs = new HashSet<>(Sets.intersection(elementIDs, allElementIDs));
	}

	@Override
	public void notifyHighlightUpdate(IEntityRepresentation updateSource) {
		for (IEntityRepresentation rep : representations) {
			rep.highlightChanged(highlightElementIDs, updateSource);
		}
	}

	@Override
	public void setSelectedItems(Set<Object> elementIDs) {
		this.selectedElementIDs = new HashSet<>(Sets.intersection(elementIDs, allElementIDs));
		// notifySelectionUpdate(updateSource);
	}

	@Override
	public void notifySelectionUpdate(IEntityRepresentation updateSource) {

		for (IEntityRepresentation rep : representations) {
			rep.selectionChanged(selectedElementIDs, updateSource);
		}

	}

	@Override
	public Set<Object> getBroadcastingIDsFromElementIDs(Set<Object> elementIDs) {
		Set<Object> myElementIDs = new HashSet<>(Sets.intersection(elementIDs, allElementIDs));
		Set<Object> broadcastIDs = new HashSet<>();
		for (Object elementID : myElementIDs) {
			broadcastIDs.addAll(getBroadcastingIDsFromElementID(elementID));
		}

		return broadcastIDs;
	}

	@Override
	public Set<Object> getElementIDsFromForeignIDs(Set<Object> foreignIDs, IDType foreignIDType) {
		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(getBroadcastingIDType());

		Set<Object> elementIDs = new HashSet<>();
		Set<Object> broadcastIDs = mappingManager.getIDTypeMapper(foreignIDType, getBroadcastingIDType()).apply(
				foreignIDs);
		for (Object bcID : broadcastIDs) {
			elementIDs.addAll(getElementIDsFromBroadcastID(bcID));
		}

		return new HashSet<>(Sets.intersection(elementIDs, allElementIDs));
	}

	@Override
	public Set<Object> getBroadcastingIDsFromElementID(Object elementID) {
		if (!allElementIDs.contains(elementID))
			return new HashSet<>();
		return getBroadcastIDsFromElementID(elementID);
	}

	protected abstract Set<Object> getBroadcastIDsFromElementID(Object elementID);

	@Override
	public Set<Object> getElementIDsFromBroadcastingID(Object broadcastingID) {
		Set<Object> elementIDs = getElementIDsFromBroadcastID(broadcastingID);
		return new HashSet<>(Sets.intersection(elementIDs, allElementIDs));
	}

	protected abstract Set<Object> getElementIDsFromBroadcastID(Object broadcastID);

	@Override
	public String getLabel() {
		return label;
	}

	/**
	 * @param label
	 *            setter, see {@link label}
	 */
	public void setLabel(String label) {
		this.label = label;
	}

	@Override
	public void addEntityRepresentation(IEntityRepresentation rep) {
		representations.add(rep);
	}

	@Override
	public void removeEntityRepresentation(IEntityRepresentation rep) {
		representations.remove(rep);
	}

	@Override
	public void reset() {
		restoreAllEntities();
		highlightElementIDs.clear();
		selectedElementIDs.clear();
		representations.clear();
	}

	@Override
	public void restoreAllEntities() {
		filteredElementIDs = new HashSet<>(allElementIDs.size());
		filteredElementIDs.addAll(allElementIDs);
	}

	@Override
	public IColumnModel createColumnModel() {
		if (columnFactory == null)
			columnFactory = getDefaultColumnFactory();
		return columnFactory.create(this, contour);
	}

	/**
	 * @param columnFactory
	 *            setter, see {@link columnFactory}
	 */
	public void setColumnFactory(IColumnFactory columnFactory) {
		this.columnFactory = columnFactory;
	}

	@Override
	public DetailViewWindow createDetailViewWindow() {
		if (detailViewWindowFactory == null)
			detailViewWindowFactory = DetailViewWindowFactories
.createDefaultDetailViewWindowFactory(contour);
		return detailViewWindowFactory.createWindow(this);
	}

	/**
	 * @param detailViewWindowFactory
	 *            setter, see {@link detailViewWindowFactory}
	 */
	public void setDetailViewWindowFactory(IDetailViewWindowFactory detailViewWindowFactory) {
		this.detailViewWindowFactory = detailViewWindowFactory;
	}

	/**
	 * @param detailViewFactory
	 *            setter, see {@link detailViewFactory}
	 */
	public void setDetailViewFactory(IDetailViewFactory detailViewFactory) {
		this.detailViewFactory = detailViewFactory;
	}

	@Override
	public GLElement createDetailView(DetailViewWindow window) {
		if (detailViewFactory == null)
			detailViewFactory = DetailViewFactories.createDefaultDetailViewWindowFactory(contour);
		return detailViewFactory.create(this, window);
	}

	protected abstract IColumnFactory getDefaultColumnFactory();

	/**
	 * @return the columnFactory, see {@link #columnFactory}
	 */
	public IColumnFactory getColumnFactory() {
		return columnFactory;
	}
}
