/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

/**
 * @author Christian
 *
 */
public class CompositeComparator<T> extends ArrayList<Comparator<T>> implements Comparator<T> {

	/**
	 *
	 */
	private static final long serialVersionUID = -4246689097366899024L;

	/**
	 *
	 */
	public CompositeComparator() {
		// TODO Auto-generated constructor stub
	}

	public CompositeComparator(Collection<? extends Comparator<T>> collection) {
		super(collection);
	}

	public CompositeComparator(int capacity) {
		super(capacity);
	}

	@SafeVarargs
	public CompositeComparator(Comparator<T>... comparators) {
		super(comparators.length);
		for (Comparator<T> c : comparators) {
			add(c);
		}
	}

	@Override
	public int compare(T o1, T o2) {

		for (Comparator<T> c : this) {
			int result = c.compare(o1, o2);
			if (result != 0)
				return result;
		}
		return 0;
	}

}
