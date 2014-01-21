/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.GLLayoutDatas;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.internal.Activator;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 *
 *
 * @author Christian
 *
 */
public class RelationshipExplorerElement extends GLElementContainer {

	public RelationshipExplorerElement() {
		super(GLLayouts.flowHorizontal(5));
		GLElement header = new GLElement(GLRenderers.drawText("Pathways"));
		header.setSize(200, 20);
		add(new EntityColumn(header, new PathwayContentProvider()));

		header = new GLElement(GLRenderers.drawText("Genes"));
		header.setSize(200, 20);
		add(new EntityColumn(header, new GeneContentProvider()));

		header = new GLElement(GLRenderers.drawText("Compounds"));
		header.setSize(200, 20);
		add(new EntityColumn(header, new CompoundContentProvider()));

		header = new GLElement(GLRenderers.drawText("Clusters"));
		header.setSize(200, 20);
		add(new EntityColumn(header, new ClusterContentProvider()));
	}

	public Iterable<TablePerspective> getTablePerspectives() {
		return Iterables.filter(Iterables.transform(this, GLLayoutDatas.toLayoutData(TablePerspective.class, null)),
				Predicates.notNull());
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.pushResourceLocator(Activator.getResourceLocator());
		super.renderImpl(g, w, h);
		g.popResourceLocator();
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		g.pushResourceLocator(Activator.getResourceLocator());
		super.renderPickImpl(g, w, h);
		g.popResourceLocator();
	}

}
