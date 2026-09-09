# Yuki User Guide

Yuki is a task manager that accepts commands through its graphical or command-line interface.

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
