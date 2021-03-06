/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.internal;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.DataSetDescription;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.collection.GroupCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.IDCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.PathwayCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.idprovider.IElementIDProvider;
import org.caleydo.view.relationshipexplorer.ui.column.CompositeComparator;
import org.caleydo.view.relationshipexplorer.ui.column.ItemComparators;
import org.caleydo.view.relationshipexplorer.ui.column.ItemComparators.SelectionMappingComparator;
import org.caleydo.view.relationshipexplorer.ui.column.ItemComparators.TotalMappingComparator;
import org.caleydo.view.relationshipexplorer.ui.column.ItemComparators.VisibleMappingComparator;
import org.caleydo.view.relationshipexplorer.ui.column.factory.ActivityColumnFactory;
import org.caleydo.view.relationshipexplorer.ui.command.AddChildColumnCommand;
import org.caleydo.view.relationshipexplorer.ui.command.AddColumnTreeCommand;
import org.caleydo.view.relationshipexplorer.ui.command.ColumnSortingCommand;
import org.caleydo.view.relationshipexplorer.ui.command.CompositeHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.detail.compound.CompoundDetailViewFactory;
import org.caleydo.view.relationshipexplorer.ui.detail.pathway.HTSPathwayDetailViewFactory;
import org.caleydo.view.relationshipexplorer.ui.list.ColumnTree;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;
import org.caleydo.view.relationshipexplorer.ui.list.NestableColumn;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;

/**
 * @author Christian
 *
 */
public class HTSRelationshipExplorerElementFactory implements IGLElementFactory {

	private class PathwayIDProvider implements IElementIDProvider {

		@Override
		public Set<Object> getElementIDs() {
			Set<PathwayGraph> allPathways = new HashSet<>(PathwayManager.get().getAllItems());

			IDType compoundIDType = IDType.getIDType("COMPOUND_ID");

			Set<Object> filteredPathways = new HashSet<>();
			for (PathwayGraph pathway : allPathways) {
				if (pathway.getLabel().toLowerCase().contains("metabolic pathway"))
					continue;
				Set<Object> compoundIDs = PathwayManager.get().getPathwayGeneIDs(pathway, compoundIDType);
				if (compoundIDs != null && !compoundIDs.isEmpty())
					filteredPathways.add(pathway);
			}

			return filteredPathways;
		}

	}

	private class GeneIDProvider implements IElementIDProvider {

		@Override
		public Set<Object> getElementIDs() {
			IDType geneIDType = IDType.getIDType(EGeneIDTypes.ENTREZ_GENE_ID.name());
			IDType compoundIDType = IDType.getIDType("COMPOUND_ID");
			IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
					geneIDType.getIDCategory());
			Set<Object> allGeneIDs = new HashSet<Object>(mappingManager.getAllMappedIDs(geneIDType));
			Set<Object> filteredGeneIDs = new HashSet<>();
			for (Object geneID : allGeneIDs) {
				Set<Object> compoundIDs = mappingManager.getIDAsSet(geneIDType, compoundIDType, geneID);
				if (compoundIDs != null && !compoundIDs.isEmpty()) {
					filteredGeneIDs.add(geneID);
				}
			}

			return filteredGeneIDs;
		}

	}

	@Override
	public String getId() {
		return "relationship explorer";
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {

		ConTourElement contour = new ConTourElement();

		PathwayCollection pathwayCollection = new PathwayCollection(new PathwayIDProvider(), contour);
		contour.registerEntityCollection(pathwayCollection);

		IDCollection geneCollection = new IDCollection(IDType.getIDType(EGeneIDTypes.ENTREZ_GENE_ID.name()), IDCategory
				.getIDCategory(EGeneIDTypes.GENE.name()).getHumanReadableIDType(), new GeneIDProvider(),
 contour);
		contour.registerEntityCollection(geneCollection);

		pathwayCollection.setDetailViewFactory(new HTSPathwayDetailViewFactory());

		TabularDataCollection activityCollection = null;

		for (IDataDomain dd : DataDomainManager.get().getAllDataDomains()) {
			if (dd instanceof ATableBasedDataDomain && dd.getLabel().contains("Activity")) {
				ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) dd;
				if (dataDomain.hasIDCategory(IDCategory.getIDCategory(EGeneIDTypes.GENE.name()))) {
					// ColumnTree activityColumn = new ColumnTree();
					activityCollection = new TabularDataCollection(dataDomain.getDefaultTablePerspective(),
							IDCategory.getIDCategory(EGeneIDTypes.GENE.name()), null, contour);
					contour.registerEntityCollection(activityCollection);
					activityCollection.setLabel("Activities");
					activityCollection.setColumnFactory(new ActivityColumnFactory());

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
				IDType.getIDType("COMPOUND_ID"), null, contour);
		contour.registerEntityCollection(compoundCollection);
		compoundCollection.setLabel("Compounds");

		ATableBasedDataDomain smilesDataDomain = null;
		// Not the most elegant way but it does the job to get the smiles dataset
		for (IDataDomain dd : DataDomainManager.get().getAllDataDomains()) {
			if (dd instanceof ATableBasedDataDomain && dd.getLabel().contains("Smiles")) {
				smilesDataDomain = (ATableBasedDataDomain) dd;
				break;
			}
		}
		compoundCollection.setDetailViewFactory(new CompoundDetailViewFactory(smilesDataDomain));

		TabularDataCollection fingerprintCollection = null;
		for (IDataDomain dd : DataDomainManager.get().getAllDataDomains()) {
			if (dd instanceof ATableBasedDataDomain && dd.getLabel().contains("Finger")) {
				ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) dd;
				if (dataDomain.hasIDCategory(IDCategory.getIDCategory(EGeneIDTypes.GENE.name()))) {
					fingerprintCollection = new TabularDataCollection(dataDomain.getDefaultTablePerspective(),
							IDCategory.getIDCategory(EGeneIDTypes.GENE.name()), null, contour);
					contour.registerEntityCollection(fingerprintCollection);
					fingerprintCollection.setLabel("Fingerprints");

					// ColumnTree fingerprintColumn = new ColumnTree(fingerprintCollection.createColumnModel());
					//
					// relationshipExplorer.addColumn(fingerprintColumn);

					//

				}
				break;
			}
		}

		GroupCollection clusterCollection100 = getClusterColumn(contour, "100");
		contour.registerEntityCollection(clusterCollection100);
		clusterCollection100.setLabel("Clusters");
		// GroupCollection clusterCollection50 = getClusterColumn(relationshipExplorer, "50");

		GroupCollection therapeuticGroupCollection = getClusterColumn(contour, "therap");
		contour.registerEntityCollection(therapeuticGroupCollection);
		therapeuticGroupCollection.setLabel("Therapeutic Groups");

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

		IHistoryCommand c = new AddColumnTreeCommand(pathwayCollection, contour);
		initCommand.add(c);
		ColumnTree pathwayColumn = (ColumnTree) c.execute();

		c = new AddChildColumnCommand(clusterCollection100, pathwayColumn.getRootColumn().getColumnModel()
				.getHistoryID(), contour);
		// c = new AddChildColumnCommand(geneCollection, pathwayColumn.getRootColumn().getColumnModel().getHistoryID(),
		// relationshipExplorer.getHistory());
		initCommand.add(c);
		NestableColumn childColumn = (NestableColumn) c.execute();

		addDefaultSortingCommand(contour, pathwayColumn.getRootColumn().getColumnModel(),
				childColumn.getColumnModel(), initCommand);

		// c = new AddChildColumnCommand(compoundCollection, childColumn.getColumnModel().getHistoryID(),
		// relationshipExplorer.getHistory());
		// initCommand.add(c);
		// childColumn = (NestableColumn) c.execute();

		// addDefaultSortingCommand(relationshipExplorer, childColumn.getColumnModel(),
		// childColumn.getColumnModel(), initCommand);

		// ----

		c = new AddColumnTreeCommand(geneCollection, contour);
		initCommand.add(c);
		ColumnTree geneColumn = (ColumnTree) c.execute();

		c = new AddChildColumnCommand(activityCollection, geneColumn.getRootColumn().getColumnModel().getHistoryID(),
				contour);
		initCommand.add(c);
		childColumn = (NestableColumn) c.execute();

		addDefaultSortingCommand(contour, geneColumn.getRootColumn().getColumnModel(),
				childColumn.getColumnModel(), initCommand);

		// ----

		c = new AddColumnTreeCommand(compoundCollection, contour);
		initCommand.add(c);
		ColumnTree compoundColumn = (ColumnTree) c.execute();

		c = new AddChildColumnCommand(activityCollection, compoundColumn.getRootColumn().getColumnModel()
				.getHistoryID(), contour);
		initCommand.add(c);
		childColumn = (NestableColumn) c.execute();

		addDefaultSortingCommand(contour, compoundColumn.getRootColumn().getColumnModel(),
				childColumn.getColumnModel(), initCommand);

		// ----

		// c = new AddColumnTreeCommand(fingerprintCollection, relationshipExplorer);
		// initCommand.add(c);
		// ColumnTree fingerprintColumn = (ColumnTree) c.execute();
		//
		// c = new AddChildColumnCommand(compoundCollection, fingerprintColumn.getRootColumn().getColumnModel()
		// .getHistoryID(), relationshipExplorer);
		// initCommand.add(c);
		// childColumn = (NestableColumn) c.execute();
		//
		// addDefaultSortingCommand(relationshipExplorer, fingerprintColumn.getRootColumn().getColumnModel(),
		// childColumn.getColumnModel(), initCommand);

		// ----

		// c = new AddColumnTreeCommand(clusterCollection50, relationshipExplorer);
		// initCommand.add(c);
		// ColumnTree cluster50Column = (ColumnTree) c.execute();

		c = new AddColumnTreeCommand(clusterCollection100, contour);
		initCommand.add(c);
		ColumnTree cluster100Column = (ColumnTree) c.execute();

		c = new AddChildColumnCommand(fingerprintCollection, cluster100Column.getRootColumn().getColumnModel()
				.getHistoryID(), contour);
		initCommand.add(c);
		NestableColumn col = (NestableColumn) c.execute();

		// ColumnTree clusterColumn = new ColumnTree(clusterCollection.createColumnModel());

		// c = new SetSummaryItemFactoryCommand((AEntityColumn) col.getColumnModel(),
		// new MedianSummaryItemFactoryCreator(), relationshipExplorer.getHistory(), true);
		// initCommand.add(c);
		c.execute();

		addDefaultSortingCommand(contour, cluster100Column.getRootColumn().getColumnModel(),
				col.getColumnModel(), initCommand);
		//
		// c = new AddChildColumnCommand(fingerprintCollection, clusterColumn.getRootColumn().getColumnModel()
		// .getHistoryID(), relationshipExplorer);
		// initCommand.add(c);
		// NestableColumn fCol = (NestableColumn) c.execute();
		//
		// // ColumnTree clusterColumn = new ColumnTree(clusterCollection.createColumnModel());
		//
		// c = new SetSummaryItemFactoryCommand((AEntityColumn) fCol.getColumnModel(), MedianSummaryItemFactory.class,
		// relationshipExplorer.getHistory(), true);
		// initCommand.add(c);
		// c.execute();
		//
		// addDefaultSortingCommand(relationshipExplorer, clusterColumn.getRootColumn().getColumnModel(),
		// fCol.getColumnModel(), initCommand);

		// ---------

		c = new AddColumnTreeCommand(therapeuticGroupCollection, contour);
		initCommand.add(c);
		ColumnTree groupColumn = (ColumnTree) c.execute();

		c = new AddChildColumnCommand(compoundCollection, groupColumn.getRootColumn().getColumnModel().getHistoryID(),
				contour);
		initCommand.add(c);
		col = (NestableColumn) c.execute();

		// ColumnTree clusterColumn = new ColumnTree(clusterCollection.createColumnModel());

		// c = new SetSummaryItemFactoryCommand((AEntityColumn) col.getColumnModel(), MedianSummaryItemFactory.class,
		// relationshipExplorer.getHistory(), true);
		// initCommand.add(c);
		// c.execute();

		addDefaultSortingCommand(contour, groupColumn.getRootColumn().getColumnModel(),
				col.getColumnModel(), initCommand);

		contour.getHistory().setInitCommand(initCommand);

		// System.out.println("Before overlap computation");
		// List<MappingOverlap> overlaps = EntityMappingUtil.getMappingOverlap(pathwayCollection, clusterCollection,
		// compoundCollection);
		// System.out.println("Overlap size: " + overlaps.size());
		// clusterColumn.addNestedColumn(fingerCol, clusterColumn.getRootColumn());
		//
		// relationshipExplorer.addColumn(clusterColumn);

		return contour;
	}

	protected void addDefaultSortingCommand(ConTourElement relationshipExplorer, IColumnModel parentColumn,
			IColumnModel childColumn, CompositeHistoryCommand initCommand) {
		CompositeComparator<NestableItem> comparator = new CompositeComparator<>(
				ItemComparators.SELECTED_ITEMS_COMPARATOR, new SelectionMappingComparator(childColumn,
						relationshipExplorer.getHistory()), new VisibleMappingComparator(childColumn,
						relationshipExplorer.getHistory()), new TotalMappingComparator(childColumn,
						relationshipExplorer.getHistory()));

		ColumnSortingCommand c = new ColumnSortingCommand(parentColumn, comparator, null,
				relationshipExplorer.getHistory());
		c.execute();
		initCommand.add(c);
	}

	protected GroupCollection getClusterColumn(ConTourElement relationshipExplorer, String name) {
		for (IDataDomain dataDomain : DataDomainManager.get().getAllDataDomains()) {
			if (dataDomain instanceof ATableBasedDataDomain && dataDomain.getLabel().contains("Finger")) {
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
								if (perspective.getLabel().toLowerCase().contains(name))
									return new GroupCollection(perspective, null, relationshipExplorer);
							}
						}

					} else {
						Set<String> perspectiveIDs = dd.getRecordPerspectiveIDs();
						String defaultPerspectiveID = dd.getDefaultTablePerspective().getRecordPerspective()
								.getPerspectiveID();
						for (String perspectiveID : perspectiveIDs) {
							if (!perspectiveID.equals(defaultPerspectiveID)) {
								Perspective perspective = dd.getTable().getRecordPerspective(perspectiveID);
								if (perspective.getLabel().toLowerCase().contains(name))
									return new GroupCollection(perspective, null, relationshipExplorer);
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
