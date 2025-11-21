package tichu;

import tichu.controller.TichuGameController;

public class Application {
    public static void main(String[] args) {
        TichuGameController tichuGameController = new TichuGameController();
        tichuGameController.start();
    }
}
