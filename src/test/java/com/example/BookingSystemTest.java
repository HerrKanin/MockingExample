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
}
