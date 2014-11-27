/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.util;

import java.util.ArrayList;

import com.google.common.base.Predicate;

/**
 * @author Christian
 *
 */
public class CompositePredicate<T> extends ArrayList<Predicate<T>> implements Predicate<T> {

	/**
	 *
	 */
	private static final long serialVersionUID = 1053975441303494943L;

	@Override
	public boolean apply(T input) {
		for (Predicate<T> p : this) {
			if (!p.apply(input))
				return false;
		}
		return true;
	}

}
