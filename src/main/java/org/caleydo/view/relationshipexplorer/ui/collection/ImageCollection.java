/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.collection;

import java.util.Set;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.id.IDType;
import org.caleydo.datadomain.image.ImageDataDomain;
import org.caleydo.datadomain.image.ImageSet;
import org.caleydo.datadomain.image.LayeredImage;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.column.factory.IColumnFactory;

import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public class ImageCollection extends AEntityCollection {

	protected final ImageDataDomain dataDomain;

	public ImageCollection(ImageDataDomain dataDomain, ConTourElement relationshipExplorer) {
		super(relationshipExplorer);
		this.dataDomain = dataDomain;

		ImageDataDomain dd = (ImageDataDomain) DataDomainManager.get().getDataDomainByType(
				ImageDataDomain.DATA_DOMAIN_TYPE);

		ImageSet imageSet = dataDomain.getImageSet();

		for (String imageName : imageSet.getImageNames()) {
			LayeredImage img = imageSet.getImage(imageName);
			allElementIDs.addAll(img.getLayers().keySet());
		}

		// Map<String, Layer> map = img.getLayers();
		// Layer l = img.getLayer("");
		// File f = l.highlight.image;

		filteredElementIDs.addAll(allElementIDs);
		setLabel(dataDomain.getLabel());
	}

	@Override
	public IDType getBroadcastingIDType() {
		return IDType.getIDType(dataDomain.getImageSet().getIDTypeLayer());
	}

	@Override
	public IDType getMappingIDType() {
		return getBroadcastingIDType();
	}

	@Override
	protected Set<Object> getBroadcastIDsFromElementID(Object elementID) {
		return Sets.newHashSet(elementID);
	}

	@Override
	protected Set<Object> getElementIDsFromBroadcastID(Object broadcastID) {
		return Sets.newHashSet(broadcastID);
	}

	@Override
	protected IColumnFactory getDefaultColumnFactory() {
		// TODO Auto-generated method stub
		return null;
	}



}
