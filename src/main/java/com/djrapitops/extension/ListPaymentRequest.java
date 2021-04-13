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

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

/**
 * Request to Tebex API for payment listings.
 *
 * @author AuroraLS3
 */
public class ListPaymentRequest {

    private final String secret;

    public ListPaymentRequest(String secret) {
        this.secret = secret;
    }

    public PaginatedPaymentsResponse requestPage(int pageNumber) {
        JsonElement json = null;
        try {
            URL url = new URL("https://plugin.tebex.io/payments?page=" + pageNumber);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty("X-Tebex-Secret", secret);

            try {
                InputStreamReader reader = new InputStreamReader(connection.getInputStream());
                json = new JsonParser().parse(reader);
            } finally {
                connection.disconnect();
            }

            int responseCode = connection.getResponseCode();
            if (responseCode != 200) throw new IllegalStateException("Invalid response code: " + responseCode);

            if (json.isJsonArray()) {
                return getPaymentsWhenNotPaginated(json);
            } else {
                return new Gson().fromJson(json, PaginatedPaymentsResponse.class);
            }
        } catch (JsonSyntaxException e) {
            throw new IllegalStateException("Could not parse json, " + e.getMessage() + ": " + json, e);
        } catch (IOException e) {
            throw new IllegalStateException("Could not request, " + e.getMessage(), e);
        }
    }

    private PaginatedPaymentsResponse getPaymentsWhenNotPaginated(JsonElement json) {
        TypeToken<List<PaginatedPaymentsResponse.Payment>> typeToken = new TypeToken<List<PaginatedPaymentsResponse.Payment>>() {};
        List<PaginatedPaymentsResponse.Payment> payments = new Gson().fromJson(json, typeToken.getType());
        int size = payments.size();
        return new PaginatedPaymentsResponse(size, size, 1, 1, 1, size, payments);
    }
}