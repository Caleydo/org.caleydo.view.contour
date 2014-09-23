/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.item.factory.impl;

import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.base.Runnables;
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
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

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

	}

	private static class SelectImageDataDomainDialog extends Dialog {

		private ImageDataDomain dataDomain;
		private org.eclipse.swt.widgets.List dataDomainList;
		private Map<Integer, ImageDataDomain> dataDomainMap = new HashMap<>();

		/**
		 * @param shell
		 * @param contour
		 * @param caption
		 * @param isDefaultChecked
		 */
		public SelectImageDataDomainDialog(Shell shell) {
			super(shell);
		}

		@Override
		protected void configureShell(Shell newShell) {
			super.configureShell(newShell);
			newShell.setText("Select Image Dataset");
		}

		@Override
		protected Control createDialogArea(Composite parent) {

			Composite parentComposite = new Composite(parent, SWT.NONE);
			parentComposite.setLayout(new GridLayout());
			parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
			createDataDomainList(parentComposite);

			return super.createDialogArea(parent);
		}

		protected void createDataDomainList(Composite parentComposite) {
			dataDomainList = new org.eclipse.swt.widgets.List(parentComposite, SWT.BORDER);
			GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
			gd.heightHint = 50;
			dataDomainList.setLayoutData(gd);
			dataDomainList.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					setOKEnabled(true);
				}
			});
			int index = 0;
			for (IDataDomain dd : DataDomainManager.get().getDataDomainsByType(ImageDataDomain.DATA_DOMAIN_TYPE)) {
				dataDomainList.add(dd.getLabel());
				dataDomainMap.put(index, (ImageDataDomain) dd);
				index++;
			}
		}

		@Override
		protected void createButtonsForButtonBar(Composite parent) {
			super.createButtonsForButtonBar(parent);
			setOKEnabled(false);
		}

		private void setOKEnabled(boolean enabled) {
			getButton(IDialogConstants.OK_ID).setEnabled(enabled);
		}

		@Override
		protected void okPressed() {
			if (dataDomainList.getSelectionIndex() >= 0) {
				dataDomain = dataDomainMap.get(dataDomainList.getSelectionIndex());
			}

			super.okPressed();
		}

		/**
		 * @return the dataDomain, see {@link #dataDomain}
		 */
		public ImageDataDomain getDataDomain() {
			return dataDomain;
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
	public void configure(final ICallback<IItemFactoryCreator> callback) {
		Runnables.withinSWTThread(new Runnable() {

			@Override
			public void run() {
				SelectImageDataDomainDialog dialog = new SelectImageDataDomainDialog(Display.getDefault()
						.getActiveShell());
				if (dialog.open() == Window.OK) {
					callback.on(new HTIImageAreaFactoryCreator(dialog.getDataDomain()));
				}
			}
		}).run();

	}

	@Override
	public String getLabel() {
		return "HTI Image Area";
	}
}
