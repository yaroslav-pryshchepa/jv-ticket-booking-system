# Online ticket booking system

Imagine you're developing an online ticket booking system for a cinema. The cinema has a limited number of seats available for each show. Our system must handle multiple booking requests simultaneously, ensuring that no more than the available seats are booked for any particular show. This is a classic case of a shared resource (cinema seats) being accessed by multiple entities (booking requests).

In this scenario, a semaphore can effectively manage the access to the finite number of seats. The semaphore count would be initialized to the total number of available seats for a show. Each booking attempt by a user is handled by a separate thread. The thread must acquire a semaphore before proceeding with the booking. If the semaphore is acquired, it means a seat is available, and the booking can proceed. Once the booking is confirmed, the semaphore is released, decrementing the count of available seats.

Keep in mind: each booking request is processed in a separate thread.
