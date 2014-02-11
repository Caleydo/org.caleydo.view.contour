/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.pathway;

import java.util.HashSet;
import java.util.Set;

import javax.media.opengl.GL;
import javax.media.opengl.GL2;

import org.caleydo.core.data.selection.SelectionType;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.geom.Rect;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.datadomain.genetic.EGeneIDTypes;
import org.caleydo.datadomain.pathway.IPathwayRepresentation;
import org.caleydo.datadomain.pathway.IVertexRepSelectionListener;
import org.caleydo.datadomain.pathway.graph.item.vertex.EPathwayVertexType;
import org.caleydo.datadomain.pathway.graph.item.vertex.PathwayVertexRep;
import org.caleydo.view.pathway.v2.ui.augmentation.APerVertexAugmentation;
import org.caleydo.view.relationshipexplorer.ui.AEntityColumn;
import org.caleydo.view.relationshipexplorer.ui.MultiSelectionUtil;
import org.caleydo.view.relationshipexplorer.ui.MultiSelectionUtil.IMultiSelectionHandler;
import org.caleydo.view.relationshipexplorer.ui.SelectionBasedHighlightOperation;

import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public class MultiVertexHighlightAugmentation extends APerVertexAugmentation implements IVertexRepSelectionListener,
		IMultiSelectionHandler<PathwayVertexRep> {

	protected Set<PathwayVertexRep> selectedVertexReps = new HashSet<>();
	protected AEntityColumn referenceColumn;

	/**
	 * @param pathwayRepresentation
	 */
	public MultiVertexHighlightAugmentation(IPathwayRepresentation pathwayRepresentation, AEntityColumn referenceColumn) {
		super(pathwayRepresentation);
		this.referenceColumn = referenceColumn;
		pathwayRepresentation.addVertexRepSelectionListener(this);
	}

	@Override
	public void onSelect(PathwayVertexRep vertexRep, Pick pick) {
		boolean update = MultiSelectionUtil.handleSelection(pick, vertexRep, this);
		if (update) {
			repaint();
			Set<Object> selectedDavidIDs = new HashSet<>();
			for (PathwayVertexRep v : selectedVertexReps) {
				selectedDavidIDs.addAll(v.getDavidIDs());
			}

			Set<Object> selectionCandidateIDs = referenceColumn.getElementIDsFromForeignIDs(selectedDavidIDs,
					IDType.getIDType(EGeneIDTypes.DAVID.name()));
			Set<Object> filteredElementIDs = referenceColumn.getFilteredElementIDs();

			Set<Object> selectedElementIDs = Sets.intersection(selectionCandidateIDs, filteredElementIDs);
			Set<Object> broadcastIDs = new HashSet<>();
			for (Object id : selectedElementIDs) {
				broadcastIDs.addAll(referenceColumn.getBroadcastingIDsFromElementID(id));
			}

			SelectionBasedHighlightOperation o = new SelectionBasedHighlightOperation(selectedElementIDs, broadcastIDs,
					true);
			o.execute(referenceColumn);
			referenceColumn.getRelationshipExplorer().getHistory().addColumnOperation(referenceColumn, o);
		}
	}

	@Override
	protected void renderVertexAugmentation(GLGraphics g, float w, float h, PathwayVertexRep vertexRep, Rect bounds) {

		if (selectedVertexReps.contains(vertexRep)) {
			renderHighlight(g, bounds, SelectionType.SELECTION.getColor(), vertexRep);
		}
	}

	protected void renderHighlight(GLGraphics g, Rect bounds, Color color, PathwayVertexRep vertexRep) {
		g.gl.glPushAttrib(GL2.GL_LINE_BIT);
		g.gl.glEnable(GL.GL_BLEND);
		g.gl.glBlendFunc(GL.GL_SRC_ALPHA, GL.GL_ONE_MINUS_SRC_ALPHA);
		g.lineWidth(3).color(color);
		if (vertexRep.getType() == EPathwayVertexType.compound) {
			float radius = bounds.width() / 2.0f;
			g.drawCircle(bounds.x() + radius + 0.5f, bounds.y() + radius + 0.5f, radius);
		} else {
			g.drawRect(bounds.x(), bounds.y(), bounds.width() + 1, bounds.height());
		}
		g.gl.glPopAttrib();
	}

	@Override
	public boolean isSelected(PathwayVertexRep object) {
		return selectedVertexReps.contains(object);
	}

	@Override
	public void removeFromSelection(PathwayVertexRep object) {
		selectedVertexReps.remove(object);

	}

	@Override
	public void addToSelection(PathwayVertexRep object) {
		selectedVertexReps.add(object);

	}

	@Override
	public void setSelection(PathwayVertexRep object) {
		selectedVertexReps.clear();
		addToSelection(object);
	}

}
