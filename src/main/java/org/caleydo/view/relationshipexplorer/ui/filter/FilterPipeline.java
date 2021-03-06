/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.filter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.collection.EDimension;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.layout.Column.VAlign;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.IGLElementContext;
import org.caleydo.core.view.opengl.layout2.animation.AnimatedGLElementContainer;
import org.caleydo.core.view.opengl.layout2.basic.GLButton;
import org.caleydo.core.view.opengl.layout2.basic.GLButton.ISelectionCallback;
import org.caleydo.core.view.opengl.layout2.basic.ScrollBar;
import org.caleydo.core.view.opengl.layout2.basic.ScrollingDecorator;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.History.IHistoryCommand;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;

import com.google.common.collect.Lists;

/**
 * @author Christian
 *
 */
public class FilterPipeline extends AnimatedGLElementContainer {

	protected static final URL REMOVE_ICON = AEntityColumn.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/abort.png");
	protected static final URL REDUCE_FILTER_ICON = AEntityColumn.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/delete.png");
	protected static final URL ADD_FILTER_ICON = AEntityColumn.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/add.png");

	protected final ConTourElement relationshipExplorer;

	protected List<IFilterCommand> filterCommands = new ArrayList<>();
	protected GLElementContainer filterContainer;

	protected class ReapplyFiltersCommand implements IHistoryCommand {

		protected final List<IFilterCommand> commandsToRemove;

		public ReapplyFiltersCommand(List<IFilterCommand> commandsToRemove) {
			this.commandsToRemove = commandsToRemove;
		}

		@Override
		public Object execute() {
			for (IFilterCommand c : commandsToRemove) {
				filterCommands.remove(c);
			}

			relationshipExplorer.restoreAllEntities();
			List<IFilterCommand> commands = new ArrayList<>(filterCommands);
			clearFilterCommands();
			if (commands.isEmpty()) {
				relationshipExplorer.getEnrichmentScores().updateScores();
				for (IEntityCollection collection : relationshipExplorer.getEntityCollections()) {
					collection.filterChanged(null, null, null);
				}
			} else {
				for (IFilterCommand c : commands) {
					c.execute();
					// addFilterCommand(c);
				}
			}
			return null;
		}

		@Override
		public String getDescription() {
			return "Changed filter setup";
		}

	}

	protected class FilterRepresentation extends GLElementContainer {
		protected IFilterCommand filterCommand;

		public FilterRepresentation(IFilterCommand filterCommand) {
			this.filterCommand = filterCommand;
			setSize(Float.NaN, 16);
			setLayout(GLLayouts.LAYERS);
			setMinSizeProvider(GLMinSizeProviders.createLayeredMinSizeProvider(this));
			setVisibility(EVisibility.PICKABLE);

		}

		@Override
		protected void init(IGLElementContext context) {
			super.init(context);

			GLElementContainer contentBar = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 1,
					GLPadding.ZERO));
			contentBar.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(contentBar, 1,
					GLPadding.ZERO));
			add(contentBar);

			GLElement icon = new GLElement(GLRenderers.fillImage(filterCommand.getSetOperation().getIcon()));
			icon.setSize(16, 16);
			icon.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(16, 16));
			contentBar.add(icon);

			GLElement text = new GLElement(GLRenderers.drawText(filterCommand.getSource().getLabel(),
					VAlign.LEFT));
			text.setSize(Float.NaN, 14);
			// IGLElementParent parent = getParent();
			// while (parent.getParent() != null) {
			// parent = parent.getParent();
			// }
			// GLContextLocal local = parent.getLayoutDataAs(GLContextLocal.class, null);
			// float textWidth = local.getText().getTextWidth(filterCommand.getCollection().getLabel(), 16) + 5;
			// text.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(textWidth, 16));
			contentBar.add(text);

			// setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(textWidth + 17, 16));
			setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(Float.NaN, 16));

			final GLElementContainer buttonBar = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 1,
					GLPadding.ZERO));
			buttonBar.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(buttonBar, 1,
					GLPadding.ZERO));
			buttonBar.setVisibility(EVisibility.HIDDEN);
			buttonBar.setzDelta(0.5f);
			add(buttonBar);

			GLButton removeButton = new GLButton();
			removeButton.setVisibility(EVisibility.PICKABLE);
			removeButton.setSize(12, 12);
			removeButton.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(12, 12));
			removeButton.setRenderer(GLRenderers.fillImage(REMOVE_ICON));

			removeButton.setCallback(new ISelectionCallback() {

				@Override
				public void onSelectionChanged(GLButton button, boolean selected) {
					removeFilter(filterCommand);
				}
			});
			GLElement spacing = new GLElement();
			buttonBar.add(spacing);
			buttonBar.add(removeButton);

			onPick(new APickingListener() {
				@Override
				protected void mouseOver(Pick pick) {
					buttonBar.setVisibility(EVisibility.PICKABLE);
				}

				@Override
				protected void mouseOut(Pick pick) {
					buttonBar.setVisibility(EVisibility.HIDDEN);
				}
			});

			this.onPick(context.getSWTLayer().createTooltip(new ILabeled() {
				@Override
				public String getLabel() {
					return filterCommand.getDescription();
				}
			}));
		}
	}

	public FilterPipeline(ConTourElement relationshipExplorer) {
		setLayout(new GLSizeRestrictiveFlowLayout2(false, 4, GLPadding.ZERO));

		filterContainer = new GLElementContainer(new GLSizeRestrictiveFlowLayout(false, 4, new GLPadding(2, 2, 2, 2)));
		filterContainer.setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(filterContainer, 4,
				new GLPadding(2, 2, 2, 2)));

		ScrollingDecorator scrollingDecorator = new ScrollingDecorator(filterContainer, new ScrollBar(true),
				new ScrollBar(false), 8, EDimension.RECORD);
		scrollingDecorator.setMinSizeProvider(filterContainer);

		GLElement caption = createCaption();
		add(caption);
		add(scrollingDecorator);

		this.relationshipExplorer = relationshipExplorer;
	}

	protected GLElement createCaption() {
		GLElementContainer captionContainer = new GLElementContainer(GLLayouts.LAYERS);
		captionContainer.setSize(Float.NaN, 16);
		captionContainer.setMinSizeProvider(GLMinSizeProviders.createLayeredMinSizeProvider(captionContainer));
		GLElement caption = new GLElement(GLRenderers.drawText("Filters", VAlign.CENTER));
		caption.setSize(Float.NaN, 16);
		captionContainer.add(caption);

		final GLElementContainer buttonBar = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 1,
				GLPadding.ZERO));
		buttonBar.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(buttonBar, 1,
				GLPadding.ZERO));
		buttonBar.setVisibility(EVisibility.HIDDEN);
		buttonBar.setzDelta(0.5f);
		captionContainer.add(buttonBar);

		GLButton removeButton = new GLButton();
		removeButton.setVisibility(EVisibility.PICKABLE);
		removeButton.setSize(16, 16);
		removeButton.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(16, 16));
		removeButton.setRenderer(GLRenderers.fillImage(REMOVE_ICON));

		removeButton.setCallback(new ISelectionCallback() {

			@Override
			public void onSelectionChanged(GLButton button, boolean selected) {
				clearFilters();
			}
		});
		removeButton.setTooltip("Remove All Filters");

		GLElement spacing = new GLElement();
		buttonBar.add(spacing);
		buttonBar.add(removeButton);

		captionContainer.setVisibility(EVisibility.PICKABLE);
		captionContainer.onPick(new APickingListener() {
			@Override
			protected void mouseOver(Pick pick) {
				buttonBar.setVisibility(EVisibility.PICKABLE);
			}

			@Override
			protected void mouseOut(Pick pick) {
				buttonBar.setVisibility(EVisibility.HIDDEN);
			}
		});

		return captionContainer;
	}

	public void addFilterCommand(IFilterCommand filterCommand) {
		// if (filterCommand.getSetOperation() == ESetOperation.REPLACE)
		// clearFilterCommands();
		filterCommands.add(filterCommand);
		filterContainer.add(new FilterRepresentation(filterCommand));
	}

	public void clearFilterCommands() {
		filterCommands.clear();
		filterContainer.clear();
	}

	protected void removeFilter(IFilterCommand filterCommand) {
		if (filterCommands.contains(filterCommand)) {
			ReapplyFiltersCommand c = new ReapplyFiltersCommand(Lists.newArrayList(filterCommand));
			c.execute();
			relationshipExplorer.getHistory().addHistoryCommand(c);
		}
	}

	protected void clearFilters() {
		if (filterCommands.isEmpty())
			return;
		ReapplyFiltersCommand c = new ReapplyFiltersCommand(Lists.newArrayList(filterCommands));
		c.execute();
		relationshipExplorer.getHistory().addHistoryCommand(c);
	}

}
