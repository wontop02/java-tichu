package tichu.exception;

public class RoundEndSignal extends RuntimeException {
    public RoundEndSignal(String message) {
        super(message);
    }
}