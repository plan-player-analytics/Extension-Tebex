/*
    Copyright(c) 2019 Risto Lahtela (AuroraLS3)

    The MIT License(MIT)

    Permission is hereby granted, free of charge, to any person obtaining a copy
    of this software and associated documentation files(the "Software"), to deal
    in the Software without restriction, including without limitation the rights
    to use, copy, modify, merge, publish, distribute, sublicense, and / or sell
    copies of the Software, and to permit persons to whom the Software is
    furnished to do so, subject to the following conditions :
    The above copyright notice and this permission notice shall be included in
    all copies or substantial portions of the Software.

    THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
    IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
    FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.IN NO EVENT SHALL THE
    AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
    LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
    OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
    THE SOFTWARE.
*/
package com.djrapitops.extension;

import com.djrapitops.plan.extension.NotReadyException;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Request to Buycraft API for payment listings.
 *
 * @author AuroraLS3
 */
public class ListPaymentRequest {

    private final String secret;

    public ListPaymentRequest(String secret) {
        this.secret = secret;
    }

    public List<Payment> makeRequest() {
        try {
            URL url = new URL("https://plugin.tebex.io/payments");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Tebex-Secret", secret);

            JsonElement json;
            try {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                json = new JsonParser().parse(reader);
            } finally {
                connection.disconnect();
            }

            if (json == null || json.isJsonNull()) {
                throw new NullPointerException("JSON should not be null");
            }

            List<Payment> payments = new ArrayList<>();
            if (json.isJsonObject()) {
                throw new NotReadyException();
            } else if (json.isJsonArray()) {
                readAndAddPayments(json, payments);
            }
            return payments;
        } catch (IOException e) {
            throw new NotReadyException();
        }
    }

    private void readAndAddPayments(JsonElement json, List<Payment> payments) {
        JsonArray jsonArray = json.getAsJsonArray();
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");

        for (JsonElement element : jsonArray) {
            JsonObject payment = element.getAsJsonObject();
            double amount = payment.get("amount").getAsDouble();
            String dateString = payment.get("date").getAsString();
            Date dateObj = dateFormat.parse(dateString, new ParsePosition(0));
            long date = dateObj.getTime();
            String currency = payment.get("currency").getAsJsonObject().get("iso_4217").getAsString();
            JsonObject player = payment.get("player").getAsJsonObject();
            String playerName = player.get("name").getAsString();
            StringBuilder packages = new StringBuilder();
            for (JsonElement pack : payment.get("packages").getAsJsonArray()) {
                packages.append(pack.getAsJsonObject().get("name")).append("<br>");
            }

            payments.add(new Payment(amount, currency, null, playerName, date, packages.toString()));
        }
    }
}