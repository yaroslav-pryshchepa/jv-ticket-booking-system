package mate.academy;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Timeout;

@Timeout(value = 2, unit = TimeUnit.MINUTES)
class TicketBookingSystemTest {

    private TicketBookingSystem bookingSystem;

    @BeforeEach
    void setUp() {
        bookingSystem = new TicketBookingSystem(5); // Assuming 5 seats for simplicity
    }

    @RepeatedTest(100)
    void attemptBooking_WhenSeatsAvailable_ShouldBookSuccessfully() {
        // given
        String user = "User1";

        // when
        BookingResult result = bookingSystem.attemptBooking(user);

        // then
        assertTrue(result.success());
        assertEquals("Booking successful.", result.message());
    }

    @RepeatedTest(100)
    void attemptBooking_WhenNoSeatsAvailable_ShouldFailToBook() {
        // given
        bookingSystem = new TicketBookingSystem(1); // Only 1 seat available
        bookingSystem.attemptBooking("User1"); // First user books the only seat

        // when
        BookingResult result = bookingSystem.attemptBooking("User2");

        // then
        assertFalse(result.success());
        assertEquals("No seats available.", result.message());
    }

    @RepeatedTest(100)
    void attemptBooking_MultipleUsers_ShouldHandleConcurrentAccess() {
        // given
        int numberOfUsers = 10;
        Thread[] threads = new Thread[numberOfUsers];
        BookingResult[] results = new BookingResult[numberOfUsers];

        // when
        for (int i = 0; i < numberOfUsers; i++) {
            final int userIndex = i;
            threads[i] = new Thread(() -> results[userIndex] = bookingSystem.attemptBooking("User" + userIndex));
            threads[i].start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                fail("The execution was interrupted", e);
            }
        }

        // then
        int successfulBookings = 0;
        for (BookingResult result : results) {
            if (result != null && result.success()) {
                successfulBookings++;
            }
        }
        assertEquals(5, successfulBookings); // Only 5 out of 10 should be able to book successfully
    }

    @RepeatedTest(100)
    void attemptBooking_HighVolumeConcurrentRequests_ShouldHandleCorrectly() throws InterruptedException {
        // given
        int totalSeats = 350;
        int totalRequests = 3000;
        TicketBookingSystem bookingSystem = new TicketBookingSystem(totalSeats);
        CountDownLatch startLatch = new CountDownLatch(1); // Ensures all threads start at the same time
        Thread[] threads = new Thread[totalRequests];
        BookingResult[] results = new BookingResult[totalRequests];

        // when
        for (int i = 0; i < totalRequests; i++) {
            final int userIndex = i;
            threads[i] = new Thread(() -> {
                try {
                    startLatch.await(); // Wait for the signal to start
                    results[userIndex] = bookingSystem.attemptBooking("User" + userIndex);
                } catch (InterruptedException e) {
                    fail("The execution was interrupted", e);
                }
            });
            threads[i].start();
        }

        startLatch.countDown(); // Signal all threads to start booking

        for (Thread thread : threads) {
            thread.join();
        }

        // then
        int successfulBookings = 0;
        for (BookingResult result : results) {
            if (result != null && result.success()) {
                successfulBookings++;
            }
        }
        assertEquals(totalSeats, successfulBookings); // Only 350 out of 3000 should be able to book successfully
    }

    @RepeatedTest(100)
    void attemptBooking_LessRequestsThanSeats_AllRequestsShouldSucceed() throws InterruptedException {
        // given
        int totalSeats = 100;
        int totalRequests = 50;
        TicketBookingSystem bookingSystem = new TicketBookingSystem(totalSeats);
        CountDownLatch startLatch = new CountDownLatch(1); // Ensures all threads start at the same time
        Thread[] threads = new Thread[totalRequests];
        BookingResult[] results = new BookingResult[totalRequests];

        // when
        for (int i = 0; i < totalRequests; i++) {
            final int userIndex = i;
            threads[i] = new Thread(() -> {
                try {
                    startLatch.await(); // Wait for the signal to start
                    results[userIndex] = bookingSystem.attemptBooking("User" + userIndex);
                } catch (InterruptedException e) {
                    fail("The execution was interrupted", e);
                }
            });
            threads[i].start();
        }

        startLatch.countDown(); // Signal all threads to start booking

        for (Thread thread : threads) {
            thread.join();
        }

        // then
        int successfulBookings = 0;
        for (BookingResult result : results) {
            if (result != null && result.success()) {
                successfulBookings++;
            }
        }
        assertEquals(totalRequests, successfulBookings); // All 50 requests should be successful
    }
}
