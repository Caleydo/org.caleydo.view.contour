/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.view.relationshipexplorer.ui.History;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.ATextColumn;
import org.caleydo.view.relationshipexplorer.ui.column.AttributeFilterEvent;
import org.caleydo.view.relationshipexplorer.ui.filter.IEntityFilter;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

/**
 * @author Christian
 *
 */
public class StringFilterDialog extends Dialog {

	protected String title;
	// protected Object receiver;
	protected Point loc;
	// protected Map<Object, GLElement> itemPool;
	protected final ATextColumn column;
	// protected Set<Object> filterElementIDPool;
	protected Map<IEntityCollection, Set<Object>> originalFilteredItemIDs;

	protected Text queryText;
	protected String query = "";

	protected static class TextFilter implements IEntityFilter {

		protected final int columHistoryID;
		protected final String query;
		protected final History history;

		public TextFilter(int columnHistoryID, String query, History history) {
			this.columHistoryID = columnHistoryID;
			this.query = query;
			this.history = history;
		}

		@Override
		public boolean apply(Object elementID) {
			ATextColumn column = history.getHistoryObjectAs(ATextColumn.class, columHistoryID);
			return column.getText(elementID).toLowerCase().contains(query.toLowerCase());
		}

		@Override
		public String getDescription() {
			ATextColumn column = history.getHistoryObjectAs(ATextColumn.class, columHistoryID);
			return "Filtered " + column.getLabel() + " containing '" + query + "'";
		}
	}

	/**
	 * @param parentShell
	 */
	public StringFilterDialog(Shell parentShell, String title, Point loc, ATextColumn column) {
		super(parentShell);
		this.title = title;
		this.column = column;

		originalFilteredItemIDs = new HashMap<>();
		for (IEntityCollection collection : column.getRelationshipExplorer().getEntityCollections()) {
			originalFilteredItemIDs.put(collection, new HashSet<>(collection.getFilteredElementIDs()));
		}

		// this.receiver = receiver;
		this.loc = loc;
		// this.itemPool = itemPool;
		setShellStyle(SWT.CLOSE);
	}

	@Override
	public void create() {
		super.create();
		getShell().setText(title);
	}

	@Override
	protected Point getInitialLocation(Point initialSize) {
		Point computeSize = getShell().getChildren()[0].computeSize(SWT.DEFAULT, SWT.DEFAULT);
		return new Point(loc.x, loc.y - computeSize.y);
	}

	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = new Composite(parent, 0);
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.verticalSpacing = 0;
		layout.numColumns = 2;
		composite.setLayout(layout);
		composite.setLayoutData(new GridData(GridData.FILL_BOTH));

		addQueryText(composite);
		// TODO: implement
		Button useGlobalFilterButton = new Button(composite, SWT.CHECK);
		useGlobalFilterButton.setSelection(true);
		useGlobalFilterButton.setText("Update all columns");
		// addOKButton(composite);

		composite.pack();
		return composite;
	}

	// @Override
	// protected Control createContents(Composite parent) {
	// Composite composite = new Composite(parent, 0);
	// GridLayout layout = new GridLayout();
	// layout.marginHeight = 0;
	// layout.marginWidth = 0;
	// layout.verticalSpacing = 0;
	// layout.numColumns = 2;
	// composite.setLayout(layout);
	// composite.setLayoutData(new GridData(GridData.FILL_BOTH));
	//
	// addQueryText(composite);
	// // addOKButton(composite);
	//
	// composite.pack();
	// return composite;
	// }

	private final void addQueryText(Composite composite) {
		queryText = new Text(composite, SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.widthHint = 150;
		queryText.setLayoutData(gd);
		queryText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				query = queryText.getText();

				IEntityFilter filter = getFilter();
				EventPublisher.trigger(new AttributeFilterEvent(filter, getFilterElementIDPool(), false).to(column));
				// triggerEvent(false);
			}
		});
	}

	// private final void addOKButton(Composite composite) {
	// Button b = new Button(composite, SWT.PUSH);
	// b.setText(IDialogConstants.OK_LABEL);
	// GridData gd = new GridData(SWT.RIGHT, SWT.CENTER, false, false);
	// b.setLayoutData(gd);
	//
	// b.getShell().setDefaultButton(b);
	// b.addSelectionListener(new SelectionAdapter() {
	// @Override
	// public void widgetSelected(SelectionEvent e) {
	// setReturnCode(OK);
	// // triggerEvent(true);
	// close();
	// }
	// });
	// }

	@Override
	protected void handleShellCloseEvent() {
		query = "";
		// triggerEvent(false);
		super.handleShellCloseEvent();
	}

	// private void triggerEvent(boolean save) {
	// EventPublisher.trigger(new FilterEvent<String>(query, itemPool, save)
	// .to(receiver));
	// }

	// /**
	// * @return the itemPool, see {@link #itemPool}
	// */
	// public Map<Object, GLElement> getItemPool() {
	// return itemPool;
	// }

	/**
	 * @return the query, see {@link #query}
	 */
	public String getQuery() {
		return query;
	}

	public IEntityFilter getFilter() {
		return new TextFilter(column.getHistoryID(), query, column.getRelationshipExplorer().getHistory());
	}

	/**
	 * @return the filterElementIDPool, see {@link #filterElementIDPool}
	 */
	public Set<Object> getFilterElementIDPool() {
		return originalFilteredItemIDs.get(column.getCollection());
	}

	/**
	 * @return the originalFilteredItemIDs, see {@link #originalFilteredItemIDs}
	 */
	public Map<IEntityCollection, Set<Object>> getOriginalFilteredItemIDs() {
		return originalFilteredItemIDs;
	}

	public void restoreOriginalState() {
		for (Entry<IEntityCollection, Set<Object>> entry : originalFilteredItemIDs.entrySet()) {
			entry.getKey().setFilteredItems(entry.getValue());
			entry.getKey().notifyFilterUpdate(null);
		}
	}

}
