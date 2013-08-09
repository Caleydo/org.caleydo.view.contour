/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.template.internal.serial;

import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.caleydo.core.serialize.ASerializedMultiTablePerspectiveBasedView;
import org.caleydo.core.view.IMultiTablePerspectiveBasedView;
import org.caleydo.view.template.internal.GLTemplateSingleView;

/**
 *
 * @author AUTHOR
 *
 */
@XmlRootElement
@XmlType
public class SerializedTemplateMultiView extends ASerializedMultiTablePerspectiveBasedView {

	/**
	 * Default constructor with default initialization
	 */
	public SerializedTemplateMultiView() {
	}

	public SerializedTemplateMultiView(IMultiTablePerspectiveBasedView view) {
		super(view);
	}

	@Override
	public String getViewType() {
		return GLTemplateSingleView.VIEW_TYPE;
	}
}