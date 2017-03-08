package com.milesbone.queue.task;

import java.util.concurrent.Callable;


public interface IPoolCallableTask<E,T> extends Callable<E>,IPoolTask<T> {

}
