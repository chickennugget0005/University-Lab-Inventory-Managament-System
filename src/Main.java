package src;
import javax.swing.SwingUtilities;
import ui.LabSetupScreen;
import ui.LoginScreen;

public final class Main {
    
    private Main() {}

    public static void main(String[] args) {
        SwingUtilities.invokeLater(
        () -> {
            boolean isConfigured = false;

            if (!isConfigured) {
                new LabSetupScreen().setVisible(true);
            } else {
                new LoginScreen().setVisible(true);
            }
        });
    }
}
