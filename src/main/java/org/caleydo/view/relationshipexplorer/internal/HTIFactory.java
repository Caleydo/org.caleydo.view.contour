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
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.image.ImageDataDomain;
import org.caleydo.datadomain.image.LayeredImage;
import org.caleydo.datadomain.pathway.graph.PathwayGraph;
import org.caleydo.datadomain.pathway.manager.PathwayManager;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.collection.IDCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.PathwayCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.idprovider.ElementIDProviders;
import org.caleydo.view.relationshipexplorer.ui.collection.idprovider.ExcludingPathwayIDProvider;
import org.caleydo.view.relationshipexplorer.ui.collection.idprovider.IElementIDProvider;
import org.caleydo.view.relationshipexplorer.ui.collection.idprovider.PathwayMappingBasedIDProvider;
import org.caleydo.view.relationshipexplorer.ui.column.factory.ColumnFactories.TabularDataColumnFactory;
import org.caleydo.view.relationshipexplorer.ui.column.factory.ImageAreaColumnFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.impl.HTIMutationItemConfigurationAddon.HTIMutationItemFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.impl.HTIVariantCallConfigurationAddon.HTIVariantCallItemFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.impl.MappingSummaryConfigurationAddon.MappingSummaryItemFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.command.AddColumnTreeCommand;
import org.caleydo.view.relationshipexplorer.ui.command.CompositeHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.detail.image.HTIImageDetailViewFactory;
import org.caleydo.view.relationshipexplorer.ui.detail.pathway.DefaultPathwayDetailViewFactory;
import org.caleydo.view.relationshipexplorer.ui.detail.pathway.MultiVertexHighlightAugmentationFactory;

/**
 * @author Christian
 *
 */
public class HTIFactory implements IGLElementFactory {

	private class GeneIDProvider implements IElementIDProvider {

		@Override
		public Set<Object> getElementIDs() {
			IDType geneIDType = IDType.getIDType(EGeneIDTypes.GENE_SYMBOL.name());
			IDType compoundIDType = IDType.getIDType("VARIANT_CALL_ID");
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

	private class PathwayIDProvider implements IElementIDProvider {

		@Override
		public Set<Object> getElementIDs() {
			Set<PathwayGraph> allPathways = new HashSet<>(PathwayManager.get().getAllItems());

			IDType mutationIDType = IDType.getIDType("VARIANT_ID");

			Set<Object> filteredPathways = new HashSet<>();
			for (PathwayGraph pathway : allPathways) {
				if (pathway.getLabel().toLowerCase().contains("metabolic pathway"))
					continue;
				Set<Object> compoundIDs = PathwayManager.get().getPathwayGeneIDs(pathway, mutationIDType);
				if (compoundIDs != null && !compoundIDs.isEmpty())
					filteredPathways.add(pathway);
			}

			return filteredPathways;
		}

	}

	@Override
	public String getId() {
		return "relationship explorer";
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {

		final ConTourElement contour = new ConTourElement();

		IDCollection patientCollection = new IDCollection(IDType.getIDType("PATIENT"), IDType.getIDType("PATIENT"),
				null, contour);
		contour.registerEntityCollection(patientCollection);
		patientCollection.setLabel("Patients");

		final ImageDataDomain imageDD = (ImageDataDomain) DataDomainManager.get().getDataDomainByType(
				ImageDataDomain.DATA_DOMAIN_TYPE);

		IDType layerIDType = imageDD.getImageSet().getIDTypeLayer();

		IDCollection layerCollection = new IDCollection(layerIDType, layerIDType, new IElementIDProvider() {

			@Override
			public Set<Object> getElementIDs() {
				Set<Object> elementIDs = new HashSet<>();

				for (String imageName : imageDD.getImageSet().getImageNames()) {
					LayeredImage img = imageDD.getImageSet().getImage(imageName);
					elementIDs.addAll(img.getLayers().keySet());
				}
				return elementIDs;
			}
		}, contour);
		contour.registerEntityCollection(layerCollection);
		layerCollection.setLabel("Areas");
		layerCollection.setColumnFactory(new ImageAreaColumnFactory(layerCollection, imageDD, contour));
		layerCollection.setDetailViewFactory(new HTIImageDetailViewFactory(imageDD, contour));

		TabularDataCollection mutationsCollection = null;

		for (IDataDomain dd : DataDomainManager.get().getAllDataDomains()) {
			if (dd instanceof ATableBasedDataDomain && dd.getLabel().contains("Variants")) {
				ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) dd;
				if (dataDomain.hasIDCategory(IDCategory.getIDCategory(EGeneIDTypes.GENE.name()))) {
					// ColumnTree activityColumn = new ColumnTree();
					final TabularDataCollection coll = new TabularDataCollection(
							dataDomain.getDefaultTablePerspective(),
							IDCategory.getIDCategory(EGeneIDTypes.GENE.name()), null, contour);
					contour.registerEntityCollection(coll);
					mutationsCollection = coll;
					mutationsCollection.setLabel("Variant Descriptions");
					mutationsCollection.setColumnFactory(new TabularDataColumnFactory() {

						{
							addItemFactoryCreator(new HTIMutationItemFactoryCreator(), true);
							addSummaryItemFactoryCreator(new MappingSummaryItemFactoryCreator(), true);
						}

					});
				}
				break;
			}
		}

		TabularDataCollection mutationSamplesCollection = null;

		for (IDataDomain dd : DataDomainManager.get().getAllDataDomains()) {
			if (dd instanceof ATableBasedDataDomain && dd.getLabel().contains("Variant Calls")) {
				ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) dd;
				if (dataDomain.hasIDCategory(IDCategory.getIDCategory(EGeneIDTypes.GENE.name()))) {
					// ColumnTree activityColumn = new ColumnTree();
					final TabularDataCollection coll = new TabularDataCollection(
							dataDomain.getDefaultTablePerspective(),
							IDCategory.getIDCategory(EGeneIDTypes.GENE.name()), null, contour);
					contour.registerEntityCollection(coll);
					mutationSamplesCollection = coll;
					mutationSamplesCollection.setLabel("Variant Calls");
					mutationSamplesCollection.setColumnFactory(new TabularDataColumnFactory() {

						{
							addItemFactoryCreator(new HTIVariantCallItemFactoryCreator(), true);
							addSummaryItemFactoryCreator(new MappingSummaryItemFactoryCreator(), true);
						}

					});
				}
				break;
			}
		}

		final IDCollection geneCollection = new IDCollection(IDType.getIDType(EGeneIDTypes.GENE_SYMBOL.name()),
				IDType.getIDType(EGeneIDTypes.GENE_SYMBOL.name()), new GeneIDProvider(), contour);
		contour.registerEntityCollection(geneCollection);
		// geneCollection.setColumnFactory(new IColumnFactory() {
		//
		// @Override
		// public IColumnModel create() {
		// IDColumn column = new IDColumn(geneCollection, contour);
		// column.setItemFactory(new GeneSequenceItemFactory(geneCollection));
		// column.init();
		// return column;
		// }
		// });
		PathwayCollection pathwayCollection = new PathwayCollection(ElementIDProviders.intersectionOf(
				 ExcludingPathwayIDProvider.NO_METABOLIC_PATHWAY_PROVIDER,
				 new PathwayMappingBasedIDProvider(IDType.getIDType("VARIANT_ID"))), contour);
		contour.registerEntityCollection(pathwayCollection);

		pathwayCollection
				.setDetailViewFactory(new DefaultPathwayDetailViewFactory(contour)
						.addForegroundAugmentationFactory(new MultiVertexHighlightAugmentationFactory(geneCollection,
								contour)));
		CompositeHistoryCommand initCommand = new CompositeHistoryCommand();

		// -----

		IHistoryCommand c = new AddColumnTreeCommand(patientCollection, contour);
		initCommand.add(c);
		c.execute();

		// -----

		c = new AddColumnTreeCommand(layerCollection, contour);
		initCommand.add(c);
		c.execute();

		// -----

		c = new AddColumnTreeCommand(mutationSamplesCollection, contour);
		initCommand.add(c);
		c.execute();

		// -----

		c = new AddColumnTreeCommand(mutationsCollection, contour);
		initCommand.add(c);
		c.execute();

		// -----

		c = new AddColumnTreeCommand(geneCollection, contour);
		initCommand.add(c);
		c.execute();

		// -----

		c = new AddColumnTreeCommand(pathwayCollection, contour);
		initCommand.add(c);
		c.execute();

		// -----
		contour.getHistory().setInitCommand(initCommand);

		return contour;
	}

	@Override
	public boolean apply(GLElementFactoryContext context) {
		return true;
	}
}
