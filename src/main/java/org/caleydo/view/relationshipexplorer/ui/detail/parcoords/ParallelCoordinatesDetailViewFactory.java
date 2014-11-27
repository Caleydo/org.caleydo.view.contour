/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail.parcoords;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.detail.DetailViewWindow;
import org.caleydo.view.relationshipexplorer.ui.detail.IDetailViewFactory;

/**
 * @author Christian
 *
 */
public class ParallelCoordinatesDetailViewFactory implements IDetailViewFactory {

	@Override
	public GLElement createDetailView(IEntityCollection collection, DetailViewWindow window, ConTourElement contour) {
		TablePerspective tablePerspective = ((TabularDataCollection) collection).getTablePerspective();
		// GLElementFactoryContext context = GLElementFactoryContext.builder().withData(tablePerspective).build();
		// List<GLElementSupplier> suppliers = GLElementFactories.getExtensions(context, "relexplorer",
		// new Predicate<String>() {
		//
		// @Override
		// public boolean apply(String input) {
		// return input.equals("paco");
		// }
		// });
		//
		// GLElement detailView = null;
		// if (suppliers.isEmpty()) {
		// detailView = new GLElement() {
		// @Override
		// public Vec2f getMinSize() {
		// return new Vec2f(300, 300);
		// }
		// };
		// detailView.setRenderer(GLRenderers.fillRect(Color.BLUE));
		//
		// } else {
		// detailView = suppliers.get(0).get();
		// }

		ParCoordsElement element = new ParCoordsElement(tablePerspective, (TabularDataCollection) collection, contour);

		window.clearTitleElements();
		window.addShowFilteredItems(element, false);

		return element;

	}

	@Override
	public DetailViewWindow createWindow(IEntityCollection collection, ConTourElement contour) {
		return new DetailViewWindow(collection, contour);
	}

}
