/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.item.factory;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.id.IDType;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.ArrayDoubleList;
import org.caleydo.core.util.function.IDoubleList;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactories;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactories.GLElementSupplier;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext.Builder;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.column.TabularDataColumn;
import org.caleydo.view.relationshipexplorer.ui.list.EUpdateCause;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
import org.caleydo.view.relationshipexplorer.ui.util.EntityMappingUtil;

import com.google.common.base.Predicate;

/**
 * @author Christian
 *
 */
public class ActivitySummaryItemFactory implements ISummaryItemFactory {

	protected final TabularDataColumn column;
	protected final TabularDataCollection collection;
	protected int dimensionID;
	protected NumericalProperties numericalProperties;
	protected IDType recordIDType;

	public ActivitySummaryItemFactory(TabularDataColumn column) {
		this.column = column;
		collection = (TabularDataCollection) column.getCollection();

		recordIDType = collection.getDataDomain().getOppositeIDType(collection.getDimensionPerspective().getIdType());
		for (int dimensionID : collection.getDimensionPerspective().getVirtualArray()) {
			Object dataClassDesc = collection.getDataDomain().getTable().getDataClassSpecificDescription(dimensionID);
			if (dataClassDesc instanceof NumericalProperties) {
				numericalProperties = (NumericalProperties) dataClassDesc;
				this.dimensionID = dimensionID;
				break;
			}
		}
	}

	@Override
	public GLElement createSummaryItem(NestableItem parentItem, Set<NestableItem> items) {
		if (column.getParentColumn() == null)
			return new GLElement(GLRenderers.drawText("Summary of " + items.size()));

		GLPadding padding = new GLPadding(2, 0);
		GLElementContainer container = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(false, 1, padding));
		container.setMinSizeProvider(GLMinSizeProviders.createVerticalFlowMinSizeProvider(container, 1, padding));

		Set<Object> allElementIDs = EntityMappingUtil.getAllMappedElementIDs(parentItem.getElementData(), column
				.getParentColumn().getColumnModel().getCollection(), collection);
		Set<Object> filteredElementIDs = new HashSet<>(items.size());
		for (NestableItem item : items) {
			filteredElementIDs.addAll(item.getElementData());
		}

		container.add(getBoxPlot("All", getValues(allElementIDs), Color.LIGHT_GRAY));
		container.add(getBoxPlot("Filtered", getValues(filteredElementIDs), Color.GRAY));

		// AdvancedDoubleStatistics normalizedStats = AdvancedDoubleStatistics.of(normalizedValues);

		// IC50SummaryRenderer renderer = new IC50SummaryRenderer(allElementIDs, filteredElementIDs);
		// renderer.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(80, 16));
		// container.add(renderer);

		// renderer = new IC50SummaryRenderer(allElementIDs, filteredElementIDs);
		// renderer.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(80, 16));
		// container.add(renderer);

		return container;
		// return renderer;
	}

	protected GLElement getBoxPlot(String label, IDoubleList values, Color color) {

		Float max = numericalProperties.getMax();
		Float min = numericalProperties.getMin();

		// return new ListBoxAndWhiskersElement(values, EDetailLevel.LOW, EDimension.DIMENSION, false, false, label,
		// color, min != null ? min : Float.NaN, max != null ? max : Float.NaN);

		Builder b = GLElementFactoryContext.builder().put(IDoubleList.class, values).put("label", label)
				.put("color", color);
		if (max != null)
			b.put("max", max);
		if (min != null)
			b.put("min", min);
		GLElementFactoryContext context = b.build();

		List<GLElementSupplier> suppliers = GLElementFactories.getExtensions(context, "relexplorer",
				new Predicate<String>() {

					@Override
					public boolean apply(String input) {
						return input.equals("boxandwhiskers");
					}
				});

		if (!suppliers.isEmpty()) {
			GLElement plot = suppliers.get(0).get();
			// plot.setSize(80, 16);
			plot.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(80, 16));
			return plot;
		}

		return null;
	}

	protected IDoubleList getValues(Set<Object> elementIDs) {
		double[] normalizedValues = new double[elementIDs.size()];

		int i = 0;
		for (Object elementID : elementIDs) {
			float normalizedValue = (float) collection.getDataDomain().getRaw(recordIDType, (int) elementID,
					collection.getDimensionPerspective().getIdType(), dimensionID);
			Float max = numericalProperties.getMax();
			if (max != null && normalizedValue > max) {
				normalizedValue = max;
			}
			Float min = numericalProperties.getMin();
			if (min != null && normalizedValue < min) {
				normalizedValue = min;
			}

			normalizedValues[i] = normalizedValue;
			i++;
		}
		return new ArrayDoubleList(normalizedValues);

	}

	protected class IC50SummaryRenderer extends GLElement {

		protected final Set<Object> filteredElementIDs;
		protected final Set<Object> allElementIDs;

		/**
		 *
		 */
		public IC50SummaryRenderer(Set<Object> allElementIDs, Set<Object> filteredElementIDs) {
			this.allElementIDs = allElementIDs;
			this.filteredElementIDs = filteredElementIDs;
		}

		@Override
		protected void renderImpl(GLGraphics g, float w, float h) {

			g.lineWidth(2);

			for (Object elementID : allElementIDs) {

				float normalizedValue = collection.getDataDomain().getNormalizedValue(recordIDType, (int) elementID,
						collection.getDimensionPerspective().getIdType(), dimensionID);

				g.color(Color.LIGHT_GRAY).drawLine(2 + normalizedValue * (w - 4), 0, 2 + normalizedValue * (w - 4),
						h / 2);
			}

			for (Object elementID : filteredElementIDs) {

				float normalizedValue = collection.getDataDomain().getNormalizedValue(recordIDType, (int) elementID,
						collection.getDimensionPerspective().getIdType(), dimensionID);

				g.color(Color.GRAY).drawLine(2 + normalizedValue * (w - 4), h / 2f, 2 + normalizedValue * (w - 4), h);
			}
			g.lineWidth(1);
			// g.color(Color.WHITE).drawLine(0, h / 2, w, h / 2);
			// g.drawLine(0, 0, w, 0);
			// g.drawLine(0, h, w, h);

		}
	}

	@Override
	public boolean needsUpdate(EUpdateCause cause) {
		return true;
	}

}
