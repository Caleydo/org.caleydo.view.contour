/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail;

import gleem.linalg.Vec2f;

import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactories;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactories.GLElementSupplier;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.detail.parcoords.ParCoordsElement;

import com.google.common.base.Predicate;

/**
 * @author Christian
 *
 */
public class ParallelCoordinatesDetailViewFactory implements IDetailViewFactory {

	protected final RelationshipExplorerElement relationshipExplorer;

	public ParallelCoordinatesDetailViewFactory(RelationshipExplorerElement relationshipExplorer) {
		this.relationshipExplorer = relationshipExplorer;
	}

	@Override
	public GLElement create(IEntityCollection collection) {
		TablePerspective tablePerspective = ((TabularDataCollection) collection).getTablePerspective();
		GLElementFactoryContext context = GLElementFactoryContext.builder().withData(tablePerspective).build();
		List<GLElementSupplier> suppliers = GLElementFactories.getExtensions(context, "relexplorer",
				new Predicate<String>() {

					@Override
					public boolean apply(String input) {
						return input.equals("paco");
					}
				});

		GLElement detailView = null;
		if (suppliers.isEmpty()) {
			detailView = new GLElement() {
				@Override
				public Vec2f getMinSize() {
					return new Vec2f(300, 300);
				}
			};
			detailView.setRenderer(GLRenderers.fillRect(Color.BLUE));

		} else {
			detailView = suppliers.get(0).get();
		}

		return new ParCoordsElement(tablePerspective, collection, relationshipExplorer);

	}

}
