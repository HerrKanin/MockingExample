package com.example;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Time;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class BookingSystemTest {

    @Mock TimeProvider timeProvider;
    @Mock RoomRepository roomRepository;
    @Mock NotificationService notificationService;

    @Captor ArgumentCaptor<Booking> bookingCaptor;

    private BookingSystem bookingSystem;
    private String roomId;
    private LocalDateTime now;
    private LocalDateTime start;
    private LocalDateTime end;
    private Room room;

    @BeforeEach
    void setUp(){
        bookingSystem = new BookingSystem(timeProvider, roomRepository, notificationService);

        roomId = "R1";
        now = LocalDateTime.of(2026, 2, 2, 10, 0);
        start = now.plusHours(1);
        end = now.plusHours(2);

        room = new Room(roomId, "Conference Room");

        when(timeProvider.getCurrentTime()).thenReturn(now);
        when(roomRepository.findById(roomId)).thenReturn(Optional.of(room));

    }

    @Test
    @DisplayName("bookroom: returns true when rook is available")
    void bookRoomShouldReturnTrueWhenRoomIsAvailable(){
        boolean result = bookingSystem.bookRoom(roomId, start, end);

        assertThat(result).isTrue();
    }
    @Test
    @DisplayName("bookroom: saves room when cooking is successful")
    void bookRoomShouldSaveRoomWhenRoomIsAvailable(){
        bookingSystem.bookRoom(roomId, start, end);

        verify(roomRepository).save(room);
    }
    @Test
    @DisplayName("bookRoom: sends booking confirmation with correct booking details")
    void bookRoomShouldSendConfirmationWithCorrectBookingDetails() throws Exception {
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
        room.addBooking(new Booking("B1", roomId, start.minusMinutes(30), start.plusMinutes(30)));

        boolean result = bookingSystem.bookRoom(roomId, start, end);

        assertThat(result).isFalse();
        verify(roomRepository, never()).save(any());
        verify(notificationService, never()).sendBookingConfirmation(any());
    }
}
