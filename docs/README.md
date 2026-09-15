# Yuki User Guide

Yuki is a task manager that accepts commands through its graphical or command-line interface.

## Entering tasks safely

Yuki ignores leading and trailing spaces and accepts multiple spaces between command parts. For
example, `deadline   submit report   /by   26/8/2026 1800` is valid. Descriptions are stored with
single spaces so that an accidental spacing difference does not create a duplicate task.

Yuki recognizes dates written as `d/M/yyyy` or `d/M/yyyy HHmm` (a colon in the time is also
accepted). If a value cannot be parsed, including a nonexistent date such as `30/2/2026`, Yuki
preserves it as ordinary text. When both event values are parsed dates, they must either both include
a time or both omit it, and the event's end must be later than its start.

Each deadline must contain exactly one `/by` parameter. Each event must contain exactly one `/from`
and one `/to` parameter, in that order. Yuki also rejects duplicate tasks and invalid task numbers
instead of changing the task list.

If the data file is missing, Yuki starts with an empty task list and creates the file when a task is
saved. If the file is unreadable or contains invalid data, Yuki reports the problem and continues to
accept read-only commands. It blocks saves until the file is repaired or moved and Yuki is restarted,
which prevents unreadable data from being overwritten accidentally. Saves use a temporary file so
that an interrupted write does not partly overwrite the last valid task list.

## Viewing upcoming tasks

Enter `reminders` to list incomplete deadlines and events that are due within the next 24 hours.
Yuki also displays the same reminder automatically when it starts, but stays quiet when no task is
due soon.

```text
reminders
```

Example output:

```text
Here... These tasks are due in the next 24 hours:
2.[D][ ] submit report (by: Sep 10 2026 18:00)
4.[E][ ] project demo (from: Sep 10 2026 20:00 to: Sep 10 2026 21:00)
```

If no task is due soon, Yuki responds with:

```text
You have no tasks due in the next 24 hours.
```

The 24-hour window includes tasks due at the current minute and exactly 24 hours later. Deadlines
use their due time, while events use their start time. A date without a time is treated as 23:59 on
that date. Completed tasks, todo tasks, and tasks with dates that Yuki could not parse are omitted.

Task numbers remain the same as in the main task list, even when upcoming tasks are displayed in a
different order. The command does not change or save any task.

The command takes no additional arguments. For example, `reminders tomorrow` is invalid.
