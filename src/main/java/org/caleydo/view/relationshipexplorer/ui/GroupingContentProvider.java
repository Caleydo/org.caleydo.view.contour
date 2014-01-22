/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;

/**
 * @author Christian
 *
 */
public class GroupingContentProvider extends TextualContentProvider {

	protected final ATableBasedDataDomain dataDomain;
	protected final Perspective perspective;
	protected final GroupList groupList;

	public GroupingContentProvider(Perspective perspective) {

		this.perspective = perspective;
		this.dataDomain = (ATableBasedDataDomain) perspective.getDataDomain();
		this.groupList = perspective.getVirtualArray().getGroupList();

		for (Group group : groupList) {
			GLElement el = new GLElement(GLRenderers.drawText(group.getLabel()));
			el.setSize(Float.NaN, ITEM_HEIGHT);
			items.add(el);
		}

		// for (IDataDomain dataDomain : DataDomainManager.get().getAllDataDomains()) {
		// if (dataDomain instanceof ATableBasedDataDomain) {
		// ATableBasedDataDomain dd = (ATableBasedDataDomain) dataDomain;
		// DataSetDescription desc = dd.getDataSetDescription();
		// if (desc.getColumnIDSpecification().getIdType().equals("COMPOUND_ID")
		// || desc.getRowIDSpecification().getIdType().equals("COMPOUND_ID")) {
		//
		// IDType compoundIDType = IDType.getIDType("COMPOUND_ID");
		// if (dd.getDimensionIDCategory() == compoundIDType.getIDCategory()) {
		// Set<String> perspectiveIDs = dd.getDimensionPerspectiveIDs();
		// String defaultPerspectiveID = dd.getDefaultTablePerspective().getDimensionPerspective()
		// .getPerspectiveID();
		// for (String perspectiveID : perspectiveIDs) {
		// if (!perspectiveID.equals(defaultPerspectiveID)) {
		// GroupList groupList = dd.getDimensionVA(perspectiveID).getGroupList();
		//
		// for (Group group : groupList) {
		// GLElement el = new GLElement(GLRenderers.drawText(group.getLabel()));
		// el.setSize(Float.NaN, ITEM_HEIGHT);
		// items.add(el);
		// }
		// return;
		// }
		// }
		//
		// } else {
		// Set<String> perspectiveIDs = dd.getRecordPerspectiveIDs();
		// String defaultPerspectiveID = dd.getDefaultTablePerspective().getRecordPerspective()
		// .getPerspectiveID();
		// for (String perspectiveID : perspectiveIDs) {
		// if (!perspectiveID.equals(defaultPerspectiveID)) {
		// GroupList groupList = dd.getRecordVA(perspectiveID).getGroupList();
		// for (Group group : groupList) {
		// GLElement el = new GLElement(GLRenderers.drawText(group.getLabel()));
		// el.setSize(Float.NaN, ITEM_HEIGHT);
		// items.add(el);
		// }
		// return;
		// }
		// }
		// }
		// return;
		// }
		// }

	}

	@Override
	public String getLabel() {
		return perspective.getLabel();
	}

}
