/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;

/**
 * @author Christian
 *
 */
public interface IScoreProvider {

	public Float getScore(Object primaryID, IEntityCollection primaryCollection, Object secondaryID,
			IEntityCollection secondaryCollection);

}
