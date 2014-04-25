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

import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryIDOwner;
import org.caleydo.view.relationshipexplorer.ui.column.AttributeFilterEvent;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;
import org.caleydo.view.relationshipexplorer.ui.column.IScoreProvider;
import org.caleydo.view.relationshipexplorer.ui.column.SortingEvent;
import org.caleydo.view.relationshipexplorer.ui.list.NestableColumn.ISelectionUpdateListener;

/**
 * @author Christian
 *
 */
public interface IColumnModel extends ILabeled, ISelectionUpdateListener, IEntityRepresentation, IHistoryIDOwner {

	public void fill(NestableColumn column, NestableColumn parentColumn);

	public GLElement getSummaryItemElement(NestableItem parentItem, Set<NestableItem> items, NestableItem summaryItem,
			EUpdateCause cause);

	public GLElement getItemElement(NestableItem item, EUpdateCause cause);

	public Set<NestableItem> getItems(Set<Object> elementIDs);

	public void updateMappings();

	public Comparator<NestableItem> getDefaultComparator();

	public Comparator<NestableItem> getCurrentComparator();

	public void sortBy(Comparator<NestableItem> comparator);

	public NestableColumn getColumn();

	public List<GLElement> getHeaderOverlayElements();

	public void onAttributeFilter(AttributeFilterEvent event);

	public void onSort(SortingEvent event);

	public void setScoreProvider(IScoreProvider scoreProvider);

	public void init();

	public GLElement getHeaderExtension();

	public Collection<? extends AContextMenuItem> getContextMenuItems();

	public void takeDown();

}
