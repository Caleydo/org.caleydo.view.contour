/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.column;

import gleem.linalg.Vec2f;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.collection.table.NumericalTable;
import org.caleydo.core.data.collection.table.Table;
import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.perspective.variable.Perspective;
import org.caleydo.core.data.virtualarray.group.Group;
import org.caleydo.core.data.virtualarray.group.GroupList;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.MappingType;
import org.caleydo.core.util.color.Color;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.layout.GLLayouts;
import org.caleydo.core.view.opengl.layout2.layout.GLMinSizeProviders;
import org.caleydo.core.view.opengl.layout2.layout.GLPadding;
import org.caleydo.core.view.opengl.layout2.renderer.GLRenderers;
import org.caleydo.view.relationshipexplorer.ui.RelationshipExplorerElement;
import org.caleydo.view.relationshipexplorer.ui.list.NestableColumn;
import org.caleydo.view.relationshipexplorer.ui.list.NestableItem;
import org.caleydo.view.relationshipexplorer.ui.util.KeyBasedGLElementContainer;
import org.caleydo.view.relationshipexplorer.ui.util.SimpleAggregateDataRenderer;

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

	public final Comparator<GLElement> GROUP_COMPARATOR = new Comparator<GLElement>() {

		@Override
		public int compare(GLElement arg0, GLElement arg1) {
			MinSizeTextElement r1 = asMinSizeTextElement(arg0);
			MinSizeTextElement r2 = asMinSizeTextElement(arg1);

			return r2.getLabel().compareTo(r1.getLabel());
		}
	};

	public GroupingColumn(Perspective perspective, RelationshipExplorerElement relationshipExplorer) {
		super(relationshipExplorer);
		this.perspective = perspective;
		this.dataDomain = (ATableBasedDataDomain) perspective.getDataDomain();
		this.groupList = perspective.getVirtualArray().getGroupList();
	}

	@Override
	public String getLabel() {
		return perspective.getLabel();
	}

	@Override
	protected void setContent() {
		Table table = dataDomain.getTable();
		if (table instanceof NumericalTable) {
			NumericalTable numTable = (NumericalTable) table;

			for (final Group group : groupList) {
				List<Integer> indices = perspective.getVirtualArray().getIDsOfGroup(group.getGroupIndex());
				IDType dimensionIDType = dataDomain.getOppositeIDType(perspective.getIdType());
				Perspective dimensionPerspective = dataDomain.getDefaultTablePerspective().getPerspective(
						dimensionIDType);

				KeyBasedGLElementContainer<GLElement> container = new KeyBasedGLElementContainer<>(
						GLLayouts.sizeRestrictiveFlowHorizontal(2));
				MinSizeTextElement textElement = new MinSizeTextElement(group.getLabel());
				textElement.setMinSize(new Vec2f(40, ITEM_HEIGHT));
				textElement.setSize(45, 16);
				container.setElement(GROUP_NAME_KEY, textElement);
				SimpleAggregateDataRenderer renderer = new SimpleAggregateDataRenderer(dataDomain, indices,
						perspective.getIdType(), dimensionPerspective, (float) numTable.getMin(),
						(float) numTable.getMax(), numTable.getDataCenter().floatValue());
				container.setElement(AGGREGATED_DATA_KEY, renderer);
				container.setMinSizeProvider(GLMinSizeProviders.createHorizontalFlowMinSizeProvider(container, 2,
						GLPadding.ONE));

				addElement(container, group);

				// addTextElement(group.getLabel(), group);
			}
		}

	}

	@Override
	public IDType getBroadcastingIDType() {
		return perspective.getIdType();
	}

	@Override
	public Set<Object> getBroadcastingIDsFromElementID(Object elementID) {
		Group g = (Group) elementID;
		Set<Object> bcIDs = new HashSet<>();
		bcIDs.addAll(perspective.getVirtualArray().getIDsOfGroup(g.getGroupIndex()));
		return bcIDs;
	}

	@Override
	public Set<Object> getElementIDsFromBroadcastingID(Integer broadcastingID) {

		List<Group> groups = perspective.getVirtualArray().getGroupOf(broadcastingID);

		Set<Object> elementIDs = new HashSet<>(groups.size());
		for (Group group : groups) {
			elementIDs.add(group);
		}
		return elementIDs;
	}

	@Override
	protected AEntityColumn getNearestMappingColumn(List<MappingType> path) {

		List<AEntityColumn> foreignColumns = relationshipExplorer
				.getColumnsWithBroadcastIDType(getBroadcastingIDType());
		foreignColumns.remove(this);
		for (AEntityColumn column : foreignColumns) {
			if (column instanceof TabularDataColumn) {
				return column;
			}
		}
		AEntityColumn foreignColumn = this;

		if (path != null) {
			for (int i = path.size() - 1; i >= 0; i--) {
				foreignColumn = getForeignColumnWithMappingIDType(path.get(i).getFromIDType());
				if (foreignColumn != null)
					break;
			}
		}
		return foreignColumn;
	}

	@Override
	public IDType getMappingIDType() {
		return getBroadcastingIDType();
	}

	@Override
	@SuppressWarnings("unchecked")
	protected MinSizeTextElement asMinSizeTextElement(GLElement element) {
		KeyBasedGLElementContainer<GLElement> c = (KeyBasedGLElementContainer<GLElement>) ((KeyBasedGLElementContainer<GLElement>) element)
				.getElement(DATA_KEY);
		return (MinSizeTextElement) c.getElement(GROUP_NAME_KEY);
	}

	@Override
	public Comparator<GLElement> getDefaultElementComparator() {

		return GROUP_COMPARATOR;
	}

	@Override
	public void showDetailView() {
		GLElement dummy = new GLElement() {
			@Override
			public Vec2f getMinSize() {
				return new Vec2f(300, 300);
			}
		};
		dummy.setRenderer(GLRenderers.fillRect(Color.BLUE));

		relationshipExplorer.showDetailView(this, dummy, this);

	}

	@Override
	public void fill(NestableColumn column, NestableColumn parentColumn) {
		// TODO Auto-generated method stub

	}

	@Override
	public GLElement getSummaryElement(Set<NestableItem> items) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Set<NestableItem> getItems(Set<Object> elementIDs) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void updateMappings() {
		// TODO Auto-generated method stub

	}

}
