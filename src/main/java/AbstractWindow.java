import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public abstract class AbstractWindow extends JFrame {

    private final StartController controller;

//=========================================================

    protected AbstractWindow(final StartController controller) {
        this.controller = controller;
        addCloseListener();
        setSizeFrame();
        setLocationRelativeTo(null);
    }

//=========================================================

    public StartController getController() {
        return controller;
    }

    protected void setSizeFrame() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getHeight() / 1.6), (int) (screenSize.getWidth() / 2));
    }

    protected JPanel newPanel() {
        JPanel panel = new JPanel();
        panel.setBackground(new Color(118, 0, 169));
        return panel;
    }

    protected JPanel newPanel(LayoutManager mgr) {
        JPanel panel = newPanel();
        panel.setLayout(mgr);
        return panel;
    }

    protected JPanel prepareBorderLayoutPanel(JPanel panel) {
        panel.setLayout(new BorderLayout());
        return panel;
    }

    protected JButton prepareButton(JButton button) {
        button.setFont(new Font("GothamPro-Bold", Font.BOLD, 14));
        button.setBackground(Color.WHITE);
        return button;
    }

    protected JLabel prepareLabel(JLabel label) {
        return prepareLabel(label, 18);
    }

    protected JLabel prepareLabel(JLabel label, int textSize) {
        label.setHorizontalAlignment(SwingConstants.CENTER);
        label.setFont(new Font("GothamPro-Bold", Font.BOLD, textSize));
        label.setForeground(Color.WHITE);
        return label;
    }

    protected JLabel prepareLabelLowerHalf(JLabel label, int alignment) {
        label.setFont(new Font("GothamPro-Bold", Font.BOLD, 14));
        label.setForeground(Color.WHITE);
        label.setHorizontalAlignment(alignment);
        return label;
    }

    protected JPanel frameOnFourSides(JPanel panel) {
        panel.add(newPanel(), BorderLayout.EAST);
        return frameSouthWestNorth(panel);
    }

    protected JPanel frameSouthWestNorth(JPanel panel) {
        panel.add(newPanel(), BorderLayout.SOUTH);
        return westNorth(panel);
    }

    protected JPanel frameWestNorthEast(JPanel panel) {
        panel.add(newPanel(), BorderLayout.EAST);
        return westNorth(panel);
    }

    protected JPanel westEast(JPanel panel) {
        panel.add(newPanel(), BorderLayout.WEST);
        panel.add(newPanel(), BorderLayout.EAST);
        return panel;
    }

    private JPanel westNorth(JPanel panel) {
        panel.add(newPanel(), BorderLayout.WEST);
        panel.add(newPanel(), BorderLayout.NORTH);
        return panel;
    }

    private void addCloseListener() {
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        addWindowListener(new WindowAdapter() {                    //	при закрытии крестиком выполяем действия
            @Override
            public void windowClosing(WindowEvent e) {
                controller.viewStartGUI();
                e.getWindow().dispose();
            }
        });
    }

}
