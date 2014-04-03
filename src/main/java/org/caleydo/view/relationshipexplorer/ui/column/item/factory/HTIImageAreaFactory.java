/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.item.factory;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLImageElement;
import org.caleydo.core.view.opengl.layout2.GLScaleStack;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.datadomain.image.ImageDataDomain;
import org.caleydo.datadomain.image.LayeredImage;
import org.caleydo.datadomain.image.LayeredImage.Layer;
import org.caleydo.view.relationshipexplorer.ui.column.ATextColumn;

/**
 * @author Christian
 *
 */
public class HTIImageAreaFactory extends TextItemFactory {

	protected static final int IMAGE_SIZE = 70;

	protected final ImageDataDomain dataDomain;

	public HTIImageAreaFactory(ImageDataDomain dataDomain, ATextColumn column) {
		super(column);
		this.dataDomain = dataDomain;
	}

	@Override
	public GLElement createItem(Object elementID) {

		GLElementContainer container = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 2,
				new GLPadding(2)));
		container.setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(container, 2,
				new GLPadding(2)));

		GLElement textElement = super.createItem(elementID);
		textElement.setSize(Float.NaN, ITEM_HEIGHT);
		textElement.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(IMAGE_SIZE, ITEM_HEIGHT));
		container.add(textElement);

		LayeredImage img = dataDomain.getImageSet().getImageForLayer((String) elementID);
		if (img != null) {

			GLElementContainer cont = new GLElementContainer(GLLayouts.LAYERS);
			cont.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(IMAGE_SIZE, IMAGE_SIZE));
			GLScaleStack imageElement = new GLScaleStack();

			// imageElement.setScale(0.5f);
			imageElement.scaleToFit();
			imageElement.setBackgroundColor(Color.TRANSPARENT);
			imageElement.add(new GLImageElement(img.getBaseImage().thumbnail.getAbsolutePath()));
			Layer layer = img.getLayer((String) elementID);
			if (layer.area != null) {
				imageElement.add(new GLImageElement(layer.area.thumbnail.getAbsolutePath()));
			}
			// imageElement.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(IMAGE_SIZE, IMAGE_SIZE));
			cont.add(imageElement);
			container.add(cont);
		}

		return container;
	}

}
