package src;

import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;

import src.controller.AppController;
import src.ui.LabSetupScreen;
import src.ui.LoginScreen;

//Application entry point for the Swing client.
public final class Main {
    //Prevents instantiation of the application entry-point class.
    private Main() {
    }
    
    //Creates the controller and displays the login window on Swing's event thread.
    public static void main(String[] args) {
        // Build Swing components on the event-dispatch thread so UI events stay responsive.
        SwingUtilities.invokeLater(() -> {
            try {
                AppController controller = new AppController();
                // R1.1 first installation shows the setup; launches after show the login
                if (controller.isSetupRequired()) new LabSetupScreen(controller).setVisible(true);
                else new LoginScreen(controller).setVisible(true);
            } catch (RuntimeException ex) {
                // R1.8 never raplace damaged date with empty lab or bypass authentication
                JOptionPane.showMessageDialog(null, ex.getMessage(), "ULIMS Startup Error", JOptionPane.ERROR_MESSAGE);
            }
            
        });
    }
}
