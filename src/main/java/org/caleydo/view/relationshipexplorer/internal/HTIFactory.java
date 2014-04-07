/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.internal;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.IGLElementFactory;
import org.caleydo.datadomain.image.ImageDataDomain;
import org.caleydo.datadomain.image.LayeredImage;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.collection.IDCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.IElementIDProvider;
import org.caleydo.view.relationshipexplorer.ui.column.factory.ImageAreaColumnFactory;
import org.caleydo.view.relationshipexplorer.ui.command.AddColumnTreeCommand;
import org.caleydo.view.relationshipexplorer.ui.command.CompositeHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.detail.image.HTIImageDetailViewFactory;

/**
 * @author Christian
 *
 */
public class HTIFactory implements IGLElementFactory {

	@Override
	public String getId() {
		return "relationship explorer";
	}

	@Override
	public GLElement create(GLElementFactoryContext context) {

		ConTourElement relationshipExplorer = new ConTourElement();

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
		}, relationshipExplorer);
		layerCollection.setLabel("Areas");
		layerCollection.setColumnFactory(new ImageAreaColumnFactory(layerCollection, imageDD, relationshipExplorer));
		layerCollection.setDetailViewFactory(new HTIImageDetailViewFactory(imageDD, relationshipExplorer));

		CompositeHistoryCommand initCommand = new CompositeHistoryCommand();

		IHistoryCommand c = new AddColumnTreeCommand(layerCollection, relationshipExplorer);
		initCommand.add(c);
		c.execute();

		relationshipExplorer.getHistory().setInitCommand(initCommand);

		return relationshipExplorer;
	}

	@Override
	public boolean apply(GLElementFactoryContext context) {
		return true;
	}
}
