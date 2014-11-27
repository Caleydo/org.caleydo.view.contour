/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

/**
 * @author Christian
 *
 */
public class InverseComparatorDecorator<T> implements IInvertibleComparator<T> {

	protected final IInvertibleComparator<T> comparator;

	public InverseComparatorDecorator(IInvertibleComparator<T> comparator) {
		this.comparator = comparator;
	}

	@Override
	public int compare(T o1, T o2) {
		return -comparator.compare(o1, o2);
	}

	@Override
	public IInvertibleComparator<T> getInverted() {
		return comparator;
	}

}
