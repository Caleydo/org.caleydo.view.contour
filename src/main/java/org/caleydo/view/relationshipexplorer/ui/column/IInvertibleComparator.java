/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.util.Comparator;

/**
 * @author Christian
 *
 */
public interface IInvertibleComparator<T> extends Comparator<T> {

	public IInvertibleComparator<T> getInverted();

}
