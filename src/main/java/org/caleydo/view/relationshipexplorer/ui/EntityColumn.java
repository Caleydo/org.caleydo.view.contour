/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import org.caleydo.core.event.EventListenerManager.DeepScan;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;

/**
 * @author Christian
 *
 */
public class EntityColumn extends GLElementContainer {
	//
	// public static final int ROW_GAP = 2;

	protected static final int HEADER_HEIGHT = 20;
	protected static final int HEADER_BODY_SPACING = 5;


	// private final ColumnBody body;
	private GLElement header;
	@DeepScan
	private final IEntityColumnContentProvider contentProvider;

	//
	// private boolean isCtrlPressed = false;
	// private boolean isShiftPressed = false;
	// private IGLCanvas canvas;
	//
	// private IGLKeyListener keyListener;
	//
	// private Set<EntityColumnItem<?>> selectedItems = new HashSet<>();

	private GLElementList itemList = new GLElementList();

	public static interface IEntityColumnContentProvider extends ILabeled {
		// public void setColumnBody(ColumnBody body);

		public void setContent(GLElementList itemList);

		public void setEntityColumn(EntityColumn column);

		public void takeDown();
	}

	// public class ColumnBody extends GLElementContainer implements IHasMinSize {
	//
	// protected ScrollingDecorator scrollingDecorator;
	//
	// public ColumnBody(IGLLayout2 layout) {
	// super(layout);
	// }
	//
	// @Override
	// public void layout(int deltaTimeMs) {
	// super.layout(deltaTimeMs);
	//
	// Rect clippingArea = scrollingDecorator.getClipingArea();
	// for (GLElement child : this) {
	// Rect bounds = child.getRectBounds();
	// if (child.getVisibility() != EVisibility.NONE) {
	// if (clippingArea.asRectangle2D().intersects(bounds.asRectangle2D())) {
	// if (child.getVisibility() != EVisibility.PICKABLE) {
	// child.setVisibility(EVisibility.PICKABLE);
	// repaintAll();
	// }
	// } else {
	// if (child.getVisibility() != EVisibility.HIDDEN) {
	// child.setVisibility(EVisibility.HIDDEN);
	// repaintAll();
	// }
	// }
	// }
	// }
	// }
	//
	// /**
	// * @param scrollingDecorator
	// * setter, see {@link scrollingDecorator}
	// */
	// public void setScrollingDecorator(ScrollingDecorator scrollingDecorator) {
	// this.scrollingDecorator = scrollingDecorator;
	// }
	//
	// @Override
	// public Vec2f getMinSize() {
	// float maxWidth = Float.MIN_VALUE;
	// float sumHeight = 0;
	// int numItems = 0;
	// for (GLElement el : this) {
	// if (el.getVisibility() != EVisibility.NONE) {
	// @SuppressWarnings("unchecked")
	// EntityColumnItem<? extends IHasMinSize> item = (EntityColumnItem<? extends IHasMinSize>) el;
	// Vec2f minSize = item.getContent().getMinSize();
	// sumHeight += minSize.y();
	// if (maxWidth < minSize.x())
	// maxWidth = minSize.x();
	//
	// numItems++;
	// }
	// }
	// return new Vec2f(Math.max(maxWidth, 100), sumHeight + (numItems - 1) * EntityColumn.ROW_GAP);
	// }
	//
	// }

	public EntityColumn(IEntityColumnContentProvider contentProvider) {
		super(GLLayouts.flowVertical(HEADER_BODY_SPACING));
		// this.body = new ColumnBody(GLLayouts.flowVertical(ROW_GAP));
		this.contentProvider = contentProvider;
		header = new GLElement(GLRenderers.drawText(contentProvider.getLabel(), VAlign.CENTER));
		header.setSize(Float.NaN, HEADER_HEIGHT);
		add(header);
		add(itemList.asGLElement());
		contentProvider.setEntityColumn(this);
		contentProvider.setContent(itemList);
		// for (GLElement element : contentProvider.getContent()) {
		// itemList.add(element);
		// }
		// contentProvider.updateContent(itemList);
		// ScrollingDecorator scrollingDecorator = new ScrollingDecorator(body, new ScrollBar(true), new
		// ScrollBar(false),
		// SCROLLBAR_WIDTH, EDimension.RECORD);
		// body.setScrollingDecorator(scrollingDecorator);
		// contentProvider.setColumnBody(body);
		// scrollingDecorator.setMinSizeProvider(body);
		// add(scrollingDecorator);
		// for (final EntityColumnItem<?> item : contentProvider.getContent()) {
		// body.add(item);
		// item.onPick(new IPickingListener() {
		//
		// @Override
		// public void pick(Pick pick) {
		// if (pick.getPickingMode() == PickingMode.CLICKED) {
		// if (isCtrlPressed) {
		// addToSelection(item);
		// } else {
		// setSelection(item);
		// }
		// } else if (pick.getPickingMode() == PickingMode.RIGHT_CLICKED) {
		// if (isCtrlPressed) {
		// addToSelection(item);
		// } else if (!selectedItems.contains(item)) {
		// setSelection(item);
		// }
		// }
		// }
		// });
		// }

	}

	// public void clearSelection() {
	// for (EntityColumnItem<?> item : selectedItems) {
	// item.setHighlight(false);
	// }
	// selectedItems.clear();
	// }
	//
	// public void setSelection(EntityColumnItem<?> item) {
	// clearSelection();
	// addToSelection(item);
	// }
	//
	// public void setSelection(int index) {
	// EntityColumnItem<?> item = (EntityColumnItem<?>) body.get(index);
	// setSelection(item);
	// }
	//
	// public void addToSelection(EntityColumnItem<?> item) {
	// selectedItems.add(item);
	// item.setHighlight(true);
	// item.setHighlightColor(SelectionType.SELECTION.getColor());
	// }
	//
	// public void addToSelection(Collection<EntityColumnItem<?>> items) {
	// for (EntityColumnItem<?> item : items) {
	// addToSelection(item);
	// }
	// }
	//
	// /**
	// * @return the selectedItems, see {@link #selectedItems}
	// */
	// public Set<EntityColumnItem<?>> getSelectedItems() {
	// return selectedItems;
	// }

	// @Override
	// protected void init(IGLElementContext context) {
	// super.init(context);
	// AGLElementView view = findParent(AGLElementView.class);
	// canvas = view.getParentGLCanvas();
	// keyListener = new IGLKeyListener() {
	//
	// @Override
	// public void keyReleased(IKeyEvent e) {
	// update(e);
	// }
	//
	// @Override
	// public void keyPressed(IKeyEvent e) {
	// update(e);
	// }
	//
	// protected void update(IKeyEvent e) {
	// isCtrlPressed = e.isControlDown();
	// isShiftPressed = e.isShiftDown();
	// }
	// };
	// canvas.addKeyListener(keyListener);
	// }

	/**
	 * @return the body, see {@link #body}
	 */
	// public ColumnBody getBody() {
	// return body;
	// }

	@Override
	protected void takeDown() {
		contentProvider.takeDown();
		// canvas.removeKeyListener(keyListener);
		super.takeDown();
	}

	public void setCaption(String caption) {
		header.setRenderer(GLRenderers.drawText(caption, VAlign.CENTER));
	}

	/**
	 * @return the itemList, see {@link #itemList}
	 */
	public GLElementList getItemList() {
		return itemList;
	}
}
