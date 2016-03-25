package dmytro;

import static spark.Spark.get;
import static spark.Spark.port;
import static spark.Spark.post;
import static spark.Spark.staticFileLocation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import spark.ModelAndView;
import spark.template.freemarker.FreeMarkerEngine;

import com.google.gson.Gson;

import dmytro.algo.RotN;
import dmytro.model.ChartData;
import dmytro.model.Message;
import dmytro.model.Result;

public class Main {

    /**
     * key is an encoded string. value is a message before encoding.
     */
    private static final Map<String, Message> history = new HashMap<>();

    public static void main(String[] args) {
        port(getHerokuAssignedPort());
        staticFileLocation("/public"); // Static files

        get("/", (req, res) -> {
            Map<String, Object> attributes = new HashMap<>();
            return new ModelAndView(attributes, "index.html");
        }, new FreeMarkerEngine());

        Gson gson = new Gson();
        post("/encode", (req, res) -> {

            String messageJson = req.body();
            Message message = gson.fromJson(messageJson, Message.class);
            String resultString = RotN.encode(message.getText(), message.getRotateNumber());
            history.put(resultString, message);
            return new Result(resultString);

        }, gson::toJson);

        post("/decode", (req, res) -> {

            String messageJson = req.body();
            Message message = gson.fromJson(messageJson, Message.class);
            String resultString = RotN.decode(message.getText(), message.getRotateNumber());
            return new Result(resultString);

        }, gson::toJson);

        post("/count", (req, res) -> {

            String messageJson = req.body();
            Message message = gson.fromJson(messageJson, Message.class);

            String text = message.getText().toLowerCase();

            List<Object[]> rows = new ArrayList<>();
            for (char alphabetChar : RotN.ALPHABET_LOWERCASE.toCharArray()) {
                int count = 0;
                for (char textChar : text.toCharArray()) {
                    if (textChar == alphabetChar) {
                        count++;
                    }
                }
                Object[] row = new Object[2];
                row[0] = alphabetChar;
                row[1] = count;
                rows.add(row);
            }

            ChartData chartData = new ChartData();
            chartData.setChartRows(rows);
            return chartData;

        }, gson::toJson);

        post("/tooltip", (req, res) -> {

            String messageJson = req.body();
            Message message = gson.fromJson(messageJson, Message.class);

            String text = message.getText();

            String shiftValue = null;
            for (String key : history.keySet()) {
                if (key.contains(text) || text.contains(key)) {
                    Message historicalMessage = history.get(text);
                    shiftValue = String.valueOf(historicalMessage.getRotateNumber());
                    break;
                }
            }

            Result result = new Result(shiftValue);
            return result;

        }, gson::toJson);
    }
    
    static int getHerokuAssignedPort() {
        ProcessBuilder processBuilder = new ProcessBuilder();
        if (processBuilder.environment().get("PORT") != null) {
            return Integer.parseInt(processBuilder.environment().get("PORT"));
        }
        return 4567; //return default port if heroku-port isn't set (i.e. on localhost)
    }
}
