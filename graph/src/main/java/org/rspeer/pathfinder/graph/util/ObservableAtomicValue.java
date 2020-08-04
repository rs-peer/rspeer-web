package org.rspeer.pathfinder.graph.util;

import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Consumer;
import java.util.function.Function;

public class ObservableAtomicValue<T> {

    private final AtomicReference<T> reference;
    private final LinkedList<Consumer<T>> listeners = new LinkedList<>();

    public ObservableAtomicValue(T initialValue) {
        this.reference = new AtomicReference<>(initialValue);
    }

    public void addListener(Consumer<T> listener) {
        this.listeners.add(listener);
    }

    public AtomicReference<T> getReference() {
        return reference;
    }

    public void update(Function<T, T> updater) {
        T update = reference.updateAndGet(t -> updater.apply(get()));
        listeners.forEach(e -> e.accept(update));
    }

    public T get() {
        return reference.get();
    }
}
