import javax.swing.*;
import java.awt.*;
import java.awt.event.KeyEvent;
import java.util.Set;

public class ListWindow extends AbstractWindow {

    private static final ListWindow instance = new ListWindow();

    static ListWindow getInstance() {
        return instance;
    }

    private final Spisok spisok;

    private JLabel labelForAddress;
    private JLabel labelForDataTomorrow;
    private JLabel labelForData;
    private JLabel labelForTime;
    private JLabel balance;
    private JLabel sendPzm;

    private JTextField inputTextField;
    private JTextField integerPZM;
    private JTextField hundredthPZM;

    private JTextArea logTextArea;

//=========================================================

    private ListWindow() {
        super(StartController.getInstance());
        this.spisok = new Spisok(getController().readFile(), this);
        getController().setListWindow(this);
        visualizationDetails();
    }

//=========================================================

    void setSelectAddress(String address) {
        labelForAddress.setText(address);
    }

    void alert(String alert) {
        JOptionPane.showMessageDialog(this, alert);
    }

    void afterUpdateSpisok(Set<String> addresses) {
        inputTextField.setText("");
        getController().updateFileWithAddresses(addresses);
    }

    void updateTextPzmLabel(String textBalance, String textSendPzm) {
        balance.setText(textBalance);
        sendPzm.setText(textSendPzm);
    }

    void updateDateLabels() {
        labelForDataTomorrow.setText(getController().getDateTomorrow());
        labelForTime.setText(getController().getTime());
        labelForData.setText(getController().getToday());
    }

    void printMessage(String msg) {
        logTextArea.append(msg);
    }

    private void visualizationDetails() {
        JPanel panelUpperHalf = prepareBorderLayoutPanel(newPanel());
        panelUpperHalf.add(createPanelForSpisok(), BorderLayout.CENTER);
        panelUpperHalf.add((createPanelToRightAtSpisok(prepareBorderLayoutPanel(newPanel()))), BorderLayout.EAST);

        JPanel panelLowerHalf = prepareBorderLayoutPanel(newPanel());
        panelLowerHalf.add(createPanelLowerHalf(prepareBorderLayoutPanel(newPanel())), BorderLayout.CENTER);

        this.setLayout(new GridLayout(2, 0, 0, 0));
        this.add(panelUpperHalf);
        this.add(panelLowerHalf);
    }

    private JPanel createPanelForSpisok() {
        JPanel panelForSpisok = prepareBorderLayoutPanel(newPanel());
        panelForSpisok.add(new JScrollPane(spisok), BorderLayout.CENTER);
        return frameSouthWestNorth(panelForSpisok);
    }

    private JPanel createPanelToRightAtSpisok(JPanel panel) {
        panel.add(prepareBasePanel(newPanel()), BorderLayout.CENTER);
        return frameOnFourSides(panel);
    }

    private JPanel prepareBasePanel(JPanel panel) {
        panel.setLayout(new GridLayout(6, 1, 0, 6));
        panel.add(preparePanelForTextFieldAndLabels(newPanel()));
        panel.add(preparePanelForAddButton(prepareBorderLayoutPanel(newPanel())));
        panel.add(preparePanelForDeleteButton(prepareBorderLayoutPanel(newPanel())));
        panel.add(newPanel());
        panel.add(preparePanelForSendToAllButton(prepareBorderLayoutPanel(newPanel())));
        panel.add(preparePanelForAmountPZM(newPanel()));
        return panel;
    }

    private JPanel preparePanelForTextFieldAndLabels(JPanel panel) {
        JPanel panelForTwoLabels = newPanel();
        panelForTwoLabels.setLayout(new GridLayout(2, 1));
        panelForTwoLabels.add(prepareLabel(new JLabel("введи Адрес кошелька", SwingConstants.CENTER), 11));
        panelForTwoLabels.add(prepareLabel(new JLabel("или выбери из списка", SwingConstants.CENTER), 11));

        inputTextField = new JTextField(5);
        inputTextField.setFont(new Font("Gotham Pro", Font.BOLD, 12));
        inputTextField.setHorizontalAlignment(JTextField.CENTER);

        panel.setLayout(new GridLayout(2, 1));
        panel.add(panelForTwoLabels);
        panel.add(inputTextField);
        return panel;
    }

    private JPanel preparePanelForAddButton(JPanel panel) {
        JButton add = prepareButton(new JButton("ДОБАВИТЬ В СПИСОК"));
        add.addActionListener(e -> getController().addAddress(inputTextField.getText()));

        panel.add(add, BorderLayout.CENTER);
        return frameWestNorthEast(panel);
    }

    private JPanel preparePanelForDeleteButton(JPanel panel) {
        JButton delete = prepareButton(new JButton("УДАЛИТЬ ИЗ СПИСКА"));
        delete.addActionListener(e -> spisok.removeElement(spisok.getSelectedAddress()));

        panel.add(delete, BorderLayout.CENTER);
        return frameWestNorthEast(panel);
    }

    private JPanel preparePanelForSendToAllButton(JPanel panel) {
        JButton sendAll = prepareButton(new JButton("ОТПРАВИТЬ ВСЕМ"));
        sendAll.addActionListener(e -> getController().sendAll(integerPZM.getText(), hundredthPZM.getText()));

        panel.add(sendAll, BorderLayout.CENTER);
        return frameWestNorthEast(panel);
    }

    private JPanel preparePanelForAmountPZM(JPanel panel) {
        JLabel po = prepareLabel(new JLabel("ПО"), 11);
        JLabel point = prepareLabel(new JLabel("."), 18);
        JLabel pzm = prepareLabel(new JLabel("PZM"), 11);

        JTextField integerPZM = new JTextField(5);
        integerPZM.setHorizontalAlignment(JTextField.RIGHT);
        this.integerPZM = integerPZM;

        JTextField hundredthPZM = new JTextField(2);
        hundredthPZM.setHorizontalAlignment(JTextField.LEFT);
        hundredthPZM.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyTyped(java.awt.event.KeyEvent evt) {
                if (hundredthPZM.getText().length() >= 2 && ! (evt.getKeyChar() == KeyEvent.VK_DELETE
                        || evt.getKeyChar() == KeyEvent.VK_BACK_SPACE)) {
                    getToolkit().beep();
                    evt.consume();
                }
            }
        });
        this.hundredthPZM = hundredthPZM;

        JPanel panelForIntegerPZM = prepareBorderLayoutPanel(newPanel());
        panelForIntegerPZM.add(integerPZM, BorderLayout.CENTER);
        frameOnFourSides(panelForIntegerPZM);

        JPanel panelForHundredthPZM = prepareBorderLayoutPanel(newPanel());
        panelForHundredthPZM.add(hundredthPZM, BorderLayout.CENTER);
        frameOnFourSides(panelForHundredthPZM);

        panel.setLayout(new BoxLayout(panel, BoxLayout.X_AXIS));
        return centerAlignmentElements(panel, new JLabel[] {po, point, pzm}, new JPanel[] {panelForIntegerPZM, panelForHundredthPZM});
    }

    private JPanel centerAlignmentElements(JPanel panelToAdd, JLabel[] labels, JPanel[] panels) {
        addFourEmptyPanels(panelToAdd);
        panelToAdd.add(labels[0]);
        panelToAdd.add(panels[0]);
        panelToAdd.add(labels[1]);
        panelToAdd.add(panels[1]);
        panelToAdd.add(labels[2]);
        return addFourEmptyPanels(panelToAdd);
    }

    private JPanel addFourEmptyPanels(JPanel panel) {
        for (int i = 0; i < 3; ++i) {
            panel.add(newPanel());
        }
        return panel;
    }

    private JPanel createPanelLowerHalf(JPanel panel) {
        JPanel panelLining = prepareBorderLayoutPanel(newPanel());
        panelLining.add(prepareMiddleNorthPanel(prepareBorderLayoutPanel(newPanel())), BorderLayout.NORTH);
        panelLining.add(prepareMiddleCenterPanel(prepareBorderLayoutPanel(newPanel())), BorderLayout.CENTER);
        panelLining.add(prepareLogPanel(prepareBorderLayoutPanel(newPanel())), BorderLayout.SOUTH);

        panel.add(prepareTopPanel(prepareBorderLayoutPanel(newPanel())), BorderLayout.NORTH);
        panel.add(panelLining, BorderLayout.CENTER);
        panel.add(newPanel(), BorderLayout.SOUTH);
        return panel;
    }

    private JPanel prepareTopPanel(JPanel panel) {
        labelForAddress = prepareLabel(new JLabel());
        panel.add(labelForAddress, BorderLayout.CENTER);
        return panel;
    }

    private JPanel prepareMiddleCenterPanel(JPanel panel) {
        JLabel timerStatus = prepareLabelLowerHalf(new JLabel("Автоматическая рассылка отключена "), SwingConstants.RIGHT);

        JPanel panelCenterLeft = preparePanelCenterLeft(prepareBorderLayoutPanel(newPanel()), timerStatus);
        JPanel panelCenterRight = preparePanelCenterRight(prepareBorderLayoutPanel(newPanel()), timerStatus);

        JPanel panelCenter = newPanel();
        panelCenter.add(panelCenterLeft);
        panelCenter.add(panelCenterRight);

        panel.add(panelCenter, BorderLayout.CENTER);
        return frameOnFourSides(panel);
    }

    private JPanel preparePanelCenterLeft(JPanel panelCenterLeft, JLabel timerStatus) {
        JPanel pStatus = prepareBorderLayoutPanel(newPanel());
        pStatus.add(timerStatus, BorderLayout.NORTH);
        pStatus.add(newPanel(), BorderLayout.CENTER);
        pStatus.add(newPanel(), BorderLayout.SOUTH);

        JPanel pLeftCenter = newPanel();
        pLeftCenter.setComponentOrientation(ComponentOrientation.RIGHT_TO_LEFT);
        pLeftCenter.setLayout(new GridLayout(4, 2, 0, 2));
        pLeftCenter.add(prepareLabelLowerHalf(new JLabel("отправка монет произойдёт:"), SwingConstants.RIGHT));
        pLeftCenter.add(prepareLabelLowerHalf(new JLabel("таймер стартовал в:"), SwingConstants.RIGHT));
        pLeftCenter.add(prepareLabelLowerHalf(new JLabel("последнее обновление:"), SwingConstants.RIGHT));
        pLeftCenter.add(prepareLabelLowerHalf(new JLabel("в:"), SwingConstants.RIGHT));

        JPanel pBalance = prepareBorderLayoutPanel(newPanel());
        pBalance.add(newPanel(), BorderLayout.NORTH);
        pBalance.add(prepareLabelLowerHalf(new JLabel("баланс рассылочного кошелька:"), SwingConstants.RIGHT), BorderLayout.CENTER);
        pBalance.add(prepareLabelLowerHalf(new JLabel("будет расослано по:"), SwingConstants.RIGHT), BorderLayout.SOUTH);

        panelCenterLeft.add(pStatus, BorderLayout.NORTH);
        panelCenterLeft.add(pLeftCenter, BorderLayout.CENTER);
        panelCenterLeft.add(pBalance, BorderLayout.SOUTH);
        return panelCenterLeft;
    }

    private JPanel preparePanelCenterRight(JPanel panelCenterRight, JLabel timerStatus) {
        JPanel pButton = prepareBorderLayoutPanel(newPanel());
        pButton.add(prepareButtonTimer(prepareButton(new JButton("включить")), timerStatus), BorderLayout.NORTH);
        pButton.add(newPanel(), BorderLayout.CENTER);

        labelForDataTomorrow = prepareLabelLowerHalf(new JLabel(getController().getDateTomorrow()), SwingConstants.LEFT);
        labelForData = prepareLabelLowerHalf(new JLabel(getController().getToday()), SwingConstants.LEFT);
        labelForTime = prepareLabelLowerHalf(new JLabel(getController().getTime()), SwingConstants.LEFT);
        JPanel pRightCenter = newPanel();
        pRightCenter.setLayout(new GridLayout(4, 2, 0, 2));
        pRightCenter.add(labelForDataTomorrow);
        pRightCenter.add(prepareLabelLowerHalf(new JLabel(getController().getStartTime()), SwingConstants.LEFT));
        pRightCenter.add(labelForData);
        pRightCenter.add(labelForTime);

        balance = prepareLabelLowerHalf(new JLabel("  ...."), SwingConstants.LEFT);
        sendPzm = prepareLabelLowerHalf(new JLabel("  ...."), SwingConstants.LEFT);
        JPanel pPzm = prepareBorderLayoutPanel(newPanel());
        pPzm.add(newPanel(), BorderLayout.NORTH);
        pPzm.add(balance, BorderLayout.CENTER);
        pPzm.add(sendPzm, BorderLayout.SOUTH);

        panelCenterRight.add(pButton, BorderLayout.NORTH);
        panelCenterRight.add(pRightCenter, BorderLayout.CENTER);
        panelCenterRight.add(pPzm, BorderLayout.SOUTH);
        return panelCenterRight;
    }

    private JButton prepareButtonTimer(JButton bTimer, JLabel timerStatus) {
        bTimer.setFont(new Font("GothamPro-Bold", Font.BOLD, 14));
        bTimer.addActionListener(e -> {
            if (bTimer.getText().equals("включить")) {
                bTimer.setText("отключить");
                timerStatus.setText("Автоматическая рассылка включена ");
                updateTextPzmLabel(getController().getStringBalance(), getController().getStringSendPzm());
                StartController.getInstance().setUserPeriod();
                StartController.BalanceInfo.setAllowed(true);
                return;
            }
            updateTextPzmLabel("  ....", "  ....");
            bTimer.setText("включить");
            timerStatus.setText("Автоматическая рассылка отключена ");
            StartController.BalanceInfo.setAllowed(false);
        });
        return bTimer;
    }

    private JPanel prepareLogPanel(JPanel panel) {
        logTextArea = new JTextArea(10, 5);
        logTextArea.setSize(panel.getSize());
        panel.add(new JScrollPane(logTextArea, ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS,
                ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED), BorderLayout.CENTER);
        return westEast(panel);
    }

    private JPanel prepareMiddleNorthPanel(JPanel panel) {
        JLabel labelUnderAddress = prepareLabel(new JLabel(""));
        panel.add(labelUnderAddress, BorderLayout.CENTER);
        return panel;
    }

//=========================================================

    public Spisok getSpisok() {
        return spisok;
    }

}
