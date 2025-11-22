package tichu.exception;

public class PhaseEndSignal extends RuntimeException {
    public PhaseEndSignal() {
        super();
    }

    public PhaseEndSignal(String message) {
        super(message);
    }
}
