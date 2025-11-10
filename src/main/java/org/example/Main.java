import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class Main {
    public static void main(String[] args) {
        double lat = 42.824043;  // Находка, Примоский край
        double lon = 132.892820;
        String url = String.format("https://api.weather.yandex.ru/v2/forecast?lat=%s&lon=%s&limit=7", lat, lon);
        HttpClient httpClient = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .header("X-Yandex-API-Key", "a8124111-adc4-4ef8-9e29-ec8069a9e16a")
                .header("User-Agent", "Mozilla/5.0")
                .GET()
                .build();

        System.out.println("Запрос: " + request.uri());

        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
            System.out.println("Код ответа: " + response.statusCode());

            if (response.statusCode() == 200) {
                WeatherKeeper weatherKeeper = parseJSON(response.body());
                System.out.println("Данные успешно получены и обработаны");
                printInfoAboutWeather(weatherKeeper, 7);
            } else {
                System.out.println("Ошибка при запросе: " + response.statusCode());
                System.out.println("Тело ответа: " + response.body());
            }

        } catch (Exception e) {
            System.err.println("Ошибка при выполнении HTTP запроса: " + e.getMessage());
            e.printStackTrace();
        }
    }

    static public WeatherKeeper parseJSON(String responseBody) {
        JSONObject jsonObject = new JSONObject(responseBody);
        
        // Получаем координаты
        JSONObject info = jsonObject.getJSONObject("info");
        double latitude = info.getDouble("lat");
        double longitude = info.getDouble("lon");
        
        WeatherKeeper weatherKeeper = new WeatherKeeper(latitude, longitude);
        
        // Текущая температура
        JSONObject fact = jsonObject.getJSONObject("fact");
        int currentTemp = fact.getInt("temp");
        weatherKeeper.setTempNow(currentTemp);
        
        // Прогноз на несколько дней
        JSONArray forecasts = jsonObject.getJSONArray("forecasts");
        for (int i = 0; i < forecasts.length(); i++) {
            JSONObject forecast = forecasts.getJSONObject(i);
            JSONObject parts = forecast.getJSONObject("parts");
            
            // Температуры для разных частей дня
            if (parts.has("morning")) {
                weatherKeeper.addAVGTempInMorning(parts.getJSONObject("morning").getInt("temp_avg"));
            }
            if (parts.has("day")) {
                weatherKeeper.addAVGTempInDays(parts.getJSONObject("day").getInt("temp_avg"));
            }
            if (parts.has("evening")) {
                weatherKeeper.addAVGTempInEvening(parts.getJSONObject("evening").getInt("temp_avg"));
            }
            if (parts.has("night")) {
                weatherKeeper.addAVGTempInNight(parts.getJSONObject("night").getInt("temp_avg"));
            }
        }
        
        return weatherKeeper;
    }

    static public void printInfoAboutWeather(WeatherKeeper w, int limit) {
        System.out.println("\n------------ ПОГОДА ------------");
        System.out.printf("Координаты: Широта: %.4f, Долгота: %.4f%n", w.getLat(), w.getLon());
        System.out.printf("Температура сейчас: %d°C%n", w.getTempNow());
        System.out.printf("Средняя температура на ближайшие %d дней:%n", limit);
        System.out.printf("  Утро: %.1f°C%n", w.getAvgTempForPart(WeatherKeeper.Part.MORNING, limit));
        System.out.printf("  День: %.1f°C%n", w.getAvgTempForPart(WeatherKeeper.Part.DAY, limit));
        System.out.printf("  Вечер: %.1f°C%n", w.getAvgTempForPart(WeatherKeeper.Part.EVENING, limit));
        System.out.printf("  Ночь: %.1f°C%n", w.getAvgTempForPart(WeatherKeeper.Part.NIGHT, limit));
        System.out.println("--------------------------------\n");
    }
}