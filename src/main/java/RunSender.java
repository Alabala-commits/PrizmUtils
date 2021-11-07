import javax.swing.*;
import java.io.IOException;
import java.util.logging.*;

class RunSender {

    private static final Logger logger = Logger.getLogger("");

    static {
        logger.setLevel(Level.INFO);
        logger.getHandlers()[0].setLevel(Level.WARNING);
        try {
            FileHandler fh = new FileHandler(UserFileHandler.logFile());
            fh.setFormatter(new SimpleFormatter());
            logger.addHandler(fh);
        } catch (IOException e) {
            logger.warning("IOException when trying to get logFile");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {

        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            logger.warning("UIManager did not setLookAndFeel()");
        }

        StartController ctrl = StartController.getInstance();

        SwingUtilities.invokeLater(StartGUI::getInstance);
        SwingUtilities.invokeLater(ListWindow::getInstance);
        SwingUtilities.invokeLater(SettingWindow::getInstance);

        ctrl.setClient(new ClientPrizmHTTP());
        ctrl.setCalendar(new CalendarHandler(ctrl));

        logger.info("Sender start\n");
    }
}
