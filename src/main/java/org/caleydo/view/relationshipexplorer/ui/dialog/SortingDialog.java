/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.collection.column.container.CategoricalClassDescription;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.gui.util.AHelpButtonDialog;
import org.caleydo.core.io.DataDescription;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.view.relationshipexplorer.ui.collection.EnrichmentScores;
import org.caleydo.view.relationshipexplorer.ui.collection.EnrichmentScores.AEnrichmentScoreComparator;
import org.caleydo.view.relationshipexplorer.ui.collection.EnrichmentScores.EnrichmentScore;
import org.caleydo.view.relationshipexplorer.ui.collection.EnrichmentScores.EnrichmentScoreComparator;
import org.caleydo.view.relationshipexplorer.ui.collection.EnrichmentScores.MaxEnrichmentScoreComparator;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.column.CompositeComparator;
import org.caleydo.view.relationshipexplorer.ui.column.IInvertibleComparator;
import org.caleydo.view.relationshipexplorer.ui.column.IScoreProvider;
import org.caleydo.view.relationshipexplorer.ui.column.ItemComparators;
import org.caleydo.view.relationshipexplorer.ui.column.ItemComparators.AMappingComparator;
import org.caleydo.view.relationshipexplorer.ui.column.ItemComparators.NumericalAttributeComparator;
import org.caleydo.view.relationshipexplorer.ui.column.ItemComparators.SelectionMappingComparator;
import org.caleydo.view.relationshipexplorer.ui.column.ItemComparators.TotalMappingComparator;
import org.caleydo.view.relationshipexplorer.ui.column.ItemComparators.VisibleMappingComparator;
import org.caleydo.view.relationshipexplorer.ui.column.TabularDataColumn;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;
import org.caleydo.view.relationshipexplorer.ui.list.NestableColumn;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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

	protected Set<IInvertibleComparator<NestableItem>> comparators;

	protected Button considerSelectionsButton;
	protected Button sortByNumberOfChildItemsButton;
	protected Button sortByEnrichmentScoreButton;
	protected Button sortByAttributeButton;

	protected Combo scoreCombo;
	protected Combo childColumnCombo;
	protected Combo attributeCombo;
	protected Group criteriaGroup;

	protected Map<Integer, IColumnModel> childColumnMap = new HashMap<>();
	protected Map<Integer, EnrichmentScore> scoreMap = new HashMap<>();
	protected Map<Integer, Integer> attributeMap = new HashMap<>();

	protected IInvertibleComparator<NestableItem> definedComparator;

	protected IScoreProvider scoreProvider;

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
		List<IInvertibleComparator<NestableItem>> currentComparators = new ArrayList<>();
		if (column.getCurrentComparator() instanceof CompositeComparator<?>) {
			CompositeComparator<?> compositeComparator = (CompositeComparator<?>) column.getCurrentComparator();
			for (Object o : compositeComparator) {
				currentComparators.add((IInvertibleComparator<NestableItem>) o);
			}
		} else {
			currentComparators.add(column.getCurrentComparator());
		}

		Composite parentComposite = new Composite(parent, SWT.NONE);
		parentComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		parentComposite.setLayout(new GridLayout(1, true));

		criteriaGroup = new Group(parentComposite, SWT.SHADOW_ETCHED_IN);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		criteriaGroup.setLayout(new GridLayout(3, false));
		// gd.widthHint = 400;
		criteriaGroup.setLayoutData(gd);
		criteriaGroup.setText("Sorting Criteria");
		// final org.eclipse.swt.widgets.Table table = new org.eclipse.swt.widgets.Table(group, SWT.CHECK | SWT.BORDER
		// | SWT.V_SCROLL | SWT.H_SCROLL);
		for (IInvertibleComparator<NestableItem> c : comparators) {

			Button button = new Button(criteriaGroup, SWT.RADIO);
			button.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 3, 1));
			button.setText(c.toString());
			button.setData(c);
			button.setSelection(currentComparators.contains(c));
			// TableItem item = new TableItem(table, SWT.NONE);
			// item.setText(c.toString());
			// item.setChecked(true);
			// item.setData(c);
		}

		List<NestableColumn> childColumns = column.getColumn().getChildren();
		AMappingComparator mappingComparator = null;
		EnrichmentScore enrichmentScore = null;
		NumericalAttributeComparator numericalAttributeComparator = null;
		for (IInvertibleComparator<NestableItem> c : currentComparators) {
			if (c instanceof AMappingComparator) {
				mappingComparator = (AMappingComparator) c;
				break;
			} else if (c instanceof AEnrichmentScoreComparator) {
				enrichmentScore = ((AEnrichmentScoreComparator) c).getEnrichmentScore();
			} else if (c instanceof NumericalAttributeComparator) {
				numericalAttributeComparator = (NumericalAttributeComparator) c;
			}
		}
		if (!childColumns.isEmpty()) {

			sortByNumberOfChildItemsButton = new Button(criteriaGroup, SWT.RADIO);
			sortByNumberOfChildItemsButton.setText("Sort by number of child items in");
			sortByNumberOfChildItemsButton.setSelection(mappingComparator != null);
			sortByNumberOfChildItemsButton.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false));
			childColumnCombo = new Combo(criteriaGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
			childColumnCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));

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

		sortByEnrichmentScoreButton = new Button(criteriaGroup, SWT.RADIO);
		sortByEnrichmentScoreButton.setText("Sort by enrichment score");
		sortByEnrichmentScoreButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				scoreCombo.setEnabled(sortByEnrichmentScoreButton.getSelection());
			}
		});

		scoreCombo = new Combo(criteriaGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
		gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		gd.widthHint = 200;
		scoreCombo.setLayoutData(gd);
		scoreCombo.setEnabled(false);

		if (enrichmentScore != null) {
			sortByEnrichmentScoreButton.setSelection(true);
			scoreCombo.setEnabled(true);
		}
		updateScoreCombo(enrichmentScore);

		Button newScoreButton = new Button(criteriaGroup, SWT.PUSH);
		newScoreButton.setText("New");
		newScoreButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				CreateEnrichmentScoreDialog dialog = new CreateEnrichmentScoreDialog(getShell(), column
						.getRelationshipExplorer());
				if (dialog.open() == Window.OK) {
					updateScoreCombo(dialog.getScore());
				}
			}
		});

		if (column instanceof TabularDataColumn) {
			sortByAttributeButton = new Button(criteriaGroup, SWT.RADIO);
			sortByAttributeButton.setText("Sort by attribute");
			if (numericalAttributeComparator != null)
				sortByAttributeButton.setSelection(true);
			sortByAttributeButton.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					attributeCombo.setEnabled(sortByAttributeButton.getSelection());
				}
			});

			attributeCombo = new Combo(criteriaGroup, SWT.DROP_DOWN | SWT.READ_ONLY);
			attributeCombo.setLayoutData(new GridData(SWT.FILL, SWT.TOP, true, false, 2, 1));
			attributeCombo.setEnabled(numericalAttributeComparator != null);
			updateAttributeCombo(numericalAttributeComparator);

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

	protected void updateScoreCombo(EnrichmentScore currentScore) {
		scoreCombo.removeAll();
		scoreMap.clear();
		EnrichmentScores enrichmentScores = column.getRelationshipExplorer().getEnrichmentScores();
		Collection<EnrichmentScore> allScores = enrichmentScores.getAllScoresForTargetOrEnrichment(column
				.getCollection());
		Iterator<EnrichmentScore> it = allScores.iterator();
		for (int i = 0; i < allScores.size(); i++) {
			EnrichmentScore score = it.next();
			scoreMap.put(i, score);
			scoreCombo.add(score.getLabel());
			if (score == currentScore)
				scoreCombo.select(i);
		}
		if (allScores.size() == 1)
			scoreCombo.select(0);
	}

	protected void updateAttributeCombo(NumericalAttributeComparator c) {
		TabularDataCollection collection = (TabularDataCollection) column.getCollection();
		ATableBasedDataDomain dataDomain = collection.getDataDomain();
		Table table = dataDomain.getTable();
		int i = 0;
		// TODO: Currently only available for numerical columns
		if (table.isDataHomogeneous()) {
			DataDescription desc = dataDomain.getDataSetDescription().getDataDescription();
			CategoricalClassDescription<?> categoricalClassDesc = desc.getCategoricalClassDescription();
			if (categoricalClassDesc == null) {
				for (int dimensionID : collection.getDimensionPerspective().getVirtualArray()) {
					String label = dataDomain.getDimensionLabel(dimensionID);
					attributeCombo.add(label);
					attributeMap.put(i, dimensionID);
					if (c != null && c.getDimensionID() == dimensionID) {
						attributeCombo.select(i);
					}
					i++;
				}
			}

		} else {
			for (int dimensionID : collection.getDimensionPerspective().getVirtualArray()) {
				Object dataClassDesc = table.getDataClassSpecificDescription(dimensionID);
				if (dataClassDesc == null || dataClassDesc instanceof NumericalProperties) {
					String label = dataDomain.getDimensionLabel(dimensionID);
					attributeCombo.add(label);
					attributeMap.put(i, dimensionID);
					if (c != null && c.getDimensionID() == dimensionID) {
						attributeCombo.select(i);
					}
					i++;
				}

			}
		}

		if (attributeMap.size() == 1)
			attributeCombo.select(0);

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
					} else if (button == sortByEnrichmentScoreButton) {

						EnrichmentScore score = scoreMap.get(scoreCombo.getSelectionIndex());
						if (score == null)
							return;

						if (column.getParentColumn() != null
								&& score.hasEnrichmentOrTargetCollection(column.getParentColumn().getColumnModel()
										.getCollection())) {
							EnrichmentScoreComparator c = new EnrichmentScoreComparator(score);
							scoreProvider = c;
							comparator.add(c);
						} else {
							MaxEnrichmentScoreComparator c = new MaxEnrichmentScoreComparator(score);
							scoreProvider = c;
							comparator.add(c);
						}

					} else if (button == sortByAttributeButton) {
						Integer dimensionID = attributeMap.get(attributeCombo.getSelectionIndex());
						if (dimensionID == null)
							return;
						comparator.add(new NumericalAttributeComparator((TabularDataCollection) column.getCollection(),
								dimensionID));
					} else {
						comparator.add((IInvertibleComparator<NestableItem>) button.getData());
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
	public IInvertibleComparator<NestableItem> getComparator() {
		return definedComparator;
	}

	/**
	 * @return the scoreProvider, see {@link #scoreProvider}
	 */
	public IScoreProvider getScoreProvider() {
		return scoreProvider;
	}

}
