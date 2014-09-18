/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import gleem.linalg.Vec2f;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.contextmenu.ThreadSyncEvent;
import org.caleydo.view.relationshipexplorer.ui.dialog.SearchDialog;
import org.caleydo.view.relationshipexplorer.ui.dialog.StringFilterDialog;
import org.caleydo.view.relationshipexplorer.ui.filter.IEntityFilter;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

/**
 * @author Christian
 *
 */
public abstract class ATextColumn extends AEntityColumn {

	// protected Map<Object, Set<NestableItem>> mapIDToItems = new HashMap<>();

	protected final TextItemComparator textItemComparator;

	protected static class TextItemComparator extends AInvertibleComparator<NestableItem> {

		protected final ATextColumn column;

		public TextItemComparator(ATextColumn column) {
			this.column = column;
		}

		@Override
		public int compare(NestableItem o1, NestableItem o2) {
			String label1 = column.getText(o1.getElementData().iterator().next());
			String label2 = column.getText(o2.getElementData().iterator().next());
			// MinSizeTextElement r1 = (MinSizeTextElement) ((ScoreElement) o1.getElement()).getElement();
			// MinSizeTextElement r2 = (MinSizeTextElement) ((ScoreElement) o2.getElement()).getElement();
			return label1.compareTo(label2);
		}

		@Override
		public String toString() {
			return "Alphabetical";
		}
	}

	public class MinSizeTextElement extends PickableGLElement implements ILabeled {

		private String text;
		private Vec2f minSize;

		public MinSizeTextElement(String text) {
			setRenderer(GLRenderers.drawText(text, VAlign.LEFT, new GLPadding(0, 0, 0, 2)));
			this.text = text;
		}

		@Override
		public Vec2f getMinSize() {
			return minSize;
		}

		/**
		 * @param minSize
		 *            setter, see {@link minSize}
		 */
		public void setMinSize(Vec2f minSize) {
			this.minSize = minSize;
		}

		@Override
		public String getLabel() {
			return text;
		}

		@Override
		public String getTooltip() {
			return getLabel();
		}

	}

	/**
	 * @param relationshipExplorer
	 */
	public ATextColumn(IEntityCollection entityCollection, final ConTourElement relationshipExplorer) {
		super(entityCollection, relationshipExplorer);
		this.textItemComparator = new TextItemComparator(this);
		// setItemFactory(new TextItemFactory(this));
		final GLButton filterButton = addHeaderButton(FILTER_ICON, "Filter by Name");

		filterButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				final Vec2f location = filterButton.getAbsoluteLocation();

				relationshipExplorer.getContext().getSWTLayer().run(new ISWTLayerRunnable() {
					@Override
					public void run(Display display, Composite canvas) {

						Point loc = canvas.toDisplay((int) location.x(), (int) location.y());
						// StringFilterDialog dialog = new StringFilterDialog(canvas.getShell(), "Filter " + getLabel(),
						// ATextColumn.this, loc, new HashMap<>(mapFilteredElements));
						StringFilterDialog dialog = new StringFilterDialog(canvas.getShell(), "Filter " + getLabel(),
								loc, ATextColumn.this);

						if (dialog.open() == Window.OK) {
							IEntityFilter filter = dialog.getFilter();
							EventPublisher.trigger(new AttributeFilterEvent(filter, dialog.getFilterElementIDPool(),
									true).to(ATextColumn.this));
						} else {
							// EventPublisher.trigger(new ResetAttributeFilterEvent(dialog.getOriginalFilteredItemIDs())
							// .to(ATextColumn.this.relationshipExplorer));
						}
					}
				});
			}
		});

		final GLButton findButton = addHeaderButton(FIND_ICON, "Search for Items");

		findButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				final Vec2f location = findButton.getAbsoluteLocation();

				relationshipExplorer.getContext().getSWTLayer().run(new ISWTLayerRunnable() {
					@Override
					public void run(Display display, Composite canvas) {

						Point loc = canvas.toDisplay((int) location.x(), (int) location.y());
						// StringFilterDialog dialog = new StringFilterDialog(canvas.getShell(), "Filter " + getLabel(),
						// ATextColumn.this, loc, new HashMap<>(mapFilteredElements));
						SearchDialog dialog = new SearchDialog(canvas.getShell(), "Search " + getLabel(), loc,
								ATextColumn.this);

						if (dialog.open() != Window.OK) {
							final IInvertibleComparator<NestableItem> comparator = dialog.getOriginalComparator();
							EventPublisher.trigger(new ThreadSyncEvent(new Runnable() {
								@Override
								public void run() {
									ATextColumn.this.sortBy(comparator);
								}
							}).to(getRelationshipExplorer()));
						}
					}
				});
			}
		});
	}

	// public MinSizeTextElement createTextItem(String text) {
	// MinSizeTextElement el = new MinSizeTextElement(text);
	// el.setMinSize(new Vec2f(MIN_TEXT_WIDTH, ITEM_HEIGHT));
	// return el;
	// }

	@Override
	public IInvertibleComparator<NestableItem> getDefaultComparator() {
		return textItemComparator;
	}

	// @Override
	// protected GLElement newElement(Object elementID) {
	// return createTextItem(getText(elementID));
	// }

	public abstract String getText(Object elementID);

}
