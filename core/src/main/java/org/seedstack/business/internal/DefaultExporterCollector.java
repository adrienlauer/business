/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.business.internal;

import com.google.inject.Key;
import com.google.inject.TypeLiteral;
import com.google.inject.util.Types;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.internal.utils.BusinessUtils;
import org.seedstack.seed.Application;
import org.seedstack.seed.DataExporter;
import org.seedstack.seed.DataImporter;
import org.seedstack.seed.core.internal.guice.BindingStrategy;
import org.seedstack.seed.core.internal.guice.GenericBindingStrategy;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;


class DefaultExporterCollector {
    private static final String DEFAULT_EXPORTER_KEY = "defaultExporter";
    private static final String DEFAULT_IMPORTER_KEY = "defaultImporter";
    private final Collection<Class<? extends AggregateRoot>> aggregateClasses;
    private final Application application;
    private final Collection<Class<? extends DataExporter>> defaultExporterImplementations;
    private final Collection<Class<? extends DataImporter>> defaultImporterImplementations;

    DefaultExporterCollector(Collection<Class<? extends AggregateRoot>> aggregateClasses, Collection<Class<? extends DataExporter>> defaultExporterImplementations, Collection<Class<? extends DataImporter>> defaultImporterImplementations, Application application) {
        this.aggregateClasses = aggregateClasses;
        this.defaultExporterImplementations = defaultExporterImplementations;
        this.defaultImporterImplementations = defaultImporterImplementations;
        this.application = application;
    }

    /**
     * Prepares the binding strategies which bind default importers and exporters.
     *
     * @return a binding strategy
     */
    Collection<BindingStrategy> collect() {
        Collection<BindingStrategy> bindingStrategies = new ArrayList<>();

        // Extract the type variables which will be passed to the constructor
        Map<Type[], Key<?>> exporterGenerics = new HashMap<>();
        Map<Type[], Key<?>> importerGenerics = new HashMap<>();
        for (Class<?> aggregateClass : BusinessUtils.includeSuperClasses(aggregateClasses)) {
            exporterGenerics.put(
                    new Type[]{aggregateClass},
                    BusinessUtils.defaultQualifier(
                            application,
                            DEFAULT_EXPORTER_KEY,
                            aggregateClass,
                            TypeLiteral.get(Types.newParameterizedType(DataExporter.class, aggregateClass)
                            )
                    )
            );
            importerGenerics.put(
                    new Type[]{aggregateClass},
                    BusinessUtils.defaultQualifier(
                            application,
                            DEFAULT_IMPORTER_KEY,
                            aggregateClass,
                            TypeLiteral.get(Types.newParameterizedType(DataImporter.class, aggregateClass))
                    )
            );
        }

        // Create a binding strategy for each default exporter implementation
        for (Class<? extends DataExporter> defaultExporterImpl : defaultExporterImplementations) {
            bindingStrategies.add(new GenericBindingStrategy<>(DataExporter.class, defaultExporterImpl, exporterGenerics));
        }

        // Create a binding strategy for each default importer implementation
        for (Class<? extends DataImporter> defaultImporterImpl : defaultImporterImplementations) {
            bindingStrategies.add(new GenericBindingStrategy<>(DataImporter.class, defaultImporterImpl, importerGenerics));
        }

        return bindingStrategies;
    }
}
