package provider.domain.availability;

import provider.domain.availability.exception.SlotConflictException;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Availability {

    private final String providerId;
    private final List<BookableSlot> slots = new ArrayList<>();
    private final List<WeeklySlot> weeklySlots = new ArrayList<>();

    private Availability(String providerId) {
        this.providerId = providerId;
    }

    public static Availability forProvider(String providerId) {
        return new Availability(providerId);
    }

    public void addWeeklySlot(WeeklySlot slot) { weeklySlots.add(slot); }

    public void addSlot(BookableSlot slot) {
        for (BookableSlot existing : slots) {
            if (existing.overlaps(slot)) {
                throw new SlotConflictException("Slot overlaps existing");
            }
        }
        this.slots.add(slot);
    }

    public void removeSlot(Instant start) {
        slots.removeIf(s -> s.start().equals(start));
    }

    public boolean isAvailable(Instant start, int durationHours) {
        return slots.stream().anyMatch(s ->
                !start.isBefore(s.start()) && !start.plusSeconds(durationHours * 3600L).isAfter(s.end()));
    }

    public String providerId() { return providerId; }
    public List<BookableSlot> slots() { return Collections.unmodifiableList(slots); }

    public boolean isAvailableFromWeekly(java.time.DayOfWeek day, java.time.LocalTime start, int durationHours) {
        java.time.LocalTime end = start.plusHours(durationHours);
        return weeklySlots.stream().anyMatch(s -> s.day() == day && s.covers(start) && !end.isAfter(s.end()));
    }

    public void blockSlot(java.time.Instant start, int durationHours) {
        slots.removeIf(s -> s.overlaps(BookableSlot.of(start, durationHours)));
    }
}
