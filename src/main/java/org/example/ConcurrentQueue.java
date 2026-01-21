package org.example;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class ConcurrentQueue {
    private final int maxSize;
    private int currSize;
    private final int[] elements;
    private int tail;
    private int head;

    // 1. Добавляем ReentrantLock для защиты состояния очереди
    private final ReentrantLock lock = new ReentrantLock();
    // 2. Добавляем два Condition для управления ожиданием
    //    notEmpty: для потоков-потребителей, чтобы ждать, когда очередь станет непустой
    private final Condition notEmpty = lock.newCondition();
    //    notFull: для потоков-производителей, чтобы ждать, когда очередь станет несвободной (есть место)
    private final Condition notFull = lock.newCondition();

    public ConcurrentQueue(int size){
        if (size <= 0) {
            throw new IllegalArgumentException("Размер очереди должен быть положительным.");
        }
        this.maxSize = size;
        this.currSize = 0;
        this.elements = new int[size];
        this.tail = 0;
        this.head = 0;
    }

    // Добавление элемента (Производитель)
    public void put(int el) throws InterruptedException {
        lock.lock(); // 3. Захватываем lock
        try {
            // Если очередь полна, ждем, пока не появится свободное место
            while (isFull()) { // Используем while, чтобы перепроверить условие после пробуждения
                notFull.await(); // Отпускаем lock и засыпаем
            }

            // Теперь у нас есть место и lock захвачен
            this.elements[tail] = el;
            this.tail = (this.tail + 1) % maxSize;
            this.currSize++;

            // Сообщаем потокам, ждущим извлечения, что появился новый элемент
            notEmpty.signal();
        } finally {
            lock.unlock();
        }
    }

    public boolean putNonBlocking(int el) {
        lock.lock();
        try {
            if (isFull()) {
                return false; // Очередь полна, не можем добавить
            }

            this.elements[tail] = el;
            this.tail = (this.tail + 1) % maxSize;
            this.currSize++;

            notEmpty.signal(); // Сообщаем, что стало непусто
            return true;
        } finally {
            lock.unlock();
        }
    }

    // Извлечение элемента (Потребитель)
    public Integer poll() throws InterruptedException {
        lock.lock(); // 3. Захватываем lock
        try {
            // Если очередь пуста, ждем, пока не появится новый элемент
            while (isEmpty()) {
                notEmpty.await(); // Отпускаем lock и засыпаем
            }

            // Теперь в очереди есть элементы и lock захвачен
            int element = this.elements[head];
            this.head = (this.head + 1) % maxSize;
            this.currSize--;

            // Сообщаем потокам, ждущим добавления, что освободилось место
            notFull.signal();
            return element;
        } finally {
            lock.unlock();
        }
    }

    public Integer pollNonBlocking() {
        lock.lock();
        try {
            if (isEmpty()) {
                return null;
            }

            int element = this.elements[head];
            this.head = (this.head + 1) % maxSize;
            this.currSize--;

            notFull.signal();
            return element;
        } finally {
            lock.unlock();
        }
    }

    public Integer peek() {
        lock.lock();
        try {
            if (isEmpty()) {
                return null;
            }
            return this.elements[head];
        } finally {
            lock.unlock();
        }
    }

    private boolean isEmpty(){
        return this.currSize == 0;
    }

    private boolean isFull(){
        return this.currSize == maxSize;
    }

    public int size() {
        lock.lock();
        try {
            return currSize;
        } finally {
            lock.unlock();
        }
    }
}
