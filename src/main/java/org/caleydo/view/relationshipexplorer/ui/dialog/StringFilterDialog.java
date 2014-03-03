/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog;

import org.caleydo.view.relationshipexplorer.ui.column.ATextColumn;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

import com.google.common.base.Predicate;

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
	protected Text queryText;
	protected String query = "";

	/**
	 * @param parentShell
	 */
	public StringFilterDialog(Shell parentShell, String title, Point loc, ATextColumn column) {
		super(parentShell);
		this.title = title;
		this.column = column;
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

	public Predicate<Object> getFilter() {
		return new Predicate<Object>() {

			@Override
			public boolean apply(Object elementID) {
				return column.getText(elementID).toLowerCase().contains(query.toLowerCase());
			}
		};
	}

}
