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

import com.djrapitops.plan.extension.CallEvents;
import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.annotation.PluginInfo;
import com.djrapitops.plan.extension.annotation.TableProvider;
import com.djrapitops.plan.extension.icon.Color;
import com.djrapitops.plan.extension.icon.Family;
import com.djrapitops.plan.extension.icon.Icon;
import com.djrapitops.plan.extension.table.Table;
import com.djrapitops.plan.settings.SchedulerService;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * BuyCraft DataExtension.
 *
 * @author AuroraLS3
 */
@PluginInfo(name = "Buycraft", iconName = "shopping-bag", iconFamily = Family.SOLID, color = Color.BLUE)
public class BuycraftExtension implements DataExtension {

    private SimpleDateFormat formatter;
    private DecimalFormat decimalFormatter;
    private PaymentStorage storage;
    private PaymentStorageUpdateTask updateTask;

    public BuycraftExtension(String secret) {
        if (secret != null) {
            storage = new PaymentStorage();
            updateTask = new PaymentStorageUpdateTask(storage, secret);

            formatter = new SimpleDateFormat("MMM d yyyy, HH:mm");
            decimalFormatter = new DecimalFormat("#0.00");

            SchedulerService.getInstance().runAsync(updateTask);
        }
    }

    @Override
    public CallEvents[] callExtensionMethodsOn() {
        return new CallEvents[]{
                CallEvents.SERVER_PERIODICAL,
                CallEvents.PLAYER_LEAVE
        };
    }

    @TableProvider(tableColor = Color.BLUE)
    public Table purchaseTable() {
        SchedulerService.getInstance().runAsync(updateTask);

        Table.Factory table = Table.builder()
                .columnOne("Player", Icon.called("user").build())
                .columnTwo("Date", Icon.called("calendar").of(Family.REGULAR).build())
                .columnThree("Amount", Icon.called("money-bill-wave").build())
                .columnFour("Packages", Icon.called("cube").build());

        List<StoredPayment> payments = storage.fetchPayments();
        Collections.sort(payments);

        for (StoredPayment payment : payments) {
            String name = payment.getPlayerName();
            table.addRow(
                    name,
                    formatter.format(payment.getDate()),
                    decimalFormatter.format(payment.getAmount()) + " " + payment.getCurrency(),
                    payment.getPackages()
            );
        }

        return table.build();
    }

    @TableProvider(tableColor = Color.BLUE)
    public Table playerPurchaseTable(UUID playerUUID) {
        Table.Factory table = Table.builder()
                .columnOne("Date", Icon.called("calendar").of(Family.REGULAR).build())
                .columnTwo("Amount", Icon.called("money-bill-wave").build())
                .columnThree("Packages", Icon.called("cube").build());

        List<StoredPayment> payments = storage.fetchPayments(playerUUID);
        Collections.sort(payments);

        for (StoredPayment payment : payments) {
            table.addRow(
                    formatter.format(payment.getDate()),
                    decimalFormatter.format(payment.getAmount()) + " " + payment.getCurrency(),
                    payment.getPackages()
            );
        }

        return table.build();
    }
}