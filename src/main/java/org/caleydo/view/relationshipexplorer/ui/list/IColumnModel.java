/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.list;

import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

import org.caleydo.core.id.IDType;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryIDOwner;
import org.caleydo.view.relationshipexplorer.ui.column.AttributeFilterEvent;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;
import org.caleydo.view.relationshipexplorer.ui.column.SortingEvent;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.ContextMenuCommandEvent;
import org.caleydo.view.relationshipexplorer.ui.list.NestableColumn.ISelectionUpdateListener;

/**
 * @author Christian
 *
 */
public interface IColumnModel extends ILabeled, ISelectionUpdateListener, IEntityRepresentation, IHistoryIDOwner {

	public void fill(NestableColumn column, NestableColumn parentColumn);

	public GLElement getSummaryElement(NestableItem parentItem, Set<NestableItem> items, NestableItem summaryItem,
			EUpdateCause cause);

	public Set<NestableItem> getItems(Set<Object> elementIDs);

	public Set<Object> getElementIDsFromForeignIDs(Set<Object> foreignIDs, IDType foreignIDType);

	public IDType getBroadcastingIDType();

	public Set<Object> getBroadcastingIDsFromElementID(Object elementID);

	public Set<Object> getBroadcastingIDsFromElementIDs(Collection<Object> elementIDs);

	public Set<Object> getElementIDsFromBroadcastingID(Integer broadcastingID);

	public Set<Object> getFilteredElementIDs();

	public void updateMappings();

	public Comparator<NestableItem> getDefaultComparator();

	public Comparator<NestableItem> getCurrentComparator();

	public void sortBy(Comparator<NestableItem> comparator);

	public NestableColumn getColumn();

	public List<GLElement> getHeaderOverlayElements();

	public void onAttributeFilter(AttributeFilterEvent event);

	public void onSort(SortingEvent event);

	public void onHandleContextMenuOperation(ContextMenuCommandEvent event);

	public void init();

}
