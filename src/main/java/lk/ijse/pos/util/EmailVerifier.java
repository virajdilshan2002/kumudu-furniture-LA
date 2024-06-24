package lk.ijse.pos.util;

import javafx.scene.control.Alert;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EmailVerifier {
        private static final String API_KEY = "00a96763f0215b80a1888886dbba8198f3fb9725";  // Replace with your actual API key
        private static final String API_URL = "https://api.hunter.io/v2/email-verifier?email=";  // Hunter.io API URL

        public static boolean checkIsValidMail(String email) {
            try {
                URL url = new URL(API_URL + email + "&api_key=" + API_KEY);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("GET");
                int responseCode = con.getResponseCode();

                if (responseCode == HttpURLConnection.HTTP_OK) {
                    BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Parse JSON response to determine email validity
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    String result = jsonResponse.getJSONObject("data").getString("result");

                    if ("deliverable".equals(result)) {
                        System.out.println("Email is valid.");
                        return true;
                    } else {
                        System.out.println("Email is not valid.");
                        new Alert(Alert.AlertType.INFORMATION, "Invalid Email Address!").show();
                        return false;
                    }
                } else {
                    System.out.println("Failed to verify email. HTTP Error Code: " + responseCode);
                    new Alert(Alert.AlertType.ERROR, "Failed to verify email. HTTP Error Code: " + responseCode).show();
                    return false;
                }
            } catch (Exception e) {
                new Alert(Alert.AlertType.ERROR, "CHECK INTERNET CONNECTION!").show();
                return false;
            }
        }
}
