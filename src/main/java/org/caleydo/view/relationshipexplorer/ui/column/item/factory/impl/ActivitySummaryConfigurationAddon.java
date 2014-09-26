/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column.item.factory.impl;

import java.net.URL;
import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.id.IDType;
import org.caleydo.core.io.NumericalProperties;
import org.caleydo.core.util.base.ICallback;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.util.function.AdvancedDoubleStatistics;
import org.caleydo.core.util.function.ArrayDoubleList;
import org.caleydo.core.util.function.DoubleFunctions;
import org.caleydo.core.util.function.IDoubleIterator;
import org.caleydo.core.util.function.IDoubleList;
import org.caleydo.core.util.function.IInvertableDoubleFunction;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.PickableGLElement;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout2;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;
import org.caleydo.view.relationshipexplorer.ui.collection.TabularDataCollection;
import org.caleydo.view.relationshipexplorer.ui.column.TabularDataColumn;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.ISummaryItemFactory;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.ISummaryItemFactoryConfigurationAddon;
import org.caleydo.view.relationshipexplorer.ui.column.item.factory.ISummaryItemFactoryCreator;
import org.caleydo.view.relationshipexplorer.ui.list.EUpdateCause;
import org.caleydo.view.relationshipexplorer.ui.list.IColumnModel;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
import org.caleydo.view.relationshipexplorer.ui.util.EntityMappingUtil;

/**
 * @author Christian
 *
 */
public class ActivitySummaryConfigurationAddon implements ISummaryItemFactoryConfigurationAddon {

	public static class ActivitySummaryItemFactoryCreator implements ISummaryItemFactoryCreator {
		protected static final URL BOX_AND_WHISKERS_ICON = ActivitySummaryItemFactory.class
				.getResource("/org/caleydo/view/relationshipexplorer/icons/chart_boxandwhiskers.png");

		public static class ActivitySummaryItemFactory implements ISummaryItemFactory {

			protected final TabularDataColumn column;
			protected final TabularDataCollection collection;
			protected int dimensionID;
			protected NumericalProperties numericalProperties;
			protected IDType recordIDType;

			public ActivitySummaryItemFactory(TabularDataColumn column) {
				this.column = column;
				collection = (TabularDataCollection) column.getCollection();

				recordIDType = collection.getDataDomain().getOppositeIDType(
						collection.getDimensionPerspective().getIdType());
				for (int dimensionID : collection.getDimensionPerspective().getVirtualArray()) {
					Object dataClassDesc = collection.getDataDomain().getTable()
							.getDataClassSpecificDescription(dimensionID);
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
				GLElementContainer container = new GLElementContainer(new GLSizeRestrictiveFlowLayout2(false, 1,
						padding));
				container.setMinSizeProvider(GLMinSizeProviders
						.createVerticalFlowMinSizeProvider(container, 1, padding));

				Set<Object> allElementIDs = EntityMappingUtil.getAllMappedElementIDs(parentItem.getElementData(),
						column.getParentColumn().getColumnModel().getCollection(), collection);
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
				//
				// renderer = new IC50SummaryRenderer(allElementIDs, filteredElementIDs);
				// renderer.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(80, 16));
				// container.add(renderer);

				return container;
				// return renderer;
			}

			protected GLElement getBoxPlot(String label, IDoubleList values, Color color) {

				Float max = numericalProperties.getMax();
				Float min = numericalProperties.getMin();
				BoxPlot p = new BoxPlot(values, min != null ? min : Float.NaN, max != null ? max : Float.NaN, color,
						label);
				p.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(80, 16));
				return p;

				// return new ListBoxAndWhiskersElement(values, EDetailLevel.LOW, EDimension.DIMENSION, false, false,
				// label,
				// color, min != null ? min : Float.NaN, max != null ? max : Float.NaN, GLPadding.ZERO);

				// Builder b = GLElementFactoryContext.builder().put(IDoubleList.class, values).put("label", label)
				// .put("color", color);
				// if (max != null)
				// b.put("max", max);
				// if (min != null)
				// b.put("min", min);
				// GLElementFactoryContext context = b.build();
				//
				// List<GLElementSupplier> suppliers = GLElementFactories.getExtensions(context, "relexplorer",
				// new Predicate<String>() {
				//
				// @Override
				// public boolean apply(String input) {
				// return input.equals("boxandwhiskers");
				// }
				// });
				//
				// if (!suppliers.isEmpty()) {
				// GLElement plot = suppliers.get(0).get();
				// // plot.setSize(80, 16);
				// // plot.setMinSizeProvider(GLMinSizeProviders.createDefaultMinSizeProvider(80, 16));
				// return plot;
				// }
				//
				// return null;
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

			// FIXME: This is in the end a duplicate of the ABoxAndWhiskersPlot, however using this implementation is 5
			// times
			// faster, don't know why yet.
			protected class BoxPlot extends PickableGLElement {

				private static final float BOX_HEIGHT_PERCENTAGE = 1 / 3.f;
				private static final float LINE_TAIL_HEIGHT_PERCENTAGE = 0.75f;

				/**
				 * value which is just above the <code>25 quartile - iqr*1.5</code> margin
				 */
				private double nearestIQRMin;
				/**
				 * value which is just below the <code>75 quartile + iqr*1.5</code> margin
				 */
				private double nearestIQRMax;

				// private IDoubleList outliers;
				// private final boolean showOutlier;
				// private final boolean showMinMax;
				// private boolean showScale = false;

				private AdvancedDoubleStatistics stats;
				protected IInvertableDoubleFunction normalize;
				protected Color color;
				protected String label;

				public BoxPlot(IDoubleList data, double min, double max, Color color, String label) {
					this.color = color;
					this.label = label;
					setData(data, min, max);
				}

				public void setData(IDoubleList list, double min, double max) {
					this.stats = AdvancedDoubleStatistics.of(list);
					updateIQRMatches(list);
					min = Double.isNaN(min) ? stats.getMin() : min;
					max = Double.isNaN(max) ? stats.getMax() : max;
					normalize = DoubleFunctions.normalize(min, max);
					repaint();
				}

				private void updateIQRMatches(IDoubleList l) {
					final double lowerIQRBounds = stats.getQuartile25() - stats.getIQR() * 1.5;
					final double upperIQRBounds = stats.getQuartile75() + stats.getIQR() * 1.5;

					if (l == null) { // invalid raw data
						nearestIQRMin = lowerIQRBounds;
						nearestIQRMax = upperIQRBounds;
						// outliers = null;
						return;
					}

					nearestIQRMin = upperIQRBounds;
					nearestIQRMax = lowerIQRBounds;

					// values which are out of the iqr bounds
					// List<Double> outliers = new ArrayList<>();

					// find the values which are at the within iqr bounds
					for (IDoubleIterator it = l.iterator(); it.hasNext();) {
						double v = it.nextPrimitive();
						if (Double.isNaN(v))
							continue;
						if (v > lowerIQRBounds && v < nearestIQRMin)
							nearestIQRMin = v;
						if (v < upperIQRBounds && v > nearestIQRMax)
							nearestIQRMax = v;
						// optionally compute the outliers
						// if (showOutlier && (v < lowerIQRBounds || v > upperIQRBounds))
						// outliers.add(v);
					}
					// this.outliers = new ArrayDoubleList(Doubles.toArray(outliers));
				}

				@Override
				public String getTooltip() {
					if (stats == null)
						return null;
					StringBuilder b = new StringBuilder();
					b.append(label).append('\n');
					b.append(String.format("%s:\t%d", "count", stats.getN()));
					if (stats.getNaNs() > 0) {
						b.append(String.format("(+%d invalid)\n", stats.getNaNs()));
					} else
						b.append('\n');
					b.append(String.format("%s:\t%.3f\n", "median", stats.getMedian()));
					b.append(String.format("%s:\t%.3f\n", "mean", stats.getMean()));
					b.append(String.format("%s:\t%.3f\n", "median", stats.getMedian()));
					b.append(String.format("%s:\t%.3f\n", "sd", stats.getSd()));
					b.append(String.format("%s:\t%.3f\n", "var", stats.getVar()));
					b.append(String.format("%s:\t%.3f\n", "mad", stats.getMedianAbsoluteDeviation()));
					b.append(String.format("%s:\t%.3f\n", "min", stats.getMin()));
					b.append(String.format("%s:\t%.3f", "max", stats.getMax()));
					return b.toString();
				}

				@Override
				protected void renderImpl(GLGraphics g, float w, float h) {
					final float hi = h * BOX_HEIGHT_PERCENTAGE;
					final float y = (h - hi) * 0.5f;
					final float center = h / 2;

					{
						final float firstQuantrileBoundary = (float) (normalize.apply(stats.getQuartile25())) * w;
						final float thirdQuantrileBoundary = (float) (normalize.apply(stats.getQuartile75())) * w;

						g.color(color).fillRect(firstQuantrileBoundary, y,
								thirdQuantrileBoundary - firstQuantrileBoundary, hi);
						g.color(Color.BLACK).drawRect(firstQuantrileBoundary, y,
								thirdQuantrileBoundary - firstQuantrileBoundary, hi);

						final float iqrMin = (float) Math.min(normalize.apply(nearestIQRMin) * w,
								firstQuantrileBoundary);
						final float iqrMax = (float) Math.max(normalize.apply(nearestIQRMax) * w,
								thirdQuantrileBoundary);

						// Median
						float median = (float) normalize.apply(stats.getMedian()) * w;
						g.color(0.2f, 0.2f, 0.2f).drawLine(median, y, median, y + hi);

						// Whiskers
						g.color(0, 0, 0);
						// line to whiskers
						g.drawLine(iqrMin, center, firstQuantrileBoundary, center);
						g.drawLine(iqrMax, center, thirdQuantrileBoundary, center);

						float h_whiskers = hi * LINE_TAIL_HEIGHT_PERCENTAGE;
						g.drawLine(iqrMin, center - h_whiskers * 0.5f, iqrMin, center + h_whiskers * 0.5f);
						g.drawLine(iqrMax, center - h_whiskers * 0.5f, iqrMax, center + h_whiskers * 0.5f);
					}
					//
					// renderOutliers(g, w, hi, center, normalize);
					//
					// if (showMinMax) {
					// g.gl.glPushAttrib(GL2.GL_POINT_BIT);
					// g.gl.glPointSize(2f);
					// g.color(0f, 0f, 0f, 1f);
					// float min = (float) normalize.apply(stats.getMin()) * w;
					// float max = (float) normalize.apply(stats.getMax()) * w;
					// g.drawPoint(min, center);
					// g.drawPoint(max, center);
					// g.gl.glPopAttrib();
					// }

				}

			}

			protected class IC50SummaryRenderer extends PickableGLElement {

				protected final Set<Object> filteredElementIDs;
				protected final Set<Object> allElementIDs;

				/**
		 *
		 */
				public IC50SummaryRenderer(Set<Object> allElementIDs, Set<Object> filteredElementIDs) {
					this.allElementIDs = allElementIDs;
					this.filteredElementIDs = filteredElementIDs;
					setTooltip("holadrio");
				}

				@Override
				protected void renderImpl(GLGraphics g, float w, float h) {

					g.lineWidth(2);

					for (Object elementID : allElementIDs) {

						float normalizedValue = collection.getDataDomain().getNormalizedValue(recordIDType,
								(int) elementID, collection.getDimensionPerspective().getIdType(), dimensionID);

						g.color(Color.LIGHT_GRAY).drawLine(2 + normalizedValue * (w - 4), 0,
								2 + normalizedValue * (w - 4), h / 2);
					}

					for (Object elementID : filteredElementIDs) {

						float normalizedValue = collection.getDataDomain().getNormalizedValue(recordIDType,
								(int) elementID, collection.getDimensionPerspective().getIdType(), dimensionID);

						g.color(Color.GRAY).drawLine(2 + normalizedValue * (w - 4), h / 2f,
								2 + normalizedValue * (w - 4), h);
					}
					g.lineWidth(1);
					// g.color(Color.WHITE).drawLine(0, h / 2, w, h / 2);
					// g.drawLine(0, 0, w, 0);
					// g.drawLine(0, h, w, h);

				}

			}

			@Override
			public boolean needsUpdate(EUpdateCause cause) {
				return cause != EUpdateCause.SELECTION && cause != EUpdateCause.HIGHLIGHT;
			}

		}

		@Override
		public ISummaryItemFactory create(IEntityCollection collection, IColumnModel column, ConTourElement contour) {
			return new ActivitySummaryItemFactory((TabularDataColumn) column);
		}

		@Override
		public URL getIconURL() {
			return BOX_AND_WHISKERS_ICON;
		}

	}

	@Override
	public String getLabel() {
		return "Activity Box Plot";
	}

	@Override
	public boolean accepts(IEntityCollection collection) {
		if (!(collection instanceof TabularDataCollection))
			return false;

		TabularDataCollection coll = (TabularDataCollection) collection;

		for (int dimensionID : coll.getDimensionPerspective().getVirtualArray()) {
			Object dataClassDesc = coll.getDataDomain().getTable().getDataClassSpecificDescription(dimensionID);
			if (dataClassDesc instanceof NumericalProperties) {
				return true;
			}
		}
		return false;
	}

	@Override
	public void configure(ICallback<ISummaryItemFactoryCreator> callback) {
		callback.on(new ActivitySummaryItemFactoryCreator());

	}

}
