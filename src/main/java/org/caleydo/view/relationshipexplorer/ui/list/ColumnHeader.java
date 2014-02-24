/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.list;

import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.EButtonMode;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;

/**
 * @author Christian
 *
 */
public class ColumnHeader extends GLElementContainer {

	protected GLElement headerItem;
	protected NestableColumn column;

	public ColumnHeader(NestableColumn column, String caption, GLElementContainer headerParent) {
		setLayout(new GLSizeRestrictiveFlowLayout2(true, ColumnTreeRenderStyle.HORIZONTAL_SPACING, new GLPadding(
				ColumnTreeRenderStyle.HORIZONTAL_PADDING, ColumnTreeRenderStyle.HEADER_TOP_PADDING,
				ColumnTreeRenderStyle.HORIZONTAL_PADDING, 0)));
		setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(this,
				ColumnTreeRenderStyle.HORIZONTAL_SPACING, new GLPadding(ColumnTreeRenderStyle.HORIZONTAL_PADDING,
						ColumnTreeRenderStyle.HEADER_TOP_PADDING, ColumnTreeRenderStyle.HORIZONTAL_PADDING, 0)));

		GLElementContainer captionContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true,
				ColumnTreeRenderStyle.HORIZONTAL_SPACING, GLPadding.ZERO));
		captionContainer.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(captionContainer,
				ColumnTreeRenderStyle.HORIZONTAL_SPACING, GLPadding.ZERO));
		captionContainer.setSize(Float.NaN, ColumnTreeRenderStyle.CAPTION_HEIGHT);

		if (!column.isRoot()) {
			GLButton collapseButton = new GLButton(EButtonMode.CHECKBOX);
			collapseButton.setSelected(false);
			collapseButton.setRenderer(GLRenderers.fillImage("resources/icons/general/minus.png"));
			collapseButton.setSelectedRenderer(GLRenderers.fillImage("resources/icons/general/plus.png"));
			collapseButton.setSize(ColumnTreeRenderStyle.CAPTION_HEIGHT, ColumnTreeRenderStyle.CAPTION_HEIGHT);

			captionContainer.add(collapseButton);
		}

		this.column = column;
		headerItem = ColumnTree.createTextElement(caption, ColumnTreeRenderStyle.CAPTION_HEIGHT);

		captionContainer.add(headerItem);

		GLElementContainer spacingContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(false, 0,
				new GLPadding(0, 0, 0, 4)));
		spacingContainer.setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(spacingContainer, 0,
				new GLPadding(0, 0, 0, 4)));

		GLElement spacing = new GLElement();
		spacing.setSize(0, Float.NaN);
		spacingContainer.add(spacing);
		spacingContainer.add(captionContainer);
		// spacingContainer.setRenderer(GLRenderers.drawRect(Color.GREEN));

		add(spacingContainer);
		setRenderer(GLRenderers.drawRect(Color.GRAY));

		headerParent.add(this);
	}

	public void updateSize() {
		float width = column.columnWidth;
		width += column.calcNestingWidth();
		setSize(width, Float.NaN);
	}

}
