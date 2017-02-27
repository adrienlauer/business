/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.business.internal;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import com.google.inject.Key;
import org.kametic.specifications.Specification;
import org.seedstack.business.Producible;
import org.seedstack.business.domain.Factory;
import org.seedstack.business.domain.DomainObject;
import org.seedstack.business.internal.strategy.FactoryPatternBindingStrategy;
import org.seedstack.seed.core.internal.guice.BindingStrategy;
import org.seedstack.seed.core.internal.guice.GenericBindingStrategy;
import org.seedstack.seed.core.internal.utils.SpecificationBuilder;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;

import static org.seedstack.shed.reflect.ClassPredicates.classIsDescendantOf;


class DefaultFactoryCollector {
    private static final Specification<Class<?>> PRODUCIBLE_SPEC = new SpecificationBuilder<>(classIsDescendantOf(Producible.class).and(classIsDescendantOf(DomainObject.class))).build();
    private final Collection<Class<?>> aggregateOrVOClasses;
    private final Multimap<Type, Class<?>> producibleClasses;

    DefaultFactoryCollector(Collection<Class<?>> aggregateOrVOClasses, Map<Key<?>, Class<?>> bindings) {
        this.aggregateOrVOClasses = aggregateOrVOClasses;
        this.producibleClasses = filterProducibleClasses(bindings);
    }

    Collection<BindingStrategy> collect() {
        Collection<BindingStrategy> strategies = new ArrayList<>();
        BindingStrategy aggregateBindings = buildAggregateDefaultFactoryBindings();
        if (aggregateBindings != null) {
            strategies.add(aggregateBindings);
        }
        strategies.add(buildDefaultFactoryBindings());
        return strategies;
    }

    /**
     * Prepares the binding strategy which binds default factories of aggregate roots and value objects.
     * <p>
     * For instance:
     * </p>
     * <pre>
     * {@literal @}Inject
     * Factory&lt;Customer&gt; customerFactory;
     * </pre>
     *
     * @return a binding strategy
     */
    private BindingStrategy buildAggregateDefaultFactoryBindings() {
        Collection<Type[]> generics = new ArrayList<>();
        if (aggregateOrVOClasses != null && !aggregateOrVOClasses.isEmpty()) {
            for (Class<?> aggregateClass : aggregateOrVOClasses) {
                generics.add(new Type[]{aggregateClass});
            }
        }
        if (!generics.isEmpty()) {
            return new GenericBindingStrategy<>(Factory.class, FactoryInternal.class, generics);
        }
        return null;
    }

    /**
     * Prepares the binding strategy which binds default factories of {@link Producible}
     * objects other than aggregate roots and value objects.
     * <p>
     * It binds for instance default factories of policies.
     * </p>
     * <pre>
     * {@literal @}Inject
     * Factory&lt;Customer&gt; customerFactory;
     * </pre>
     *
     * @return a binding strategy
     */
    private BindingStrategy buildDefaultFactoryBindings() {
        // The guice assisted factory is already bound
        return new FactoryPatternBindingStrategy<>(Factory.class, FactoryInternal.class, producibleClasses, false);
    }

    /**
     * Filter a map containing pairs of interface/implementation in order to get only producible classes.
     *
     * @param bindings map of interface/implementation
     * @return producible pairs
     */
    private Multimap<Type, Class<?>> filterProducibleClasses(Map<Key<?>, Class<?>> bindings) {
        Multimap<Type, Class<?>> defaultFactoryToBind = ArrayListMultimap.create();
        for (Map.Entry<Key<?>, Class<?>> keyClassEntry : bindings.entrySet()) {
            if (PRODUCIBLE_SPEC.isSatisfiedBy(keyClassEntry.getKey().getTypeLiteral().getRawType())) {
                defaultFactoryToBind.put(keyClassEntry.getKey().getTypeLiteral().getType(), keyClassEntry.getValue());
            }
        }
        return defaultFactoryToBind;
    }
}
