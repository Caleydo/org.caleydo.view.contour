/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import gleem.linalg.Vec2f;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.collection.GroupCollection;

/**
 * @author Christian
 *
 */
public class GroupingColumn extends ATextColumn {

	protected static final int GROUP_NAME_KEY = 0;
	protected static final int AGGREGATED_DATA_KEY = 1;

	protected final ATableBasedDataDomain dataDomain;
	protected final Perspective perspective;
	protected final GroupList groupList;
	protected final GroupCollection groupCollection;

	// public final Comparator<GLElement> GROUP_COMPARATOR = new Comparator<GLElement>() {
	//
	// @Override
	// public int compare(GLElement arg0, GLElement arg1) {
	// MinSizeTextElement r1 = asMinSizeTextElement(arg0);
	// MinSizeTextElement r2 = asMinSizeTextElement(arg1);
	//
	// return r2.getLabel().compareTo(r1.getLabel());
	// }
	// };

	public GroupingColumn(GroupCollection groupCollection, RelationshipExplorerElement relationshipExplorer) {
		super(groupCollection, relationshipExplorer);
		this.groupCollection = groupCollection;
		this.perspective = groupCollection.getPerspective();
		this.dataDomain = (ATableBasedDataDomain) perspective.getDataDomain();
		this.groupList = perspective.getVirtualArray().getGroupList();
		currentComparator = new CompositeComparator<>(ItemComparators.SELECTED_ITEMS_COMPARATOR, getDefaultComparator());
	}

	// @Override
	// protected void setContent() {
		// Table table = dataDomain.getTable();
		// if (table instanceof NumericalTable) {
		// NumericalTable numTable = (NumericalTable) table;
		//
		// for (final Group group : groupList) {
		// List<Integer> indices = perspective.getVirtualArray().getIDsOfGroup(group.getGroupIndex());
		// IDType dimensionIDType = dataDomain.getOppositeIDType(perspective.getIdType());
		// Perspective dimensionPerspective = dataDomain.getDefaultTablePerspective().getPerspective(
		// dimensionIDType);
		//
		// KeyBasedGLElementContainer<GLElement> container = new KeyBasedGLElementContainer<>(
		// GLLayouts.sizeRestrictiveFlowHorizontal(2));
		// MinSizeTextElement textElement = new MinSizeTextElement(group.getLabel());
		// textElement.setMinSize(new Vec2f(40, ITEM_HEIGHT));
		// textElement.setSize(45, 16);
		// container.setElement(GROUP_NAME_KEY, textElement);
		// SimpleAggregateDataRenderer renderer = new SimpleAggregateDataRenderer(dataDomain, indices,
		// perspective.getIdType(), dimensionPerspective, (float) numTable.getMin(),
		// (float) numTable.getMax(), numTable.getDataCenter().floatValue());
		// container.setElement(AGGREGATED_DATA_KEY, renderer);
		// container.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(container, 2,
		// GLPadding.ONE));
		//
		// addElement(container, group);
		//
		// // addTextElement(group.getLabel(), group);
		// }
		// }

	// }

	// @Override
	// public IDType getBroadcastingIDType() {
	// return perspective.getIdType();
	// }
	//
	// @Override
	// public Set<Object> getBroadcastingIDsFromElementID(Object elementID) {
	// Group g = (Group) elementID;
	// Set<Object> bcIDs = new HashSet<>();
	// bcIDs.addAll(perspective.getVirtualArray().getIDsOfGroup(g.getGroupIndex()));
	// return bcIDs;
	// }
	//
	// @Override
	// public Set<Object> getElementIDsFromBroadcastingID(Integer broadcastingID) {
	//
	// List<Group> groups = perspective.getVirtualArray().getGroupOf(broadcastingID);
	//
	// Set<Object> elementIDs = new HashSet<>(groups.size());
	// for (Group group : groups) {
	// elementIDs.add(group);
	// }
	// return elementIDs;
	// }

	// @Override
	// protected AEntityColumn getNearestMappingColumn(List<MappingType> path) {
	//
	// List<AEntityColumn> foreignColumns = relationshipExplorer
	// .getColumnsWithBroadcastIDType(getBroadcastingIDType());
	// foreignColumns.remove(this);
	// for (AEntityColumn column : foreignColumns) {
	// if (column instanceof TabularDataColumn) {
	// return column;
	// }
	// }
	// AEntityColumn foreignColumn = this;
	//
	// if (path != null) {
	// for (int i = path.size() - 1; i >= 0; i--) {
	// foreignColumn = getForeignColumnWithMappingIDType(path.get(i).getFromIDType());
	// if (foreignColumn != null)
	// break;
	// }
	// }
	// return foreignColumn;
	// }

	// @Override
	// public IDType getMappingIDType() {
	// return getBroadcastingIDType();
	// }

	// @Override
	// @SuppressWarnings("unchecked")
	// protected MinSizeTextElement asMinSizeTextElement(GLElement element) {
	// KeyBasedGLElementContainer<GLElement> c = (KeyBasedGLElementContainer<GLElement>)
	// ((KeyBasedGLElementContainer<GLElement>) element)
	// .getElement(DATA_KEY);
	// return (MinSizeTextElement) c.getElement(GROUP_NAME_KEY);
	// }
	//
	// @Override
	// public Comparator<GLElement> getDefaultElementComparator() {
	//
	// return GROUP_COMPARATOR;
	// }

	@Override
	public void showDetailView() {
		GLElement dummy = new GLElement() {
			@Override
			public Vec2f getMinSize() {
				return new Vec2f(300, 300);
			}
		};
		dummy.setRenderer(GLRenderers.fillRect(Color.BLUE));

		relationshipExplorer.showDetailView(groupCollection, dummy, this);

	}

	@Override
	public String getText(Object elementID) {
		return ((Group) elementID).getLabel();
	}


}
