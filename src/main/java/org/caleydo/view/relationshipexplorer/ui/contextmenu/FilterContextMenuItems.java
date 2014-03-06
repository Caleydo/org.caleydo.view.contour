/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.contextmenu;

import java.util.List;

import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.GenericContextMenuItem;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;
import org.caleydo.view.relationshipexplorer.ui.column.operation.ESetOperation;

import com.google.common.collect.Lists;

/**
 * @author Christian
 *
 */
public final class FilterContextMenuItems {

	private FilterContextMenuItems() {

	}

	public static List<AContextMenuItem> getDefaultFilterItems(RelationshipExplorerElement relationshipExplorer,
			IEntityRepresentation representation, Object contextMenuCommandReceiver) {
		AContextMenuItem replaceFilterItem = new GenericContextMenuItem(
				"Replace items with those related to the selected " + representation.getCollection().getLabel(),
				new ContextMenuCommandEvent(new FilterCommand(ESetOperation.REPLACE, representation,
						relationshipExplorer)).to(contextMenuCommandReceiver));
		AContextMenuItem andFilterITem = new GenericContextMenuItem("Filter items to those related to the selected "
				+ representation.getCollection().getLabel(), new ContextMenuCommandEvent(new FilterCommand(
				ESetOperation.INTERSECTION, representation, relationshipExplorer)).to(contextMenuCommandReceiver));
		AContextMenuItem orFilterITem = new GenericContextMenuItem("Add all items related to the selected "
				+ representation.getCollection().getLabel(), new ContextMenuCommandEvent(new FilterCommand(
				ESetOperation.UNION, representation, relationshipExplorer)).to(contextMenuCommandReceiver));

		return Lists.newArrayList(replaceFilterItem, andFilterITem, orFilterITem);
	}

}
