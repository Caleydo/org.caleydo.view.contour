/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui;

import org.caleydo.core.event.AEvent;
import org.caleydo.core.util.base.ILabeled;
import org.caleydo.core.view.opengl.layout2.GLElement;

/**
 * @author Christian
 *
 */
public class ShowDetailViewEvent extends AEvent {

	protected AEntityColumn srcColumn;
	protected GLElement detailView;
	protected ILabeled labeled;

	public ShowDetailViewEvent(AEntityColumn srcColumn, GLElement detailView, ILabeled labeled) {
		this.srcColumn = srcColumn;
		this.detailView = detailView;
	}

	@Override
	public boolean checkIntegrity() {
		return srcColumn != null && detailView != null && labeled != null;
	}

	/**
	 * @return the srcColumn, see {@link #srcColumn}
	 */
	public AEntityColumn getSrcColumn() {
		return srcColumn;
	}

	/**
	 * @param srcColumn
	 *            setter, see {@link srcColumn}
	 */
	public void setSrcColumn(AEntityColumn srcColumn) {
		this.srcColumn = srcColumn;
	}

	/**
	 * @return the detailView, see {@link #detailView}
	 */
	public GLElement getDetailView() {
		return detailView;
	}

	/**
	 * @param detailView
	 *            setter, see {@link detailView}
	 */
	public void setDetailView(GLElement detailView) {
		this.detailView = detailView;
	}

	/**
	 * @return the labeled, see {@link #labeled}
	 */
	public ILabeled getLabeled() {
		return labeled;
	}

	/**
	 * @param labeled
	 *            setter, see {@link labeled}
	 */
	public void setLabeled(ILabeled labeled) {
		this.labeled = labeled;
	}

}
