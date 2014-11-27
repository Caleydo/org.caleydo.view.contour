/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.util.ArrayList;
import java.util.Collection;

/**
 * @author Christian
 *
 */
public class CompositeComparator<T> extends ArrayList<IInvertibleComparator<T>> implements IInvertibleComparator<T> {

	/**
	 *
	 */
	private static final long serialVersionUID = -4246689097366899024L;

	protected boolean invert = false;

	/**
	 *
	 */
	public CompositeComparator() {
		// TODO Auto-generated constructor stub
	}

	public CompositeComparator(Collection<? extends IInvertibleComparator<T>> collection) {
		super(collection);
	}

	public CompositeComparator(int capacity) {
		super(capacity);
	}

	@SafeVarargs
	public CompositeComparator(IInvertibleComparator<T>... comparators) {
		super(comparators.length);
		for (IInvertibleComparator<T> c : comparators) {
			add(c);
		}
	}

	@Override
	public int compare(T o1, T o2) {

		for (IInvertibleComparator<T> c : this) {
			int result = c.compare(o1, o2);
			if (result != 0)
				return result;
		}
		return 0;
	}

	@Override
	public IInvertibleComparator<T> getInverted() {
		CompositeComparator<T> comp = new CompositeComparator<>(size());
		for (IInvertibleComparator<T> c : this) {
			comp.add(c.getInverted());
		}
		return comp;
	}


}
