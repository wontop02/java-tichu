package tichu;

import tichu.controller.TichuGameController;
import tichu.service.TichuGameService;

public class Application {
    public static void main(String[] args) {
        TichuGameController tichuGameController = new TichuGameController(new TichuGameService());
        tichuGameController.start();
    }
}
