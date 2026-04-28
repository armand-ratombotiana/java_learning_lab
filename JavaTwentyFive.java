import java.util.concurrent.StructuredTaskScope;
import java.util.concurrent.StructuredTaskScope.Subtask;

void main() throws Exception {
    try (var scope = new StructuredTaskScope.ShutdownOnFailure()) {
        Subtask<String> nameTask = scope.fork(() -> "Judicael");
        Subtask<Integer> ageTask = scope.fork(() -> 30);

        scope.join();           // Wait for all tasks
        scope.throwIfFailed();  // Throw if any failed

        System.out.println("Name: " + nameTask.get());
        System.out.println("Age: " + ageTask.get());
    }
}