/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import java.util.Set;

import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;

/**
 * @author Christian
 *
 */
public class IDContentProvider extends TextualContentProvider {

	protected final IDType idType;

	public IDContentProvider(IDType idType) {
		this.idType = idType;
		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(idType.getIDCategory());
		IIDTypeMapper<Object, String> mapper = mappingManager.getIDTypeMapper(idType, idType.getIDCategory()
				.getHumanReadableIDType());

		for (Object id : mappingManager.getAllMappedIDs(idType)) {
			Set<String> humanReadableNames = mapper.apply(id);
			if (humanReadableNames != null) {
				for (String geneName : humanReadableNames) {
					GLElement el = new GLElement(GLRenderers.drawText(geneName));
					el.setSize(Float.NaN, ITEM_HEIGHT);
					// el.setVisibility(EVisibility.HIDDEN);
					items.add(el);
				}
			}
		}
	}

	@Override
	public String getLabel() {
		return idType.getIDCategory().getDenominationPlural(true);
	}

}
