package main;

import java.awt.EventQueue;
import java.util.Scanner;

import vision.MenuWindow;

/**
 * The main entry point for the Java Chess Engine application.
 * This class is responsible for bootstrapping the application and launching
 * the initial Graphical User Interface (GUI).
 */
public class Main {
	
    /**
     * A globally accessible, static Scanner instance tied to standard input.
     * Can be used for debugging or handling console-based inputs.
     */
    public static final Scanner sc = new Scanner(System.in);

    /**
     * The main method that starts the execution of the program.
     * It uses {@link EventQueue#invokeLater(Runnable)} to ensure that the
     * Swing GUI components are created and updated on the Event Dispatch Thread (EDT),
     * which is the standard safety practice for Java Swing applications.
     * * @param args Command-line arguments passed to the program (currently unused).
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    MenuWindow menu = new MenuWindow();
                    menu.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}