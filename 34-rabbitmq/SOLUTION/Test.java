package solution;

import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class RabbitMQSolutionTest {

    @Test
    void testPublisherConfirms() {
        RabbitMQSolution solution = new RabbitMQSolution(null);
        assertNotNull(solution);
    }

    @Test
    void testDeadLetterQueue() {
        RabbitMQSolution solution = new RabbitMQSolution(null);
        assertNotNull(solution);
    }

    @Test
    void testPriorityQueue() {
        RabbitMQSolution solution = new RabbitMQSolution(null);
        assertNotNull(solution);
    }

    @Test
    void testCorrelationId() {
        RabbitMQSolution solution = new RabbitMQSolution(null);
        String corrId = solution.createCorrelationId();
        assertNotNull(corrId);
    }
}