import org.example.ConcurrentQueue;
import org.jetbrains.lincheck.Lincheck;
import org.jetbrains.lincheck.datastructures.IntGen;
import org.jetbrains.lincheck.datastructures.ModelCheckingOptions;
import org.jetbrains.lincheck.datastructures.Operation;
import org.jetbrains.lincheck.datastructures.Param;
import org.junit.Test;

// Объявляем генератор параметров для всей структуры
@Param(name = "value", gen = IntGen.class, conf = "1:10")
public class QueueLincheckTest {

    // в тестах используются только неблокирующие методы для обеспечения отсутсвия зависаний,
    // т.к. Lincheck моделирует все возможные комбинации вызовов, т.е. pull может быть
    // вызван раньше чем put и put не вызван вообще что приведет к бесконечному циклу для потока
    private final ConcurrentQueue queue = new ConcurrentQueue(5);

    @Operation
    public void put(@Param(name = "value") int x) throws InterruptedException {
        queue.putNonBlocking(x);
    }

    @Operation
    public Integer poll() throws InterruptedException {
        return queue.pollNonBlocking();
    }

    @Operation
    public Integer peek() {
        return queue.peek();
    }

    @Test
    public void modelCheckingTest() {
        new ModelCheckingOptions()
                .threads(3)
                .iterations(15)
                .checkObstructionFreedom(false)
                .check(QueueLincheckTest.class);
    }
}
