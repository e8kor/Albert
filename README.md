[![Build Status](https://travis-ci.org/e8kor/Albert.svg?branch=master)](https://travis-ci.org/e8kor/Albert)

# Albert

 Albert is system for structural execution of context. For example you have a bunch of scripts that need to be executed in defined order, stop/continue/restart depends on previous execution result.

## Phases

Albert system execution from consists of phases described below, flow is not completed yet, phases may be added or modified.

### Initialization

When `Albert` application started it searches for its children (each of children performs same action) and initialize them.

- Each folder that contain `root.conf` file can be counted as execution root
- Each folder inside of described above and contain `suite.conf`
- If folder doesn't contain `suite.conf` but its children has then they will be skipped from execution

### Execution

When `RootExecutor` performs `StartSuite` command to its children its indicates that execution started

- Each suite (root or subfolder) is responsible to detect its children and runner, perform start and collect results
- Suite will not performe its execution until its children respond with completion status.
- Suite will not send its completion status to parent until its children and then runners will not complete its execution.

### Completion (Require implementation)

When suites complete their execution all suite reports collected and bundle will be forwarded to reporter plugin.

## Runners

Runners is plugin actor that represent logic for its context execution, each plugin responsible for receiving `StartWork` command and perform its execution. After plugin complete execution its responsible to send report and completion status to its parent


