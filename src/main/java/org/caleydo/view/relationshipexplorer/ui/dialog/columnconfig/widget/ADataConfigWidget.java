/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig.widget;

import org.caleydo.core.util.base.ICallback;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;

/**
 * Base interface for the different config widgets
 *
 * @author Christian
 *
 */
public abstract class ADataConfigWidget extends Composite {

	protected final ICallback<ADataConfigWidget> callback;

	/**
	 * @param parent
	 * @param style
	 */
	public ADataConfigWidget(Composite parent, ICallback<ADataConfigWidget> callback) {
		super(parent, SWT.NONE);
		this.callback = callback;
	}

	/**
	 * Determines whether the configuration selected by the user in this widget is valid.
	 *
	 * @return
	 */
	public abstract boolean isConfigValid();

}
