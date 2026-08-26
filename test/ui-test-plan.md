# UI test plan

These command-driven tests protect Yuki's complete console transcript while responsibilities and application lifecycle move out of `main`.
The visible `␠` marker represents a trailing space in the banner so that whitespace remains reviewable in this file.

## Test case: Parse and execute valid commands

### Aim
Verify that every supported command still works when Yuki is constructed as an object and its instance `run()` method owns the session.

### Inputs
Run with a fresh data directory. Add todo, deadline, and event tasks; list them; update a task; delete a task; then exit.

### Command
```powershell
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)
$projectRoot = (Get-Location).Path
$caseRoot = Join-Path $projectRoot ('_temp\test-ui\valid-' + [guid]::NewGuid().ToString('N'))
$build = Join-Path $caseRoot 'build'
$run = Join-Path $caseRoot 'run'
New-Item -ItemType Directory -Path $build, $run | Out-Null
$sources = @(Get-ChildItem -LiteralPath (Join-Path $projectRoot 'src\main\java') -Filter '*.java' | Select-Object -ExpandProperty FullName)
& javac -d $build $sources
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
Push-Location $run
@('  todo read book  ', 'deadline return book /by 26/8/2026 1800', 'event project meeting /from 26/8/2026 1400 /to 26/8/2026 1600', '  list  ', 'mark 1', 'unmark 1', 'delete 2', 'bye') | & java '-Dfile.encoding=UTF-8' '-Dstdout.encoding=UTF-8' '-Dstderr.encoding=UTF-8' -cp $build Yuki | ForEach-Object { $_ -replace ' $', '␠' }
Pop-Location
```

### Expected output
```text
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
__   __     _    _␠
\ \ / /   _| | _(_)
 \ V / | | | |/ / |
  | || |_| |   <| |
  |_| \__,_|_|\_\_|

...Hello. This is Yuki.
What do you need?
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
Alright... I've added it.
  [T][ ] read book
There are 1 tasks now.
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
Alright... I've added it.
  [D][ ] return book (by: Aug 26 2026 18:00)
There are 2 tasks now.
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
Alright... I've added it.
  [E][ ] project meeting (from: Aug 26 2026 14:00 to: Aug 26 2026 16:00)
There are 3 tasks now.
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
Here... These are the tasks you have:
1.[T][ ] read book
2.[D][ ] return book (by: Aug 26 2026 18:00)
3.[E][ ] project meeting (from: Aug 26 2026 14:00 to: Aug 26 2026 16:00)
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
It's done now... I think.
[T][X] read book
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
The task is no longer marked as done:
[T][ ] read book
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
Alright... I've removed it.
  [D][ ] return book (by: Aug 26 2026 18:00)
There are 2 tasks now.
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
...Goodbye.
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
```

## Test case: Reject invalid command formats

### Aim
Verify that parser errors and TaskList's out-of-range error remain specific, and that an invalid `bye` does not end the session.

### Inputs
Run with an empty task list and exercise empty, unknown, incomplete, malformed, out-of-range, and extra-argument commands.

### Command
```powershell
[Console]::OutputEncoding = [System.Text.UTF8Encoding]::new($false)
$projectRoot = (Get-Location).Path
$caseRoot = Join-Path $projectRoot ('_temp\test-ui\invalid-' + [guid]::NewGuid().ToString('N'))
$build = Join-Path $caseRoot 'build'
$run = Join-Path $caseRoot 'run'
New-Item -ItemType Directory -Path $build, $run | Out-Null
$sources = @(Get-ChildItem -LiteralPath (Join-Path $projectRoot 'src\main\java') -Filter '*.java' | Select-Object -ExpandProperty FullName)
& javac -d $build $sources
if ($LASTEXITCODE -ne 0) { exit $LASTEXITCODE }
Push-Location $run
@('', 'unknown', 'todo', 'deadline return book', 'event meeting /from 26/8/2026 1800', 'event backwards /from 26/8/2026 2000 /to 26/8/2026 1800', 'mark', 'mark abc', 'mark 1', 'list extra', 'bye extra', 'bye') | & java '-Dfile.encoding=UTF-8' '-Dstdout.encoding=UTF-8' '-Dstderr.encoding=UTF-8' -cp $build Yuki | ForEach-Object { $_ -replace ' $', '␠' }
Pop-Location
```

### Expected output
```text
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
__   __     _    _␠
\ \ / /   _| | _(_)
 \ V / | | | |/ / |
  | || |_| |   <| |
  |_| \__,_|_|\_\_|

...Hello. This is Yuki.
What do you need?
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
I couldn't process that. No command was entered. Please enter a command.
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
I couldn't process that. That command isn't familiar to me.
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
I couldn't process that. The todo description is missing. Please add a task description after 'todo'.
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
I couldn't process that. The deadline needs a description, date and time. For example: deadline return book /by 26/8/2026 1800.
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
I couldn't process that. The event needs an end time.
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
I couldn't process that. The event's end time cannot be before its start time.
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
I couldn't process that. The task number is missing.
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
I couldn't process that. The task number must be a positive integer
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
I couldn't process that. I couldn't find a task with that number. Please enter a number between 1 and 0.
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
I couldn't process that. ..There’s no need to add anything else to the list command.
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
I couldn't process that. ..There’s no need to add anything else to the bye command.
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
...Goodbye.
❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄─────❄
```
