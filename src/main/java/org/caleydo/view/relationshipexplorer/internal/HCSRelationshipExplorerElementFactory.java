/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.internal;

import gleem.linalg.Vec2f;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.view.relationshipexplorer.ui.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.GroupingColumn;
import org.caleydo.view.relationshipexplorer.ui.IDColumn;
import org.caleydo.view.relationshipexplorer.ui.PathwayColumn;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.TabularDataColumn;

/**
 * @author Christian
 *
 */
public class HCSRelationshipExplorerElementFactory implements IGLElementFactory {

	@Override
	public String getId() {
		return "relationship explorer";
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {
		RelationshipExplorerElement relationshipExplorer = new RelationshipExplorerElement();

		List<AEntityColumn> columns = new ArrayList<>();

		columns.add(new PathwayColumn(relationshipExplorer));

		columns.add(new IDColumn(IDType.getIDType(EGeneIDTypes.ENTREZ_GENE_ID.name()), IDCategory
.getIDCategory(
				EGeneIDTypes.GENE.name()).getHumanReadableIDType(), relationshipExplorer));

		for (IDataDomain dd : DataDomainManager.get().getAllDataDomains()) {
			if (dd instanceof ATableBasedDataDomain && dd.getLabel().contains("Activity")) {
				ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) dd;
				if (dataDomain.hasIDCategory(IDCategory.getIDCategory(EGeneIDTypes.GENE.name()))) {
					columns.add(new TabularDataColumn(dataDomain.getDefaultTablePerspective(), IDCategory
							.getIDCategory(EGeneIDTypes.GENE.name()), relationshipExplorer));
				}
				break;
			}
		}
		IDColumn compoundColumn = new IDColumn(IDType.getIDType("COMPOUND_ID"),
 IDType.getIDType("COMPOUND_ID"),
				relationshipExplorer);
		compoundColumn.setLabel("Compounds");
		columns.add(compoundColumn);

		for (IDataDomain dd : DataDomainManager.get().getAllDataDomains()) {
			if (dd instanceof ATableBasedDataDomain && dd.getLabel().contains("Finger")) {
				ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) dd;
				if (dataDomain.hasIDCategory(IDCategory.getIDCategory(EGeneIDTypes.GENE.name()))) {
					columns.add(new TabularDataColumn(dataDomain.getDefaultTablePerspective(), IDCategory
							.getIDCategory(EGeneIDTypes.GENE.name()), relationshipExplorer));
				}
				break;
			}
		}

		addGroupings(columns, relationshipExplorer);

		float totalMinSize = 0;
		for (AEntityColumn column : columns) {
			Vec2f minSize = column.getMinSize();
			totalMinSize += minSize.x();
		}

		for (int i = 0; i < columns.size(); i++) {
			AEntityColumn column = columns.get(i);
			Vec2f minSize = column.getMinSize();
			column.setLayoutData(minSize.x() / totalMinSize);
			relationshipExplorer.addEntityColumn(column);
			// if (i < columns.size() - 1) {
			// GLElement columnSpacer = new GLElement(GLRenderers.fillRect(Color.LIGHT_GRAY));
			// columnSpacer.setSize(2, Float.NaN);
			// relationshipExplorer.add(columnSpacer);
			// }
		}

		return relationshipExplorer;
	}

	protected void addGroupings(List<AEntityColumn> columns, RelationshipExplorerElement relationshipExplorer) {
		for (IDataDomain dataDomain : DataDomainManager.get().getAllDataDomains()) {
			if (dataDomain instanceof ATableBasedDataDomain) {
				ATableBasedDataDomain dd = (ATableBasedDataDomain) dataDomain;
				DataSetDescription desc = dd.getDataSetDescription();
				if (desc.getColumnIDSpecification().getIdType().equals("FINGERPRINT_ID")
						|| desc.getRowIDSpecification().getIdType().equals("FINGERPRINT_ID")) {

					IDType compoundIDType = IDType.getIDType("FINGERPRINT_ID");
					if (dd.getDimensionIDCategory() == compoundIDType.getIDCategory()) {
						Set<String> perspectiveIDs = dd.getDimensionPerspectiveIDs();
						String defaultPerspectiveID = dd.getDefaultTablePerspective().getDimensionPerspective()
								.getPerspectiveID();
						for (String perspectiveID : perspectiveIDs) {
							if (!perspectiveID.equals(defaultPerspectiveID)) {
								Perspective perspective = dd.getTable().getDimensionPerspective(perspectiveID);
								columns.add(new GroupingColumn(perspective, relationshipExplorer));
								return;
							}
						}

					} else {
						Set<String> perspectiveIDs = dd.getRecordPerspectiveIDs();
						String defaultPerspectiveID = dd.getDefaultTablePerspective().getRecordPerspective()
								.getPerspectiveID();
						for (String perspectiveID : perspectiveIDs) {
							if (!perspectiveID.equals(defaultPerspectiveID)) {
								Perspective perspective = dd.getTable().getRecordPerspective(perspectiveID);
								columns.add(new GroupingColumn(perspective, relationshipExplorer));
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
	public boolean apply(GLElementFactoryContext context) {
		return true;
	}

}
