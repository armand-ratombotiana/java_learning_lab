package com.dsacademy.lab20.immutable;

import java.util.Objects;

public final class ImmutablePair<A, B> {
    public final A first;
    public final B second;

    public ImmutablePair(A first, B second) {
        this.first = first;
        this.second = second;
    }

    public static <A, B> ImmutablePair<A, B> of(A a, B b) { return new ImmutablePair<>(a, b); }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof ImmutablePair)) return false;
        ImmutablePair<?, ?> that = (ImmutablePair<?, ?>) o;
        return Objects.equals(first, that.first) && Objects.equals(second, that.second);
    }

    @Override
    public int hashCode() { return Objects.hash(first, second); }

    @Override
    public String toString() { return "(" + first + ", " + second + ")"; }
}
