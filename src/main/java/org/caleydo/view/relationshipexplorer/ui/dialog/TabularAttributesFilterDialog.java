/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.column.container.CategoryProperty;
import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.gui.util.AHelpButtonDialog;
import org.caleydo.core.io.DataDescription;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.view.relationshipexplorer.ui.column.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.column.TabularDataColumn;
import org.caleydo.view.relationshipexplorer.ui.util.CompositePredicate;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Predicate;

/**
 * @author Christian
 *
 */
public class TabularAttributesFilterDialog extends AHelpButtonDialog {

	protected final TabularDataColumn column;
	protected final TabularDataCollection collection;

	protected Set<IAttributeFilterFactory> filterFactories = new HashSet<>();

	protected CompositePredicate<Object> filter;

	protected interface IAttributeFilterFactory {
		public Predicate<Object> createFilter();

		public boolean use();
	}

	protected class NumericalAttributeFilterFactory implements IAttributeFilterFactory {

		protected final int dimensionID;
		protected final Text upperLimitText;
		protected final Text lowerLimitText;
		protected boolean use = false;

		public NumericalAttributeFilterFactory(int dimensionID, Text upperLimitText, Text lowerLimitText) {
			this.dimensionID = dimensionID;
			this.upperLimitText = upperLimitText;
			this.lowerLimitText = lowerLimitText;
		}

		@Override
		public Predicate<Object> createFilter() {

			final float upperLimit = Float.parseFloat(upperLimitText.getText());
			final float lowerLimit = Float.parseFloat(lowerLimitText.getText());

			return new Predicate<Object>() {

				@Override
				public boolean apply(Object elementID) {
					float value = (float) collection.getDataDomain().getRaw(collection.getItemIDType(),
							(int) elementID, collection.getDimensionPerspective().getIdType(), dimensionID);
					return value >= lowerLimit && value <= upperLimit;
				}
			};
		}

		@Override
		public boolean use() {
			return use;
		}
	}

	protected class CategoricalAttributeFilterFactory implements IAttributeFilterFactory {

		protected final int dimensionID;
		protected final org.eclipse.swt.widgets.Table table;
		protected boolean use = false;

		public CategoricalAttributeFilterFactory(int dimensionID, org.eclipse.swt.widgets.Table table) {
			this.dimensionID = dimensionID;
			this.table = table;
		}

		@Override
		public Predicate<Object> createFilter() {

			final Set<String> validCategories = new HashSet<>(table.getSelection().length);
			for (TableItem item : table.getItems()) {
				if (item.getChecked()) {
					validCategories.add(item.getText());
				}
			}

			return new Predicate<Object>() {

				@Override
				public boolean apply(Object elementID) {
					String value = (String) collection.getDataDomain().getRaw(collection.getItemIDType(),
							(int) elementID, collection.getDimensionPerspective().getIdType(), dimensionID);

					return validCategories.contains(value);
				}
			};
		}

		@Override
		public boolean use() {
			return use;
		}

	}

	/**
	 * @param parentShell
	 */
	public TabularAttributesFilterDialog(Shell parentShell, TabularDataColumn column) {
		super(parentShell);
		this.column = column;
		this.collection = (TabularDataCollection) column.getCollection();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Attribute Filter for " + collection.getLabel());
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		ATableBasedDataDomain dataDomain = collection.getDataDomain();
		Table table = dataDomain.getTable();

		ScrolledComposite scrolledComposite = new ScrolledComposite(parent, SWT.H_SCROLL);
		scrolledComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		scrolledComposite.setExpandVertical(true);
		scrolledComposite.setExpandHorizontal(true);

		Composite parentComposite = new Composite(scrolledComposite, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(collection.getDimensionPerspective().getVirtualArray().size(), true));
		scrolledComposite.setContent(parentComposite);
		scrolledComposite.setMinSize(collection.getDimensionPerspective().getVirtualArray().size() * 200, 100);

		if (table.isDataHomogeneous()) {
			DataDescription desc = dataDomain.getDataSetDescription().getDataDescription();
			CategoricalClassDescription<?> categoricalClassDesc = desc.getCategoricalClassDescription();
			for (int dimensionID : collection.getDimensionPerspective().getVirtualArray()) {
				String label = dataDomain.getDimensionLabel(dimensionID);
				if (categoricalClassDesc == null) {
					NumericalTable numericalTable = (NumericalTable) table;
					// float dataCenter = numericalTable.getDataCenter().floatValue();
					float min = (float) numericalTable.getMin();
					float max = (float) numericalTable.getMax();
					addNumericalAttributeGroup(parentComposite, dimensionID, label, min, max);
				} else {
					addCategoricalAttributeGroup(parentComposite, dimensionID, label, categoricalClassDesc);
				}
			}

		} else {

			for (int dimensionID : collection.getDimensionPerspective().getVirtualArray()) {
				Object dataClassDesc = table.getDataClassSpecificDescription(dimensionID);
				String label = dataDomain.getDimensionLabel(dimensionID);
				if (dataClassDesc == null || dataClassDesc instanceof NumericalProperties) {
					// TODO: use correct data center
					addNumericalAttributeGroup(parentComposite, dimensionID, label, 0, 0);
				} else {

					addCategoricalAttributeGroup(parentComposite, dimensionID, label,
							(CategoricalClassDescription<?>) dataClassDesc);
				}

			}
		}

		return super.createDialogArea(parent);
	}

	protected void addNumericalAttributeGroup(Composite parent, int dimensionID, String label, float min, float max) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 200;
		group.setLayoutData(gd);
		group.setText(label);
		group.setLayout(new GridLayout(2, false));
		final Label maxLabel = new Label(group, SWT.NONE);
		maxLabel.setText("Upper Limit");
		maxLabel.setEnabled(false);
		final Text maxText = new Text(group, SWT.BORDER);
		maxText.setText(Float.toString(max));
		maxText.setEnabled(false);
		maxText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		final Label minLabel = new Label(group, SWT.NONE);
		minLabel.setText("Lower Limit");
		minLabel.setEnabled(false);
		final Text minText = new Text(group, SWT.BORDER);
		minText.setText(Float.toString(min));
		minText.setEnabled(false);
		minText.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));

		final Button useAttributeButton = new Button(group, SWT.CHECK);
		useAttributeButton.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true, 2, 1));
		useAttributeButton.setText("Enabled");
		final NumericalAttributeFilterFactory factory = new NumericalAttributeFilterFactory(dimensionID, maxText,
				minText);
		filterFactories.add(factory);
		useAttributeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean enabled = useAttributeButton.getSelection();
				maxLabel.setEnabled(enabled);
				maxText.setEnabled(enabled);
				minLabel.setEnabled(enabled);
				minText.setEnabled(enabled);
				factory.use = enabled;
			}
		});

	}

	protected void addCategoricalAttributeGroup(Composite parent, int dimensionID, String label,
			CategoricalClassDescription<?> categoricalClassDesc) {
		Group group = new Group(parent, SWT.SHADOW_ETCHED_IN);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 200;
		group.setLayoutData(gd);
		group.setText(label);
		group.setLayout(new GridLayout());
		final org.eclipse.swt.widgets.Table table = new org.eclipse.swt.widgets.Table(group, SWT.CHECK | SWT.BORDER
				| SWT.V_SCROLL | SWT.H_SCROLL);
		for (CategoryProperty<?> cp : categoricalClassDesc.getCategoryProperties()) {
			TableItem item = new TableItem(table, SWT.NONE);
			item.setText(cp.getCategoryName());
			item.setChecked(true);
		}
		table.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		table.setEnabled(false);

		final CategoricalAttributeFilterFactory factory = new CategoricalAttributeFilterFactory(dimensionID, table);
		filterFactories.add(factory);

		final Button useAttributeButton = new Button(group, SWT.CHECK);
		useAttributeButton.setLayoutData(new GridData(SWT.FILL, SWT.BOTTOM, true, true));
		useAttributeButton.setText("Enabled");
		useAttributeButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				boolean enabled = useAttributeButton.getSelection();
				table.setEnabled(enabled);
				factory.use = enabled;
			}
		});

	}

	@Override
	protected void helpPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void okPressed() {
		filter = new CompositePredicate<>();
		for(IAttributeFilterFactory f : filterFactories) {
			if (f.use()) {
				filter.add(f.createFilter());
			}
		}
		super.okPressed();
	}

	public Predicate<Object> getFilter() {
		return filter;
	}

}