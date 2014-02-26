/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.structure;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLGraphics;

/**
 * @author Christian
 *
 */
public class CompoundStructureElement extends GLElement {

	protected ATableBasedDataDomain dataDomain;

	/**
	 *
	 */
	public CompoundStructureElement() {

		// Not the most elegant way but it does the job to get the smiles dataset
		for (IDataDomain dd : DataDomainManager.get().getAllDataDomains()) {
			if (dd instanceof ATableBasedDataDomain && dd.getLabel().contains("Smiles")) {
				dataDomain = (ATableBasedDataDomain) dd;
				break;
			}
		}
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {

		IDType internalCompoundIDType = dataDomain.getDefaultTablePerspective().getRecordPerspective().getIdType();
		IDType originalCompoundIDType = dataDomain.getDatasetDescriptionIDType(internalCompoundIDType.getIDCategory());
		int internalCompoundID = dataDomain.getDefaultTablePerspective().getRecordPerspective().getVirtualArray()
				.get(0);
		IDMappingManager idMappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
				originalCompoundIDType.getIDCategory());
		int originalCompoundID = idMappingManager.getID(internalCompoundIDType, originalCompoundIDType,
				internalCompoundID);

		IDType internalDimensionIDType = dataDomain.getDefaultTablePerspective().getDimensionPerspective().getIdType();
		int smilesColumnID = dataDomain.getDefaultTablePerspective().getRecordPerspective().getVirtualArray().get(0);

		String smileString = (String) dataDomain.getRaw(internalCompoundIDType, internalCompoundID,
				internalDimensionIDType, smilesColumnID);

		g.color(Color.RED).fillRect(0, 0, w, h);
		g.drawText("First Compound ID: " + originalCompoundID + ", Smile String: " + smileString, 0, 0, w, 20);
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		super.renderPickImpl(g, w, h);
	}

}
