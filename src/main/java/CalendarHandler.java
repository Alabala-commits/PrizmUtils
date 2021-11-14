import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Timer;
import java.util.TimerTask;

public class CalendarHandler {

    private final LocalTime start;

    private Timer timer;
    private LocalDate tomorrow;
    private int period;

//=========================================================

    CalendarHandler() {
        tomorrow = LocalDate.now().plusDays(1);
        start = LocalTime.now();
        period = 1000 * 60 * 60;
        initTimer();
    }

//=========================================================

    void restartTimer() {
        timer.cancel();
        initTimer();
    }

    private void initTimer() {
        timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                if (updateTomorrow()) {
                    StartController.getInstance().updateBalanceInfo();
                    StartController.getInstance().sendAll();
                }
                StartController.getInstance().updateUiInfo();
            }
        };
        timer.scheduleAtFixedRate(task, 0, period);
    }

    private boolean updateTomorrow() {
        LocalDate now = LocalDate.now();
        if (tomorrow.isEqual(now)) {
            tomorrow = now.plusDays(1);
            return true;
        }
        return false;
    }

//=========================================================

    public LocalDate getTomorrow() {
        return tomorrow;
    }

    public LocalTime getStart() {
        return start;
    }

    public String getDate(LocalDate date) {
        return date.toString().replaceAll("(\\d{4})-(\\d{2})-(\\d{2})", "$3.$2.$1");
    }

    public String getTime(LocalTime time) {
        return time.toString().replaceAll("(\\d{2}):(\\d{2}):(\\d{2}.\\d+)", "$1:$2");
    }

    public void setPeriod(int period) {
        this.period = 1000 * 60 * period;
    }

}
