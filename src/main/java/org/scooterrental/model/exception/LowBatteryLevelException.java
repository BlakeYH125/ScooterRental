package org.scooterrental.model.exception;

public class LowBatteryLevelException extends RuntimeException {
    public LowBatteryLevelException() {
        super("Низкий заряд батареи у самоката с этим ID");
    }
}
