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
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Christian
 *
 */
public class AddColumnDialog extends AHelpButtonDialog {

	protected final ConTourElement relationshipExplorerElement;
	protected Table collectionList;
	protected Set<IEntityCollection> collections;

	/**
	 * @param shell
	 */
	public AddColumnDialog(Shell shell, ConTourElement relationshipExplorerElement) {
		super(shell);
		this.relationshipExplorerElement = relationshipExplorerElement;
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Add columns");
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayout(new GridLayout());
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		collectionList = new Table(parentComposite, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		collectionList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		for (IEntityCollection collection : relationshipExplorerElement.getEntityCollections()) {
			TableItem item = new TableItem(collectionList, SWT.NONE);
			item.setText(collection.getLabel());
			item.setChecked(false);
			item.setData(collection);
		}

		return super.createDialogArea(parent);
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
