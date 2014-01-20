/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.view.relationshipexplorer.ui.EntityColumn.IEntityColumnContentProvider;

/**
 * @author Christian
 *
 */
public class GeneContentProvider implements IEntityColumnContentProvider {

	protected List<GLElement> items = new ArrayList<>();

	public GeneContentProvider() {
		IDCategory geneCategory = IDCategory.getIDCategory(EGeneIDTypes.GENE.name());
		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(geneCategory);
		IIDTypeMapper<Integer, String> mapper = mappingManager.getIDTypeMapper(
				IDType.getIDType(EGeneIDTypes.ENTREZ_GENE_ID.name()), geneCategory.getHumanReadableIDType());

		for (Object id : mappingManager.getAllMappedIDs(IDType.getIDType(EGeneIDTypes.ENTREZ_GENE_ID.name()))) {
			Set<String> geneNames = mapper.apply((Integer) id);
			if (geneNames != null) {
				for (String geneName : geneNames) {
					GLElement el = new GLElement(GLRenderers.drawText(geneName));
					el.setSize(200, 16);
					// el.setVisibility(EVisibility.HIDDEN);
					items.add(el);
				}
			}
		}
	}

	@Override
	public Vec2f getMinSize() {
		return new Vec2f(200, items.size() * 16 + (items.size() - 1) * 2);
	}

	@Override
	public List<GLElement> getContent() {
		return items;
	}

}
