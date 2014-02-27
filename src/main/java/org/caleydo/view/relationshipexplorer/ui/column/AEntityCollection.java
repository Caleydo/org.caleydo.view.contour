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
	public void setFilteredItems(Set<Object> elementIDs, IEntityRepresentation updateSource) {
		this.filteredElementIDs = elementIDs;
		notifyFilterUpdate(updateSource);
		// TODO
	}

	protected void notifyFilterUpdate(IEntityRepresentation updateSource) {

		for (IEntityRepresentation rep : representations) {
			rep.filterChanged(filteredElementIDs, updateSource);
		}

	}

	@Override
	public void setHighlightItems(Set<Object> elementIDs, IEntityRepresentation updateSource) {
		notifyHighlightUpdate(updateSource, elementIDs);
	}

	protected void notifyHighlightUpdate(IEntityRepresentation updateSource, Set<Object> highlightElementIDs) {
		for (IEntityRepresentation rep : representations) {
			rep.highlightChanged(highlightElementIDs, updateSource);
		}
	}

	@Override
	public void setSelectedItems(Set<Object> elementIDs, IEntityRepresentation updateSource) {
		this.selectedElementIDs = elementIDs;
		notifySelectionUpdate(updateSource);
	}

	protected void notifySelectionUpdate(IEntityRepresentation updateSource) {

		for (IEntityRepresentation rep : representations) {
			rep.selectionChanged(selectedElementIDs, updateSource);
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

	@Override
	public void updateSelectionMappings(IEntityRepresentation srcRep) {
		for (IEntityRepresentation rep : representations) {
			rep.updateMappings(srcRep);
		}

	}

	public abstract IColumnModel createColumnModel();

}
