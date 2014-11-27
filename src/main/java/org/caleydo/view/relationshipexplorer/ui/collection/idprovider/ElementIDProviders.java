/*******************************************************************************
 * Caleydo - Visualization for Molecular Biology - http://caleydo.org
 * Copyright (c) The Caleydo Team. All rights reserved.
 * Licensed under the new BSD license, available at http://caleydo.org/license
 *******************************************************************************/
package org.caleydo.view.relationshipexplorer.ui.collection.idprovider;

import java.util.HashSet;
import java.util.Set;

import org.caleydo.core.id.IDMappingManager;
import org.caleydo.core.id.IDMappingManagerRegistry;
import org.caleydo.core.id.IDType;
import org.caleydo.core.id.IIDTypeMapper;

import com.google.common.collect.Sets;

/**
 * @author Christian
 *
 */
public final class ElementIDProviders {

	private ElementIDProviders() {
	}

	/**
	 * Provides a specified set of ids.
	 *
	 * @author Christian
	 *
	 */
	public static class SimpleElementIDProvider implements IElementIDProvider {
		private final Set<Object> elementIDs;

		public SimpleElementIDProvider(Set<Object> elementIDs) {
			this.elementIDs = elementIDs;
		}

		@Override
		public Set<Object> getElementIDs() {
			return elementIDs;
		}

	}

	/**
	 * Provides all ids of a source id type that has a mapping to a target id type.
	 *
	 * @author Christian
	 *
	 */
	public static class MappingBasedIDProvider implements IElementIDProvider {

		private final IDType sourceIDType;
		private final IDType targetIDType;

		public MappingBasedIDProvider(IDType sourceIDType, IDType targetIDType) {
			this.sourceIDType = sourceIDType;
			this.targetIDType = targetIDType;
		}

		@Override
		public Set<Object> getElementIDs() {

			IDMappingManager mappingManager = IDMappingManagerRegistry.get().getIDMappingManager(
					sourceIDType.getIDCategory());

			Set<Object> allSourceIDs = new HashSet<Object>(mappingManager.getAllMappedIDs(sourceIDType));
			IIDTypeMapper<Object, Object> mapper = mappingManager.getIDTypeMapper(sourceIDType, targetIDType);

			return mapper.apply(allSourceIDs);
		}
	}

	/**
	 * Gets a Provider that returns the union of all elements given by the specified providers. Note that this union is
	 * cached once and will not be recalculated when calling {@link IElementIDProvider#getElementIDs()}.
	 *
	 * @param providers
	 * @return
	 */
	public static IElementIDProvider unionOf(IElementIDProvider... providers) {

		Set<Object> elementIDs = new HashSet<>();

		for (IElementIDProvider provider : providers) {
			elementIDs = Sets.union(elementIDs, provider.getElementIDs());
		}

		return new SimpleElementIDProvider(elementIDs);
	}

	/**
	 * Gets a Provider that returns the intersection of all elements given by the specified providers. Note that this
	 * intersection is cached once and will not be recalculated when calling {@link IElementIDProvider#getElementIDs()}.
	 *
	 * @param providers
	 * @return
	 */
	public static IElementIDProvider intersectionOf(IElementIDProvider... providers) {

		Set<Object> elementIDs = new HashSet<>();

		for (int i = 0; i < providers.length; i++) {
			IElementIDProvider provider = providers[i];
			if (i == 0) {
				elementIDs = provider.getElementIDs();
			} else {
				elementIDs = Sets.intersection(elementIDs, provider.getElementIDs());
			}
		}

		return new SimpleElementIDProvider(elementIDs);
	}

	/**
	 * Gets a Provider that returns the difference of all elements given by the specified providers. Note that this
	 * difference is cached once and will not be recalculated when calling {@link IElementIDProvider#getElementIDs()}.
	 *
	 * @param providers
	 * @return
	 */
	public static IElementIDProvider differenceOf(IElementIDProvider... providers) {

		Set<Object> elementIDs = new HashSet<>();

		for (IElementIDProvider provider : providers) {
			elementIDs = Sets.difference(elementIDs, provider.getElementIDs());
		}

		return new SimpleElementIDProvider(elementIDs);

	}

}
