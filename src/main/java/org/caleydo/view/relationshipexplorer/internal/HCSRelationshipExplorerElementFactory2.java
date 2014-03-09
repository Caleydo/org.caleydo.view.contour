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
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.collection.GroupCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.IDCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.PathwayCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.column.CompositeComparator;
import org.caleydo.view.relationshipexplorer.ui.column.ItemComparators;
import org.caleydo.view.relationshipexplorer.ui.column.ItemComparators.SelectionMappingComparator;
import org.caleydo.view.relationshipexplorer.ui.column.ItemComparators.TotalMappingComparator;
import org.caleydo.view.relationshipexplorer.ui.column.ItemComparators.VisibleMappingComparator;
import org.caleydo.view.relationshipexplorer.ui.column.factory.ActivityColumnFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.MedianSummaryItemFactory;
import org.caleydo.view.relationshipexplorer.ui.column.operation.AddChildColumnCommand;
import org.caleydo.view.relationshipexplorer.ui.column.operation.AddColumnTreeCommand;
import org.caleydo.view.relationshipexplorer.ui.column.operation.ColumnSortingCommand;
import org.caleydo.view.relationshipexplorer.ui.column.operation.CompositeHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.column.operation.SetSummaryItemFactoryCommand;
import org.caleydo.view.relationshipexplorer.ui.list.ColumnTree;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;
import org.caleydo.view.relationshipexplorer.ui.list.NestableColumn;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;

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

		IDCollection geneCollection = new IDCollection(IDType.getIDType(EGeneIDTypes.ENTREZ_GENE_ID.name()), IDCategory
				.getIDCategory(EGeneIDTypes.GENE.name()).getHumanReadableIDType(), relationshipExplorer);

		TabularDataCollection activityCollection = null;

		for (IDataDomain dd : DataDomainManager.get().getAllDataDomains()) {
			if (dd instanceof ATableBasedDataDomain && dd.getLabel().contains("Activity")) {
				ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) dd;
				if (dataDomain.hasIDCategory(IDCategory.getIDCategory(EGeneIDTypes.GENE.name()))) {
					// ColumnTree activityColumn = new ColumnTree();
					activityCollection = new TabularDataCollection(dataDomain.getDefaultTablePerspective(),
							IDCategory.getIDCategory(EGeneIDTypes.GENE.name()), relationshipExplorer);
					activityCollection.setLabel("Activities");
					activityCollection.setColumnFactory(new ActivityColumnFactory(activityCollection,
							relationshipExplorer));

					// ColumnTree activityColumn = new ColumnTree(activityCollection.createColumnModel());
					//
					// relationshipExplorer.addColumn(activityColumn);

					// geneColumn.addNestedColumn(activityCollection.createColumnModel(), geneColumn.getRootColumn());
					// row.add(activityColumn);
				}
				break;
			}
		}

		IDCollection compoundCollection = new IDCollection(IDType.getIDType("COMPOUND_ID"),
				IDType.getIDType("COMPOUND_ID"), relationshipExplorer);
		compoundCollection.setLabel("Compounds");

		TabularDataCollection fingerprintCollection = null;
		for (IDataDomain dd : DataDomainManager.get().getAllDataDomains()) {
			if (dd instanceof ATableBasedDataDomain && dd.getLabel().contains("Finger")) {
				ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) dd;
				if (dataDomain.hasIDCategory(IDCategory.getIDCategory(EGeneIDTypes.GENE.name()))) {
					fingerprintCollection = new TabularDataCollection(dataDomain.getDefaultTablePerspective(),
							IDCategory.getIDCategory(EGeneIDTypes.GENE.name()), relationshipExplorer);

					// ColumnTree fingerprintColumn = new ColumnTree(fingerprintCollection.createColumnModel());
					//
					// relationshipExplorer.addColumn(fingerprintColumn);

					//

				}
				break;
			}
		}

		GroupCollection clusterCollection = getClusterColumn(relationshipExplorer);

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

		CompositeHistoryCommand initCommand = new CompositeHistoryCommand();

		IHistoryCommand c = new AddColumnTreeCommand(pathwayCollection, relationshipExplorer);
		initCommand.add(c);
		ColumnTree pathwayColumn = (ColumnTree) c.execute();

		c = new AddChildColumnCommand(clusterCollection, pathwayColumn.getRootColumn().getColumnModel().getHistoryID(),
				relationshipExplorer.getHistory());
		initCommand.add(c);
		NestableColumn childColumn = (NestableColumn) c.execute();

		addDefaultSortingCommand(relationshipExplorer, pathwayColumn.getRootColumn().getColumnModel(),
				childColumn.getColumnModel(), initCommand);

		// c = new AddChildColumnCommand(compoundCollection, childColumn.getColumnModel().getHistoryID(),
		// relationshipExplorer.getHistory());
		// initCommand.add(c);
		// childColumn = (NestableColumn) c.execute();

		// addDefaultSortingCommand(relationshipExplorer, childColumn.getColumnModel(),
		// childColumn.getColumnModel(), initCommand);

		// ----

		c = new AddColumnTreeCommand(geneCollection, relationshipExplorer);
		initCommand.add(c);
		ColumnTree geneColumn = (ColumnTree) c.execute();

		c = new AddChildColumnCommand(activityCollection, geneColumn.getRootColumn().getColumnModel().getHistoryID(),
				relationshipExplorer.getHistory());
		initCommand.add(c);
		childColumn = (NestableColumn) c.execute();

		addDefaultSortingCommand(relationshipExplorer, geneColumn.getRootColumn().getColumnModel(),
				childColumn.getColumnModel(), initCommand);

		// ----

		c = new AddColumnTreeCommand(compoundCollection, relationshipExplorer);
		initCommand.add(c);
		ColumnTree compoundColumn = (ColumnTree) c.execute();

		c = new AddChildColumnCommand(activityCollection, compoundColumn.getRootColumn().getColumnModel()
				.getHistoryID(), relationshipExplorer.getHistory());
		initCommand.add(c);
		childColumn = (NestableColumn) c.execute();

		addDefaultSortingCommand(relationshipExplorer, compoundColumn.getRootColumn().getColumnModel(),
				childColumn.getColumnModel(), initCommand);

		// ----

		c = new AddColumnTreeCommand(fingerprintCollection, relationshipExplorer);
		initCommand.add(c);
		ColumnTree fingerprintColumn = (ColumnTree) c.execute();

		c = new AddChildColumnCommand(compoundCollection, fingerprintColumn.getRootColumn().getColumnModel()
				.getHistoryID(), relationshipExplorer.getHistory());
		initCommand.add(c);
		childColumn = (NestableColumn) c.execute();

		addDefaultSortingCommand(relationshipExplorer, fingerprintColumn.getRootColumn().getColumnModel(),
				childColumn.getColumnModel(), initCommand);

		// ----

		c = new AddColumnTreeCommand(clusterCollection, relationshipExplorer);
		initCommand.add(c);
		ColumnTree clusterColumn = (ColumnTree) c.execute();

		c = new AddChildColumnCommand(fingerprintCollection, clusterColumn.getRootColumn().getColumnModel()
				.getHistoryID(), relationshipExplorer.getHistory());
		initCommand.add(c);
		NestableColumn fCol = (NestableColumn) c.execute();

		// ColumnTree clusterColumn = new ColumnTree(clusterCollection.createColumnModel());

		c = new SetSummaryItemFactoryCommand((AEntityColumn) fCol.getColumnModel(), MedianSummaryItemFactory.class,
				relationshipExplorer.getHistory());
		initCommand.add(c);
		c.execute();

		addDefaultSortingCommand(relationshipExplorer, clusterColumn.getRootColumn().getColumnModel(),
				fCol.getColumnModel(), initCommand);

		relationshipExplorer.getHistory().setInitCommand(initCommand);

		// System.out.println("Before overlap computation");
		// List<MappingOverlap> overlaps = EntityMappingUtil.getMappingOverlap(pathwayCollection, clusterCollection,
		// compoundCollection);
		// System.out.println("Overlap size: " + overlaps.size());
		// clusterColumn.addNestedColumn(fingerCol, clusterColumn.getRootColumn());
		//
		// relationshipExplorer.addColumn(clusterColumn);

		return relationshipExplorer;
	}

	protected void addDefaultSortingCommand(RelationshipExplorerElement relationshipExplorer,
			IColumnModel parentColumn, IColumnModel childColumn, CompositeHistoryCommand initCommand) {
		CompositeComparator<NestableItem> comparator = new CompositeComparator<>(
				ItemComparators.SELECTED_ITEMS_COMPARATOR, new SelectionMappingComparator(childColumn,
						relationshipExplorer.getHistory()), new VisibleMappingComparator(childColumn,
						relationshipExplorer.getHistory()), new TotalMappingComparator(childColumn,
						relationshipExplorer.getHistory()));

		ColumnSortingCommand c = new ColumnSortingCommand(parentColumn, comparator, relationshipExplorer.getHistory());
		c.execute();
		initCommand.add(c);
	}

	protected GroupCollection getClusterColumn(RelationshipExplorerElement relationshipExplorer) {
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
								return new GroupCollection(perspective, relationshipExplorer);
							}
						}

					} else {
						Set<String> perspectiveIDs = dd.getRecordPerspectiveIDs();
						String defaultPerspectiveID = dd.getDefaultTablePerspective().getRecordPerspective()
								.getPerspectiveID();
						for (String perspectiveID : perspectiveIDs) {
							if (!perspectiveID.equals(defaultPerspectiveID)) {
								Perspective perspective = dd.getTable().getRecordPerspective(perspectiveID);
								return new GroupCollection(perspective, relationshipExplorer);
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
