package com.minechinima.utils;

import java.util.Objects;

public class Pair<A, B> implements ICopy<Pair<A, B>> {
    protected A a;
    protected B b;

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }

    public A getA() {
        return this.a;
    }

    public void setA(A a) {
        this.a = a;
    }

    public B getB() {
        return this.b;
    }

    public void setB(B b) {
        this.b = b;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Pair<?, ?> pair)) return false;
        return Objects.equals(this.a, pair.a) && Objects.equals(b, pair.b);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.a, this.b);
    }

    @Override
    public String toString() {
        return "Pair{" +
                "a=" + this.a +
                ", b=" + this.b +
                '}';
    }

    @Override
    public Pair<A, B> copy() {
        A a = this.a;
        if (a instanceof ICopy<?> aCopy) {
            a = (A) aCopy.copy();
        }

        B b = this.b;
        if (b instanceof ICopy<?> bCopy) {
            b = (B) bCopy.copy();
        }

        return new Pair<>(a, b);
    }
}
