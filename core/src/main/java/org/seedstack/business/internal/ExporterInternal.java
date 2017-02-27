package org.seedstack.business.internal;

import com.google.inject.assistedinject.Assisted;
import org.seedstack.business.domain.AggregateRoot;
import org.seedstack.seed.DataExporter;

import javax.inject.Inject;
import java.util.Iterator;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

class ExporterInternal<T extends AggregateRoot> implements DataExporter<T> {
    private final Class<T> aggregateClass;

    @SuppressWarnings("unchecked")
    @Inject
    ExporterInternal(@Assisted Object[] aggregateClass) {
        Object[] clonedClasses = aggregateClass.clone();
        checkNotNull(clonedClasses);
        checkArgument(clonedClasses.length == 1);
        this.aggregateClass = (Class<T>) clonedClasses[0];
    }

    @Override
    public Iterator<T> exportData() {
        return null;
    }
}
