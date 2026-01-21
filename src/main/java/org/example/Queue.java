package org.example;

public class Queue {
    private final int maxSize;
    private int currSize;
    private final int[] elements;
    private int tail;
    private int head;

    public Queue(int size){
        this.maxSize = size;
        this.currSize = 0;
        this.elements = new int[size];
        this.tail = 0;
        this.head = 0;
    }

    public boolean put(int el){
        if (isFull()){
            return false;
        }

        this.elements[tail] = el;
        this.tail = (this.tail + 1) % maxSize;
        this.currSize++;
        return true;
    }

    public Integer poll(){
        if (isEmpty()){
            return null;
        }

        int element = this.elements[head];
        this.head = (this.head + 1) % maxSize;
        this.currSize--;
        return element;
    }

    public Integer peak(){
        if (isEmpty()){
            return null;
        }

        return this.elements[head];
    }

    private boolean isEmpty(){
        return this.currSize == 0;
    }

    private boolean isFull(){
        return this.currSize == maxSize;
    }
}
