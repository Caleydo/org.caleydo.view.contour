/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.item.factory;

import gleem.linalg.Vec2f;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDType;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.util.SimpleBarRenderer;

/**
 * @author Christian
 *
 */
public class HTIVariantCallItemFactory implements IItemFactory {

	protected static final String VARIANT_FREQUENCY = "Var. Freq.";
	protected static final String ALLELIC_DEPTH = "Allelic depths for the ref and alt alleles in the order listed";
	protected static final String GENOTYPE_QUALITY = "Genotype Quality, the Phred-scaled marginal (or unconditional) probability of the called genotypey";

	protected final TabularDataCollection collection;

	public HTIVariantCallItemFactory(TabularDataCollection collection) {
		this.collection = collection;
	}

	@Override
	public GLElement createItem(Object elementID) {
		ATableBasedDataDomain dataDomain = collection.getDataDomain();
		Perspective dimensionPerspective = collection.getDimensionPerspective();

		IDType recordIDType = dataDomain.getOppositeIDType(collection.getDimensionPerspective().getIdType());

		GLElementContainer container = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 2, GLPadding.ZERO));
		container.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(container, 2,
				GLPadding.ZERO));

		for (int dimensionID : dimensionPerspective.getVirtualArray()) {
			Object dataClassDesc = dataDomain.getDataClassSpecificDescription(recordIDType, (Integer) elementID,
					dimensionPerspective.getIdType(), dimensionID);
			if (dataClassDesc instanceof NumericalProperties) {

				// NumericalProperties properties = (NumericalProperties)dataClassDesc;

				Number rawValue = (Number) dataDomain.getRaw(recordIDType, (int) elementID,
						dimensionPerspective.getIdType(), dimensionID);
				float normalizedValue = dataDomain.getNormalizedValue(recordIDType, (int) elementID,
						dimensionPerspective.getIdType(), dimensionID);

				if (dataDomain.getDimensionLabel(dimensionID).equalsIgnoreCase(VARIANT_FREQUENCY)) {
					addBarRenderer(container, VARIANT_FREQUENCY, normalizedValue, rawValue, dataDomain.getColor());
				} else if (dataDomain.getDimensionLabel(dimensionID).equalsIgnoreCase(ALLELIC_DEPTH)) {
					addBarRenderer(container, ALLELIC_DEPTH, normalizedValue, rawValue, dataDomain.getColor());
				} else if (dataDomain.getDimensionLabel(dimensionID).equalsIgnoreCase(GENOTYPE_QUALITY)) {
					addBarRenderer(container, GENOTYPE_QUALITY, normalizedValue, rawValue, dataDomain.getColor());
				}

			}
		}
		return container;
	}

	protected void addBarRenderer(GLElementContainer container, String caption, float normalizedValue, Number rawValue,
			Color color) {
		SimpleBarRenderer renderer = new SimpleBarRenderer(normalizedValue, true);
		renderer.setColor(color);
		renderer.setTooltip(caption + ": " + rawValue);
		container.add(renderer);
		renderer.setMinSize(new Vec2f(50, 16));
	}

	@Override
	public GLElement createHeaderExtension() {
		GLElementContainer container = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 1, GLPadding.ZERO));
		container.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(container, 1,
				GLPadding.ZERO));

		container.add(createHeaderElement(VARIANT_FREQUENCY, 50));
		container.add(createHeaderSeparatorElement());
		container.add(createHeaderElement(ALLELIC_DEPTH, 50));
		container.add(createHeaderSeparatorElement());

		container.add(createHeaderElement(GENOTYPE_QUALITY, 50));

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

}
