/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog;

import java.util.Comparator;
import java.util.Set;

import org.caleydo.core.gui.util.AHelpButtonDialog;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.dialog.TabularAttributesFilterDialog.CompositeAttributeFilter;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Christian
 *
 */
public class SortingDialog extends AHelpButtonDialog {

	protected final AEntityColumn column;

	protected Set<Comparator<NestableItem>> comparators;

	protected CompositeAttributeFilter filter;

	/**
	 * @param parentShell
	 */
	public SortingDialog(Shell parentShell, AEntityColumn column) {
		super(parentShell);
		this.column = column;
		comparators = column.getComparators();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Define Order of " + column.getLabel());
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(3, true));

		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 200;
		group.setLayoutData(gd);
		group.setText("Available Sorting Criteria");
		group.setLayout(new GridLayout());
		final org.eclipse.swt.widgets.Table table = new org.eclipse.swt.widgets.Table(group, SWT.CHECK | SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL);
		for (Comparator<NestableItem> c : comparators) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(c.toString());
			item.setChecked(true);
			item.setData(c);
		}
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
			}
		});

		return super.createDialogArea(parent);
	}

	@Override
	protected void helpPressed() {
		// TODO Auto-generated method stub

	}


}
