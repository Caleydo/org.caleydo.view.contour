/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.collection;

import java.util.Set;

import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;
import org.caleydo.view.relationshipexplorer.ui.detail.DetailViewWindow;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;

/**
 * @author Christian
 *
 */
public interface IEntityCollection extends ILabeled {

	public Set<Object> getAllElementIDs();

	public Set<Object> getFilteredElementIDs();

	public Set<Object> getSelectedElementIDs();

	public Set<Object> getHighlightElementIDs();

	public void reset();

	public void restoreAllEntities();

	public void setFilteredItems(Set<Object> elementIDs);

	public void setHighlightItems(Set<Object> elementIDs);

	public void setSelectedItems(Set<Object> elementIDs);

	public void notifySelectionUpdate(IEntityRepresentation updateSource);

	public void notifyHighlightUpdate(IEntityRepresentation updateSource);

	public void notifyFilterUpdate(IEntityRepresentation updateSource);

	public IDType getBroadcastingIDType();

	public IDType getMappingIDType();

	public Set<Object> getBroadcastingIDsFromElementID(Object elementID);

	public Set<Object> getBroadcastingIDsFromElementIDs(Set<Object> elementIDs);

	public Set<Object> getElementIDsFromBroadcastingID(Object broadcastingID);

	public Set<Object> getElementIDsFromForeignIDs(Set<Object> foreignIDs, IDType foreignIDType);

	public void addEntityRepresentation(IEntityRepresentation rep);

	public void removeEntityRepresentation(IEntityRepresentation rep);

	public IColumnModel createColumnModel();

	public DetailViewWindow createDetailViewWindow();

	public GLElement createDetailView(DetailViewWindow window);

}
