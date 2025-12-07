package app;

import context.AppContext;
import view.GuiLogin;
import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import com.formdev.flatlaf.themes.FlatMacLightLaf;
import model.Admin;
import utils.SecurityUtil;

public class App {
    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(new FlatMacLightLaf());
            UIManager.put("Button.arc", 12);
            UIManager.put("Component.arc", 12);
            UIManager.put("TextComponent.arc", 12);
            UIManager.put("TabbedPane.showTabSeparators", true);
        } catch (Exception ex) { 
            ex.printStackTrace();
        }

        AppContext context = AppContext.getInstance();

        if (context.getUserRepo().findByUsername("admin") == null) {
            System.out.println("Membuat akun admin default...");
            String passHash = SecurityUtil.hashPassword("admin");
            Admin defaultAdmin = new Admin("A001", "admin", passHash, "Administrator", "admin@lms.com");
            context.getUserRepo().addUser(defaultAdmin);
        }

        SwingUtilities.invokeLater(() -> {
            new GuiLogin(context).setVisible(true);
        });
    }
}