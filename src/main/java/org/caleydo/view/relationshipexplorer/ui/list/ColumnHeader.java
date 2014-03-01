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
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.relationshipexplorer.ui.util.AnimationUtil;

/**
 * @author Christian
 *
 */
public class ColumnHeader extends AnimatedGLElementContainer implements ISelectionCallback {

	protected GLElement headerItem;
	protected NestableColumn column;
	protected GLButton collapseButton;

	public ColumnHeader(NestableColumn column, String caption, AnimatedGLElementContainer headerParent) {
		setLayout(new GLSizeRestrictiveFlowLayout2(true, ColumnTreeRenderStyle.HORIZONTAL_SPACING, new GLPadding(
				ColumnTreeRenderStyle.HORIZONTAL_PADDING, ColumnTreeRenderStyle.HEADER_TOP_PADDING,
				ColumnTreeRenderStyle.HORIZONTAL_PADDING, 0)));
		setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(this,
				ColumnTreeRenderStyle.HORIZONTAL_SPACING, new GLPadding(ColumnTreeRenderStyle.HORIZONTAL_PADDING,
						ColumnTreeRenderStyle.HEADER_TOP_PADDING, ColumnTreeRenderStyle.HORIZONTAL_PADDING, 0)));

		AnimatedGLElementContainer captionContainer = new AnimatedGLElementContainer(new GLSizeRestrictiveFlowLayout2(
				true, ColumnTreeRenderStyle.HORIZONTAL_SPACING, GLPadding.ZERO));
		captionContainer.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(captionContainer,
				ColumnTreeRenderStyle.HORIZONTAL_SPACING, GLPadding.ZERO));
		captionContainer.setSize(Float.NaN, ColumnTreeRenderStyle.CAPTION_HEIGHT);

		GLElementContainer headerContainer = new GLElementContainer(GLLayouts.LAYERS);
		headerContainer.setMinSizeProvider(GLMinSizeProviders.createLayeredMinSizeProvider(headerContainer));
		// headerContainer.setRenderer(GLRenderers.drawRect(Color.GREEN));

		add(headerContainer);

		GLElementContainer spacingContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(false, 0,
				new GLPadding(0, 0, 0, 4)));
		spacingContainer.setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(spacingContainer, 0,
				new GLPadding(0, 0, 0, 4)));

		GLElement spacing = new GLElement();
		spacing.setSize(0, Float.NaN);
		spacingContainer.setVisibility(EVisibility.PICKABLE);
		spacingContainer.add(spacing);
		spacingContainer.add(captionContainer);
		// spacingContainer.setRenderer(GLRenderers.drawRect(Color.BLUE));

		final GLElementContainer buttonBar = new GLElementContainer(GLLayouts.sizeRestrictiveFlowHorizontal(1));
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
			GLElement collapseSpacing = new GLElement();
			collapseSpacing.setSize(ColumnTreeRenderStyle.CAPTION_HEIGHT, ColumnTreeRenderStyle.CAPTION_HEIGHT);
			buttonBar.add(collapseSpacing);
		}
		for (GLElement element : column.getColumnModel().getHeaderOverlayElements()) {
			buttonBar.add(element);
		}

		this.column = column;
		headerItem = ColumnTree.createTextElement(caption, ColumnTreeRenderStyle.CAPTION_HEIGHT);

		captionContainer.add(headerItem);

		// spacingContainer.setRenderer(GLRenderers.drawRect(Color.GREEN));

		// add(headerContainer);
		// add(itemList.asGLElement());
		//
		headerContainer.onPick(new APickingListener() {
			@Override
			protected void doubleClicked(Pick pick) {
				ColumnHeader.this.column.sortBy(ColumnHeader.this.column.getColumnModel().getDefaultComparator());
			}

			@Override
			protected void mouseOver(Pick pick) {
				buttonBar.setVisibility(EVisibility.VISIBLE);
				buttonBar.repaintAll();
			}

			@Override
			protected void mouseOut(Pick pick) {
				buttonBar.setVisibility(EVisibility.HIDDEN);
			}
		});

		setRenderer(GLRenderers.drawRect(Color.GRAY));

		headerParent.add(this);
	}

	public void updateSize() {
		float width = column.columnWidth;
		width += column.calcNestingWidth();
		AnimationUtil.resizeElement(this, width, Float.NaN);
		// if (Float.compare(width, getSize().x()) != 0) {
		// IGLElementParent parent = getParent();
		//
		// if (parent != null && parent instanceof AnimatedGLElementContainer) {
		// ((AnimatedGLElementContainer) parent).resizeChild(this, width, Float.NaN);
		// } else {
		// setSize(width, Float.NaN);
		// }
		// }
	}

	@Override
	public void onSelectionChanged(GLButton button, boolean selected) {
		column.collapseAll(selected);

	}

}
