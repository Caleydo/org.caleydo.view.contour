/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.item.factory.impl;

import java.net.URL;
import java.util.List;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.util.base.ICallback;
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
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IDCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.ATextColumn;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactoryConfigurationAddon;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.impl.TextConfigurationAddon.TextItemFactoryCreator.TextItemFactory;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;

/**
 * @author Christian
 *
 */
public class HTIImageAreaConfigurationAddon implements IItemFactoryConfigurationAddon {

	public static class HTIImageAreaFactoryCreator implements IItemFactoryCreator {

		protected static final URL PLOT_ICON = HTIImageAreaFactoryCreator.class
				.getResource("/org/caleydo/view/relationshipexplorer/icons/hbar.png");

		public static class HTIImageAreaFactory extends TextItemFactory {

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
				textElement
						.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(IMAGE_SIZE, ITEM_HEIGHT));
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
						GLImageElement el = new GLImageElement(layer.area.thumbnail.getAbsolutePath());
						el.setColor(Color.BLUE);
						imageElement.add(el);
					}
					// imageElement.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(IMAGE_SIZE,
					// IMAGE_SIZE));
					cont.add(imageElement);
					container.add(cont);
				}

				return container;
			}

		}

		private final ImageDataDomain dataDomain;

		public HTIImageAreaFactoryCreator(ImageDataDomain dataDomain) {
			this.dataDomain = dataDomain;
		}

		@Override
		public IItemFactory create(IEntityCollection collection, IColumnModel column, ConTourElement contour) {
			return new HTIImageAreaFactory(dataDomain, (ATextColumn) column);
		}

		@Override
		public URL getIconURL() {
			return PLOT_ICON;
		}

		@Override
		public String getLabel() {
			return "HTI Image Area";
		}
	}

	@Override
	public boolean canCreate(IEntityCollection collection) {
		if (!(collection instanceof IDCollection))
			return false;

		List<IDataDomain> dds = DataDomainManager.get().getDataDomainsByType(ImageDataDomain.DATA_DOMAIN_TYPE);

		return !dds.isEmpty();
	}

	@Override
	public void configure(ICallback<IItemFactoryCreator> callback) {
		// TODO

	}
}
