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

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.EntityColumn.IEntityColumnContentProvider;

/**
 * @author Christian
 *
 */
public class ClusterContentProvider implements IEntityColumnContentProvider {

	protected List<GLElement> items = new ArrayList<>();

	public ClusterContentProvider() {
		for (IDataDomain dataDomain : DataDomainManager.get().getAllDataDomains()) {
			if (dataDomain instanceof ATableBasedDataDomain) {
				ATableBasedDataDomain dd = (ATableBasedDataDomain) dataDomain;
				DataSetDescription desc = dd.getDataSetDescription();
				if (desc.getColumnIDSpecification().getIdType().equals("COMPOUND_ID")
						|| desc.getRowIDSpecification().getIdType().equals("COMPOUND_ID")) {

					IDType compoundIDType = IDType.getIDType("COMPOUND_ID");
					if (dd.getDimensionIDCategory() == compoundIDType.getIDCategory()) {
						Set<String> perspectiveIDs = dd.getDimensionPerspectiveIDs();
						String defaultPerspectiveID = dd.getDefaultTablePerspective().getDimensionPerspective()
								.getPerspectiveID();
						for (String perspectiveID : perspectiveIDs) {
							if (!perspectiveID.equals(defaultPerspectiveID)) {
								GroupList groupList = dd.getDimensionVA(perspectiveID).getGroupList();
								for (Group group : groupList) {
									GLElement el = new GLElement(GLRenderers.drawText(group.getLabel()));
									el.setSize(200, 16);
									items.add(el);
								}
								return;
							}
						}

					} else {
						Set<String> perspectiveIDs = dd.getRecordPerspectiveIDs();
						String defaultPerspectiveID = dd.getDefaultTablePerspective().getRecordPerspective()
								.getPerspectiveID();
						for (String perspectiveID : perspectiveIDs) {
							if (!perspectiveID.equals(defaultPerspectiveID)) {
								GroupList groupList = dd.getRecordVA(perspectiveID).getGroupList();
								for (Group group : groupList) {
									GLElement el = new GLElement(GLRenderers.drawText(group.getLabel()));
									el.setSize(200, 16);
									items.add(el);
								}
								return;
							}
						}
					}
					return;
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
