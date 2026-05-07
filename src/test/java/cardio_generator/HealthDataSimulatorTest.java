package cardio_generator;

import com.cardio_generator.HealthDataSimulator;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertSame;

class HealthDataSimulatorTest {

    @Test
    void healthDataSimulatorGetInstanceReturnsSingletonObject() {
        HealthDataSimulator firstSimulator = HealthDataSimulator.getInstance();
        HealthDataSimulator secondSimulator = HealthDataSimulator.getInstance();

        assertNotNull(firstSimulator);
        assertSame(firstSimulator, secondSimulator);
    }
}
