/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import gleem.linalg.Vec2f;

import java.util.Comparator;

import org.caleydo.core.event.EventPublisher;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.dialog.StringFilterDialog;
import org.caleydo.view.relationshipexplorer.ui.filter.IEntityFilter;
import org.caleydo.view.relationshipexplorer.ui.list.NestableColumn;
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

	protected static final int MIN_TEXT_WIDTH = 150;
	protected static final int ITEM_HEIGHT = 16;

	protected final static Comparator<NestableItem> TEXT_ITEM_COMPARATOR = new Comparator<NestableItem>() {

		@Override
		public int compare(NestableItem o1, NestableItem o2) {
			MinSizeTextElement r1 = (MinSizeTextElement) o1.getElement();
			MinSizeTextElement r2 = (MinSizeTextElement) o2.getElement();
			return r1.getLabel().compareTo(r2.getLabel());
		}

		@Override
		public String toString() {
			return "Alphabetical";
		}
	};

	public class MinSizeTextElement extends GLElement implements ILabeled {

		private String text;
		private Vec2f minSize;

		public MinSizeTextElement(String text) {
			super(GLRenderers.drawText(text));
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

	}

	/**
	 * @param relationshipExplorer
	 */
	public ATextColumn(IEntityCollection entityCollection, RelationshipExplorerElement relationshipExplorer) {
		super(entityCollection, relationshipExplorer);

		final GLButton filterButton = addHeaderButton(FILTER_ICON);

		filterButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				final Vec2f location = filterButton.getAbsoluteLocation();

				column.getColumnTree().getContext().getSWTLayer().run(new ISWTLayerRunnable() {
					@Override
					public void run(Display display, Composite canvas) {

						Point loc = canvas.toDisplay((int) location.x(), (int) location.y());
						// StringFilterDialog dialog = new StringFilterDialog(canvas.getShell(), "Filter " + getLabel(),
						// ATextColumn.this, loc, new HashMap<>(mapFilteredElements));
						StringFilterDialog dialog = new StringFilterDialog(canvas.getShell(), "Filter " + getLabel(),
								loc, ATextColumn.this);

						if (dialog.open() == Window.OK) {
							IEntityFilter filter = dialog.getFilter();
							EventPublisher.trigger(new AttributeFilterEvent(filter).to(ATextColumn.this));
						}
					}
				});
			}
		});
	}

	// @SuppressWarnings("unchecked")
	// protected MinSizeTextElement asMinSizeTextElement(GLElement element) {
	// return (MinSizeTextElement) ((KeyBasedGLElementContainer<GLElement>) element).getElement(DATA_KEY);
	// }

	// public MinSizeTextElement addTextElement(String text, Object elementID) {
	// MinSizeTextElement el = new MinSizeTextElement(text);
	// el.setMinSize(new Vec2f(MIN_TEXT_WIDTH, ITEM_HEIGHT));
	// addElement(el, elementID);
	// return el;
	// }

	public MinSizeTextElement addTextItem(String text, Object elementID, NestableColumn column, NestableItem parentItem) {
		MinSizeTextElement el = new MinSizeTextElement(text);
		el.setMinSize(new Vec2f(MIN_TEXT_WIDTH, ITEM_HEIGHT));
		addItem(el, elementID, column, parentItem);
		// addElement(el, elementID);
		return el;
	}

	public MinSizeTextElement createTextItem(String text) {
		MinSizeTextElement el = new MinSizeTextElement(text);
		el.setMinSize(new Vec2f(MIN_TEXT_WIDTH, ITEM_HEIGHT));
		return el;
	}

	// @Override
	// public Comparator<GLElement> getDefaultElementComparator() {
	// return TEXT_COMPARATOR;
	// }

	@Override
	public Comparator<NestableItem> getDefaultComparator() {
		return TEXT_ITEM_COMPARATOR;
	}

	// @ListenTo(sendToMe = true)
	// public void onAttributeFilter(final FilterEvent<String> event) {
	//
	// // AttributeFilterCommand c = new AttributeFilterCommand(this, getTextFilteredElementIDs(
	// // event.getFilterDefinitionData(), event.getItemPool()));
	// // c.execute();
	// // if (event.isSave()) {
	// // relationshipExplorer.getHistory().addHistoryCommand(c, Color.LIGHT_BLUE);
	// // }
	// }

	// protected Set<Object> getTextFilteredElementIDs(String query, Map<Object, GLElement> itemPool) {
	// final String q = query.toLowerCase();
	//
	// Predicate<Entry<Object, GLElement>> textFilter = new Predicate<Entry<Object, GLElement>>() {
	//
	// @Override
	// public boolean apply(Entry<Object, GLElement> input) {
	// return asMinSizeTextElement(input.getValue()).getLabel().toLowerCase().contains(q);
	// }
	// };
	//
	// Set<Object> elementIDs = new HashSet<>();
	// for (Entry<Object, GLElement> entry : itemPool.entrySet()) {
	// if (textFilter.apply(entry)) {
	// elementIDs.add(entry.getKey());
	// }
	// }
	// return elementIDs;
	// }

	@Override
	protected GLElement createElement(Object elementID) {
		return createTextItem(getText(elementID));
	}

	public abstract String getText(Object elementID);

}
