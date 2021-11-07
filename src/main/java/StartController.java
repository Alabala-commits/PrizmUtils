import java.io.*;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Pattern;

public class StartController {

    static final boolean DEBAG = true;

    private static final Logger logger = Logger.getLogger(StartController.class.getName());

    private static final StartController instance = new StartController();

    static StartController getInstance() {
        return instance;
    }

    private StartGUI startGUI;
    private ListWindow listWindow;
    private SettingWindow settingWindow;
    private ClientPrizmHTTP client;
    private CalendarHandler calendar;

//=========================================================

    void viewStartGUI() {
        startGUI.setVisible(true);
    }

    void viewAddressesListWindow() {
        listWindow.setVisible(true);
        startGUI.setVisible(false);
    }

    void viewSettingWindow() {
        settingWindow.setVisible(true);
        startGUI.setVisible(false);
    }

    void updateBalanceInfo() {
        BalanceInfo.setBalance(client.getBalance());
        BalanceInfo.setAmount(calculateAmount(BalanceInfo.balance, getAddresses().size()));
    }

    void updateUiInfo() {
        if (listWindow != null) {
            if (BalanceInfo.allowed) {
                listWindow.updateTextPzmLabel(getStringBalance(), getStringSendPzm());
            }
            listWindow.updateDateLabels();
        }
    }

    String getStringBalance() {
        updateBalanceInfo();
        return formatPzm(BalanceInfo.balance);
    }

    String getStringSendPzm() {
        return formatPzm(BalanceInfo.amount);
    }

    Set<String> getAddresses() {
        return listWindow.getSpisok().getListAddresses();
    }

    void addAddress(String address) {
        if (address == null || address.isEmpty()) {
            return;
        }
        if ( ! address.matches("^PRIZM-[^_^\\W]{4}-[^_^\\W]{4}-[^_^\\W]{4}-[^_^\\W]{5}")) {
            listWindow.alert("проверь правильность ввода\nPRIZM-XXXX-XXXX-XXXX-XXXXX");
            return;
        }
        listWindow.getSpisok().addElement(address);
    }

    void updateFileWithAddresses(Set<String> addresses) {
        try {
            UserFileHandler.writeSpisokIntoFile(addresses);
            System.out.println("список сохранён в файл");
        } catch (IOException e) {
            System.out.println("IOException при сохранении списока в файл");
            e.printStackTrace();
        }
    }

    void sendAll() {
        if (BalanceInfo.allowed) {
            client.sendAll(BalanceInfo.amount);
        }
    }

    void sendAll(String integer, String hundredth) {
        updateBalanceInfo();
        if (verify(integer, hundredth)) {
            client.sendAll(BalanceInfo.userAmount);
        }
    }

    String getToday() {
        return calendar.getDate(LocalDate.now());
    }

    String getStartTime() {
        return calendar.getTime(calendar.getStart());
    }

    String getDateTomorrow() {
        return calendar.getDate(calendar.getTomorrow());
    }

    String getTime() {
        return calendar.getTime(LocalTime.now());
    }

    void setUserPeriod() {
        calendar.setPeriod(getUserPeriod());
        calendar.restartTimer();
    }

    int getUserPeriod() {
        String stringPeriod = settingWindow.getTextForPeriod();
        if (stringPeriod.isEmpty()) {
            return 60;
        }
        if ( ! Pattern.matches("\\d+", stringPeriod)) {
            listWindow.alert("период должен быть числом больше нуля");
            return 60;
        }
        int period = Integer.parseInt(stringPeriod);
        if (period <= 0) {
            listWindow.alert("период должен быть числом больше нуля");
            return 60;
        }
        return period;
    }

    Set<String> readFile() {
        Set<String> addresses;

        try {
            addresses = UserFileHandler.readSpisokInFile();
        } catch (IOException e) {
            logger.log(Level.WARNING, "IOException when read file Spisok", e);
            return new TreeSet<>();
        }
        return addresses != null ? addresses : new TreeSet<>();
    }

    String formatPzm(int nqt) {
        String pzm = String.valueOf(nqt);
        if (pzm.equals("-1") || pzm.equals("0")) {
            return pzm;
        }
        if (pzm.length() == 1) {
            return "0.0" + pzm + " pzm";
        }
        if (pzm.length() == 2) {
            return "0." + pzm + " pzm";
        }
        return pzm.replaceAll("(\\d+)(\\d{2}$)", "$1.$2 pzm");
    }

    private int calculateAmount(int balance, int size) {
        if (balance <= 5 || size <= 0) {
            return 0;
        }
        if (balance < (size * 6)) {
            return 0;
        }

        int amount = Math.floorDiv(balance, size);
        int fee = (int) (amount / 100 * 0.5);
        if (fee < 5) {
            amount = amount - 5;
        } else if (fee > 5 && fee < 1000) {
            amount = amount - fee;
        } else {
            amount = amount - 1000;
        }

        try {
            if ((Math.multiplyExact(amount, size) + Math.multiplyExact(fee, size)) > balance) {
                return 0;
            }
        } catch (ArithmeticException e) {
            logger.log(Level.WARNING, "ArithmeticException when control check\namount ({0}) + fee ({1}) = {2}\naddresses size = {3}, balance = {4}",
                    new String[] {String.valueOf(amount), String.valueOf(fee), String.valueOf(amount + fee), String.valueOf(size), String.valueOf(balance)});
            logger.log(Level.WARNING, "continuation of ArithmeticException", e);
        }
        return amount;
    }

    private boolean verify(String integer, String hundredth) {
        if ( ! integer.isEmpty() && hundredth.isEmpty()) {
            hundredth = "00";
        }
        if (hundredth.length() == 1) {
            hundredth = hundredth + "0";
        }

        int pzm;
        if (integer.isEmpty() & ! hundredth.isEmpty()) {
            if ( ! Pattern.matches("\\d+", hundredth)) {
                listWindow.alert("количество pzm должно быть числом больше нуля");
                return false;
            }
            pzm = Integer.parseInt(hundredth);
        } else {
            if ( ! Pattern.matches("\\d+", integer) && ! Pattern.matches("\\d+", hundredth)) {
                listWindow.alert("количество pzm должно быть числом больше нуля");
                return false;
            }
            pzm = Integer.parseInt(integer + hundredth);
        }

        if (pzm <= 0) {
            listWindow.alert("количество pzm должно быть числом больше нуля");
            return false;
        }
        if (pzm > BalanceInfo.amount) {
            listWindow.alert("на балансе не хватит pzm на всех\nна всех максимум по " + getStringSendPzm());
            return false;
        }
        BalanceInfo.setUserAmount(pzm);
        return true;
    }

//=========================================================

    void setClient(ClientPrizmHTTP client) {
        this.client = client;
    }

    void setCalendar(CalendarHandler calendar) {
        this.calendar = calendar;
    }

    void setStartGUI(StartGUI startGUI) {
        this.startGUI = startGUI;
    }

    void setSettingWindow(SettingWindow settingWindow) {
        this.settingWindow = settingWindow;
    }

    void setListWindow(ListWindow listWindow) {
        this.listWindow = listWindow;
    }

//=========================================================

    static class BalanceInfo {

        private static int balance;
        private static int amount;
        private static int userAmount;
        private static boolean allowed;

        public static void setBalance(int balance) {
            BalanceInfo.balance = balance;
        }

        public static void setAmount(int amount) {
            BalanceInfo.amount = amount;
        }

        public static void setUserAmount(int userAmount) {
            BalanceInfo.userAmount = userAmount;
        }

        public static void setAllowed(boolean allowed) {
            BalanceInfo.allowed = allowed;
        }
    }
}
