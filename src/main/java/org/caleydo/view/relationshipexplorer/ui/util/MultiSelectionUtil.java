/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.util;

import org.caleydo.core.view.opengl.canvas.IGLMouseListener.IMouseEvent;
import org.caleydo.core.view.opengl.picking.Pick;
import org.caleydo.core.view.opengl.picking.PickingMode;

/**
 * @author Christian
 *
 */
public class MultiSelectionUtil {

	public interface IMultiSelectionHandler<T> {
		public boolean isSelected(T object);

		public void removeFromSelection(T object);

		public void addToSelection(T object);

		public void setSelection(T object);

		public boolean isHighlight(T object);

		public void setHighlight(T object);

		public void removeHighlight(T object);
	}

	public static <T> boolean handleSelection(Pick pick, T pickedObject, IMultiSelectionHandler<T> handler) {

		boolean isSelected = handler.isSelected(pickedObject);
		boolean isCtrlDown = ((IMouseEvent) pick).isCtrlDown();

		if (pick.getPickingMode() == PickingMode.CLICKED) {
			if (isCtrlDown) {
				if (isSelected) {
					handler.removeFromSelection(pickedObject);
				} else {
					handler.addToSelection(pickedObject);
				}
				return true;
			} else {
				handler.setSelection(pickedObject);
				return true;
			}
		} else if (pick.getPickingMode() == PickingMode.RIGHT_CLICKED) {
			if (!isSelected) {
				if (isCtrlDown) {
					handler.addToSelection(pickedObject);
				} else {
					handler.setSelection(pickedObject);
				}
				return true;
			}
		}
		return false;
	}

	public static <T> boolean handleHighlight(Pick pick, T pickedObject, IMultiSelectionHandler<T> handler) {

		boolean isHighlight = handler.isHighlight(pickedObject);

		if (pick.getPickingMode() == PickingMode.MOUSE_OVER) {
			if (!isHighlight) {
				handler.setHighlight(pickedObject);
				return true;
			}
		} else if (pick.getPickingMode() == PickingMode.MOUSE_OUT) {
			if (isHighlight) {
				handler.removeHighlight(pickedObject);
				return true;
			}
		}

		return false;
	}
}
