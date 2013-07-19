/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.template.internal;

import java.util.Iterator;
import java.util.List;

import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.AGLElementDecorator;
import org.caleydo.core.view.opengl.layout2.view.AMultiTablePerspectiveElementView;
import org.caleydo.view.template.internal.serial.SerializedTemplateMultiView;
import org.caleydo.view.template.ui.TemplateElement;
import org.caleydo.view.template.ui.TemplateMultiElement;

import com.google.common.collect.Iterators;

/**
 *
 * @author AUTHOR
 *
 */
public class GLTemplateMultiView extends AMultiTablePerspectiveElementView {
	public static final String VIEW_TYPE = "org.caleydo.view.template";
	public static final String VIEW_NAME = "Template";

	private static final Logger log = Logger.create(GLTemplateMultiView.class);

	public GLTemplateMultiView(IGLCanvas glCanvas) {
		super(glCanvas, VIEW_TYPE, VIEW_NAME);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);
		getRootDecorator().setContent(new TemplateMultiElement());
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return new SerializedTemplateMultiView(this);
	}

	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		return DataSupportDefinitions.tableBased;
	}

	@Override
	protected TemplateMultiElement getContent() {
		return (TemplateMultiElement) super.getContent();
	}

	@Override
	protected void applyTablePerspectives(AGLElementDecorator root, List<TablePerspective> all,
			List<TablePerspective> added, List<TablePerspective> removed) {
		TemplateMultiElement content = getContent();
		for (TablePerspective add : added) {
			content.add(new TemplateElement(add));
		}
		for(TablePerspective rem : removed) {
			for(Iterator<TemplateElement> it = Iterators.filter(content.iterator(),TemplateElement.class); it.hasNext(); ) {
				if (it.next().getTablePerspective().equals(rem)) {
					it.remove();
					break;
				}
			}
		}
	}


}
