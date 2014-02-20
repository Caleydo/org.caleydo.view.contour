/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.relationshipexplorer.internal;

import java.util.List;

import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.GLElementDecorator;
import org.caleydo.core.view.opengl.layout2.view.AMultiTablePerspectiveElementView;
import org.caleydo.view.relationshipexplorer.internal.serial.SerializedRelationshipExplorerView;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.list.INestableColumn;

/**
 *
 * @author Christian
 *
 */
public class GLRelationshipExplorerView extends AMultiTablePerspectiveElementView {
	public static final String VIEW_TYPE = "org.caleydo.view.relationshipexplorer";
	public static final String VIEW_NAME = "RelationshipExplorer";

	private static final Logger log = Logger.create(GLRelationshipExplorerView.class);

	public GLRelationshipExplorerView(IGLCanvas glCanvas) {
		super(glCanvas, VIEW_TYPE, VIEW_NAME);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);
		// HCSRelationshipExplorerElementFactory factory = new HCSRelationshipExplorerElementFactory();
		// getRootDecorator().setContent(factory.create(null));
		getRootDecorator().setContent(new INestableColumn());
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return new SerializedRelationshipExplorerView(this);
	}

	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		return DataSupportDefinitions.tableBased;
	}

	@Override
	protected RelationshipExplorerElement getContent() {
		return (RelationshipExplorerElement) super.getContent();
	}

	@Override
	protected void applyTablePerspectives(GLElementDecorator root, List<TablePerspective> all,
			List<TablePerspective> added, List<TablePerspective> removed) {
		// RelationshipExplorerElement content = getContent();
		// for (TablePerspective add : added) {
		//
		// }
		// for (TablePerspective rem : removed) {
		// for (Iterator<TablePerspective> it = content.getTablePerspectives().iterator(); it.hasNext();) {
		// if (rem.equals(it.next())) {
		// it.remove();
		// break;
		// }
		// }
		// }
	}

}
