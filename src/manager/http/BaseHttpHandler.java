package manager.http;

import com.google.gson.*;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;


public abstract class BaseHttpHandler implements HttpHandler {

    protected final Gson gson = new GsonBuilder()
            .serializeNulls()
            .setPrettyPrinting()
            .registerTypeAdapter(LocalDateTime.class, new JsonSerializer<LocalDateTime>() {
                @Override
                public JsonElement serialize(LocalDateTime src, java.lang.reflect.Type typeOfSrc,
                                             JsonSerializationContext context) {
                    return new JsonPrimitive(src.toString());
                }
            })
            .registerTypeAdapter(LocalDateTime.class, new JsonDeserializer<LocalDateTime>() {
                @Override
                public LocalDateTime deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
                                                 JsonDeserializationContext context) {
                    return LocalDateTime.parse(json.getAsString());
                }
            })
            .registerTypeAdapter(Duration.class, new JsonSerializer<Duration>() {
                @Override
                public JsonElement serialize(Duration src, java.lang.reflect.Type typeOfSrc,
                                             JsonSerializationContext context) {
                    return new JsonPrimitive(src.toMinutes());
                }
            })
            .registerTypeAdapter(Duration.class, new JsonDeserializer<Duration>() {
                @Override
                public Duration deserialize(JsonElement json, java.lang.reflect.Type typeOfT,
                                            JsonDeserializationContext context) {
                    return Duration.ofMinutes(json.getAsLong());
                }
            })
            .create();



    protected String readBody(HttpExchange exchange) throws IOException {
        InputStream is = exchange.getRequestBody();
        return new String(is.readAllBytes(), StandardCharsets.UTF_8);
    }


    protected void sendJson(HttpExchange exchange, int status, Object responseObj) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        String response = gson.toJson(responseObj);
        sendText(exchange, status, response);
    }



    protected void sendText(HttpExchange exchange, int status, String text) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");
        byte[] bytes = text.getBytes(StandardCharsets.UTF_8);
        exchange.sendResponseHeaders(status, bytes.length);
        exchange.getResponseBody().write(bytes);
        exchange.close();
    }



    protected void sendError(HttpExchange exchange, int status, String message) throws IOException {
        sendJson(exchange, status, new ErrorResponse(message));
    }


    private static class ErrorResponse {
        final String error;

        ErrorResponse(String error) {
            this.error = error;
        }
    }
}
