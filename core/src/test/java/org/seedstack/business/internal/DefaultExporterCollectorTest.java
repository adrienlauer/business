/**
 * Copyright (c) 2013-2016, The SeedStack authors <http://seedstack.org>
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at http://mozilla.org/MPL/2.0/.
 */
package org.seedstack.business.internal;

import com.google.common.collect.Lists;
import com.google.inject.TypeLiteral;
import com.google.inject.name.Names;
import com.google.inject.util.Types;
import org.junit.Before;
import org.junit.Test;
import org.mockito.internal.util.reflection.Whitebox;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.business.domain.BaseAggregateRoot;
import org.seedstack.business.domain.Repository;
import org.seedstack.business.fixtures.repositories.MyQualifier;
import org.seedstack.business.internal.utils.BusinessUtils;
import org.seedstack.seed.Application;
import org.seedstack.seed.ClassConfiguration;
import org.seedstack.seed.DataExporter;
import org.seedstack.seed.DataImporter;
import org.seedstack.seed.core.internal.guice.BindingStrategy;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


public class DefaultExporterCollectorTest {
    private DefaultExporterCollector underTest;
    private Application application;
    private TypeLiteral<?> genericInterface = TypeLiteral.get(Types.newParameterizedType(Repository.class, new Type[]{MyAgg.class}));

    @Before
    public void before() {
        application = mock(Application.class);
        underTest = new DefaultExporterCollector(
                Lists.newArrayList(MySubAgg1.class, MySubAgg2.class),
                Lists.newArrayList(MyDefaultExporter.class),
                Lists.newArrayList(MyDefaultImporter.class),
                application
        );
    }

    @Test
    public void testCollect() throws Exception {
        Collection<BindingStrategy> bindingStrategies = underTest.collect();
        assertThat(((Map<?, ?>) Whitebox.getInternalState(bindingStrategies.iterator().next(), "constructorParamsMap")).size()).isEqualTo(2);
    }

    @Test
    public void testGetDefaultWithQualifierString() {
        when(application.getConfiguration(MyAgg.class)).thenReturn(ClassConfiguration.of(MyAgg.class, "defaultExporter", "my-qualifier1"));
        when(application.getConfiguration(MyAgg.class)).thenReturn(ClassConfiguration.of(MyAgg.class, "defaultImporter", "my-qualifier2"));
        assertThat(BusinessUtils.defaultQualifier(application, "defaultExporter", DefaultExporterCollectorTest.MyAgg.class, genericInterface).getAnnotation()).isEqualTo(Names.named("my-qualifier1"));
        assertThat(BusinessUtils.defaultQualifier(application, "defaultImporter", DefaultExporterCollectorTest.MyAgg.class, genericInterface).getAnnotation()).isEqualTo(Names.named("my-qualifier2"));
    }

    @Test
    public void testGetDefaultWithQualifierAnnotation() {
        when(application.getConfiguration(MyAgg.class)).thenReturn(ClassConfiguration.of(MyAgg.class, "defaultExporter", "org.seedstack.business.fixtures.repositories.MyQualifier"));
        when(application.getConfiguration(MyAgg.class)).thenReturn(ClassConfiguration.of(MyAgg.class, "defaultImporter", "org.seedstack.business.fixtures.repositories.MyQualifier"));
        assertThat(BusinessUtils.defaultQualifier(application, "defaultExporter", DefaultExporterCollectorTest.MyAgg.class, genericInterface).getAnnotation()).isEqualTo(MyQualifier.class);
        assertThat(BusinessUtils.defaultQualifier(application, "defaultImporter", DefaultExporterCollectorTest.MyAgg.class, genericInterface).getAnnotation()).isEqualTo(MyQualifier.class);
    }

    private static class MyAgg extends BaseAggregateRoot<Long> {
        @Override
        public Long getEntityId() {
            return null;
        }
    }

    private static class MySubAgg1 extends MyAgg {
    }

    private static class MySubAgg2 extends MyAgg {
    }

    class MyDefaultExporter<A extends AggregateRoot> implements DataExporter<A> {
        @Override
        public Iterator<A> exportData() {
            return null;
        }
    }

    class MyDefaultImporter<A extends AggregateRoot> implements DataImporter<A> {
        @Override
        public boolean isInitialized() {
            return false;
        }

        @Override
        public void importData(A a) {

        }

        @Override
        public void commit(boolean b) {

        }

        @Override
        public void rollback() {

        }
    }
}
