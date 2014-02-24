/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import gleem.linalg.Vec2f;

import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.caleydo.core.event.EventListenerManager.ListenTo;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.ISWTLayer.ISWTLayerRunnable;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.column.operation.AttributeFilterCommand;
import org.caleydo.view.relationshipexplorer.ui.list.INestableColumn.Column;
import org.caleydo.view.relationshipexplorer.ui.list.INestableColumn.NestableItem;
import org.caleydo.view.relationshipexplorer.ui.util.KeyBasedGLElementContainer;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;

import com.google.common.base.Predicate;

/**
 * @author Christian
 *
 */
public abstract class ATextColumn extends AEntityColumn {

	/**
	 * @param relationshipExplorer
	 */
	public ATextColumn(RelationshipExplorerElement relationshipExplorer) {
		super(relationshipExplorer);
	}

	protected static final int MIN_TEXT_WIDTH = 150;
	protected static final int ITEM_HEIGHT = 16;

	protected class TextComparator implements Comparator<GLElement> {

		private final ATextColumn column;

		public TextComparator(ATextColumn column) {
			this.column = column;
		}

		@Override
		public int compare(GLElement arg0, GLElement arg1) {

			MinSizeTextElement r1 = column.asMinSizeTextElement(arg0);
			MinSizeTextElement r2 = column.asMinSizeTextElement(arg1);
			return r1.getLabel().compareTo(r2.getLabel());
		}
	}

	protected final TextComparator TEXT_COMPARATOR = new TextComparator(this);

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

	@Override
	protected void init(final IGLElementContext context) {
		super.init(context);
		final GLButton filterButton = addHeaderButton(FILTER_ICON);

		filterButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				final Vec2f location = filterButton.getAbsoluteLocation();

				context.getSWTLayer().run(new ISWTLayerRunnable() {
					@Override
					public void run(Display display, Composite canvas) {
						Point loc = canvas.toDisplay((int) location.x(), (int) location.y());
						StringFilterDialog dialog = new StringFilterDialog(canvas.getShell(), "Filter " + getLabel(),
								ATextColumn.this, loc, new HashMap<>(mapFilteredElements));
						dialog.open();
					}
				});
			}
		});
	}

	@SuppressWarnings("unchecked")
	protected MinSizeTextElement asMinSizeTextElement(GLElement element) {
		return (MinSizeTextElement) ((KeyBasedGLElementContainer<GLElement>) element).getElement(DATA_KEY);
	}

	public MinSizeTextElement addTextElement(String text, Object elementID) {
		MinSizeTextElement el = new MinSizeTextElement(text);
		el.setMinSize(new Vec2f(MIN_TEXT_WIDTH, ITEM_HEIGHT));
		addElement(el, elementID);
		return el;
	}

	public MinSizeTextElement addTextElement(String text, Object elementID, Column column, NestableItem parentItem) {
		MinSizeTextElement el = new MinSizeTextElement(text);
		el.setMinSize(new Vec2f(MIN_TEXT_WIDTH, ITEM_HEIGHT));
		column.addElement(el, parentItem);
		// addElement(el, elementID);
		return el;
	}

	@Override
	public Comparator<GLElement> getDefaultElementComparator() {
		return TEXT_COMPARATOR;
	}

	@ListenTo(sendToMe = true)
	public void onAttributeFilter(final AttributeFilterEvent<String> event) {

		AttributeFilterCommand c = new AttributeFilterCommand(this, getTextFilteredElementIDs(
				event.getFilterDefinitionData(), event.getItemPool()));
		c.execute();
		if (event.isSave()) {
			relationshipExplorer.getHistory().addHistoryCommand(c, Color.LIGHT_BLUE);
		}
	}

	protected Set<Object> getTextFilteredElementIDs(String query, Map<Object, GLElement> itemPool) {
		final String q = query.toLowerCase();

		Predicate<Entry<Object, GLElement>> textFilter = new Predicate<Entry<Object, GLElement>>() {

			@Override
			public boolean apply(Entry<Object, GLElement> input) {
				return asMinSizeTextElement(input.getValue()).getLabel().toLowerCase().contains(q);
			}
		};

		Set<Object> elementIDs = new HashSet<>();
		for (Entry<Object, GLElement> entry : itemPool.entrySet()) {
			if (textFilter.apply(entry)) {
				elementIDs.add(entry.getKey());
			}
		}
		return elementIDs;
	}

}
