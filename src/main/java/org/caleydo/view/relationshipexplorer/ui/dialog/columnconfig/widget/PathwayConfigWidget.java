/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig.widget;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.util.base.ICallback;
import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.AEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.PathwayCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.idprovider.ElementIDProviders;
import org.caleydo.view.relationshipexplorer.ui.collection.idprovider.ExcludingPathwayIDProvider;
import org.caleydo.view.relationshipexplorer.ui.collection.idprovider.IElementIDProvider;
import org.caleydo.view.relationshipexplorer.ui.collection.idprovider.PathwayDatabaseIDProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Christian
 *
 */
public class PathwayConfigWidget extends ADataConfigWidget {

	private Table pathwayDBList;

	/**
	 * @param parent
	 * @param style
	 */
	public PathwayConfigWidget(Composite parent, final ICallback<ADataConfigWidget> callback) {
		super(parent, callback);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setLayout(new GridLayout(1, false));

		Label descriptionLabel = new Label(this, SWT.NONE);
		descriptionLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		descriptionLabel.setText("Use Pathways from these databases:");

		pathwayDBList = new Table(this, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		pathwayDBList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		// pathwayDBList.addSelectionListener(new SelectionAdapter() {
		// @Override
		// public void widgetSelected(SelectionEvent e) {
		//
		// }
		// });
		pathwayDBList.addListener(SWT.Selection, new Listener() {
			@Override
			public void handleEvent(Event event) {
				if (event.detail == SWT.CHECK)
					callback.on(PathwayConfigWidget.this);

			}
		});

		for (EPathwayDatabaseType db : EPathwayDatabaseType.values()) {
			TableItem item = new TableItem(pathwayDBList, SWT.NONE);
			item.setText(db.getName());
			item.setChecked(true);
			item.setData(db);
		}
	}

	public IElementIDProvider getIDProvider() {
		Set<EPathwayDatabaseType> pathwayDBs = new HashSet<>();

		for (TableItem item : pathwayDBList.getItems()) {
			if (item.getChecked())
				pathwayDBs.add((EPathwayDatabaseType) item.getData());
		}

		// by default no metabolic pathway
		return ElementIDProviders.intersectionOf(new PathwayDatabaseIDProvider(pathwayDBs),
				ExcludingPathwayIDProvider.NO_METABOLIC_PATHWAY_PROVIDER);
	}

	@Override
	public boolean isConfigValid() {
		for (TableItem item : pathwayDBList.getItems()) {
			if (item.getChecked())
				return true;
		}
		return false;
	}

	@Override
	public AEntityCollection getCollection(ConTourElement contour) {
		return new PathwayCollection(getIDProvider(), contour);
	}

}
