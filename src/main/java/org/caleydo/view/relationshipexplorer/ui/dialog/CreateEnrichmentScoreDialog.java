/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.caleydo.core.gui.util.AHelpButtonDialog;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.EnrichmentScores;
import org.caleydo.view.relationshipexplorer.ui.collection.EnrichmentScores.EnrichmentScore;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;

/**
 * @author Christian
 *
 */
public class CreateEnrichmentScoreDialog extends AHelpButtonDialog {

	protected Combo targetCollectionCombo;
	protected Combo enrichmentCollectionCombo;
	protected Combo mappingCollectionCombo;
	protected Combo itemModeCombo;
	protected Spinner thresholdSpinner;
	protected Label thresholdLabel;

	protected EnrichmentScores enrichmentScores;
	protected Collection<IEntityCollection> collections;

	protected Map<Integer, IEntityCollection> collectionMap = new HashMap<>();
	protected EnrichmentScore score;

	/**
	 * @param shell
	 */
	protected CreateEnrichmentScoreDialog(Shell shell, ConTourElement relationshipExplorer) {
		super(shell);
		this.enrichmentScores = relationshipExplorer.getEnrichmentScores();
		this.collections = relationshipExplorer.getEntityCollections();
	}

	@Override
	protected void configureShell(Shell newShell) {
		super.configureShell(newShell);
		newShell.setText("Create Enrichment Score");
	}

	@Override
	protected Control createDialogArea(Composite parent) {

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(11, false));

		Label l = new Label(parentComposite, SWT.NONE);
		l.setText("Calculate enrichment of");

		enrichmentCollectionCombo = addCollectionCombo(parentComposite);

		l = new Label(parentComposite, SWT.NONE);
		l.setText("for");

		targetCollectionCombo = addCollectionCombo(parentComposite);

		l = new Label(parentComposite, SWT.NONE);
		l.setText("via");

		mappingCollectionCombo = addCollectionCombo(parentComposite);
		mappingCollectionCombo.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				thresholdLabel.setText(mappingCollectionCombo.getText());
			}
		});

		l = new Label(parentComposite, SWT.NONE);
		l.setText("considering");

		itemModeCombo = new Combo(parentComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		itemModeCombo.add("all items");
		itemModeCombo.add("filtered items");

		int i = 0;
		for (IEntityCollection collection : collections) {
			collectionMap.put(i, collection);
			i++;
		}

		l = new Label(parentComposite, SWT.NONE);
		l.setText("with a threshold of");

		thresholdSpinner = new Spinner(parentComposite, SWT.BORDER);
		thresholdSpinner.setMinimum(1);
		thresholdSpinner.setMaximum(Integer.MAX_VALUE);
		thresholdSpinner.setIncrement(1);
		thresholdSpinner.setSelection(2);

		thresholdLabel = new Label(parentComposite, SWT.NONE);
		GridData gd = new GridData();
		gd.widthHint = 80;
		thresholdLabel.setLayoutData(gd);

		return super.createDialogArea(parent);
	}

	protected Combo addCollectionCombo(Composite parentComposite) {
		Combo combo = new Combo(parentComposite, SWT.DROP_DOWN | SWT.READ_ONLY);
		for (IEntityCollection collection : collections) {
			combo.add(collection.getLabel());
		}
		return combo;
	}

	@Override
	protected void helpPressed() {
		// TODO Auto-generated method stub

	}

	@Override
	protected void okPressed() {
		// TODO: add sanity checks
		IEntityCollection targetCollection = collectionMap.get(targetCollectionCombo.getSelectionIndex());
		IEntityCollection enrichmentCollection = collectionMap.get(enrichmentCollectionCombo.getSelectionIndex());
		IEntityCollection mappingCollection = collectionMap.get(mappingCollectionCombo.getSelectionIndex());
		int itemModeIndex = itemModeCombo.getSelectionIndex();
		if (targetCollection == null || enrichmentCollection == null || mappingCollection == null || itemModeIndex < 0)
			return;

		score = enrichmentScores.getOrCreateEnrichmentScore(targetCollection, enrichmentCollection, mappingCollection,
				itemModeIndex == 0 ? false : true, thresholdSpinner.getSelection());

		super.okPressed();
	}

	/**
	 * @return the score, see {@link #score}
	 */
	public EnrichmentScore getScore() {
		return score;
	}

}
