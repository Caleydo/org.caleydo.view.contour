/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig.widget;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.util.base.ICallback;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.AEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.column.factory.ColumnFactories.TabularDataColumnFactory;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;

/**
 * @author Christian
 *
 */
public class TabularDataConfigWidget extends ADataConfigWidget {

	private List dataDomainList;
	private Combo idCategoryCombo;

	/**
	 * @param parent
	 * @param style
	 */
	public TabularDataConfigWidget(Composite parent, final ICallback<ADataConfigWidget> callback) {
		super(parent, callback);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setLayout(new GridLayout(2, false));

		Composite datasetComposite = new Composite(this, SWT.NONE);
		datasetComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		datasetComposite.setLayout(new GridLayout(1, false));

		Label descriptionLabel = new Label(datasetComposite, SWT.NONE);
		descriptionLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		descriptionLabel.setText("Dataset:");

		dataDomainList = new List(datasetComposite, SWT.BORDER);

		// dataDomainList = new Table(datasetComposite, SWT.RADIO | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		dataDomainList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		for (IDataDomain dd : DataDomainManager.get().getAllDataDomains()) {
			if (dd instanceof ATableBasedDataDomain) {
				dataDomainList.add(dd.getLabel());
				dataDomainList.setData(dd.getLabel(), dd);

				// TableItem item = new TableItem(dataDomainList, SWT.NONE);
				// item.setText(dd.getLabel());
				// item.setData(dd);
			}
		}

		dataDomainList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				ATableBasedDataDomain dataDomain = (ATableBasedDataDomain) dataDomainList.getData(dataDomainList
						.getItem(dataDomainList.getSelectionIndex()));

				idCategoryCombo.clearSelection();
				idCategoryCombo.removeAll();
				idCategoryCombo.add(dataDomain.getRowIDCategory().getCategoryName());
				idCategoryCombo.add(dataDomain.getColumnIDCategory().getCategoryName());
				idCategoryCombo.setEnabled(true);
				callback.on(TabularDataConfigWidget.this);
			}
		});

		Composite idCategoryComposite = new Composite(this, SWT.NONE);
		idCategoryComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		idCategoryComposite.setLayout(new GridLayout(1, false));

		Label descLabel = new Label(idCategoryComposite, SWT.NONE);
		descLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		descLabel.setText("ID Type:");

		idCategoryCombo = new Combo(idCategoryComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		idCategoryCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		idCategoryCombo.setEnabled(false);
		idCategoryCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				callback.on(TabularDataConfigWidget.this);
			}
		});
	}

	public ATableBasedDataDomain getDataDomain() {
		int index = dataDomainList.getSelectionIndex();
		if (index < 0)
			return null;
		return (ATableBasedDataDomain) dataDomainList
				.getData(dataDomainList.getItem(dataDomainList.getSelectionIndex()));
	}

	public IDCategory getIDCategory() {
		return IDCategory.getIDCategory(idCategoryCombo.getText());
	}

	@Override
	public boolean isConfigValid() {
		return dataDomainList.getSelectionIndex() >= 0 && getIDCategory() != null;
	}

	@Override
	public AEntityCollection getCollection(ConTourElement contour) {
		TabularDataCollection collection = new TabularDataCollection(getDataDomain().getDefaultTablePerspective(),
				getIDCategory(), null, contour);
		collection.setColumnFactory(new TabularDataColumnFactory());
		return collection;
	}

}
