import java.util.ArrayList;
import java.util.List;

public class WeatherKeeper {
    final static int MAX_DAYS_FOR_FORECAST = 7;

    private final double lat;
    private final double lon;
    private int tempNow;

    private final List<Integer> avgTempInMornings = new ArrayList<>();
    private final List<Integer> avgTempInDays = new ArrayList<>();
    private final List<Integer> avgTempInEvening = new ArrayList<>();
    private final List<Integer> avgTempInNight = new ArrayList<>();

    public WeatherKeeper(double lat, double lon) {
        this.lat = lat;
        this.lon = lon;
    }

    public void setTempNow(int tempNow) {
        this.tempNow = tempNow;
    }

    public int getTempNow() {
        return tempNow;
    }

    public double getLat() {
        return lat;
    }

    public double getLon() {
        return lon;
    }

    public void addAVGTempInMorning(int tempAvg) {
        avgTempInMornings.add(tempAvg);
    }

    public void addAVGTempInDays(int tempAvg) {
        avgTempInDays.add(tempAvg);
    }

    public void addAVGTempInEvening(int tempAvg) {
        avgTempInEvening.add(tempAvg);
    }

    public void addAVGTempInNight(int tempAvg) {
        avgTempInNight.add(tempAvg);
    }

    public double getAvgTempForPart(Part part, int limit) {
        if (limit > WeatherKeeper.MAX_DAYS_FOR_FORECAST || limit < 0) {
            throw new IllegalArgumentException("Некорректно указан лимит дней для расчета средней температуры :"
                    + limit + " Должен быть в диапазоне от 0 до  " + WeatherKeeper.MAX_DAYS_FOR_FORECAST);
        }
        switch (part) {
            case MORNING:
                return avgTempInMornings.stream().limit(limit).mapToDouble(v -> v).average().orElse(Double.NaN);
            case DAY:
                return avgTempInDays.stream().limit(limit).mapToDouble(v -> v).average().orElse(Double.NaN);
            case EVENING:
                return avgTempInEvening.stream().limit(limit).mapToDouble(v -> v).average().orElse(Double.NaN);
            case NIGHT:
                return avgTempInNight.stream().limit(limit).mapToDouble(v -> v).average().orElse(Double.NaN);
        }
        return Double.NaN;
    }

    public enum Part {
        MORNING,
        DAY,
        EVENING,
        NIGHT
    }

    @Override
    public String toString() {
        return "WeatherKeeper{" +
                "lat=" + lat +
                ", lon=" + lon +
                ", tempNow=" + tempNow +
                ", avgTempInMornings=" + avgTempInMornings +
                ", avgTempInDays=" + avgTempInDays +
                ", avgTempInEvening=" + avgTempInEvening +
                ", avgTempInNight=" + avgTempInNight +
                '}';
    }
}