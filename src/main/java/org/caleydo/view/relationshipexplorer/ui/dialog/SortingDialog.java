/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.gui.util.AHelpButtonDialog;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.column.CompositeComparator;
import org.caleydo.view.relationshipexplorer.ui.column.ItemComparators;
import org.caleydo.view.relationshipexplorer.ui.column.ItemComparators.AMappingComparator;
import org.caleydo.view.relationshipexplorer.ui.column.ItemComparators.SelectionMappingComparator;
import org.caleydo.view.relationshipexplorer.ui.column.ItemComparators.TotalMappingComparator;
import org.caleydo.view.relationshipexplorer.ui.column.ItemComparators.VisibleMappingComparator;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;
import org.caleydo.view.relationshipexplorer.ui.list.NestableColumn;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

/**
 * @author Christian
 *
 */
public class SortingDialog extends AHelpButtonDialog {

	protected final AEntityColumn column;

	protected Set<Comparator<NestableItem>> comparators;

	protected Button considerSelectionsButton;
	protected Button sortByNumberOfChildItemsButton;
	protected Combo childColumnCombo;
	protected Group criteriaGroup;

	protected Map<Integer, IColumnModel> childColumnMap = new HashMap<>();

	protected Comparator<NestableItem> definedComparator;

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
		newShell.setText("Sort " + column.getLabel() + " by");
	}

	@SuppressWarnings("unchecked")
	@Override
	protected Control createDialogArea(Composite parent) {
		List<Comparator<NestableItem>> currentComparators = new ArrayList<>();
		if (column.getCurrentComparator() instanceof CompositeComparator<?>) {
			CompositeComparator<?> compositeComparator = (CompositeComparator<?>) column.getCurrentComparator();
			for (Object o : compositeComparator) {
				currentComparators.add((Comparator<NestableItem>) o);
			}
		} else {
			currentComparators.add(column.getCurrentComparator());
		}

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(1, true));

		criteriaGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		criteriaGroup.setLayout(new GridLayout(2, true));
		gd.widthHint = 200;
		criteriaGroup.setLayoutData(gd);
		criteriaGroup.setText("Sorting Criteria");
		// final org.eclipse.swt.widgets.Table table = new org.eclipse.swt.widgets.Table(group, SWT.CHECK | SWT.BORDER
		// | SWT.V_SCROLL | SWT.H_SCROLL);
		for (Comparator<NestableItem> c : comparators) {

			Button button = new Button(criteriaGroup, SWT.RADIO);
			button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
			button.setText(c.toString());
			button.setData(c);
			button.setSelection(currentComparators.contains(c));
			// TableItem item = new TableItem(table, SWT.NONE);
			// item.setText(c.toString());
			// item.setChecked(true);
			// item.setData(c);
		}

		List<NestableColumn> childColumns = column.getColumn().getChildren();
		if (!childColumns.isEmpty()) {
			AMappingComparator mappingComparator = null;
			for (Comparator<NestableItem> c : currentComparators) {
				if (c instanceof AMappingComparator) {
					mappingComparator = (AMappingComparator) c;
					break;
				}
			}

			sortByNumberOfChildItemsButton = new Button(criteriaGroup, SWT.RADIO);
			sortByNumberOfChildItemsButton.setText("Sort by number of child items in");
			sortByNumberOfChildItemsButton.setSelection(mappingComparator != null);
			childColumnCombo = new Combo(criteriaGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
			if (childColumns.size() <= 1)
				childColumnCombo.setEnabled(false);

			for (int i = 0; i < childColumns.size(); i++) {
				NestableColumn col = childColumns.get(i);
				childColumnMap.put(i, col.getColumnModel());
				childColumnCombo.add(col.getColumnModel().getLabel());
				if (mappingComparator != null && mappingComparator.getMappingColumn() == col.getColumnModel()) {
					childColumnCombo.select(i);
				}
			}
			if (mappingComparator == null) {
				childColumnCombo.select(0);
			}
		}

		considerSelectionsButton = new Button(parentComposite, SWT.CHECK);
		considerSelectionsButton.setText("Rank selected items on top");
		considerSelectionsButton.setSelection(currentComparators.contains(ItemComparators.SELECTED_ITEMS_COMPARATOR));

		// table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		// table.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		// }
		// });

		return super.createDialogArea(parent);
	}

	@Override
	protected void helpPressed() {
		// TODO Auto-generated method stub

	}

	@SuppressWarnings("unchecked")
	@Override
	protected void okPressed() {
		CompositeComparator<NestableItem> comparator = new CompositeComparator<>();
		if (considerSelectionsButton.getSelection()) {
			comparator.add(ItemComparators.SELECTED_ITEMS_COMPARATOR);
		}
		for (Control child : criteriaGroup.getChildren()) {
			if (child instanceof Button) {
				Button button = (Button) child;
				if (button.getSelection()) {
					if (button == sortByNumberOfChildItemsButton) {
						IColumnModel mappingColumn = childColumnMap.get(childColumnCombo.getSelectionIndex());
						if (considerSelectionsButton.getSelection()) {
							comparator.add(new SelectionMappingComparator(mappingColumn, column
									.getRelationshipExplorer().getHistory()));
						}
						comparator.add(new VisibleMappingComparator(mappingColumn, column.getRelationshipExplorer()
								.getHistory()));
						comparator.add(new TotalMappingComparator(mappingColumn, column.getRelationshipExplorer()
								.getHistory()));
					} else {
						comparator.add((Comparator<NestableItem>) button.getData());
					}
				}
			}
		}
		definedComparator = comparator;
		super.okPressed();
	}

	/**
	 * @return the definedComparator, see {@link #definedComparator}
	 */
	public Comparator<NestableItem> getComparator() {
		return definedComparator;
	}

}
