/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.template.internal;

import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.GLElementDecorator;
import org.caleydo.core.view.opengl.layout2.view.ASingleTablePerspectiveElementView;
import org.caleydo.view.template.internal.serial.SerializedTemplateSingleView;
import org.caleydo.view.template.ui.TemplateElement;

/**
 *
 * @author AUTHOR
 *
 */
public class GLTemplateSingleView extends ASingleTablePerspectiveElementView {
	public static final String VIEW_TYPE = "org.caleydo.view.template";
	public static final String VIEW_NAME = "Template";

	private static final Logger log = Logger.create(GLTemplateSingleView.class);

	public GLTemplateSingleView(IGLCanvas glCanvas) {
		super(glCanvas, VIEW_TYPE, VIEW_NAME);
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return new SerializedTemplateSingleView(this);
	}

	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		return DataSupportDefinitions.tableBased;
	}

	@Override
	protected void applyTablePerspective(GLElementDecorator root, TablePerspective tablePerspective) {
		if (tablePerspective == null)
			root.setContent(null);
		else
			root.setContent(new TemplateElement(tablePerspective));
	}
}
