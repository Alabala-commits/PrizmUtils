import java.io.*;
import java.nio.file.Paths;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserFileHandler {

    private static final Logger logger = Logger.getLogger(UserFileHandler.class.getName());

    private static final String path = Paths.get(".").toAbsolutePath().getParent().toString();

    static void writeSpisokIntoFile(final Set<String> addresses) throws IOException {
        try (ObjectOutputStream outputStreem = new ObjectOutputStream(
                new FileOutputStream(path + File.separator + "addresses.bin"))) {
            outputStreem.writeObject(addresses);
        }
    }

    static Set<String> readSpisokInFile() throws IOException {
        Set<String> result = null;

        try (ObjectInputStream inputStreem = new ObjectInputStream(
                new FileInputStream(path + File.separator + "addresses.bin"))) {
            result = (Set<String>) inputStreem.readObject();
        } catch (FileNotFoundException e) {
            logger.warning("File with addresses was not found, a new one will be created when adding addresses to the list");
        } catch (ClassNotFoundException e) {
            logger.log(Level.SEVERE, "Class Set not found", e);
            e.printStackTrace();
        }
        return result;
    }

    static String logFile() throws IOException {
        String fullPath = path + File.separator + "log.log";
        File f = new File(fullPath);
        if ( ! f.exists()) {
            f.createNewFile();
        }
        return fullPath;
    }

    static void riteLogInFile(Exception e) throws IOException {
        try (PrintWriter pw = new PrintWriter(new FileWriter(UserFileHandler.logFile(), true))) {
            e.printStackTrace(pw);
            pw.flush();
        }
    }
}
