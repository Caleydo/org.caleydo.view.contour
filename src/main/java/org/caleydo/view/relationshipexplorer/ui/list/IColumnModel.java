/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.list;

import java.util.Collection;
import java.util.Comparator;
import java.util.Set;

import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryIDOwner;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;
import org.caleydo.view.relationshipexplorer.ui.list.NestableColumn.ISelectionUpdateListener;

/**
 * @author Christian
 *
 */
public interface IColumnModel extends ILabeled, ISelectionUpdateListener, IEntityRepresentation, IHistoryIDOwner {

	public void fill(NestableColumn column, NestableColumn parentColumn);

	public GLElement getSummaryElement(Set<NestableItem> items);

	public Set<NestableItem> getItems(Set<Object> elementIDs);

	public Set<Object> getElementIDsFromForeignIDs(Set<Object> foreignIDs, IDType foreignIDType);

	public IDType getBroadcastingIDType();

	public Set<Object> getBroadcastingIDsFromElementID(Object elementID);

	public Set<Object> getBroadcastingIDsFromElementIDs(Collection<Object> elementIDs);

	public Set<Object> getElementIDsFromBroadcastingID(Integer broadcastingID);

	public Set<Object> getFilteredElementIDs();

	public void updateMappings();

	public Comparator<NestableItem> getDefaultComparator();

	public void updateFilteredItems();

	public NestableColumn getColumn();

}
