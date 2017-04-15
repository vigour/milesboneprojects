package com.milesbone.common.task;

import java.util.concurrent.Callable;


public interface IPoolCallableTask<E,T> extends Callable<E>,IPoolTask<T> {

}
