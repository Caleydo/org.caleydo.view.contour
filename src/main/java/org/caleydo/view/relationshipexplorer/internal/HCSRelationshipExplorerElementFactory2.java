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
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.view.relationshipexplorer.ui.column.GroupingColumn;
import org.caleydo.view.relationshipexplorer.ui.column.IDColumn;
import org.caleydo.view.relationshipexplorer.ui.column.PathwayColumn;
import org.caleydo.view.relationshipexplorer.ui.column.TabularDataColumn;
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

		AnimatedGLElementContainer row = new AnimatedGLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 10,
				GLPadding.ZERO));

		ColumnTree pathwayColumn = new ColumnTree(new PathwayColumn(null));

		row.add(pathwayColumn);

		ColumnTree geneColumn = new ColumnTree(new IDColumn(IDType.getIDType(EGeneIDTypes.ENTREZ_GENE_ID.name()),
				IDCategory.getIDCategory(EGeneIDTypes.GENE.name()).getHumanReadableIDType(), null));

		row.add(geneColumn);

		for (IDataDomain dd : DataDomainManager.get().getAllDataDomains()) {
			if (dd instanceof ATableBasedDataDomain && dd.getLabel().contains("Activity")) {
				ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) dd;
				if (dataDomain.hasIDCategory(IDCategory.getIDCategory(EGeneIDTypes.GENE.name()))) {
					ColumnTree activityColumn = new ColumnTree(new TabularDataColumn(
							dataDomain.getDefaultTablePerspective(),
							IDCategory.getIDCategory(EGeneIDTypes.GENE.name()), null));
					row.add(activityColumn);
				}
				break;
			}
		}

		ColumnTree compoundColumn = new ColumnTree(new IDColumn(IDType.getIDType("COMPOUND_ID"),
				IDType.getIDType("COMPOUND_ID"), null));

		row.add(compoundColumn);

		for (IDataDomain dd : DataDomainManager.get().getAllDataDomains()) {
			if (dd instanceof ATableBasedDataDomain && dd.getLabel().contains("Finger")) {
				ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) dd;
				if (dataDomain.hasIDCategory(IDCategory.getIDCategory(EGeneIDTypes.GENE.name()))) {
					ColumnTree fingerpringColumn = new ColumnTree(new TabularDataColumn(
							dataDomain.getDefaultTablePerspective(),
							IDCategory.getIDCategory(EGeneIDTypes.GENE.name()), null));
					row.add(fingerpringColumn);
				}
				break;
			}
		}

		ColumnTree clusterColumn = getClusterColumn();
		if (clusterColumn != null)
			row.add(clusterColumn);

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

		return row;
	}

	protected ColumnTree getClusterColumn() {
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
								return new ColumnTree(new GroupingColumn(perspective, null));
							}
						}

					} else {
						Set<String> perspectiveIDs = dd.getRecordPerspectiveIDs();
						String defaultPerspectiveID = dd.getDefaultTablePerspective().getRecordPerspective()
								.getPerspectiveID();
						for (String perspectiveID : perspectiveIDs) {
							if (!perspectiveID.equals(defaultPerspectiveID)) {
								Perspective perspective = dd.getTable().getRecordPerspective(perspectiveID);
								return new ColumnTree(new GroupingColumn(perspective, null));
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
