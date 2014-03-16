/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.detail;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.caleydo.core.data.datadomain.ATableBasedDataDomain;
import org.caleydo.core.data.datadomain.DataDomainManager;
import org.caleydo.core.data.datadomain.IDataDomain;
import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.util.collection.Pair;
import org.caleydo.core.util.collection.Pair.ComparablePair;
import org.caleydo.core.view.opengl.layout2.GLElement;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactories;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactories.GLElementSupplier;
import org.caleydo.core.view.opengl.layout2.manage.GLElementFactoryContext;
import org.caleydo.view.relationshipexplorer.ui.collection.IEntityCollection;

import com.google.common.base.Predicate;

/**
 * @author Christian
 *
 */
public class CompoundDetailViewFactory implements IDetailViewFactory {

	@Override
	public GLElement create(IEntityCollection collection, DetailViewWindow window) {
		Set<Object> elementIDs = collection.getSelectedElementIDs();
		if (elementIDs.isEmpty())
			return null;

		Set<Object> bcIDs = collection.getBroadcastingIDsFromElementIDs(elementIDs);

		ATableBasedDataDomain dataDomain = null;

		// Not the most elegant way but it does the job to get the smiles dataset
		for (IDataDomain dd : DataDomainManager.get().getAllDataDomains()) {
			if (dd instanceof ATableBasedDataDomain && dd.getLabel().contains("Smiles")) {
				dataDomain = (ATableBasedDataDomain) dd;
				break;
			}
		}

		// IDType targetIDType = dataDomain.getPrimaryIDType(entityCollection.getBroadcastingIDType());
		IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
				collection.getBroadcastingIDType());
		List<ComparablePair<String, String>> smiles = new ArrayList<>(bcIDs.size());
		IDType dimensionIDType = dataDomain.getDimensionIDType();
		int smilesColumnID = dataDomain.getDefaultTablePerspective().getRecordPerspective().getVirtualArray().get(0);

		for (Object bcID : bcIDs) {
			Set<Integer> smileIDs = mappingManager.getIDAsSet(collection.getBroadcastingIDType(),
					dataDomain.getRecordIDType(), bcID);
			if (smileIDs != null && !smileIDs.isEmpty()) {
				String smileString = (String) dataDomain.getRaw(dataDomain.getRecordIDType(), smileIDs.iterator()
						.next(), dimensionIDType, smilesColumnID);
				smiles.add(Pair.make((String) bcID, smileString));
			}
		}
		Collections.sort(smiles);

		Set<Object> smileIDs = mappingManager.getIDTypeMapper(collection.getBroadcastingIDType(),
				dataDomain.getRecordIDType()).apply(bcIDs);
		if (smileIDs != null && !smileIDs.isEmpty()) {

			GLElementFactoryContext context = GLElementFactoryContext.builder().put("smiles", smiles).build();
			List<GLElementSupplier> suppliers = GLElementFactories.getExtensions(context, "relexplorer",
					new Predicate<String>() {

						@Override
						public boolean apply(String input) {
							return input.equals("smiles");
						}
					});
			if (!suppliers.isEmpty()) {
				GLElement compoundView = suppliers.get(0).get();

				return compoundView;
			}
		}
		return null;
	}

}
