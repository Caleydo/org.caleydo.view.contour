/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.dialog;

import org.apache.commons.lang.StringUtils;
import org.caleydo.core.event.EventPublisher;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.column.AInvertibleComparator;
import org.caleydo.view.relationshipexplorer.ui.column.CompositeComparator;
import org.caleydo.view.relationshipexplorer.ui.column.IInvertibleComparator;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.ThreadSyncEvent;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
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

/**
 * @author Christian
 *
 */
public class SearchDialog extends Dialog {

	protected String title;
	protected Point loc;
	protected final AEntityColumn column;

	protected Text queryText;
	protected String query = "";

	protected IInvertibleComparator<NestableItem> previousComparator;

	protected static class StringDistanceComparator extends AInvertibleComparator<NestableItem> {

		protected final IEntityCollection collection;
		protected final String query;

		public StringDistanceComparator(String query, IEntityCollection collection) {
			this.query = query.toUpperCase();
			this.collection = collection;
		}

		@Override
		public int compare(NestableItem o1, NestableItem o2) {
			String label1 = collection.getText(o1.getElementData().iterator().next()).toUpperCase();
			String label2 = collection.getText(o2.getElementData().iterator().next()).toUpperCase();

			// TODO: find better string similarity measure than levenshtein and containment
			// int l1Contained = label1.contains(query) ? 1 : Integer.MAX_VALUE;
			// int l2Contained = label2.contains(query) ? 1 : Integer.MAX_VALUE;

			return StringUtils.getLevenshteinDistance(label1, query)
					- StringUtils.getLevenshteinDistance(label2, query);
		}

		@Override
		public String toString() {
			return "String Distance";
		}
	}

	protected static class SubStringContainmentComparator extends AInvertibleComparator<NestableItem> {

		protected final IEntityCollection collection;
		protected final String query;

		public SubStringContainmentComparator(String query, IEntityCollection collection) {
			this.query = query.toUpperCase();
			this.collection = collection;
		}

		@Override
		public int compare(NestableItem o1, NestableItem o2) {
			String label1 = collection.getText(o1.getElementData().iterator().next()).toUpperCase();
			String label2 = collection.getText(o2.getElementData().iterator().next()).toUpperCase();

			// TODO: find better string similarity measure than levenshtein and containment
			int l1Contained = label1.contains(query) ? 1 : 0;
			int l2Contained = label2.contains(query) ? 1 : 0;

			return l2Contained - l1Contained;
		}

		@Override
		public String toString() {
			return "String containment";
		}
	}

	/**
	 * @param parentShell
	 */
	public SearchDialog(Shell parentShell, String title, Point loc, AEntityColumn column) {
		super(parentShell);
		this.title = title;
		this.column = column;
		this.previousComparator = column.getCurrentComparator();
		// this.receiver = receiver;
		this.loc = loc;
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

		composite.pack();
		return composite;
	}

	private final void addQueryText(Composite composite) {
		queryText = new Text(composite, SWT.BORDER);
		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		gd.widthHint = 150;
		queryText.setLayoutData(gd);
		queryText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent e) {
				query = queryText.getText();

				EventPublisher.trigger(new ThreadSyncEvent(new Runnable() {

					@Override
					public void run() {
						column.sortBy(new CompositeComparator<NestableItem>(new SubStringContainmentComparator(query,
								column.getCollection()), new StringDistanceComparator(query, column.getCollection())));
					}
				}).to(column.getRelationshipExplorer()));
			}
		});
	}

	@Override
	protected void handleShellCloseEvent() {
		query = "";
		// triggerEvent(false);
		super.handleShellCloseEvent();
	}

	/**
	 * @return the previousComparator, see {@link #previousComparator}
	 */
	public IInvertibleComparator<NestableItem> getOriginalComparator() {
		return previousComparator;
	}

}
