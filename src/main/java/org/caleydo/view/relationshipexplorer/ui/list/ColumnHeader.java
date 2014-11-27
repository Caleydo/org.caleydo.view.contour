/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.list;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.relationshipexplorer.ui.History;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.command.ReverseColumnSortingCommand;
import org.caleydo.view.relationshipexplorer.ui.util.AnimationUtil;
import org.caleydo.view.relationshipexplorer.ui.util.MappingRenderer;

/**
 * @author Christian
 *
 */
public class ColumnHeader extends AnimatedGLElementContainer implements ISelectionCallback {

	protected GLElementContainer headerItem;
	protected NestableColumn column;
	protected GLButton collapseButton;
	protected MappingRenderer mappingRenderer;
	protected DragAndDropHeader dragAndDropHeader;
	protected GLElement scrollbarSpacing;

	protected AnimatedGLElementContainer captionContainer;
	protected GLElementContainer buttonBar;
	protected GLElement headerExtension;

	public ColumnHeader(final NestableColumn column, String caption) {
		this.column = column;
		setLayout(new GLSizeRestrictiveFlowLayout2(true, ColumnTreeRenderStyle.HORIZONTAL_SPACING, new GLPadding(
				ColumnTreeRenderStyle.HORIZONTAL_PADDING, ColumnTreeRenderStyle.HEADER_TOP_PADDING,
				ColumnTreeRenderStyle.HORIZONTAL_PADDING, 0)));
		setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(this,
				ColumnTreeRenderStyle.HORIZONTAL_SPACING, new GLPadding(ColumnTreeRenderStyle.HORIZONTAL_PADDING,
						ColumnTreeRenderStyle.HEADER_TOP_PADDING, ColumnTreeRenderStyle.HORIZONTAL_PADDING, 0)));

		captionContainer = new AnimatedGLElementContainer(new GLSizeRestrictiveFlowLayout2(true,
				ColumnTreeRenderStyle.HORIZONTAL_SPACING, GLPadding.ZERO));
		captionContainer.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(captionContainer,
				ColumnTreeRenderStyle.HORIZONTAL_SPACING, GLPadding.ZERO));

		GLElementContainer headerContainer = new GLElementContainer(GLLayouts.LAYERS);
		headerContainer.setMinSizeProvider(GLMinSizeProviders.createLayeredMinSizeProvider(headerContainer));
		// headerContainer.setRenderer(GLRenderers.drawRect(Color.GREEN));

		add(headerContainer);

		GLElementContainer spacingContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(false, 0,
				new GLPadding(0, 0, 0, 4)));
		spacingContainer.setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(spacingContainer, 0,
				new GLPadding(0, 0, 0, 4)));

		GLElement spacing = new GLElement();
		spacing.setSize(Float.NaN, Float.NaN);
		// spacing.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(Float.NaN, Float.NaN));
		// spacing.setRenderer(GLRenderers.fillRect(Color.RED));
		spacingContainer.setVisibility(EVisibility.PICKABLE);
		spacingContainer.add(spacing);
		spacingContainer.add(captionContainer);
		// spacingContainer.setRenderer(GLRenderers.drawRect(Color.BLUE));

		buttonBar = new GLElementContainer(new GLSizeRestrictiveFlowLayout(true, 1, new GLPadding(0, -9, 0, 9)));
		// buttonBar.setRenderer(GLRenderers.drawRect(Color.RED));

		headerContainer.add(spacingContainer);
		headerContainer.add(buttonBar);
		buttonBar.setzDelta(0.5f);
		buttonBar.setVisibility(EVisibility.HIDDEN);
		headerContainer.setVisibility(EVisibility.PICKABLE);

		if (!column.isRoot()) {
			collapseButton = new GLButton(EButtonMode.CHECKBOX);
			collapseButton.setSelected(false);
			collapseButton.setRenderer(GLRenderers.fillImage("resources/icons/general/minus.png"));
			collapseButton.setSelectedRenderer(GLRenderers.fillImage("resources/icons/general/plus.png"));
			collapseButton.setSize(ColumnTreeRenderStyle.CAPTION_HEIGHT, ColumnTreeRenderStyle.CAPTION_HEIGHT);
			collapseButton.setCallback(this);

			captionContainer.add(collapseButton);
		}
		updateButtonBar();

		this.dragAndDropHeader = new DragAndDropHeader(column);
		headerItem = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(false, 4, GLPadding.ZERO));
		headerItem.setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(headerItem, 4,
				GLPadding.ZERO));
		headerItem.add(ColumnTree.createTextElement(caption, ColumnTreeRenderStyle.CAPTION_HEIGHT));

		IEntityCollection myCollection = column.getColumnModel().getCollection();
		mappingRenderer = new MappingRenderer(myCollection.getAllElementIDs().size());
		mappingRenderer.setBarWidth(ColumnTreeRenderStyle.COLUMN_SUMMARY_BAR_HEIGHT);
		mappingRenderer.setMaximumWidthPercentage(1f);
		updateItemCounts();
		headerItem.add(mappingRenderer);

		captionContainer.add(headerItem);

		updateHeaderExtension();

		// spacingContainer.setRenderer(GLRenderers.drawRect(Color.GREEN));

		// add(headerContainer);
		// add(itemList.asGLElement());
		//
		headerContainer.onPick(new APickingListener() {
			@Override
			protected void doubleClicked(Pick pick) {
				IColumnModel model = ColumnHeader.this.column.getColumnModel();
				History history = ColumnHeader.this.column.getColumnTree().getRelationshipExplorer().getHistory();
				// model.sortBy(model.getCurrentComparator());
				ReverseColumnSortingCommand c = new ReverseColumnSortingCommand(model, model.getCurrentComparator(),
						model.getScoreProvider(), history);
				c.execute();
				history.addHistoryCommand(c);
			}

			@Override
			protected void mouseOver(Pick pick) {
				buttonBar.setVisibility(EVisibility.VISIBLE);
				buttonBar.repaintAll();

				context.getMouseLayer().addDragSource(dragAndDropHeader);
				context.getMouseLayer().addDropTarget(dragAndDropHeader);
			}

			@Override
			protected void mouseOut(Pick pick) {
				buttonBar.setVisibility(EVisibility.HIDDEN);

				context.getMouseLayer().removeDragSource(dragAndDropHeader);
				context.getMouseLayer().removeDropTarget(dragAndDropHeader);
			}

			@Override
			protected void rightClicked(Pick pick) {
				column.getColumnTree().getRelationshipExplorer()
						.addContextMenuItems(column.getColumnModel().getHeaderContextMenuItems());

			}
		});

		setRenderer(GLRenderers.drawRect(Color.GRAY));
		scrollbarSpacing = new GLElement();
		scrollbarSpacing.setSize(ColumnTreeRenderStyle.SCROLLBAR_WIDTH - ColumnTreeRenderStyle.HORIZONTAL_SPACING,
				Float.NaN);
		// add(scrollbarSpacing);
	}

	public void updateButtonBar() {
		buttonBar.clear();
		if (!column.isRoot()) {
			GLElement collapseSpacing = new GLElement();
			collapseSpacing.setSize(ColumnTreeRenderStyle.CAPTION_HEIGHT, ColumnTreeRenderStyle.CAPTION_HEIGHT);
			buttonBar.add(collapseSpacing);
		}
		for (GLElement element : column.getColumnModel().getHeaderOverlayElements()) {
			buttonBar.add(element);
		}
	}

	public void updateHeaderExtension() {

		if (headerExtension != null) {
			headerItem.remove(headerExtension);
		}

		headerExtension = column.getColumnModel().getHeaderExtension();

		captionContainer.setSize(Float.NaN, ColumnTreeRenderStyle.CAPTION_HEIGHT
				+ ColumnTreeRenderStyle.COLUMN_SUMMARY_BAR_HEIGHT + 4
				+ (headerExtension != null ? (headerExtension.getMinSize().y() + 4) : 0));

		if (headerExtension != null) {
			headerItem.add(headerExtension);
		}
	}

	public void addChild(ColumnHeader header) {
		if (size() == 0) {
			add(header);
		} else {
			GLElement lastElement = get(size() - 1);
			if (lastElement == scrollbarSpacing)
				add(size() - 1, header);
			else
				add(header);
		}

	}

	public void updateItemCounts() {
		IEntityCollection myCollection = column.getColumnModel().getCollection();
		mappingRenderer.setSelectedValue(myCollection.getSelectedElementIDs().size());
		mappingRenderer.setFilteredValue(myCollection.getFilteredElementIDs().size());
		mappingRenderer.setAllValue(myCollection.getAllElementIDs().size());
	}

	public void updateSize() {
		float width = column.columnWidth;
		if (column.isRoot() && column.getColumnTree().needsScrolling()) {
			width += ColumnTreeRenderStyle.SCROLLBAR_WIDTH;
			add(scrollbarSpacing);
		} else {
			remove(scrollbarSpacing);
		}
		width += column.calcNestingWidth();
		AnimationUtil.resizeElement(this, width, Float.NaN);
	}

	@Override
	public void onSelectionChanged(GLButton button, boolean selected) {
		column.collapseAll(selected);
	}

	@Override
	protected void takeDown() {
		context.getMouseLayer().removeDragSource(dragAndDropHeader);
		context.getMouseLayer().removeDropTarget(dragAndDropHeader);
		super.takeDown();
	}

}
