public class Event extends Task {
    /** The date or time at which this event starts. */
    protected String from;
    /** The date or time at which this event ends. */
    protected String to;

    /**
     * Creates an event task with a start and end time.
     *
     * @param description the text describing the event
     * @param from the event's start date or time
     * @param to the event's end date or time
     */
    public Event(String description, String from, String to) {
        super(description);
        this.from = from;
        this.to = to;
    }

    /**
     * Returns this event task in the format used in the task list.
     *
     * @return the formatted event task
     */
    @Override
    public String toString() {
            return "[E]" + super.toString() + " (from: " + from + " to: " + to + ")";
    }
}
