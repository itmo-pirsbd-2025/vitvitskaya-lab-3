package benchmark;

import org.example.ConcurrentQueue;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput) // количество операций в секунду
@OutputTimeUnit(TimeUnit.MICROSECONDS)
@State(Scope.Group) // Состояние разделяется между потоками в группе
@Warmup(iterations = 7, time = 1) // Предварительный прогрев
@Measurement(iterations = 50, time = 1) // Основные замеры
@Fork(1)
public class QueueBenchmark {

    private ConcurrentQueue queue;
    private ConcurrentQueue filledQueue;
    private ArrayBlockingQueue<Integer> BAqueue;
    private ArrayBlockingQueue<Integer> filledBAqueue;

    @Param({"100000"}) // Разные размеры очереди для теста
    private int capacity;

    @Setup
    public void setup() {
        queue = new ConcurrentQueue(capacity);
        BAqueue = new ArrayBlockingQueue<>(capacity);
        filledQueue = new ConcurrentQueue(capacity);
        filledBAqueue = new ArrayBlockingQueue<>(capacity);
        for (int i = 0; i < capacity; i++){
            filledQueue.putNonBlocking(i);
            queue.putNonBlocking(i);
        }
    }

    // Группа "ProducerConsumer" моделирует реальную нагрузку
    // Потоки внутри группы работают одновременно
    @Group("prodCons")
    @GroupThreads(2) // 2 потока на запись
    @Benchmark
    public void producer() throws InterruptedException {
        queue.putNonBlocking(42);
    }

    @Group("prodCons")
    @GroupThreads(2) // 2 потока на чтение
    @Benchmark
    public void consumer(Blackhole bh) throws InterruptedException {
        bh.consume(queue.pollNonBlocking());
    }

    // Группа "ProducerConsumer" моделирует реальную нагрузку
    // Потоки внутри группы работают одновременно
    @Group("prodCons")
    @GroupThreads(2) // 2 потока на запись
    @Benchmark
    public void BAQproducer() throws InterruptedException {
        BAqueue.put(42);
    }

    @Group("prodCons")
    @GroupThreads(2) // 2 потока на чтение
    @Benchmark
    public void BAQconsumer(Blackhole bh) throws InterruptedException {
        bh.consume(BAqueue.poll());
    }

    public static void main(String[] args) throws RunnerException {
        Options options = new OptionsBuilder()
                .include(QueueBenchmark.class.getSimpleName())
                .forks(1)
                .build();

        new Runner(options).run();
    }
}