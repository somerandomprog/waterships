package by.bsu.waterships.server.runnables;

import java.util.Scanner;

public class ServerStopListener extends Thread {
    @Override
    public void run() {
        Scanner scanner = new Scanner(System.in);
        while (true) {
            String line = scanner.nextLine();
            if (line.equals("q") || line.equals("quit")) {
                Server.getInstance().stop();
                break;
            }
        }
    }
}
