/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.item.factory;

import gleem.linalg.Vec2f;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.DoubleFunctions;
import org.caleydo.core.util.function.IInvertableDoubleFunction;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.list.EUpdateCause;
import org.caleydo.view.relationshipexplorer.ui.util.SimpleBarRenderer;

/**
 * @author Christian
 *
 */
public class HTIVariantCallItemFactory implements IItemFactory {

	protected enum EColumn {
		VARIANT_FREQUENCY("Var. Freq."),
		ALLELIC_DEPTH("Allelic depths for the ref and alt alleles in the order listed"),
		GENOTYPE_QUALITY(
				"Genotype Quality, the Phred-scaled marginal (or unconditional) probability of the called genotypey");

		protected final String columnCaption;

		private EColumn(String columnCaption) {
			this.columnCaption = columnCaption;
		}
	}

	// protected static final String VARIANT_FREQUENCY = "Var. Freq.";
	// protected static final String ALLELIC_DEPTH = "Allelic depths for the ref and alt alleles in the order listed";
	// protected static final String GENOTYPE_QUALITY =
	// "Genotype Quality, the Phred-scaled marginal (or unconditional) probability of the called genotypey";

	protected final TabularDataCollection collection;
	protected Map<EColumn, Integer> columnToIndex = new HashMap<>();
	protected Map<EColumn, IInvertableDoubleFunction> columnToNormalize = new HashMap<>();

	public HTIVariantCallItemFactory(TabularDataCollection collection) {
		this.collection = collection;

		for (int dimensionID : collection.getDimensionPerspective().getVirtualArray()) {

			for (EColumn column : EnumSet.allOf(EColumn.class)) {
				if (collection.getDataDomain().getDimensionLabel(dimensionID).equalsIgnoreCase(column.columnCaption)) {
					columnToIndex.put(column, dimensionID);
					break;
				}
			}
		}
		update();
	}

	@Override
	public GLElement createItem(Object elementID) {
		ATableBasedDataDomain dataDomain = collection.getDataDomain();
		Perspective dimensionPerspective = collection.getDimensionPerspective();

		IDType recordIDType = dataDomain.getOppositeIDType(collection.getDimensionPerspective().getIdType());

		GLElementContainer container = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 2, GLPadding.ZERO));
		container.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(container, 2,
				GLPadding.ZERO));

		addBarRenderer(container, EColumn.VARIANT_FREQUENCY, elementID, dataDomain, recordIDType, collection
				.getDimensionPerspective().getIdType());
		addBarRenderer(container, EColumn.ALLELIC_DEPTH, elementID, dataDomain, recordIDType, collection
				.getDimensionPerspective().getIdType());
		addBarRenderer(container, EColumn.GENOTYPE_QUALITY, elementID, dataDomain, recordIDType, collection
				.getDimensionPerspective().getIdType());

		return container;
	}

	protected void addBarRenderer(GLElementContainer container, EColumn column, Object elementID,
			ATableBasedDataDomain dataDomain, IDType recordIDType, IDType dimensionIDType) {
		Number rawValue = (Number) dataDomain.getRaw(recordIDType, (int) elementID, dimensionIDType,
				columnToIndex.get(column));

		float normalizedValue = (float) columnToNormalize.get(column).apply(rawValue.floatValue());
		// float normalizedValue = dataDomain.getNormalizedValue(recordIDType, (int) elementID, dimensionIDType,
		// columnToIndex.get(column));

		SimpleBarRenderer renderer = new SimpleBarRenderer(normalizedValue, true);
		renderer.setColor(dataDomain.getColor());
		renderer.setTooltip(column.columnCaption + ": " + rawValue);
		container.add(renderer);
		renderer.setMinSize(new Vec2f(50, 16));
	}

	@Override
	public GLElement createHeaderExtension() {
		GLElementContainer container = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 1, GLPadding.ZERO));
		container.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(container, 1,
				GLPadding.ZERO));

		container.add(createHeaderElement(EColumn.VARIANT_FREQUENCY.columnCaption, 50));
		container.add(createHeaderSeparatorElement());
		container.add(createHeaderElement(EColumn.ALLELIC_DEPTH.columnCaption, 50));
		container.add(createHeaderSeparatorElement());

		container.add(createHeaderElement(EColumn.GENOTYPE_QUALITY.columnCaption, 50));

		return container;
	}

	protected GLElement createHeaderElement(String column, float minWidth) {
		GLElement header = TextItemFactory.createTextElement(column);
		header.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(minWidth, 16));
		return header;
	}

	protected GLElement createHeaderSeparatorElement() {
		GLElement separator = new GLElement(GLRenderers.fillRect(Color.LIGHT_GRAY));
		separator.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(1, Float.NaN));
		separator.setSize(1, Float.NaN);
		return separator;
	}

	@Override
	public boolean needsUpdate(EUpdateCause cause) {
		if (cause == EUpdateCause.FILTER)
			return true;
		return false;
	}

	@Override
	public void update() {
		ATableBasedDataDomain dataDomain = collection.getDataDomain();
		IDType dimensionIDType = collection.getDimensionPerspective().getIdType();
		IDType recordIDType = dataDomain.getOppositeIDType(dimensionIDType);
		for (EColumn column : EnumSet.allOf(EColumn.class)) {
			float min = Float.MAX_VALUE;
			float max = Float.MIN_VALUE;
			for (Object elementID : collection.getFilteredElementIDs()) {

				Number rawValue = (Number) dataDomain.getRaw(recordIDType, (int) elementID, dimensionIDType,
						columnToIndex.get(column));

				if (min > rawValue.floatValue())
					min = rawValue.floatValue();
				if (max < rawValue.floatValue())
					max = rawValue.floatValue();

			}
			columnToNormalize.put(column, DoubleFunctions.normalize(min, max));
		}

	}
}
