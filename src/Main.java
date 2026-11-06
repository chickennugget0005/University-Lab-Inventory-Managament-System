package src;

import javax.swing.SwingUtilities;

import src.controller.AppController;
import src.ui.LoginScreen;

/**
 * Application entry point for the Swing client.
 */
public final class Main {
    /** Prevents instantiation of the application entry-point class. */
    private Main() {
    }

    /**
     * Creates the controller and displays the login window on Swing's event thread.
     *
     * @param args command-line arguments; currently unused
     */
    public static void main(String[] args) {
        // Build Swing components on the event-dispatch thread so UI events stay responsive.
        SwingUtilities.invokeLater(() -> {
            AppController controller = new AppController();
            LoginScreen loginScreen = new LoginScreen(controller);
            loginScreen.setVisible(true);
        });
    }
}
