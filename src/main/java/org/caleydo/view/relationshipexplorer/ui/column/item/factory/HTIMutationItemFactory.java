/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.item.factory;

import gleem.linalg.open.Vec2i;

import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactories;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactories.GLElementSupplier;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext.Builder;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * @author Christian
 *
 */
public class HTIMutationItemFactory implements IItemFactory {

	protected enum EColumn {
		CHROMOSOME("CHROM"),
		POSITION("POS"),
		AMINO_ACID(
				"Old/New amino acid for the highest-impact effect resulting from the current variant (in HGVS style)"),
		CONSERVED("Conserved"),
		IN_THOUSAND("1000g2012apr_ALL"),
		AVSIFT("AVSIFT"),
		CLASS(
				"Functional class of the highest-impact effect resulting from the current variant: [NONE, SILENT, MISSENSE, NONSENSE]"),
		IMPACT("Impact of the highest-impact effect resulting from the current variant [MODIFIER, LOW, MODERATE, HIGH]"),
		COSMIC_NUM("num of cosmic entries with this NT change");

		protected final String columnCaption;

		private EColumn(String columnCaption) {
			this.columnCaption = columnCaption;
		}
	}

	protected final TabularDataCollection collection;

	protected Map<EColumn, Integer> columnToIndex = new HashMap<>();
	protected Function<Integer, Vec2i> idToPosition;

	public HTIMutationItemFactory(TabularDataCollection collection) {
		this.collection = collection;

		final ATableBasedDataDomain dataDomain = collection.getDataDomain();
		Perspective dimensionPerspective = collection.getDimensionPerspective();

		final IDType recordIDType = dataDomain.getOppositeIDType(collection.getDimensionPerspective().getIdType());

		GLElementContainer container = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 2, GLPadding.ZERO));
		container.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(container, 2,
				GLPadding.ZERO));

		for (int dimensionID : dimensionPerspective.getVirtualArray()) {

			for (EColumn column : EnumSet.allOf(EColumn.class)) {
				if (dataDomain.getDimensionLabel(dimensionID).equalsIgnoreCase(column.columnCaption)) {
					columnToIndex.put(column, dimensionID);
					break;
				}
			}
		}

		idToPosition = new Function<Integer, Vec2i>() {

			@Override
			public Vec2i apply(Integer input) {

				int from = (int) dataDomain.getRaw(recordIDType, input, HTIMutationItemFactory.this.collection
						.getDimensionPerspective().getIdType(), columnToIndex.get(EColumn.POSITION));

				Vec2i pos = new Vec2i();
				pos.setXY(from, from + 1);

				return pos;
			}
		};

	}

	@Override
	public GLElement createItem(Object elementID) {

		GLElementContainer container = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 1, GLPadding.ZERO));
		container.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(container, 1,
				GLPadding.ZERO));
		ATableBasedDataDomain dataDomain = collection.getDataDomain();

		IDType recordIDType = dataDomain.getOppositeIDType(collection.getDimensionPerspective().getIdType());
		container.add(createTextElement(dataDomain, recordIDType, (int) elementID, EColumn.CHROMOSOME));

		GLElement positionElement = createPositionElement(elementID, dataDomain, recordIDType);
		if (positionElement != null) {
			container.add(positionElement);
		}
		container.add(createTextElement(dataDomain, recordIDType, (int) elementID, EColumn.AMINO_ACID));

		return container;
	}

	protected GLElement createTextElement(ATableBasedDataDomain dataDomain, IDType recordIDType, int elementID,
			EColumn column) {
		String text = (String) dataDomain.getRaw(recordIDType, elementID, HTIMutationItemFactory.this.collection
				.getDimensionPerspective().getIdType(), columnToIndex.get(column));
		if (text == null)
			text = "";

		PickableGLElement textElement = TextItemFactory.createTextElement(text);
		textElement.setTooltip(column.columnCaption + ": " + text);
		textElement.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(30, 16));
		return textElement;
	}

	protected GLElement createPositionElement(Object elementID, ATableBasedDataDomain dataDomain, IDType recordIDType) {
		String chromosome = (String) dataDomain.getRaw(recordIDType, (int) elementID,
				HTIMutationItemFactory.this.collection.getDimensionPerspective().getIdType(),
				columnToIndex.get(EColumn.CHROMOSOME));
		Builder builder = GLElementFactoryContext.builder();
		Set<Object> bcids = collection.getBroadcastingIDsFromElementID(elementID);
		List<Integer> ids = new ArrayList<>(bcids.size());

		for (Object id : bcids) {
			ids.add((Integer) id);
		}

		builder.put(List.class, ids).put(IDType.class, collection.getBroadcastingIDType())
				.put("chromosome", chromosome).put("id2position", idToPosition)
				.put("tooltip", "Position: " + idToPosition.apply((Integer) elementID).x());

		GLElementFactoryContext context = builder.build();
		List<GLElementSupplier> suppliers = GLElementFactories.getExtensions(context, "relexplorer",
				new Predicate<String>() {

					@Override
					public boolean apply(String input) {
						return input.equals("mutationlocation");
					}
				});

		if (!suppliers.isEmpty()) {
			GLElement sequenceView = suppliers.get(0).get();
			if (sequenceView == null)
				return null;
			sequenceView.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(100, 16));

			return sequenceView;
		}

		return null;
	}

}
