/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog.columnconfig.widget;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.datadomain.pathway.manager.EPathwayDatabaseType;
import org.caleydo.view.relationshipexplorer.ui.collection.idprovider.ElementIDProviders;
import org.caleydo.view.relationshipexplorer.ui.collection.idprovider.ExcludingPathwayIDProvider;
import org.caleydo.view.relationshipexplorer.ui.collection.idprovider.IElementIDProvider;
import org.caleydo.view.relationshipexplorer.ui.collection.idprovider.PathwayDatabaseIDProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableItem;

/**
 * @author Christian
 *
 */
public class PathwayConfigWidget extends Composite {

	private Table pathwayDBList;

	/**
	 * @param parent
	 * @param style
	 */
	public PathwayConfigWidget(Composite parent) {
		super(parent, SWT.NONE);
		setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		setLayout(new GridLayout(1, false));

		Label descriptionLabel = new Label(this, SWT.NONE);
		descriptionLabel.setLayoutData(new GridData(SWT.LEFT, SWT.TOP, false, false));
		descriptionLabel.setText("Select the Pathway Databases that should be used");

		pathwayDBList = new Table(this, SWT.CHECK | SWT.BORDER | SWT.V_SCROLL | SWT.H_SCROLL);
		pathwayDBList.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		for (EPathwayDatabaseType db : EPathwayDatabaseType.values()) {
			TableItem item = new TableItem(pathwayDBList, SWT.NONE);
			item.setText(db.getName());
			item.setChecked(true);
			item.setData(db);
		}
	}

	public IElementIDProvider getIDProvider() {
		Set<EPathwayDatabaseType> pathwayDBs = new HashSet<>();

		for (int index : pathwayDBList.getSelectionIndices()) {
			pathwayDBs.add((EPathwayDatabaseType) pathwayDBList.getItem(index).getData());
		}

		// by default no metabolic pathway
		return ElementIDProviders.intersectionOf(new PathwayDatabaseIDProvider(pathwayDBs),
				ExcludingPathwayIDProvider.NO_METABOLIC_PATHWAY_PROVIDER);
	}

}
