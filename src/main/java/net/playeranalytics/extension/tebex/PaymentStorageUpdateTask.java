package net.playeranalytics.extension.tebex;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class PaymentStorageUpdateTask implements Runnable {

    private final PaymentStorage storage;
    private final ListPaymentRequest request;

    private final AtomicBoolean running = new AtomicBoolean(false);

    public PaymentStorageUpdateTask(PaymentStorage storage, String secret) {
        this.storage = storage;
        this.request = new ListPaymentRequest(secret);
    }

    @Override
    public void run() {
        // Don't run multiple of the same task at the same time
        if (running.get()) return;

        running.set(true);
        int page = 1;
        int maxPage = 1;

        int largestStoredId = storage.fetchLargestPaymentId();

        boolean fetchedAllPayments = false;
        while (page <= maxPage) {
            PaginatedPaymentsResponse response = request.requestPage(page);
            maxPage = response.getLastPage();

            List<StoredPayment> toStore = new ArrayList<>();
            List<PaginatedPaymentsResponse.Payment> data = response.getData();
            // Highest first
            data.sort((one, two) -> Integer.compare(two.getId(), one.getId()));

            for (PaginatedPaymentsResponse.Payment payment : data) {
                fetchedAllPayments = payment.getId() <= largestStoredId;
                if (fetchedAllPayments) {
                    break; // No need to process more payments
                }
                toStore.add(StoredPayment.fromResponsePayment(payment));
            }

            if (!toStore.isEmpty()) {
                storage.storePayments(toStore);
            }

            if (fetchedAllPayments) {
                break; // No need to fetch more pages, all payments are stored.
            }

            page++;
        }
        running.set(false);
    }
}
