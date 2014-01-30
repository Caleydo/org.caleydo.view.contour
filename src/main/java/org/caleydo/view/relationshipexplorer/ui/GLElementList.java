/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElement.EVisibility;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.ScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;

/**
 * @author Christian
 *
 */
public class GLElementList implements IHasMinSize {

	protected static final int DEFAULT_ELEMENT_GAP = 2;
	protected static final int SCROLLBAR_WIDTH = 8;

	protected Set<ListElement> selectedElements = new HashSet<>();
	protected Map<GLElement, ListElement> listElementMap = new HashMap<>();

	protected ScrollableList body;

	private ScrollingDecorator scrollingDecorator;

	protected int elementGap;

	protected class ScrollableList extends AnimatedGLElementContainer {

		public ScrollableList(int elementGap) {
			super(GLLayouts.flowVertical(elementGap));
		}

		@Override
		public void layout(int deltaTimeMs) {
			super.layout(deltaTimeMs);

			Rect clippingArea = scrollingDecorator.getClipingArea();
			for (GLElement child : this) {
				Rect bounds = child.getRectBounds();
				if (child.getVisibility() != EVisibility.NONE) {
					if (clippingArea.asRectangle2D().intersects(bounds.asRectangle2D())) {
						if (child.getVisibility() != EVisibility.PICKABLE) {
							child.setVisibility(EVisibility.PICKABLE);
							repaintAll();
						}
					} else {
						if (child.getVisibility() != EVisibility.HIDDEN) {
							child.setVisibility(EVisibility.HIDDEN);
							repaintAll();
						}
					}
				}
			}
		}
	}

	/**
	 *
	 */
	public GLElementList() {
		this(DEFAULT_ELEMENT_GAP);
	}

	public GLElementList(int elementGap) {
		body = new ScrollableList(elementGap);
		scrollingDecorator = new ScrollingDecorator(body, new ScrollBar(true), new ScrollBar(false), SCROLLBAR_WIDTH,
				EDimension.RECORD);
		scrollingDecorator.setMinSizeProvider(new IHasMinSize() {
			@Override
			public Vec2f getMinSize() {
				return new Vec2f();
			}
		});

		this.elementGap = elementGap;
		setMinSizeProvider(this);
	}

	@Override
	public Vec2f getMinSize() {
		float maxWidth = Float.MIN_VALUE;
		float sumHeight = 0;
		int numItems = 0;
		for (GLElement child : body) {
			if (child.getVisibility() != EVisibility.NONE) {
				ListElement element = (ListElement) child;
				Vec2f minSize = element.getContent().getMinSize();
				sumHeight += minSize.y();
				if (maxWidth < minSize.x())
					maxWidth = minSize.x();

				numItems++;
			}
		}
		return new Vec2f(Math.max(maxWidth, 100), sumHeight + (numItems - 1) * elementGap);
	}

	public void setMinSizeProvider(IHasMinSize minSizeProvider) {
		scrollingDecorator.setMinSizeProvider(minSizeProvider);
	}

	public GLElement asGLElement() {
		return scrollingDecorator;
	}

	public void add(final GLElement element) {
		final ListElement el = new ListElement();
		el.setContent(element);
		el.onPick(new IPickingListener() {

			@Override
			public void pick(Pick pick) {
				boolean isCtrlDown = ((IMouseEvent) pick).isCtrlDown();
				boolean isSelected = selectedElements.contains(el);

				if (pick.getPickingMode() == PickingMode.CLICKED) {
					if (isCtrlDown) {
						if (isSelected) {
							removeFromSelection(el.getContent());
						} else {
							addToSelection(el.getContent());
						}
					} else {
						setSelection(el.getContent());
					}
				} else if (pick.getPickingMode() == PickingMode.RIGHT_CLICKED) {

					if (isCtrlDown) {
						addToSelection(el.getContent());
					} else if (!selectedElements.contains(el)) {
						setSelection(el.getContent());
					}
				}
			}
		});
		el.setSize(Float.NaN, element.getMinSize().y());
		listElementMap.put(element, el);
		body.add(el);
	}

	public void removeFromSelection(GLElement element) {
		ListElement el = listElementMap.get(element);
		selectedElements.remove(el);
		el.setHighlight(false);
	}

	public void clearSelection() {
		for (ListElement element : selectedElements) {
			element.setHighlight(false);
		}
		selectedElements.clear();
	}

	public void setSelection(GLElement element) {
		clearSelection();
		addToSelection(element);
	}

	public void setSelection(int index) {
		GLElement element = body.get(index);
		setSelection(element);
	}

	public void addToSelection(GLElement element) {
		ListElement el = listElementMap.get(element);
		selectedElements.add(el);
		el.setHighlight(true);
		el.setHighlightColor(SelectionType.SELECTION.getColor());
	}

	public void addToSelection(Collection<GLElement> elements) {
		for (GLElement element : elements) {
			addToSelection(element);
		}
	}

	/**
	 * @return the selectedItems, see {@link #selectedItems}
	 */
	public Set<GLElement> getSelectedElements() {
		Set<GLElement> elements = new HashSet<>();

		for (ListElement e : selectedElements) {
			elements.add(e.getContent());
		}
		return elements;
	}

	public void show(GLElement element) {
		setVisibility(element, EVisibility.PICKABLE);
	}

	public void hide(GLElement element) {
		setVisibility(element, EVisibility.NONE);
	}

	protected void setVisibility(GLElement element, EVisibility visibility) {
		ListElement el = listElementMap.get(element);
		if (el != null) {
			el.setVisibility(visibility);
		}
	}

	public void addContextMenuItem(GLElement element, AContextMenuItem item) {
		ListElement el = listElementMap.get(element);
		if (el != null) {
			el.addContextMenuItem(item);
		}
	}

	public void setElementTooltip(GLElement element, String tooltip) {
		ListElement el = listElementMap.get(element);
		if (el != null) {
			el.setToolTip(tooltip);
		}
	}

}
