package org.example;

import java.util.concurrent.ArrayBlockingQueue;

public class Main {
    public static void main(String[] args) {
        ConcurrentQueue queue = new ConcurrentQueue(3);
        ArrayBlockingQueue<Integer> q = new ArrayBlockingQueue<>(3);

        // 1. Создаем поток-Производитель (Producer)
        Thread producer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    System.out.println("[Производитель] generate #" + i);
                    queue.put(i);
                    System.out.println("[Производитель] put #" + i);

                    Thread.sleep(500);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 2. Создаем поток-Потребитель (Consumer)
        Thread consumer = new Thread(() -> {
            try {
                for (int i = 1; i <= 10; i++) {
                    Thread.sleep(2000);

                    System.out.println("[Потребитель] take..");
                    Integer item = queue.poll();
                    System.out.println("[Потребитель] poll #" + item);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        });

        // 3. Запускаем оба потока
        producer.start();
        consumer.start();
    }
}
