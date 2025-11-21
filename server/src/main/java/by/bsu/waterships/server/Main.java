package by.bsu.waterships.server;

import by.bsu.waterships.server.runnables.Server;
import by.bsu.waterships.server.runnables.ServerStopListener;

public class Main {
    public static void main(String[] args) {
        new ServerStopListener().start();
        Server.getInstance().run();
    }
}