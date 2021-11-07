import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import java.awt.*;

public class SettingWindow extends AbstractWindow {

    private static final SettingWindow instance = new SettingWindow();

    static SettingWindow getInstance() {
        return instance;
    }

    private JPasswordField passwordField;

    private JTextField textFieldForPath;
    private JTextField textFieldForPeriod;

    //=========================================================

    private SettingWindow() {
        super(StartController.getInstance());
        getController().setSettingWindow(this);
        visualizationDetails();
    }

    //=========================================================

    @Override
    protected void setSizeFrame() {
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setSize((int) (screenSize.getHeight() / 1.1), (int) (screenSize.getWidth() / 2.3));
    }

    private void visualizationDetails() {
        textFieldForPath = new JTextField(40);
        textFieldForPeriod = new JTextField(5);

        JPanel intoCenterCenter = newPanel(new GridLayout(10, 1, 0, 0));
        intoCenterCenter.add(singleElementSettingPanel("Путь к файлу", textFieldForPath,
                "укажи путь, где будет создан файл для хранения списка"));
        intoCenterCenter.add(singleElementSettingPanel("Период обновления", textFieldForPeriod,
                "укажи время в минутах, через которое таймер будет обновлять информацию"));
        textFieldForPath.setEditable(false);    //  TODO пока не нужна

        JPanel intoCenter = prepareBorderLayoutPanel(newPanel());
        intoCenter.add(createPanelForSecretPhrase(), BorderLayout.NORTH);
        intoCenter.add(intoCenterCenter, BorderLayout.CENTER);

        JPanel center = prepareBorderLayoutPanel(newPanel());
        center.add(intoCenter, BorderLayout.CENTER);
        center.add(newPanel(), BorderLayout.EAST);

        JPanel resultPanel = prepareBorderLayoutPanel(newPanel());
        resultPanel.add(new JScrollPane(center), BorderLayout.CENTER);
        this.getContentPane().add(frameOnFourSides(resultPanel));
    }

    private JPanel singleElementSettingPanel(String textForBorder, JTextField textField, String textForLabel) {
        JPanel panelForLabel = new JPanel();
        panelForLabel.add(prepareLabel(textForLabel));

        JPanel panelForTextField = new JPanel();
        textField.setHorizontalAlignment(JTextField.CENTER);
        panelForTextField.add(new JPanel());
        panelForTextField.add(textField);
        panelForTextField.add(new JPanel());

        JPanel resultPanel = newPanelWithBorder(new JPanel(new BorderLayout()), textForBorder, Color.BLACK);
        resultPanel.add(panelForLabel, BorderLayout.NORTH);
        resultPanel.add(panelForTextField, BorderLayout.CENTER);
        return resultPanel;
    }

    private JPanel createPanelForSecretPhrase() {
        JPanel panelForLabel = new JPanel();
        panelForLabel.add(prepareLabel("Парольная фраза от кошелька, с которого будут рассылаться монеты"));

        JPanel panelForPasswordField = new JPanel();
        this.passwordField = new JPasswordField(20);
        passwordField.setHorizontalAlignment(JPasswordField.CENTER);
        panelForPasswordField.add(new JPanel());
        panelForPasswordField.add(passwordField);
        panelForPasswordField.add(new JPanel());

        JPanel resultPanel = newPanelWithBorder(new JPanel(new BorderLayout()), "Secret phrase", Color.RED);
        resultPanel.add(panelForLabel, BorderLayout.NORTH);
        resultPanel.add(panelForPasswordField, BorderLayout.CENTER);
        return resultPanel;
    }

    private JPanel newPanelWithBorder(JPanel panel, String textOnBorder, Color color) {
        panel.setBorder(BorderFactory.createTitledBorder(new LineBorder(
                color, 3, true), textOnBorder, TitledBorder.LEFT, TitledBorder.CENTER));
        return panel;
    }

    private JLabel prepareLabel(String text) {
        JLabel label = new JLabel(text);
        label.setFont(new Font("Gotham Pro", Font.BOLD, 18));
        label.setHorizontalAlignment(SwingConstants.CENTER);
        return label;
    }

//=========================================================

    public char[] getPassword() {
        return passwordField.getPassword();
    }

    public String getTextForPeriod() {
        return textFieldForPeriod.getText();
    }

}
