package com.captechventures.strategy;

import java.util.List;

public interface Selector<T> {

    T select(List<AnnotatedBean> strategyBeans);

}
