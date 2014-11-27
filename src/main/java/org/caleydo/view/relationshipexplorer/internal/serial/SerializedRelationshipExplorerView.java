/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.relationshipexplorer.internal.serial;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedMultiTablePerspectiveBasedView;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.view.relationshipexplorer.internal.GLRelationshipExplorerView;

/**
 *
 * @author Christian
 *
 */
@XmlRootElement
@XmlType
public class SerializedRelationshipExplorerView extends ASerializedMultiTablePerspectiveBasedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedRelationshipExplorerView() {
	}

	public SerializedRelationshipExplorerView(IMultiTablePerspectiveBasedView view) {
		super(view);
	}

	@Override
	public String getViewType() {
		return GLRelationshipExplorerView.VIEW_TYPE;
	}
}