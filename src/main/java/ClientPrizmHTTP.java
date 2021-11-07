import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Arrays;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ClientPrizmHTTP {

    private static final Logger logger = Logger.getLogger(ClientPrizmHTTP.class.getName());
    private static HttpURLConnection connection;

    int count = 0;

//=========================================================

    ClientPrizmHTTP() {
    }

//=========================================================

    public JSONObject getJsonWithAccountId() {
        if (StartController.DEBAG) {
            logger.info("Client.getJsonWithAccountId()");
        }

        JSONObject json = null;

        char[] chars = SettingWindow.getInstance().getPassword();
        if (chars.length == 0) {
            ListWindow.getInstance().alert("не указан Secret phrase в настройках");
            logger.warning("Secret phrase request from the Settings window,\treturn json = null");
            return json;
        }

        try {
            connection = (HttpURLConnection) new URL("http://localhost:9976/prizm?"
                    + "requestType=getAccountId&secretPhrase=" + URLEncoder.encode(new String(chars), "UTF-8"))
                    .openConnection();
            json = appealToNode("POST");
            return json;
        } catch (SocketTimeoutException e) {
            logger.log(Level.WARNING, "Timeout request,\tcount = {0}", count);
            if (count < 3) {
                count++;
                logger.warning("Recursive call getJsonWithAccountId()");
                return getJsonWithAccountId();
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException when trying to get Account ID,\tcount = {0}", String.valueOf(count));
            logger.log(Level.SEVERE, "", e);
            ListWindow.getInstance().printMessage(StartController.getInstance().getTime()
                    + "\tИсключение при попытке получить ID аккаунта\n\n");
        } finally {
            Arrays.fill(chars, '.');
            closeQuietly();
        }
        return json;
    }

    public int getBalance() {
        if (StartController.DEBAG) {
            logger.info("Client.getBalance()");
        }

        int balance = -1;

        JSONObject json;
        String account = (String) getJsonWithAccountId().get("account");

        try {
            connection = (HttpURLConnection) new URL("http://localhost:9976/prizm?"
                    + "requestType=getBalance&account=" + account)
                    .openConnection();
            json = appealToNode("POST");
            balance = Integer.parseInt((String) json.get("balanceNQT"));
            ListWindow.getInstance().printMessage(StartController.getInstance().getTime()
                    + "\tID Аккаунта " + account + "\tБаланс = " + StartController.getInstance().formatPzm(balance) + "\n\n");
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException when trying to get balanceNQT, account = {0}", account);
            logger.log(Level.SEVERE, "", e);
            ListWindow.getInstance().printMessage(StartController.getInstance().getTime()
                    + "\tИсключение при попытке получить баланс аккаунта\t" + account + "\n\n");
        } finally {
            closeQuietly();
        }
        return balance;
    }

    public void sendAll(int amount) {
        if (StartController.DEBAG) {
            logger.log(Level.INFO, "Client.sendAll(controller, amount = {0})", amount);
        }

        Set<String> addresses = ListWindow.getInstance().getSpisok().getListAddresses();
        if (addresses == null || addresses.isEmpty()) {
            ListWindow.getInstance().alert("некуда отправлять - список пуст");
            logger.warning("Set<String> addresses = null or is empty");
            return;
        }

        char[] chars = SettingWindow.getInstance().getPassword();
        if (chars.length == 0) {
            ListWindow.getInstance().alert("не указан Secret phrase в настройках");
            logger.warning("Secret phrase request from the Settings window,\tused sendAll method");
            return;
        }

        JSONObject json;
        String oneAddress = "empty Address";

        try {
            for (String str : addresses) {
                oneAddress = str;
                connection = (HttpURLConnection) new URL(new StringBuilder().append("http://localhost:9976/prizm?")
                        .append("requestType=sendMoney")
                        .append("&secretPhrase=").append(URLEncoder.encode(new String(chars), "UTF-8"))
                        .append("&recipient=").append(oneAddress)
                        .append("&amountNQT=").append(amount)
                        .append("&feeNQT=0")
                        .append("&deadline=60").toString())
                        .openConnection();

                json = appealToNode("POST");

                String txId = (String) json.get("transaction");
                String stringAmount = String.valueOf(Double.valueOf(amount * 0.01));
                if (json.get("broadcasted").equals(true)) {
                    logger.log(Level.INFO, "\ttx id: {0}\t{1}\t+ {2} pzm\n", new String[] {txId, oneAddress, stringAmount});
                    ListWindow.getInstance().printMessage(StartController.getInstance().getTime()
                            + "\ttx id:\t" + txId + "\t" + oneAddress + "\t+ " + stringAmount + " pzm\n\n");
                } else {
                    logger.log(Level.WARNING, "\ttx id: {0}\t{1}\t+ {2} pzm\t no broadcasted !\n", new String[] {txId, oneAddress, stringAmount});
                    ListWindow.getInstance().printMessage(StartController.getInstance().getTime()
                            + "\tТранзакция " + txId + " на Адрес " + oneAddress + " не транслирована в сеть\n\n");
                }
            }
        } catch (IOException e) {
            logger.log(Level.SEVERE, "IOException when trying send to Address\t{0}", oneAddress);
            logger.log(Level.SEVERE, "", e);
            ListWindow.getInstance().printMessage(StartController.getInstance().getTime()
                    + "\tИсключение при попытке отправки монет на Адрес\t" + oneAddress + "\n\n");
        } finally {
            Arrays.fill(chars, '.');
            closeQuietly();
        }
        if (StartController.DEBAG) {
            logger.info("sendAll method completed successfully");
        }
    }

    private static JSONObject getJson(BufferedReader reader, StringBuffer responseContent) throws IOException {
        String line;

        while ((line = reader.readLine()) != null) {
            responseContent.append(line);
        }
        return new JSONObject(responseContent.toString());
    }

    private JSONObject appealToNode(String requestMethod) throws IOException {
        BufferedReader reader;
        JSONObject json;

        connection.setRequestMethod(requestMethod);
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);

        int status = connection.getResponseCode();
        if (status == HttpURLConnection.HTTP_OK) {
            reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
        } else {
            logger.log(Level.WARNING, "ResponseCode = {}", status);
            ListWindow.getInstance().printMessage(StartController.getInstance().getTime()
                    + "\tОшибка при обращении к ноде\tResponseCode = " + status + "\n\n");
            reader = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
        }

        json = getJson(reader, new StringBuffer());
        reader.close();
        return json;
    }

    private void closeQuietly() {
        if (connection != null) {
            connection.disconnect();
        }
    }

}
