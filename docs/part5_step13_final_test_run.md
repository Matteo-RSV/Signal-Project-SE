# Part 5 Step 13 Final Test Run

## Command used

I first ran:

`mvn clean test`

That failed during the clean step, so I then ran:

`mvn test`

## Did `mvn clean test` pass?

No.

It failed before the tests started because Maven could not fully delete the `target` folder.

Exact error:

`Failed to delete C:\Users\matte\OneDrive\Desktop\Signal-Project-SE\target\test-classes\data_management\FileDataReaderTest.class`

## Was `mvn test` needed instead?

Yes.

Because the clean step failed, I used `mvn test` to verify the actual code and tests.

## Final Maven test result

`mvn test` passed.

Maven reported:

- Tests run: 67
- Failures: 0
- Errors: 0
- Skipped: 0

## Errors encountered

The main problem was environment-related, not code-related.

The failure happened in the clean step when Maven tried to delete a file inside `target`. In this workspace, that is likely related to the file being locked, which can happen in a OneDrive folder.

During the passing `mvn test` run, there were also normal warning/error messages printed by tests for intentionally bad WebSocket input and refused test connections, but those tests still passed.

## Is the remaining issue code-related or environment-related?

The remaining issue is environment-related.

- `mvn clean test` failed because of a locked file in `target`
- `mvn test` passed, so the Part 5 code and tests are currently working

## Recommended next step

The next step should be a final live check outside the unit/integration test suite:

1. start the simulator in WebSocket mode
2. start the WebSocket reader
3. confirm that live data arrives and alerts behave as expected

If needed later, the `target` folder lock problem can be handled separately as a workspace/environment issue.
