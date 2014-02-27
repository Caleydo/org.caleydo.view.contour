/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.internal;

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
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.column.GroupCollection;
import org.caleydo.view.relationshipexplorer.ui.column.IDCollection;
import org.caleydo.view.relationshipexplorer.ui.column.PathwayCollection;
import org.caleydo.view.relationshipexplorer.ui.column.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.list.ColumnTree;

/**
 * @author Christian
 *
 */
public class HCSRelationshipExplorerElementFactory2 implements IGLElementFactory {
	@Override
	public String getId() {
		return "relationship explorer";
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {

		RelationshipExplorerElement relationshipExplorer = new RelationshipExplorerElement();
		PathwayCollection pathwayCollection = new PathwayCollection(relationshipExplorer);

		ColumnTree pathwayColumn = new ColumnTree(pathwayCollection.createColumnModel());

		relationshipExplorer.addColumn(pathwayColumn);
		IDCollection geneCollection = new IDCollection(IDType.getIDType(EGeneIDTypes.ENTREZ_GENE_ID.name()), IDCategory
				.getIDCategory(EGeneIDTypes.GENE.name()).getHumanReadableIDType(), relationshipExplorer);

		ColumnTree geneColumn = new ColumnTree(geneCollection.createColumnModel());

		relationshipExplorer.addColumn(geneColumn);

		for (IDataDomain dd : DataDomainManager.get().getAllDataDomains()) {
			if (dd instanceof ATableBasedDataDomain && dd.getLabel().contains("Activity")) {
				ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) dd;
				if (dataDomain.hasIDCategory(IDCategory.getIDCategory(EGeneIDTypes.GENE.name()))) {
					// ColumnTree activityColumn = new ColumnTree();
					TabularDataCollection activityCollection = new TabularDataCollection(
							dataDomain.getDefaultTablePerspective(),
							IDCategory.getIDCategory(EGeneIDTypes.GENE.name()), relationshipExplorer);

					// ColumnTree activityColumn = new ColumnTree(activityCollection.createColumnModel());
					//
					// relationshipExplorer.addColumn(activityColumn);

					geneColumn.addNestedColumn(activityCollection.createColumnModel(), geneColumn.getRootColumn());
					// row.add(activityColumn);
				}
				break;
			}
		}

		IDCollection compoundCollection = new IDCollection(IDType.getIDType("COMPOUND_ID"),
				IDType.getIDType("COMPOUND_ID"), relationshipExplorer);
		compoundCollection.setLabel("Compounds");
		ColumnTree compoundColumn = new ColumnTree(compoundCollection.createColumnModel());

		relationshipExplorer.addColumn(compoundColumn);

		ColumnTree clusterColumn = getClusterColumn(relationshipExplorer);

		for (IDataDomain dd : DataDomainManager.get().getAllDataDomains()) {
			if (dd instanceof ATableBasedDataDomain && dd.getLabel().contains("Finger")) {
				ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) dd;
				if (dataDomain.hasIDCategory(IDCategory.getIDCategory(EGeneIDTypes.GENE.name()))) {
					TabularDataCollection fingerprintCollection = new TabularDataCollection(
							dataDomain.getDefaultTablePerspective(),
							IDCategory.getIDCategory(EGeneIDTypes.GENE.name()), relationshipExplorer);

					// ColumnTree fingerprintColumn = new ColumnTree(fingerprintCollection.createColumnModel());
					//
					// relationshipExplorer.addColumn(fingerprintColumn);

					//
					clusterColumn.addNestedColumn(fingerprintCollection.createColumnModel(),
							clusterColumn.getRootColumn());
				}
				break;
			}
		}

		if (clusterColumn != null)
			relationshipExplorer.addColumn(clusterColumn);

		// float totalMinSize = 0;
		// for (AEntityColumn column : columns) {
		// Vec2f minSize = column.getMinSize();
		// totalMinSize += minSize.x();
		// }
		//
		// for (int i = 0; i < columns.size(); i++) {
		// AEntityColumn column = columns.get(i);
		// Vec2f minSize = column.getMinSize();
		// column.setLayoutData(minSize.x() / totalMinSize);
		// relationshipExplorer.addEntityColumn(column);
		// // if (i < columns.size() - 1) {
		// // GLElement columnSpacer = new GLElement(GLRenderers.fillRect(Color.LIGHT_GRAY));
		// // columnSpacer.setSize(2, Float.NaN);
		// // relationshipExplorer.add(columnSpacer);
		// // }
		// }

		return relationshipExplorer;
	}

	protected ColumnTree getClusterColumn(RelationshipExplorerElement relationshipExplorer) {
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
								GroupCollection clusterCollection = new GroupCollection(perspective,
										relationshipExplorer);

								return new ColumnTree(clusterCollection.createColumnModel());
							}
						}

					} else {
						Set<String> perspectiveIDs = dd.getRecordPerspectiveIDs();
						String defaultPerspectiveID = dd.getDefaultTablePerspective().getRecordPerspective()
								.getPerspectiveID();
						for (String perspectiveID : perspectiveIDs) {
							if (!perspectiveID.equals(defaultPerspectiveID)) {
								Perspective perspective = dd.getTable().getRecordPerspective(perspectiveID);
								GroupCollection clusterCollection = new GroupCollection(perspective,
										relationshipExplorer);

								return new ColumnTree(clusterCollection.createColumnModel());
							}
						}
					}
				}
			}
		}
		return null;
	}

	@Override
	public boolean apply(GLElementFactoryContext context) {
		return true;
	}
}
