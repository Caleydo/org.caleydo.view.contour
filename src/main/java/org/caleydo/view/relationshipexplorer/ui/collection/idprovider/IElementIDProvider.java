/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.collection.idprovider;

import java.util.Set;

/**
 *
 *
 * @author Christian
 *
 */
public interface IElementIDProvider {

	/**
	 * @return The provided element IDs.
	 */
	public Set<Object> getElementIDs();

}
