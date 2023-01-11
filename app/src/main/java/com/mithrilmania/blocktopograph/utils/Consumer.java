package com.mithrilmania.blocktopograph.utils;

@FunctionalInterface
public interface Consumer<T> {

    void accept(T param);
}
