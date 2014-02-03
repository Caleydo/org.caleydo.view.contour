/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 ******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import java.util.ArrayList;
import java.util.List;

import org.caleydo.core.data.perspective.table.TablePerspective;
import org.caleydo.core.id.IDType;
import org.caleydo.core.view.opengl.layout2.GLElementContainer;
import org.caleydo.core.view.opengl.layout2.GLGraphics;
import org.caleydo.core.view.opengl.layout2.layout.GLLayoutDatas;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.layout.GLSizeRestrictiveFlowLayout;
import org.caleydo.view.relationshipexplorer.internal.Activator;

import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

/**
 *
 *
 * @author Christian
 *
 */
public class RelationshipExplorerElement extends GLElementContainer {

	protected List<AEntityColumn> columns = new ArrayList<>();

	public RelationshipExplorerElement() {
		super(new GLSizeRestrictiveFlowLayout(true, 5, GLPadding.ZERO));
	}

	public void addEntityColumn(AEntityColumn column) {
		add(column);
		columns.add(column);
	}

	public Iterable<TablePerspective> getTablePerspecives() {
		return Iterables.filter(Iterables.transform(this, GLLayoutDatas.toLayoutData(TablePerspective.class, null)),
				Predicates.notNull());
	}

	@Override
	protected void renderImpl(GLGraphics g, float w, float h) {
		g.pushResourceLocator(Activator.getResourceLocator());
		super.renderImpl(g, w, h);
		g.popResourceLocator();
	}

	@Override
	protected void renderPickImpl(GLGraphics g, float w, float h) {
		g.pushResourceLocator(Activator.getResourceLocator());
		super.renderPickImpl(g, w, h);
		g.popResourceLocator();
	}

	@Override
	protected void takeDown() {
		columns.clear();
		super.takeDown();
	}

	/**
	 * @return the columns, see {@link #columns}
	 */
	public List<AEntityColumn> getColumns() {
		return columns;
	}

	public List<AEntityColumn> getColumnsWithBroadcastIDType(IDType idType) {
		List<AEntityColumn> list = new ArrayList<>();
		for (AEntityColumn column : columns) {
			if (column.getBroadcastingIDType() == idType) {
				list.add(column);
			}
		}
		return list;
	}

}
