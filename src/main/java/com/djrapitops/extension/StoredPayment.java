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

import java.util.UUID;

/**
 * Represents a BuyCraft payment.
 * <p>
 * Payments are sorted most recent first by natural ordering.
 *
 * @author AuroraLS3
 */
class StoredPayment implements Comparable<StoredPayment> {

    private final long tebexId;
    private final double amount;
    private final String currency;
    private final UUID uuid;
    private final String playerName;
    private final long date;
    private final String packages;

    StoredPayment(long tebexId, double amount, String currency, UUID uuid, String playerName, long date, String packages) {
        this.tebexId = tebexId;
        this.amount = amount;
        this.currency = currency;
        this.uuid = uuid;
        this.playerName = playerName;
        this.date = date;
        this.packages = packages;
    }

    static StoredPayment fromResponsePayment(PaginatedPaymentsResponse.Payment payment) {
        return new StoredPayment(
                payment.getId(),
                Double.parseDouble(payment.getAmount()),
                payment.getCurrency().getIso4217(),
                payment.getPlayer().getUUID(),
                payment.getPlayer().getName(),
                payment.getDate(),
                payment.getPackages()
        );
    }

    public long getTebexId() {
        return tebexId;
    }

    public double getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getPlayerName() {
        return playerName;
    }

    public String getTruncatedPlayerName(int length) {
        return playerName.length() > length ? playerName.substring(0, length - 1) : playerName;
    }

    public long getDate() {
        return date;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getPackages() {
        return packages;
    }

    public String getTruncatedPackages(int length) {
        return packages.length() > length ? packages.substring(0, length - 1) : packages;
    }

    @Override
    public int compareTo(StoredPayment o) {
        return -Long.compare(this.date, o.date);
    }
}