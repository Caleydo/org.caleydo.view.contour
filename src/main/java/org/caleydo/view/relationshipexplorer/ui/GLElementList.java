/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import gleem.linalg.Vec2f;

import java.util.Collection;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.view.contextmenu.AContextMenuItem;
import org.caleydo.core.view.contextmenu.ContextMenuCreator;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElement.EVisibility;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.ScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator.IHasMinSize;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.picking.IPickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;
import org.caleydo.view.relationshipexplorer.ui.MultiSelectionUtil.IMultiSelectionHandler;

/**
 * @author Christian
 *
 */
public class GLElementList implements IHasMinSize, IMultiSelectionHandler<GLElement> {

	protected static final int DEFAULT_ELEMENT_GAP = 2;
	protected static final int SCROLLBAR_WIDTH = 8;

	protected Set<ListElement> selectedElements = new HashSet<>();
	protected Map<GLElement, ListElement> listElementMap = new HashMap<>();

	protected ScrollableList body;

	private ScrollingDecorator scrollingDecorator;

	protected int elementGap;

	protected Set<IElementSelectionListener> selectionListeners = new HashSet<>();

	protected ContextMenuCreator contextMenuCreator = new ContextMenuCreator();

	protected Set<ListElement> highlightedElements = new HashSet<>();

	protected boolean isHighlightSelections = true;

	private class ListElementComparatorWrapper implements Comparator<GLElement> {

		private final Comparator<GLElement> wrappee;

		public ListElementComparatorWrapper(Comparator<GLElement> wrappee) {
			this.wrappee = wrappee;
		}

		@Override
		public int compare(GLElement o1, GLElement o2) {
			if (wrappee == null) {
				System.out.println("wrappee null");
			}

			return wrappee.compare(((ListElement) o1).getContent(), ((ListElement) o2).getContent());
		}

	}

	public interface IElementSelectionListener {
		public void onSelectionChanged(GLElementList list);

		public void onHighlightChanged(GLElementList list);
	}

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

		@Override
		protected void takeDown() {
			selectionListeners.clear();
			super.takeDown();
		}

		protected IGLElementContext getContext() {
			return context;
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
		el.setHighlightColor(SelectionType.MOUSE_OVER.getColor());
		el.setSelectionColor(SelectionType.SELECTION.getColor());
		el.setContent(element);
		el.onPick(new IPickingListener() {

			@Override
			public void pick(Pick pick) {

				boolean update = MultiSelectionUtil.handleSelection(pick, el.getContent(), GLElementList.this);
				if (update) {
					notifyOfSelectionUpdate();
				}
				update = MultiSelectionUtil.handleHighlight(pick, el.getContent(), GLElementList.this);
				if (update) {
					notifyOfHighlightUpdate();
				}
				// boolean isCtrlDown = ((IMouseEvent) pick).isCtrlDown();
				// boolean isSelected = selectedElements.contains(el);
				//
				// if (pick.getPickingMode() == PickingMode.CLICKED) {
				// if (isCtrlDown) {
				// if (isSelected) {
				// removeFromSelection(el.getContent());
				// } else {
				// addToSelection(el.getContent());
				// }
				// notifySelectionListeners(el.getContent(), pick);
				// } else {
				//
				// setSelection(el.getContent());
				// notifySelectionListeners(el.getContent(), pick);
				//
				//
				// }
				// } else if (pick.getPickingMode() == PickingMode.RIGHT_CLICKED) {
				// if (!selectedElements.contains(el)) {
				// if (isCtrlDown) {
				// addToSelection(el.getContent());
				// } else {
				// setSelection(el.getContent());
				// }
				// notifySelectionListeners(el.getContent(), pick);
				// }
				// }

			}
		});
		el.onPick(new IPickingListener() {

			@Override
			public void pick(Pick pick) {
				if (pick.getPickingMode() == PickingMode.RIGHT_CLICKED && contextMenuCreator.hasMenuItems()) {
					body.getContext().getSWTLayer().showContextMenu(contextMenuCreator);
				}

			}
		});

		el.setSize(Float.NaN, element.getMinSize().y());
		listElementMap.put(element, el);
		body.add(el);
	}

	@Override
	public void removeFromSelection(GLElement element) {
		ListElement el = listElementMap.get(element);
		if (el == null)
			return;
		selectedElements.remove(el);
		el.setSelected(false);
	}

	public void clearSelection() {
		for (ListElement element : selectedElements) {
			element.setSelected(false);
		}
		selectedElements.clear();
	}

	@Override
	public void setSelection(GLElement element) {
		clearSelection();
		addToSelection(element);
	}

	public void setSelection(int index) {
		GLElement element = body.get(index);
		setSelection(element);
	}

	@Override
	public void addToSelection(GLElement element) {
		ListElement el = listElementMap.get(element);
		if (el == null)
			return;
		selectedElements.add(el);
		el.setSelected(isHighlightSelections);
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

	// public void addContextMenuItem(GLElement element, AContextMenuItem item) {
	// ListElement el = listElementMap.get(element);
	// if (el != null) {
	// el.addContextMenuItem(item);
	// }
	// }

	public void addContextMenuItem(AContextMenuItem item) {
		contextMenuCreator.add(item);
	}

	public void addContextMenuItems(List<AContextMenuItem> items) {
		contextMenuCreator.addAll(items);
	}

	public void setElementTooltip(GLElement element, String tooltip) {
		ListElement el = listElementMap.get(element);
		if (el != null) {
			el.setToolTip(tooltip);
		}
	}

	public void addElementSelectionListener(IElementSelectionListener listener) {
		if (listener != null)
			selectionListeners.add(listener);
	}

	public void removeElementSelectionListener(IElementSelectionListener listener) {
		selectionListeners.remove(listener);
	}

	protected void notifyOfSelectionUpdate() {
		for (IElementSelectionListener listener : selectionListeners) {
			listener.onSelectionChanged(this);
		}
	}

	protected void notifyOfHighlightUpdate() {
		for (IElementSelectionListener listener : selectionListeners) {
			listener.onHighlightChanged(this);
		}
	}

	public void sortBy(Comparator<GLElement> comparator) {
		body.sortBy(new ListElementComparatorWrapper(comparator));
	}

	public void clear() {
		body.clear();
		selectedElements.clear();
		highlightedElements.clear();
		listElementMap.clear();
	}

	public boolean hasElement(GLElement element) {
		return listElementMap.containsKey(element);
	}

	public void removeElement(GLElement element) {
		removeFromSelection(element);
		ListElement el = listElementMap.get(element);
		if (el == null)
			return;
		listElementMap.remove(element);
		body.remove(el);
	}

	/**
	 * @param isHighlightSelections
	 *            setter, see {@link isHighlightSelections}
	 */
	public void setHighlightSelections(boolean isHighlightSelections) {
		if (isHighlightSelections != this.isHighlightSelections) {
			this.isHighlightSelections = isHighlightSelections;
			for (ListElement el : selectedElements) {
				el.setSelected(isHighlightSelections);
			}
		}
	}

	@Override
	public boolean isSelected(GLElement element) {
		ListElement el = listElementMap.get(element);
		return selectedElements.contains(el);
	}

	@Override
	public boolean isHighlight(GLElement element) {
		ListElement el = listElementMap.get(element);
		return highlightedElements.contains(el);
	}

	@Override
	public void setHighlight(GLElement element) {
		highlightedElements.clear();
		addToHighlight(element);
	}

	public void addToHighlight(GLElement element) {
		ListElement el = listElementMap.get(element);
		if (el == null)
			return;
		highlightedElements.add(el);
		el.setHighlight(true);
	}

	@Override
	public void removeHighlight(GLElement element) {
		ListElement el = listElementMap.get(element);
		if (el == null)
			return;
		highlightedElements.remove(el);
		el.setHighlight(false);
	}

	public Set<GLElement> getHighlightElements() {
		Set<GLElement> elements = new HashSet<>();

		for (ListElement e : highlightedElements) {
			elements.add(e.getContent());
		}
		return elements;
	}

	public void clearHighlight() {
		for (ListElement element : highlightedElements) {
			element.setHighlight(false);
		}
		highlightedElements.clear();
	}

}
