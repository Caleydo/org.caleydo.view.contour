/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.gui.util.AHelpButtonDialog;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Christian
 *
 */
public abstract class ASelectCollectionsDialog extends AHelpButtonDialog {

	protected final ConTourElement contour;
	protected Table collectionList;
	protected Set<IEntityCollection> collections;
	protected String caption;
	protected boolean isDefaultChecked;

	/**
	 * @param shell
	 */
	public ASelectCollectionsDialog(Shell shell, ConTourElement relationshipExplorerElement, String caption,
			boolean isDefaultChecked) {
		super(shell);
		this.contour = relationshipExplorerElement;
		this.caption = caption;
		this.isDefaultChecked = isDefaultChecked;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText(caption);
	}


	protected void createCollectionList(Composite parentComposite) {
		collectionList = new Table(parentComposite, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		collectionList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		for (IEntityCollection collection : contour.getEntityCollections()) {
			TableItem item = new TableItem(collectionList, SWT.NONE);
			item.setText(collection.getLabel());
			item.setChecked(isDefaultChecked);
			item.setData(collection);
		}
	}

	@Override
	protected void helpPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void okPressed() {
		collections = new HashSet<>();
		for (TableItem item : collectionList.getItems()) {
			if (item.getChecked())
				collections.add((IEntityCollection) item.getData());
		}
		if (collections.isEmpty())
			return;

		super.okPressed();
	}

	/**
	 * @return the collections, see {@link #collections}
	 */
	public Set<IEntityCollection> getCollections() {
		return collections;
	}

}
