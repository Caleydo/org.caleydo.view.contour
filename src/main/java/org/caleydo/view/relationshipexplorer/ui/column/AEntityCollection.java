/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.column.operation.MappingHighlightUpdateOperation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.MappingSelectionUpdateOperation;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;

/**
 * @author Christian
 *
 */
public abstract class AEntityCollection implements IEntityCollection {

	protected final RelationshipExplorerElement relationshipExplorer;
	protected String label = "";
	protected Set<Object> allElementIDs = new HashSet<>();
	protected Set<Object> filteredElementIDs = new HashSet<>();
	protected Set<Object> selectedElementIDs = new HashSet<>();

	protected Set<IEntityRepresentation> representations = new HashSet<>();

	public AEntityCollection(RelationshipExplorerElement relationshipExplorer) {
		this.relationshipExplorer = relationshipExplorer;
		relationshipExplorer.registerEntityCollection(this);
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
		return new HashSet<>();
	}

	@Override
	public void setFilteredItems(Set<Object> elementIDs) {
		this.filteredElementIDs = elementIDs;
		notifyFilterUpdate(null);
	}

	@Override
	public void updateFilteredItems(Set<Object> elementIDs, IEntityRepresentation updateSource) {
		this.filteredElementIDs = elementIDs;
		notifyFilterUpdate(updateSource);
		// TODO
	}

	protected void notifyFilterUpdate(IEntityRepresentation updateSource) {

		for (IEntityRepresentation rep : representations) {
			if (rep != updateSource)
				rep.filterChanged(filteredElementIDs);
		}

	}

	@Override
	public void setHighlightItems(Set<Object> elementIDs) {
		notifyHighlightUpdate(null, elementIDs);
	}

	@Override
	public void updateHighlightItems(Set<Object> elementIDs, IEntityRepresentation updateSource) {
		notifyHighlightUpdate(updateSource, elementIDs);

		relationshipExplorer.applyIDMappingUpdate(new MappingHighlightUpdateOperation(
				getBroadcastingIDsFromElementIDs(elementIDs), this), false);
	}

	protected void notifyHighlightUpdate(IEntityRepresentation updateSource, Set<Object> highlightElementIDs) {

		for (IEntityRepresentation rep : representations) {
			if (rep != updateSource)
				rep.highlightChanged(highlightElementIDs);
		}

	}

	@Override
	public void setSelectedItems(Set<Object> elementIDs) {
		this.selectedElementIDs = elementIDs;
		notifySelectionUpdate(null);
	}

	@Override
	public void updateSelectedItems(Set<Object> elementIDs, IEntityRepresentation updateSource) {
		this.selectedElementIDs = elementIDs;
		notifySelectionUpdate(updateSource);
		relationshipExplorer.applyIDMappingUpdate(new MappingSelectionUpdateOperation(
				getBroadcastingIDsFromElementIDs(elementIDs), this), false);
	}

	protected void notifySelectionUpdate(IEntityRepresentation updateSource) {

		for (IEntityRepresentation rep : representations) {
			if (rep != updateSource)
				rep.selectionChanged(selectedElementIDs);
		}

	}

	@Override
	public Set<Object> getBroadcastingIDsFromElementIDs(Collection<Object> elementIDs) {
		Set<Object> broadcastIDs = new HashSet<>();
		for (Object elementID : elementIDs) {
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
			elementIDs.addAll(getElementIDsFromBroadcastingID((Integer) bcID));
		}

		return elementIDs;
	}

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

	public abstract IColumnModel createColumnModel();

}
