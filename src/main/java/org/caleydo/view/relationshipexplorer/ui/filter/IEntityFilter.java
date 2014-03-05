/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.filter;

import com.google.common.base.Predicate;

/**
 * @author Christian
 *
 */
public interface IEntityFilter extends Predicate<Object> {

	public String getDescription();

}
