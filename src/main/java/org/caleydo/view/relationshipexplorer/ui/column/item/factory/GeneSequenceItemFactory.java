/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.item.factory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactories;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactories.GLElementSupplier;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.view.relationshipexplorer.ui.collection.IDCollection;

import com.google.common.base.Predicate;
import com.google.common.collect.Lists;

/**
 * @author Christian
 *
 */
public class GeneSequenceItemFactory implements IItemFactory {

	protected final IDCollection collection;

	public GeneSequenceItemFactory(IDCollection collection) {
		this.collection = collection;
	}

	@Override
	public GLElement createItem(Object elementID) {
		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
				collection.getBroadcastingIDType());
		Set<Integer> ids = mappingManager.<Object, Integer> getIDAsSet(collection.getBroadcastingIDType(),
				IDType.getIDType(EGeneIDTypes.DAVID.name()), elementID);
		if (ids == null)
			ids = new HashSet<>();
		List<Integer> davidIDs = Lists.<Integer> newArrayList(ids);

		GLElementFactoryContext context = GLElementFactoryContext.builder()
.put(List.class, davidIDs)
				.put(IDType.class, IDType.getIDType(EGeneIDTypes.DAVID.name())).build();
		List<GLElementSupplier> suppliers = GLElementFactories.getExtensions(context, "relexplorer",
				new Predicate<String>() {

					@Override
					public boolean apply(String input) {
						return input.equals("genesequence");
					}
				});
		if (!suppliers.isEmpty()) {
			GLElement sequenceView = suppliers.get(0).get();
			if (sequenceView == null)
				sequenceView = new GLElement();
			sequenceView.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(100, 16));

			return sequenceView;
		}

		return new GLElement();
	}
}
