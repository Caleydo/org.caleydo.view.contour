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
import org.caleydo.view.relationshipexplorer.ui.EntityColumn;
import org.caleydo.view.relationshipexplorer.ui.GroupingContentProvider;
import org.caleydo.view.relationshipexplorer.ui.IDContentProvider;
import org.caleydo.view.relationshipexplorer.ui.PathwayContentProvider;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.TabularDatasetContentProvider;

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

		List<EntityColumn> columns = new ArrayList<>();


		columns.add(new EntityColumn(new PathwayContentProvider()));

		columns.add(new EntityColumn(new IDContentProvider(IDType
				.getIDType(EGeneIDTypes.ENTREZ_GENE_ID.name()))));

		for (IDataDomain dd : DataDomainManager.get().getAllDataDomains()) {
			if (dd instanceof ATableBasedDataDomain) {
				ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) dd;
				if (dataDomain.hasIDCategory(IDCategory.getIDCategory(EGeneIDTypes.GENE.name()))) {
					columns.add(new EntityColumn(new TabularDatasetContentProvider(dataDomain
							.getDefaultTablePerspective(), IDCategory.getIDCategory(EGeneIDTypes.GENE.name()))));
				}
			}
		}

		addGroupings(columns);

		float totalMinSize = 0;
		for (EntityColumn column : columns) {
			Vec2f minSize = column.getBody().getMinSize();
			totalMinSize += minSize.x();
		}

		for (EntityColumn column : columns) {
			Vec2f minSize = column.getBody().getMinSize();
			column.setLayoutData(minSize.x() / totalMinSize);
			relationshipExplorer.add(column);
		}

		return relationshipExplorer;
	}

	protected void addGroupings(List<EntityColumn> columns) {
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
								Perspective perspective = dd.getTable().getDimensionPerspective(perspectiveID);
								columns.add(new EntityColumn(new GroupingContentProvider(
										perspective)));
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
								columns.add(new EntityColumn(new GroupingContentProvider(
										perspective)));
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
