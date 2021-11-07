import javax.swing.*;
import java.awt.*;
import java.util.Objects;

public class StartGUI extends JFrame {

    private static final StartGUI instance = new StartGUI();

    static StartGUI getInstance() {
        return instance;
    }

//=========================================================

    private StartGUI() {
        StartController.getInstance().setStartGUI(this);
        prepareWindow();
        StartController.getInstance().viewStartGUI();
    }

//=========================================================

    private void prepareWindow() {
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setComponents();
        pack();
        setLocationRelativeTo(null);    //	размещаем frame по центру
    }

    private void setComponents() {
        JLabel label = new JLabel(new ImageIcon(new ImageIcon(
                Objects.requireNonNull(StartGUI.class.getResource("photo_Andry_new_razmer.jpg"))).getImage()));
        label.setOpaque(false);

        JButton addressesList = createButton(new JButton("Список Адресов"));
        addressesList.addActionListener(e -> StartController.getInstance().viewAddressesListWindow());

        JButton settings = createButton(new JButton("Настройки"));
        settings.addActionListener(e -> StartController.getInstance().viewSettingWindow());

        JButton exit = createButton(new JButton("Выход"));
        exit.addActionListener(e -> System.exit(0));

        JPanel panelWithButtons = preparePanel(new JPanel());
        panelWithButtons.setLayout(new GridLayout(3, 1, 0, 10));
        panelWithButtons.add(addressesList);
        panelWithButtons.add(settings);
        panelWithButtons.add(exit);

        JPanel underLabelWithImage = preparePanel(new JPanel());
        underLabelWithImage.add(preparePanel(new JPanel()));
        underLabelWithImage.add(panelWithButtons);
        underLabelWithImage.add(preparePanel(new JPanel()));

        this.setLayout(new BorderLayout());
        add(label, BorderLayout.NORTH);
        add(underLabelWithImage, BorderLayout.CENTER);
        add(preparePanel(new JPanel()), BorderLayout.SOUTH);
        this.setFocusable(false);
    }

    private JButton createButton(JButton button) {
        button.setFont(new Font("Times new roman", Font.BOLD, 18));
        button.setBackground(Color.WHITE);
        button.setForeground(Color.BLACK);
        button.setFocusPainted(true);
        button.setFocusable(false);
        return button;
    }

    private JPanel preparePanel(JPanel panel) {
        panel.setBackground(Color.BLACK);
        panel.setFocusable(false);
        return panel;
    }
}
