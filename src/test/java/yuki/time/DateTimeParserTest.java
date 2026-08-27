package yuki.time;

import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;

/** Tests accepted date formats and the stable storage representation. */
class DateTimeParserTest {
    @Test
    void parse_supportedDateTimeFormats_dateTimeReturned() {
        TaskDateTime compactTime = DateTimeParser.parse("2/12/2026 1800");
        TaskDateTime colonTime = DateTimeParser.parse("2/12/2026 18:00");

        LocalDateTime expected = LocalDateTime.of(2026, 12, 2, 18, 0);
        assertAll(
                () -> assertEquals(expected, compactTime.getDateTime()),
                () -> assertEquals(expected, colonTime.getDateTime()));
    }

    @Test
    void parse_supportedDateFormats_dateReturned() {
        TaskDateTime localFormat = DateTimeParser.parse("2/12/2026");
        TaskDateTime isoFormat = DateTimeParser.parse("2026-12-02");

        LocalDate expected = LocalDate.of(2026, 12, 2);
        assertAll(
                () -> assertEquals(expected, localFormat.getDate()),
                () -> assertEquals(expected, isoFormat.getDate()));
    }

    @Test
    void parse_unrecognizedDate_originalTextReturned() {
        TaskDateTime value = DateTimeParser.parse("next Friday evening");

        assertAll(
                () -> assertFalse(value.hasDateTime()),
                () -> assertFalse(value.hasDateOnly()),
                () -> assertEquals("next Friday evening", value.getText()));
    }

    @Test
    void format_parsedValues_friendlyTextReturned() {
        assertAll(
                () -> assertEquals("Dec 2 2026 18:00",
                        DateTimeParser.format(TaskDateTime.of(
                                LocalDateTime.of(2026, 12, 2, 18, 0)))),
                () -> assertEquals("Dec 2 2026",
                        DateTimeParser.format(TaskDateTime.of(LocalDate.of(2026, 12, 2)))));
    }

    @Test
    void formatStoredAndParseStored_allValueTypes_roundTripPreserved() {
        TaskDateTime dateTime = TaskDateTime.of(LocalDateTime.of(2026, 12, 2, 18, 0));
        TaskDateTime date = TaskDateTime.of(LocalDate.of(2026, 12, 2));
        TaskDateTime text = TaskDateTime.of("someday");

        TaskDateTime restoredDateTime = DateTimeParser.parseStored(DateTimeParser.formatStored(dateTime));
        TaskDateTime restoredDate = DateTimeParser.parseStored(DateTimeParser.formatStored(date));
        TaskDateTime restoredText = DateTimeParser.parseStored(DateTimeParser.formatStored(text));

        assertAll(
                () -> assertEquals(dateTime.getDateTime(), restoredDateTime.getDateTime()),
                () -> assertEquals(date.getDate(), restoredDate.getDate()),
                () -> assertEquals(text.getText(), restoredText.getText()));
    }

    @Test
    void isBefore_valuesOfSameAndDifferentTypes_correctOrderingReturned() {
        TaskDateTime earlier = TaskDateTime.of(LocalDate.of(2026, 8, 6));
        TaskDateTime later = TaskDateTime.of(LocalDate.of(2026, 8, 7));
        TaskDateTime text = TaskDateTime.of("later");

        assertAll(
                () -> assertTrue(earlier.isBefore(later)),
                () -> assertFalse(later.isBefore(earlier)),
                () -> assertFalse(earlier.isBefore(text)));
    }
}
