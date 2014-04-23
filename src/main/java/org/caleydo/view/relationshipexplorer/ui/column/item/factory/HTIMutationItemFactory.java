/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.item.factory;

import gleem.linalg.Vec2f;
import gleem.linalg.open.Vec2i;

import java.net.URL;
import java.util.ArrayList;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.color.ColorBrewer;
import org.caleydo.core.util.system.BrowserUtils;
import org.caleydo.core.view.contextmenu.ActionBasedContextMenuItem;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElement.EVisibility;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactories;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactories.GLElementSupplier;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext.Builder;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.core.view.opengl.picking.APickingListener;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.column.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.util.SimpleBarRenderer;

import com.google.common.base.Function;
import com.google.common.base.Predicate;

/**
 * @author Christian
 *
 */
public class HTIMutationItemFactory implements IItemFactory {

	protected enum EColumn {
		TYPE("type"),
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
		COSMIC_NUM("num of cosmic entries with this NT change"),
		DBSNP("dbSNP137");

		protected final String columnCaption;

		private EColumn(String columnCaption) {
			this.columnCaption = columnCaption;
		}
	}

	protected static final URL CHECK_ICON = AEntityColumn.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/tick.png");
	protected static final URL CROSS_ICON = AEntityColumn.class
			.getResource("/org/caleydo/view/relationshipexplorer/icons/abort.png");

	protected final TabularDataCollection collection;
	protected final ConTourElement contour;

	protected Map<EColumn, Integer> columnToIndex = new HashMap<>();
	protected Function<Integer, Vec2i> idToPosition;

	public HTIMutationItemFactory(TabularDataCollection collection, ConTourElement contour) {
		this.collection = collection;
		this.contour = contour;

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

		GLElementContainer container = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 3, new GLPadding(
				0, 0, 4, 0)));
		container.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(container, 3,
				new GLPadding(0, 0, 4, 0)));
		ATableBasedDataDomain dataDomain = collection.getDataDomain();

		IDType recordIDType = dataDomain.getOppositeIDType(collection.getDimensionPerspective().getIdType());

		container.add(createCategoryElement(dataDomain, recordIDType, (int) elementID, EColumn.TYPE));
		container.add(createTextElement(dataDomain, recordIDType, (int) elementID, EColumn.CHROMOSOME, 35));

		GLElement positionElement = createPositionElement(elementID, dataDomain, recordIDType);
		if (positionElement != null) {
			container.add(positionElement);
		}
		container.add(createCategoryElement(dataDomain, recordIDType, (int) elementID, EColumn.CLASS));
		container.add(createCategoryElement(dataDomain, recordIDType, (int) elementID, EColumn.IMPACT));

		container.add(createTextElement(dataDomain, recordIDType, (int) elementID, EColumn.AMINO_ACID, 50));

		container.add(createNumericalElement(dataDomain, recordIDType, (int) elementID, EColumn.AVSIFT));

		container.add(createNumericalElement(dataDomain, recordIDType, (int) elementID, EColumn.CONSERVED));
		container.add(createNumericalElement(dataDomain, recordIDType, (int) elementID, EColumn.IN_THOUSAND));
		container.add(createNumericalElement(dataDomain, recordIDType, (int) elementID, EColumn.COSMIC_NUM));
		container.add(createDBSNPElement(container, elementID, dataDomain, recordIDType));

		return container;
	}

	protected GLElement createTextElement(ATableBasedDataDomain dataDomain, IDType recordIDType, int elementID,
			EColumn column, float minWidth) {
		String text = (String) dataDomain.getRaw(recordIDType, elementID, HTIMutationItemFactory.this.collection
				.getDimensionPerspective().getIdType(), columnToIndex.get(column));
		if (text == null)
			text = "";

		PickableGLElement textElement = TextItemFactory.createTextElement(text);
		textElement.setTooltip(column.columnCaption + ": " + text);
		textElement.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(minWidth, 16));
		return textElement;
	}

	protected GLElement createCategoryElement(ATableBasedDataDomain dataDomain, IDType recordIDType, int elementID,
			EColumn column) {
		Color color = new Color(dataDomain.getColor(recordIDType, elementID, HTIMutationItemFactory.this.collection
				.getDimensionPerspective().getIdType(), columnToIndex.get(column)));
		Object text = dataDomain.getRaw(recordIDType, elementID, HTIMutationItemFactory.this.collection
				.getDimensionPerspective().getIdType(), columnToIndex.get(column));

		PickableGLElement colorElement = new PickableGLElement();
		colorElement.setRenderer(GLRenderers.fillRect(color));
		colorElement.setTooltip(column.columnCaption + ": " + text);
		colorElement.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(16, 16));
		colorElement.setSize(16, 16);
		return colorElement;
	}

	protected GLElement createNumericalElement(ATableBasedDataDomain dataDomain, IDType recordIDType, int elementID,
			EColumn column) {
		float normalizedValue = dataDomain
				.getNormalizedValue(recordIDType, elementID, HTIMutationItemFactory.this.collection
						.getDimensionPerspective().getIdType(), columnToIndex.get(column));
		Number value = (Number) dataDomain.getRaw(recordIDType, elementID, HTIMutationItemFactory.this.collection
				.getDimensionPerspective().getIdType(), columnToIndex.get(column));

		SimpleBarRenderer barRenderer = new SimpleBarRenderer(normalizedValue, true);
		barRenderer.setValue(value.floatValue());
		barRenderer.setTooltip(column.columnCaption + ": " + value);
		barRenderer.setMinSize(new Vec2f(40, 16));

		if (column == EColumn.AVSIFT) {
			if (value.floatValue() < 0.05f)
				barRenderer.setColor(ColorBrewer.RdBu.get(3, 0));
			else
				barRenderer.setColor(ColorBrewer.RdBu.get(3, 2));
		} else {
			barRenderer.setColor(dataDomain.getColor());
		}

		if (!Float.isNaN(normalizedValue))
			barRenderer.setRenderer(GLRenderers.fillRect(new Color(0.9f, 0.9f, 0.9f, 0.5f)));
		return barRenderer;
	}

	protected GLElement createDBSNPElement(GLElementContainer container, Object elementID,
			ATableBasedDataDomain dataDomain, IDType recordIDType) {
		final String dbsnpID = (String) dataDomain.getRaw(recordIDType, (int) elementID,
				HTIMutationItemFactory.this.collection.getDimensionPerspective().getIdType(),
				columnToIndex.get(EColumn.DBSNP));

		PickableGLElement element = new PickableGLElement();
		if (dbsnpID != null && !dbsnpID.equals("")) {
			container.setVisibility(EVisibility.PICKABLE);
			container.onPick(new APickingListener() {
				@Override
				protected void rightClicked(Pick pick) {
					contour.addContextMenuItem(new ActionBasedContextMenuItem("Show in DBSNP", new Runnable() {

						@Override
						public void run() {
							BrowserUtils.openURL("http://www.ncbi.nlm.nih.gov/projects/SNP/snp_ref.cgi?rs="
									+ dbsnpID.substring(2));
						}
					}));
				}
			});
			element.setRenderer(GLRenderers.fillImage(CHECK_ICON));

		} else {
			element.setRenderer(GLRenderers.fillImage(CROSS_ICON));
		}

		element.setTooltip(EColumn.DBSNP.columnCaption + ": " + (dbsnpID != null ? dbsnpID : "None"));
		element.setSize(16, 16);
		element.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(16, 16));

		return element;
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

		builder.put(List.class, ids).put("chromosome", chromosome).put("id2position", idToPosition)
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

	@Override
	public GLElement createHeaderExtension() {
		GLElementContainer container = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 1, new GLPadding(
				0, 0, 4, 0)));
		container.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(container, 1,
				new GLPadding(0, 0, 4, 0)));

		container.add(createHeaderElement(EColumn.TYPE, 16, true));
		container.add(createHeaderSeparatorElement());
		container.add(createHeaderElement(EColumn.CHROMOSOME, 35, false));
		container.add(createHeaderSeparatorElement());

		container.add(createHeaderElement(EColumn.POSITION, 100, false));
		container.add(createHeaderSeparatorElement());

		container.add(createHeaderElement(EColumn.CLASS, 16, true));
		container.add(createHeaderSeparatorElement());
		container.add(createHeaderElement(EColumn.IMPACT, 16, true));
		container.add(createHeaderSeparatorElement());

		container.add(createHeaderElement(EColumn.AMINO_ACID, 50, false));
		container.add(createHeaderSeparatorElement());

		container.add(createHeaderElement(EColumn.AVSIFT, 40, false));
		container.add(createHeaderSeparatorElement());

		container.add(createHeaderElement(EColumn.CONSERVED, 40, false));
		container.add(createHeaderSeparatorElement());
		container.add(createHeaderElement(EColumn.IN_THOUSAND, 40, false));
		container.add(createHeaderSeparatorElement());
		container.add(createHeaderElement(EColumn.COSMIC_NUM, 40, false));
		container.add(createHeaderSeparatorElement());
		container.add(createHeaderElement(EColumn.DBSNP, 16, true));

		return container;
	}

	protected GLElement createHeaderElement(EColumn column, float minWidth, boolean isfixedWidth) {
		GLElement header = TextItemFactory.createTextElement(column.columnCaption);
		header.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(minWidth, 16));
		if (isfixedWidth) {
			header.setSize(minWidth, Float.NaN);
		}
		return header;
	}

	protected GLElement createHeaderSeparatorElement() {
		GLElement separator = new GLElement(GLRenderers.fillRect(Color.LIGHT_GRAY));
		separator.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(1, Float.NaN));
		separator.setSize(1, Float.NaN);
		return separator;
	}

}
