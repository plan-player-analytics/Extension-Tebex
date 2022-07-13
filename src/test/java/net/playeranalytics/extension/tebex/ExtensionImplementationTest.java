/*
    Copyright(c) 2019 AuroraLS3

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
package net.playeranalytics.extension.tebex;

import com.djrapitops.plan.extension.DataExtension;
import com.djrapitops.plan.extension.extractor.ExtensionExtractor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * Test for the implementation of the new extension
 *
 * @author AuroraLS3
 */
class ExtensionImplementationTest {

    private ExtensionExtractor extractor;

    @BeforeEach
    void prepareExtractor() {
        DataExtension extension = new BuycraftExtension(null);
        extractor = new ExtensionExtractor(extension);
    }

    @Test
    @DisplayName("API is implemented correctly")
    void noImplementationErrors() {
        extractor.validateAnnotations();
    }

    @Test
    void paymentRequestSucceeds() {
        PaginatedPaymentsResponse response = new ListPaymentRequest("166473e780b59e84d6a19f1975c9282bfcc7a2a7").requestPage(1);
        Supplier<String> errorMsg = response::toString;
        assertNotNull(response, errorMsg);
        assertNotNull(response.getData(), errorMsg);
        assertFalse(response.getData().isEmpty(), errorMsg);
        for (PaginatedPaymentsResponse.Payment payment : response.getData()) {
            assertNotNull(payment, errorMsg);
            assertNotNull(payment.getAmount(), errorMsg);
            assertNotNull(payment.getCurrency(), errorMsg);
            assertNotNull(payment.getPackages(), errorMsg);
            assertNotNull(payment.getPlayer(), errorMsg);
            assertNotNull(payment.getPlayer().getName(), errorMsg);
            assertNotNull(payment.getPlayer().getUUID(), errorMsg);
        }
    }
}