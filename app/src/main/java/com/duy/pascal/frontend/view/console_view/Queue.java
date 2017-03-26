package com.duy.pascal.frontend.view.console_view;

/**
 * Created by Duy on 10-Feb-17.
 */
public class Queue {
    private byte data[];
    private int front;
    private int rear;


    public Queue(int size) {
        data = new byte[size];
        front = 0;
        rear = 0;
    }

    public int getFront() {
        return front;
    }

    public int getRear() {
        return rear;
    }

    public synchronized byte getByte() {
        while (front == rear) {
            try {
                wait();
            } catch (InterruptedException ignored) {
            }
        }
        byte b = data[front];
        front++;
        if (front >= data.length) front = 0;
        return b;
    }

    public synchronized void putByte(byte b) {
        data[rear] = b;
        rear++;
        if (rear >= data.length) rear = 0;
        if (front == rear) {
            front++;
            if (front >= data.length) front = 0;
        }
        notify();
    }

    public synchronized void eraseByte() {
        if (rear != front) {
            rear--;
            if (rear < 0) rear = 0;
        }
        notify();
    }

    public synchronized void flush() {
        rear = front;
        notify();
    }
}