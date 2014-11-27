/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.relationshipexplorer.internal;

import java.util.List;

import javax.media.opengl.GLAutoDrawable;

import org.caleydo.core.data.datadomain.DataSupportDefinitions;
import org.caleydo.core.data.datadomain.IDataSupportDefinition;
import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.serialize.ASerializedView;
import org.caleydo.core.util.logging.Logger;
import org.caleydo.core.view.opengl.canvas.IGLCanvas;
import org.caleydo.core.view.opengl.layout2.GLElementDecorator;
import org.caleydo.core.view.opengl.layout2.view.AMultiTablePerspectiveElementView;
import org.caleydo.view.relationshipexplorer.internal.serial.SerializedRelationshipExplorerView;
import org.caleydo.view.relationshipexplorer.ui.ConTourElement;

/**
 *
 * @author Christian
 *
 */
public class GLRelationshipExplorerView extends AMultiTablePerspectiveElementView {
	public static final String VIEW_TYPE = "org.caleydo.view.relationshipexplorer";
	public static final String VIEW_NAME = "ConTour";

	private static final Logger log = Logger.create(GLRelationshipExplorerView.class);
	private ConTourElement relationshipExplorer;

	public GLRelationshipExplorerView(IGLCanvas glCanvas) {
		super(glCanvas, VIEW_TYPE, VIEW_NAME);
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		super.init(drawable);
		HTSVideoFactory factory = new HTSVideoFactory();
		// HTIFactory factory = new HTIFactory();
		relationshipExplorer = (ConTourElement) factory.create(null);
		getRootDecorator().setContent(relationshipExplorer);
		// AnimatedGLElementContainer row = new AnimatedGLElementContainer(new GLSizeRestrictiveFlowLayout2(true, 10,
		// GLPadding.ZERO));
		// ColumnTree columnTree = new ColumnTree(new IDColumn(IDType.getIDType(EGeneIDTypes.ENTREZ_GENE_ID.name()),
		// IDCategory.getIDCategory(EGeneIDTypes.GENE.name()).getHumanReadableIDType(), null));
		//
		// NestableColumn rootColumn = columnTree.getRootColumn();
		//
		// // IDColumn activityColumn = new IDColumn(IDType.getIDType("INTERACTION_ID"),
		// // IDType.getIDType("INTERACTION_ID"),
		// // null);
		// // activityColumn.setLabel("Activity");
		// // NestableColumn actiColumn = columnTree.addNestedColumn(activityColumn, rootColumn);
		//
		// IDColumn compoundColumn = new IDColumn(IDType.getIDType("COMPOUND_ID"), IDType.getIDType("COMPOUND_ID"),
		// null);
		// compoundColumn.setLabel("Compounds");
		// NestableColumn compColumn = columnTree.addNestedColumn(compoundColumn, rootColumn);
		//
		// IDColumn fingerprintColumn = new IDColumn(IDType.getIDType("FINGERPRINT_ID"),
		// IDType.getIDType("FINGERPRINT_ID"), null);
		// fingerprintColumn.setLabel("HCS Fingerprints");
		// NestableColumn fingerColumn = columnTree.addNestedColumn(fingerprintColumn, compColumn);
		//
		//
		//
		// row.add(columnTree);
		// // row.add(new ColumnTree());
		// getRootDecorator().setContent(row);
	}

	/**
	 * @return the relationshipExplorer, see {@link #relationshipExplorer}
	 */
	public ConTourElement getRelationshipExplorer() {
		return relationshipExplorer;
	}

	@Override
	public ASerializedView getSerializableRepresentation() {
		return new SerializedRelationshipExplorerView(this);
	}

	@Override
	public IDataSupportDefinition getDataSupportDefinition() {
		return DataSupportDefinitions.tableBased;
	}

	@Override
	protected ConTourElement getContent() {
		return (ConTourElement) super.getContent();
	}

	@Override
	protected void applyTablePerspectives(GLElementDecorator root, List<TablePerspective> all,
			List<TablePerspective> added, List<TablePerspective> removed) {
		// RelationshipExplorerElement content = getContent();
		// for (TablePerspective add : added) {
		//
		// }
		// for (TablePerspective rem : removed) {
		// for (Iterator<TablePerspective> it = content.getTablePerspectives().iterator(); it.hasNext();) {
		// if (rem.equals(it.next())) {
		// it.remove();
		// break;
		// }
		// }
		// }
	}

}
