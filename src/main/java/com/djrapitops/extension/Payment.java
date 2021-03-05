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
class Payment implements Comparable<Payment> {

    private final double amount;
    private final String currency;
    private final UUID uuid;
    private final String playerName;
    private final long date;
    private final String packages;

    Payment(double amount, String currency, UUID uuid, String playerName, long date, String packages) {
        this.amount = amount;
        this.currency = currency;
        this.uuid = uuid;
        this.playerName = playerName;
        this.date = date;
        this.packages = packages;
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

    public long getDate() {
        return date;
    }

    public UUID getUuid() {
        return uuid;
    }

    public String getPackages() {
        return packages;
    }

    @Override
    public int compareTo(Payment o) {
        return -Long.compare(this.date, o.date);
    }
}