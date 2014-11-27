/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.base.ICallback;
import org.caleydo.view.relationshipexplorer.ui.Addons;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.AEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.column.IEntityRepresentation;
import org.caleydo.view.relationshipexplorer.ui.column.factory.AColumnFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactoryConfigurationAddon;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.IItemFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.ThreadSyncEvent;
import org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig.widget.MultiAddonSelectionWidget;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Christian
 *
 */
public class ConfigureItemRendererDialog extends Dialog implements
		ICallback<MultiAddonSelectionWidget<IItemFactoryCreator, IItemFactoryConfigurationAddon>> {

	private final AEntityCollection collection;
	private final ConTourElement contour;

	private MultiAddonSelectionWidget<IItemFactoryCreator, IItemFactoryConfigurationAddon> addonWidget;

	/**
	 * @param parentShell
	 */
	public ConfigureItemRendererDialog(Shell parentShell, AEntityCollection collection, ConTourElement contour) {
		super(parentShell);
		this.collection = collection;
		this.contour = contour;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Configure Item Representations for " + collection.getLabel());
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		addonWidget = new MultiAddonSelectionWidget<IItemFactoryCreator, IItemFactoryConfigurationAddon>(parent, this,
				"Item Representations");
		AColumnFactory factory = (AColumnFactory) collection.getColumnFactory();
		addonWidget.updateWidgets(Addons.getItemFactoryAddonsFor(collection), factory.getItemFactoryCreators());

		return super.createDialogArea(parent);
	}

	@Override
	public void on(MultiAddonSelectionWidget<IItemFactoryCreator, IItemFactoryConfigurationAddon> data) {
		if (getButton(Window.OK) != null)
			getButton(Window.OK).setEnabled(!addonWidget.getCreators().isEmpty());

	}

	@Override
	protected void okPressed() {

		EventPublisher.trigger(new ThreadSyncEvent(new Runnable() {

			@Override
			public void run() {
				AColumnFactory factory = (AColumnFactory) collection.getColumnFactory();
				factory.clearItemFactoryCreators();
				boolean first = true;
				for (IItemFactoryCreator creator : addonWidget.getCreators()) {
					factory.addItemFactoryCreator(creator, first);
					first = false;
				}

				for (IEntityRepresentation rep : collection.getRepresentations()) {
					if (rep instanceof AEntityColumn) {
						AEntityColumn column = (AEntityColumn) rep;
						factory.updateColumn(column);
					}
				}
			}
		}).to(contour));

		super.okPressed();
	}

}
