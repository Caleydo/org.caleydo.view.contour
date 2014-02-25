/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.structure;

import org.caleydo.core.serialize.ASerializedView;

/**
 * @author Christian
 *
 */
public class SerializedCompoundStructureView extends ASerializedView {

	@Override
	public String getViewType() {
		return GLCompoundStructureView.VIEW_TYPE;
	}

}
