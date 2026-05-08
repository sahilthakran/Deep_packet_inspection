package threading;

import java.util.concurrent.ArrayBlockingQueue;

public class ThreadSafeQueue<T> {

    private ArrayBlockingQueue<T> queue;

    public ThreadSafeQueue(int size) {
        queue = new ArrayBlockingQueue<>(size);
    }

    public void push(T item) {
        try {
            queue.put(item);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public T pop() {
        try {
            return queue.take();
        } catch (InterruptedException e) {
            return null;
        }
    }

    public boolean isEmpty() {
        return queue.isEmpty();
    }
}