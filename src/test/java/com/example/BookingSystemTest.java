package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BookingSystemTest {

    @Mock
    TimeProvider timeProvider;
    @Mock
    RoomRepository roomRepository;
    @Mock
    NotificationService notificationService;

    @Captor
    ArgumentCaptor<Booking> bookingCaptor;

    private BookingSystem bookingSystem;
    private String roomId;
    private LocalDateTime now;
    private LocalDateTime start;
    private LocalDateTime end;
    private Room room;

    @BeforeEach
    void setUp() {
        bookingSystem = new BookingSystem(timeProvider, roomRepository, notificationService);

        roomId = "R1";
        now = LocalDateTime.of(2026, 2, 2, 10, 0);
        start = now.plusHours(1);
        end = now.plusHours(2);

        room = new Room(roomId, "Conference Room");

    }

    @Test
    @DisplayName("bookRoom: returns true when rook is available")
    void bookRoomShouldReturnTrueWhenRoomIsAvailable() {
        when(timeProvider.getCurrentTime()).thenReturn(now);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        boolean result = bookingSystem.bookRoom(roomId, start, end);

        assertThat(result).isTrue();
    }

    @Test
    @DisplayName("bookRoom: saves room when cooking is successful")
    void bookRoomShouldSaveRoomWhenRoomIsAvailable() {
        when(timeProvider.getCurrentTime()).thenReturn(now);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        bookingSystem.bookRoom(roomId, start, end);

        verify(roomRepository).save(room);
    }

    @Test
    @DisplayName("bookRoom: sends booking confirmation with correct booking details")
    void bookRoomShouldSendConfirmationWithCorrectBookingDetails() throws Exception {
        when(timeProvider.getCurrentTime()).thenReturn(now);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        bookingSystem.bookRoom(roomId, start, end);

        verify(notificationService).sendBookingConfirmation(bookingCaptor.capture());
        Booking sent = bookingCaptor.getValue();

        assertThat(sent.getRoomId()).isEqualTo(roomId);
        assertThat(sent.getStartTime()).isEqualTo(start);
        assertThat(sent.getEndTime()).isEqualTo(end);
        assertThat(sent.getId()).isNotBlank();
    }

    @Test
    @DisplayName("bookRoom: returns false and does not save when the room is not available")
    void bookRoomShouldReturnFalseWhenRoomIsnNotAvailable() throws Exception {
        when(timeProvider.getCurrentTime()).thenReturn(now);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        room.addBooking(new Booking("B1", roomId, start.minusMinutes(30), start.plusMinutes(30)));

        boolean result = bookingSystem.bookRoom(roomId, start, end);

        assertThat(result).isFalse();
        verify(roomRepository, never()).save(any());
        verify(notificationService, never()).sendBookingConfirmation(any());
    }

    @Test
    @DisplayName("bookRoom: succeeds even if notification sending fails")
    void bookRoomShouldSucceedEvenIfNotificationFails() throws Exception {
        when(timeProvider.getCurrentTime()).thenReturn(now);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

        doThrow(new NotificationException("Boom"))
                .when(notificationService)
                .sendBookingConfirmation(any(Booking.class));

        boolean result = bookingSystem.bookRoom(roomId, start, end);

        assertThat(result).isTrue();
        verify(roomRepository).save(room);
        verify(notificationService).sendBookingConfirmation(any(Booking.class));
    }

    @Test
    @DisplayName("bookRoom: throws exception when room does not exist")
    void bookRoomShouldThrowWhenRoomDoesNotExist() throws Exception {
        when(timeProvider.getCurrentTime()).thenReturn(now);
        when(roomRepository.findById(roomId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, start, end))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Rummet existerar inte");

        verify(roomRepository, never()).save(any());
        verify(notificationService, never()).sendBookingConfirmation(any());
    }

    @Test
    @DisplayName("bookRoom: throws exception when start time is in the past")
    void bookRoomShouldThrowWhenStartTimeIsInPast() throws Exception {
        when(timeProvider.getCurrentTime()).thenReturn(now);
        LocalDateTime pastStart = now.minusMinutes(1);
        LocalDateTime pastEnd = now.minusMinutes(10);

        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, pastStart, pastEnd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("dåtid");

        verify(roomRepository, never()).save(any());
        verify(notificationService, never()).sendBookingConfirmation(any());
    }

    @Test
    @DisplayName("bookRoom: throws exception when end is before start")
    void bookRoomShouldThrowWhenEndBeforeStart() throws Exception {
        when(timeProvider.getCurrentTime()).thenReturn(now);
        LocalDateTime badEnd = start.minusMinutes(1);

        assertThatThrownBy(() -> bookingSystem.bookRoom(roomId, start, badEnd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sluttid måste vara efter starttid");

        verify(roomRepository, never()).save(any());
        verify(notificationService, never()).sendBookingConfirmation(any());
    }

    @Test
    @DisplayName("getAvailableRooms: returns only rooms that are available in the given time range")
    void getAvailableRoomsShouldReturnOnlyAvailableRooms(){
        Room available = new Room("A", "Available");
        Room notAvailable = new Room("B", "Not Available");


        notAvailable.addBooking(new Booking("B1", "B", start.minusMinutes(10), end.plusMinutes(10)));

        when(roomRepository.findAll()).thenReturn(List.of(available, notAvailable));

        List<Room> result = bookingSystem.getAvailableRooms(start, end);

        assertThat(result)
                .containsExactly(available);
    }

    @Test
    @DisplayName("getAvailableRooms: throws exception when end time is before start time")
    void getAvailableRoomsShouldThrownWhenBeforeStart(){
        LocalDateTime badEnd = start.minusMinutes(1);

        assertThatThrownBy(()-> bookingSystem.getAvailableRooms(start, badEnd))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Sluttid måste vara efter starttid");
    }

    @Test
    @DisplayName("cancelBooking: throws exception when booking id is null")
    void cancelBookingShouldThrownWhenBookingIdIsNull(){
        assertThatThrownBy(()-> bookingSystem.cancelBooking(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Boknings-id kan inte vara null");
    }

    @Test
    @DisplayName("cancelBooking: returns false when booking cannot be found")
    void cancelBookingShouldReturnFalseWhenBookingNotFound() throws Exception{
        when(roomRepository.findAll()).thenReturn(List.of(
                new Room("A", "Room A"),
                new Room("A", "Room B")
        ));

        boolean result = bookingSystem.cancelBooking("missing");

        assertThat(result).isFalse();
        verify(roomRepository, never()).save(any());
        verify(notificationService, never()).sendCancellationConfirmation(any());
    }

    @Test
    @DisplayName("cancelBooking: throws exception when booking has already started")
    void cancelBookingShouldThrowWhenBookingAlreadyStarted() throws Exception{
        String bookingId = "B1";
        Room r = new Room("A", "Room A");
        Booking booking = new Booking(bookingId, r.getId(), now.minusMinutes(10), now.plusMinutes(50));
        r.addBooking(booking);

        when(timeProvider.getCurrentTime()).thenReturn(now);
        when(roomRepository.findAll()).thenReturn(List.of(r));

        assertThatThrownBy(()-> bookingSystem.cancelBooking(bookingId))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("Kan inte avboka");

        verify(roomRepository, never()).save(any());
        verify(notificationService, never()).sendCancellationConfirmation(any());
    }

    @Test
    @DisplayName("cancelBooking: remove booking, saves room and send cancellation confirmation when successful")
    void cancelBookingShouldRemoveSaveAndNotifyWhenSuccess() throws Exception{

        String bookingId = "B1";
        Room r = new Room("A", "Room A");
        Booking booking = new Booking(bookingId, r.getId(), now.plusHours(2), now.plusHours(3));
        r.addBooking(booking);

        when(timeProvider.getCurrentTime()).thenReturn(now);
        when(roomRepository.findAll()).thenReturn(List.of(r));

        boolean result = bookingSystem.cancelBooking(bookingId);

        assertThat(result).isTrue();
        assertThat(r.hasBooking(bookingId)).isFalse();

        verify(roomRepository).save(r);
        verify(notificationService).sendCancellationConfirmation(booking);
    }

    @Test
    @DisplayName("cancelBooking: succeeds even if cancellation notification fails")
    void cancelBookingShouldSucceedEvenIdNotificationFails() throws Exception {
        String bookingId = "B1";
        Room r = new Room("A", "Room A");
        Booking booking = new Booking(bookingId, r.getId(), now.plusHours(2), now.plusHours(3));
        r.addBooking(booking);

        when(timeProvider.getCurrentTime()).thenReturn(now);
        when(roomRepository.findAll()).thenReturn(List.of(r));
        doThrow(new NotificationException("Boom"))
                .when(notificationService)
                .sendCancellationConfirmation(any(Booking.class));

        boolean result = bookingSystem.cancelBooking(bookingId);

        assertThat(result).isTrue();
        verify(roomRepository).save(r);
        verify(notificationService).sendCancellationConfirmation(any(Booking.class));
    }
}
