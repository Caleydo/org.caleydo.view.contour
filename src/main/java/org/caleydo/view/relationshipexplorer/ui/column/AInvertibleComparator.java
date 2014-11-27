/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;


/**
 * @author Christian
 * @param <T>
 *
 */
public abstract class AInvertibleComparator<T> implements IInvertibleComparator<T> {

	@Override
	public IInvertibleComparator<T> getInverted() {
		return new InverseComparatorDecorator<>(this);
	}

}
