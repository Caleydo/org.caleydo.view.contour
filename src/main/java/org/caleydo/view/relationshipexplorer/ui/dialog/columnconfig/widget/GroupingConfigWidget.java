/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig.widget;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDCategory;
import org.caleydo.core.util.base.ICallback;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.AEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.GroupCollection;
import org.caleydo.view.relationshipexplorer.ui.column.factory.ColumnFactories.GroupColumnFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.impl.MappingSummaryConfigurationAddon.MappingSummaryItemFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.impl.TextConfigurationAddon.TextItemFactoryCreator;
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
public class GroupingConfigWidget extends ADataConfigWidget {

	private List dataDomainList;
	private Combo idCategoryCombo;
	private List groupingList;

	private Map<Integer, Perspective> perspectiveMap = new HashMap<>();

	/**
	 * @param parent
	 * @param callback
	 */
	public GroupingConfigWidget(Composite parent, final ICallback<ADataConfigWidget> callback) {
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
		dataDomainList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		for (IDataDomain dd : DataDomainManager.get().getAllDataDomains()) {
			if (dd instanceof ATableBasedDataDomain) {
				dataDomainList.add(dd.getLabel());
				dataDomainList.setData(dd.getLabel(), dd);
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
				callback.on(GroupingConfigWidget.this);

				groupingList.removeAll();
				groupingList.setEnabled(false);
			}
		});

		Composite groupingComposite = new Composite(this, SWT.NONE);
		groupingComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		groupingComposite.setLayout(new GridLayout(1, false));

		Label descLabel = new Label(groupingComposite, SWT.NONE);
		descLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		descLabel.setText("ID Type:");

		idCategoryCombo = new Combo(groupingComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		idCategoryCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
		idCategoryCombo.setEnabled(false);
		idCategoryCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				groupingList.setEnabled(true);
				groupingList.removeAll();

				ATableBasedDataDomain dataDomain = getDataDomain();

				perspectiveMap.clear();
				if (getIDCategory() == dataDomain.getDimensionIDCategory()) {
					Set<String> perspectiveIDs = dataDomain.getDimensionPerspectiveIDs();
					int index = 0;
					for (String id : perspectiveIDs) {
						Perspective perspective = dataDomain.getTable().getDimensionPerspective(id);
						groupingList.add(perspective.getLabel() + " ("
								+ perspective.getVirtualArray().getGroupList().size() + ")");
						perspectiveMap.put(index, perspective);
						index++;
					}
				} else {
					Set<String> perspectiveIDs = dataDomain.getRecordPerspectiveIDs();
					int index = 0;
					for (String id : perspectiveIDs) {
						Perspective perspective = dataDomain.getTable().getRecordPerspective(id);
						groupingList.add(perspective.getLabel() + " ("
								+ perspective.getVirtualArray().getGroupList().size() + ")");
						perspectiveMap.put(index, perspective);
						index++;
					}
				}
				callback.on(GroupingConfigWidget.this);
			}
		});

		Label label = new Label(groupingComposite, SWT.NONE);
		label.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		label.setText("Grouping:");

		groupingList = new List(groupingComposite, SWT.BORDER);
		groupingList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		groupingList.setEnabled(false);
		groupingList.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				callback.on(GroupingConfigWidget.this);
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

	public Perspective getPerspective() {
		int index = groupingList.getSelectionIndex();
		if (index < 0)
			return null;
		return perspectiveMap.get(index);
	}

	@Override
	public boolean isConfigValid() {
		return dataDomainList.getSelectionIndex() >= 0 && getIDCategory() != null
				&& groupingList.getSelectionIndex() >= 0;
	}

	@Override
	public AEntityCollection getCollection(ConTourElement contour) {
		GroupCollection collection = new GroupCollection(getPerspective(), null, contour);
		GroupColumnFactory factory = new GroupColumnFactory();
		factory.addItemFactoryCreator(new TextItemFactoryCreator(), true);
		factory.addSummaryItemFactoryCreator(new MappingSummaryItemFactoryCreator(), true);
		collection.setColumnFactory(factory);

		return collection;
	}
}
