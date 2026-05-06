package com.learning.java.generics;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatNullPointerException;

/**
 * Comprehensive test suite for Elite Generics Training
 * Covers all generic concepts from basic to advanced
 */
@DisplayName("Elite Generics Training Tests")
class EliteGenericsTrainingTest {

    @Nested
    @DisplayName("Basic Generic Classes")
    class BasicGenericClassesTests {

        @Test
        @DisplayName("Box should store and retrieve String values")
        void boxShouldStoreAndRetrieveString() {
            EliteGenericsTraining.Box<String> box = new EliteGenericsTraining.Box<>();
            box.set("Hello Generics!");
            assertThat(box.get()).isEqualTo("Hello Generics!");
        }

        @Test
        @DisplayName("Box should store and retrieve Integer values")
        void boxShouldStoreAndRetrieveInteger() {
            EliteGenericsTraining.Box<Integer> box = new EliteGenericsTraining.Box<>();
            box.set(42);
            assertThat(box.get()).isEqualTo(42);
        }

        @Test
        @DisplayName("Box should store and retrieve custom objects")
        void boxShouldStoreAndRetrieveCustomObject() {
            EliteGenericsTraining.Box<List<String>> box = new EliteGenericsTraining.Box<>();
            List<String> list = Arrays.asList("a", "b", "c");
            box.set(list);
            assertThat(box.get()).containsExactly("a", "b", "c");
        }

        @Test
        @DisplayName("Box should handle null values")
        void boxShouldHandleNull() {
            EliteGenericsTraining.Box<String> box = new EliteGenericsTraining.Box<>();
            assertThat(box.isEmpty()).isTrue();
            box.set("value");
            assertThat(box.isEmpty()).isFalse();
        }
    }

    @Nested
    @DisplayName("Multiple Type Parameters")
    class MultipleTypeParametersTests {

        @Test
        @DisplayName("Pair should store key and value")
        void pairShouldStoreKeyValue() {
            EliteGenericsTraining.Pair<String, Integer> pair = 
                new EliteGenericsTraining.Pair<>("age", 30);
            assertThat(pair.getKey()).isEqualTo("age");
            assertThat(pair.getValue()).isEqualTo(30);
        }

        @Test
        @DisplayName("Pair should support same types")
        void pairShouldSupportSameTypes() {
            EliteGenericsTraining.Pair<String, String> pair = 
                new EliteGenericsTraining.Pair<>("key", "value");
            assertThat(pair.getKey()).isEqualTo("key");
            assertThat(pair.getValue()).isEqualTo("value");
        }

        @Test
        @DisplayName("Pair should implement equals and hashCode")
        void pairShouldImplementEqualsAndHashCode() {
            EliteGenericsTraining.Pair<String, Integer> pair1 = 
                new EliteGenericsTraining.Pair<>("age", 30);
            EliteGenericsTraining.Pair<String, Integer> pair2 = 
                new EliteGenericsTraining.Pair<>("age", 30);
            EliteGenericsTraining.Pair<String, Integer> pair3 = 
                new EliteGenericsTraining.Pair<>("age", 31);

            assertThat(pair1).isEqualTo(pair2);
            assertThat(pair1).hasSameHashCodeAs(pair2);
            assertThat(pair1).isNotEqualTo(pair3);
        }

        @Test
        @DisplayName("Triple should store three values")
        void tripleShouldStoreThreeValues() {
            EliteGenericsTraining.Triple<String, Integer, Double> triple = 
                new EliteGenericsTraining.Triple<>("John", 25, 5.9);
            assertThat(triple.getFirst()).isEqualTo("John");
            assertThat(triple.getSecond()).isEqualTo(25);
            assertThat(triple.getThird()).isEqualTo(5.9);
        }
    }

    @Nested
    @DisplayName("Bounded Type Parameters")
    class BoundedTypeParametersTests {

        @Test
        @DisplayName("NumberBox should accept Integer")
        void numberBoxShouldAcceptInteger() {
            EliteGenericsTraining.NumberBox<Integer> box = new EliteGenericsTraining.NumberBox<>();
            box.setValue(100);
            assertThat(box.getValue()).isEqualTo(100);
            assertThat(box.asDouble()).isEqualTo(100.0);
        }

        @Test
        @DisplayName("NumberBox should accept Double")
        void numberBoxShouldAcceptDouble() {
            EliteGenericsTraining.NumberBox<Double> box = new EliteGenericsTraining.NumberBox<>();
            box.setValue(3.14);
            assertThat(box.getValue()).isEqualTo(3.14);
            assertThat(box.asDouble()).isEqualTo(3.14);
        }

        @Test
        @DisplayName("NumberBox should provide conversion methods")
        void numberBoxShouldProvideConversionMethods() {
            EliteGenericsTraining.NumberBox<Integer> box = new EliteGenericsTraining.NumberBox<>();
            box.setValue(42);
            assertThat(box.asInt()).isEqualTo(42);
            assertThat(box.asDouble()).isEqualTo(42.0);
        }

        @Test
        @DisplayName("ComparableBox should compare values")
        void comparableBoxShouldCompareValues() {
            EliteGenericsTraining.ComparableBox<String> box1 = new EliteGenericsTraining.ComparableBox<>();
            box1.setValue("apple");
            EliteGenericsTraining.ComparableBox<String> box2 = new EliteGenericsTraining.ComparableBox<>();
            box2.setValue("banana");

            assertThat(box1.compareTo(box2)).isNegative();
        }

        @Test
        @DisplayName("AdvancedNumberBox should compare and sum values")
        void advancedNumberBoxShouldCompareAndSum() {
            EliteGenericsTraining.AdvancedNumberBox<Integer> box1 = 
                new EliteGenericsTraining.AdvancedNumberBox<>(10);
            EliteGenericsTraining.AdvancedNumberBox<Integer> box2 = 
                new EliteGenericsTraining.AdvancedNumberBox<>(20);

            assertThat(box1.isGreaterThan(box2)).isFalse();
            assertThat(box2.isGreaterThan(box1)).isTrue();
            assertThat(box1.sum(box2)).isEqualTo(30.0);
        }
    }

    @Nested
    @DisplayName("Generic Methods")
    class GenericMethodsTests {

        @Test
        @DisplayName("getFirst should return first element")
        void getFirstShouldReturnFirstElement() {
            Integer[] array = {1, 2, 3, 4, 5};
            assertThat(EliteGenericsTraining.GenericUtils.getFirst(array)).isEqualTo(1);
        }

        @Test
        @DisplayName("getLast should return last element")
        void getLastShouldReturnLastElement() {
            Integer[] array = {1, 2, 3, 4, 5};
            assertThat(EliteGenericsTraining.GenericUtils.getLast(array)).isEqualTo(5);
        }

        @Test
        @DisplayName("swap should exchange array elements")
        void swapShouldExchangeElements() {
            Integer[] array = {1, 2, 3, 4, 5};
            EliteGenericsTraining.GenericUtils.swap(array, 1, 3);
            assertThat(array).containsExactly(1, 4, 3, 2, 5);
        }

        @Test
        @DisplayName("reverse should reverse array")
        void reverseShouldReverseArray() {
            Integer[] array = {1, 2, 3, 4, 5};
            EliteGenericsTraining.GenericUtils.reverse(array);
            assertThat(array).containsExactly(5, 4, 3, 2, 1);
        }

        @Test
        @DisplayName("findMax should find maximum element")
        void findMaxShouldFindMaximumElement() {
            Integer[] array = {3, 1, 4, 1, 5, 9, 2, 6};
            assertThat(EliteGenericsTraining.GenericUtils.findMax(array)).isEqualTo(9);
        }

        @Test
        @DisplayName("findMax should work with strings")
        void findMaxShouldWorkWithStrings() {
            String[] array = {"apple", "banana", "cherry"};
            assertThat(EliteGenericsTraining.GenericUtils.findMax(array)).isEqualTo("cherry");
        }

        @Test
        @DisplayName("findMin should find minimum element")
        void findMinShouldFindMinimumElement() {
            Integer[] array = {3, 1, 4, 1, 5, 9, 2, 6};
            assertThat(EliteGenericsTraining.GenericUtils.findMin(array)).isEqualTo(1);
        }

        @Test
        @DisplayName("sumNumbers should sum integer array")
        void sumNumbersShouldSumIntegerArray() {
            Integer[] array = {1, 2, 3, 4, 5};
            assertThat(EliteGenericsTraining.GenericUtils.sumNumbers(array)).isEqualTo(15.0);
        }

        @Test
        @DisplayName("sumNumbers should sum double array")
        void sumNumbersShouldSumDoubleArray() {
            Double[] array = {1.5, 2.5, 3.5};
            assertThat(EliteGenericsTraining.GenericUtils.sumNumbers(array)).isEqualTo(7.5);
        }

        @Test
        @DisplayName("countOccurrences should count element occurrences")
        void countOccurrencesShouldCountElementOccurrences() {
            Integer[] array = {1, 2, 2, 3, 2, 4};
            assertThat(EliteGenericsTraining.GenericUtils.countOccurrences(array, 2)).isEqualTo(3);
        }

        @Test
        @DisplayName("contains should find element in array")
        void containsShouldFindElementInArray() {
            Integer[] array = {1, 2, 3, 4, 5};
            assertThat(EliteGenericsTraining.GenericUtils.contains(array, 3)).isTrue();
            assertThat(EliteGenericsTraining.GenericUtils.contains(array, 6)).isFalse();
        }

        @Test
        @DisplayName("toList should convert array to list")
        void toListShouldConvertArrayToList() {
            Integer[] array = {1, 2, 3, 4, 5};
            List<Integer> list = EliteGenericsTraining.GenericUtils.toList(array);
            assertThat(list).containsExactly(1, 2, 3, 4, 5);
        }

        @Test
        @DisplayName("createPair should create Pair object")
        void createPairShouldCreatePairObject() {
            EliteGenericsTraining.Pair<String, Integer> pair = 
                EliteGenericsTraining.GenericUtils.createPair("key", 42);
            assertThat(pair.getKey()).isEqualTo("key");
            assertThat(pair.getValue()).isEqualTo(42);
        }
    }

    @Nested
    @DisplayName("Wildcards and Variance")
    class WildcardsAndVarianceTests {

        @Test
        @DisplayName("sumOfNumbers should sum integer list")
        void sumOfNumbersShouldSumIntegerList() {
            List<Integer> list = Arrays.asList(1, 2, 3, 4, 5);
            assertThat(EliteGenericsTraining.WildcardUtils.sumOfNumbers(list)).isEqualTo(15.0);
        }

        @Test
        @DisplayName("sumOfNumbers should sum double list")
        void sumOfNumbersShouldSumDoubleList() {
            List<Double> list = Arrays.asList(1.5, 2.5, 3.5);
            assertThat(EliteGenericsTraining.WildcardUtils.sumOfNumbers(list)).isEqualTo(7.5);
        }

        @Test
        @DisplayName("addNumbers should add integers to list")
        void addNumbersShouldAddIntegersToList() {
            List<Number> list = new ArrayList<>();
            EliteGenericsTraining.WildcardUtils.addNumbers(list);
            assertThat(list).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("copy should copy elements from source to destination")
        void copyShouldCopyElements() {
            List<Integer> source = Arrays.asList(1, 2, 3);
            List<Number> dest = new ArrayList<>();
            EliteGenericsTraining.WildcardUtils.copy(source, dest);
            assertThat(dest).containsExactly(1, 2, 3);
        }

        @Test
        @DisplayName("getSize should return list size")
        void getSizeShouldReturnListSize() {
            List<String> list = Arrays.asList("a", "b", "c");
            assertThat(EliteGenericsTraining.WildcardUtils.getSize(list)).isEqualTo(3);
        }

        @Test
        @DisplayName("max should find maximum in comparable list")
        void maxShouldFindMaximumInComparableList() {
            List<Integer> list = Arrays.asList(3, 1, 4, 1, 5, 9, 2, 6);
            assertThat(EliteGenericsTraining.WildcardUtils.max(list)).isEqualTo(9);
        }
    }

    @Nested
    @DisplayName("Generic Stack")
    class GenericStackTests {

        @Test
        @DisplayName("Stack should push and pop elements")
        void stackShouldPushAndPopElements() {
            EliteGenericsTraining.Stack<String> stack = new EliteGenericsTraining.Stack<>();
            stack.push("First");
            stack.push("Second");
            stack.push("Third");

            assertThat(stack.pop()).isEqualTo("Third");
            assertThat(stack.pop()).isEqualTo("Second");
            assertThat(stack.pop()).isEqualTo("First");
        }

        @Test
        @DisplayName("Stack should peek without removing")
        void stackShouldPeekWithoutRemoving() {
            EliteGenericsTraining.Stack<Integer> stack = new EliteGenericsTraining.Stack<>();
            stack.push(1);
            stack.push(2);
            stack.push(3);

            assertThat(stack.peek()).isEqualTo(3);
            assertThat(stack.size()).isEqualTo(3);
        }

        @Test
        @DisplayName("Stack should return null when empty")
        void stackShouldReturnNullWhenEmpty() {
            EliteGenericsTraining.Stack<String> stack = new EliteGenericsTraining.Stack<>();
            assertThat(stack.pop()).isNull();
            assertThat(stack.peek()).isNull();
        }

        @Test
        @DisplayName("Stack should clear all elements")
        void stackShouldClearAllElements() {
            EliteGenericsTraining.Stack<String> stack = new EliteGenericsTraining.Stack<>();
            stack.push("a");
            stack.push("b");
            stack.clear();
            assertThat(stack.isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("Generic Queue")
    class GenericQueueTests {

        @Test
        @DisplayName("Queue should enqueue and dequeue elements")
        void queueShouldEnqueueAndDequeueElements() {
            EliteGenericsTraining.Queue<String> queue = new EliteGenericsTraining.Queue<>();
            queue.enqueue("First");
            queue.enqueue("Second");
            queue.enqueue("Third");

            assertThat(queue.dequeue()).isEqualTo("First");
            assertThat(queue.dequeue()).isEqualTo("Second");
            assertThat(queue.dequeue()).isEqualTo("Third");
        }

        @Test
        @DisplayName("Queue should follow FIFO order")
        void queueShouldFollowFIFOOrder() {
            EliteGenericsTraining.Queue<Integer> queue = new EliteGenericsTraining.Queue<>();
            queue.enqueue(1);
            queue.enqueue(2);
            queue.enqueue(3);

            assertThat(queue.dequeue()).isEqualTo(1);
            assertThat(queue.dequeue()).isEqualTo(2);
            assertThat(queue.dequeue()).isEqualTo(3);
        }

        @Test
        @DisplayName("Queue should peek at front element")
        void queueShouldPeekAtFrontElement() {
            EliteGenericsTraining.Queue<String> queue = new EliteGenericsTraining.Queue<>();
            queue.enqueue("First");
            queue.enqueue("Second");

            assertThat(queue.peek()).isEqualTo("First");
            assertThat(queue.size()).isEqualTo(2);
        }
    }

    @Nested
    @DisplayName("Generic Cache")
    class GenericCacheTests {

        @Test
        @DisplayName("Cache should store and retrieve values")
        void cacheShouldStoreAndRetrieveValues() {
            EliteGenericsTraining.Cache<String, Integer> cache = new EliteGenericsTraining.Cache<>();
            cache.put("one", 1);
            cache.put("two", 2);

            assertThat(cache.get("one")).isEqualTo(1);
            assertThat(cache.get("two")).isEqualTo(2);
        }

        @Test
        @DisplayName("Cache should compute if absent")
        void cacheShouldComputeIfAbsent() {
            EliteGenericsTraining.Cache<String, Integer> cache = new EliteGenericsTraining.Cache<>();
            cache.put("one", 1);

            Integer value = cache.getOrCompute("two", k -> 2);
            assertThat(value).isEqualTo(2);
            assertThat(cache.get("two")).isEqualTo(2);
        }

        @Test
        @DisplayName("Cache should contain key")
        void cacheShouldContainKey() {
            EliteGenericsTraining.Cache<String, String> cache = new EliteGenericsTraining.Cache<>();
            cache.put("key", "value");

            assertThat(cache.contains("key")).isTrue();
            assertThat(cache.contains("missing")).isFalse();
        }

        @Test
        @DisplayName("Cache should remove keys")
        void cacheShouldRemoveKeys() {
            EliteGenericsTraining.Cache<String, Integer> cache = new EliteGenericsTraining.Cache<>();
            cache.put("one", 1);
            cache.put("two", 2);

            cache.remove("one");
            assertThat(cache.contains("one")).isFalse();
            assertThat(cache.contains("two")).isTrue();
        }
    }

    @Nested
    @DisplayName("Generic Builder Pattern")
    class GenericBuilderPatternTests {

        @Test
        @DisplayName("Builder should transform values")
        void builderShouldTransformValues() {
            String result = new EliteGenericsTraining.GenericBuilder<>("hello")
                    .transform(String::toUpperCase)
                    .transform(s -> s + " WORLD!")
                    .build();

            assertThat(result).isEqualTo("HELLO WORLD!");
        }

        @Test
        @DisplayName("Builder should transform between types")
        void builderShouldTransformBetweenTypes() {
            Integer result = new EliteGenericsTraining.GenericBuilder<>("42")
                    .transform(Integer::parseInt)
                    .transform(n -> n * 2)
                    .build();

            assertThat(result).isEqualTo(84);
        }
    }

    @Nested
    @DisplayName("Generic Validator")
    class GenericValidatorTests {

        @Test
        @DisplayName("Validator should validate with all rules passing")
        void validatorShouldValidateWithAllRulesPassing() {
            EliteGenericsTraining.Validator<Integer> validator = new EliteGenericsTraining.Validator<Integer>()
                    .addRule(n -> n != null)
                    .addRule(n -> n > 0)
                    .addRule(n -> n < 100);

            assertThat(validator.validate(50)).isTrue();
        }

        @Test
        @DisplayName("Validator should fail when rule violated")
        void validatorShouldFailWhenRuleViolated() {
            EliteGenericsTraining.Validator<Integer> validator = new EliteGenericsTraining.Validator<Integer>()
                    .addRule(n -> n != null)
                    .addRule(n -> n > 0)
                    .addRule(n -> n < 100);

            assertThat(validator.validate(150)).isFalse();
            assertThat(validator.validate(-5)).isFalse();
        }

        @Test
        @DisplayName("Validator should return error messages")
        void validatorShouldReturnErrorMessages() {
            EliteGenericsTraining.Validator<Integer> validator = new EliteGenericsTraining.Validator<Integer>()
                    .addRule(n -> n != null)
                    .addRule(n -> n > 0);

            List<String> errors = validator.getErrors(-5, 
                    Arrays.asList("Value cannot be null", "Value must be positive"));
            assertThat(errors.size()).isEqualTo(1);
            assertThat(errors.get(0)).isEqualTo("Value must be positive");
        }
    }

    @Nested
    @DisplayName("Edge Cases and Error Handling")
    class EdgeCasesTests {

        @Test
        @DisplayName("Generic methods should handle null arrays")
        void genericMethodsShouldHandleNullArrays() {
            assertThat((Object) EliteGenericsTraining.GenericUtils.getFirst(null)).isNull();
            assertThat((Object) EliteGenericsTraining.GenericUtils.getLast(null)).isNull();
        }

        @Test
        @DisplayName("Generic methods should handle empty arrays")
        void genericMethodsShouldHandleEmptyArrays() {
            Integer[] empty = {};
            assertThat((Object) EliteGenericsTraining.GenericUtils.getFirst(empty)).isNull();
            assertThat((Object) EliteGenericsTraining.GenericUtils.findMax(empty)).isNull();
        }

        @Test
        @DisplayName("Stack should handle multiple operations")
        void stackShouldHandleMultipleOperations() {
            EliteGenericsTraining.Stack<Integer> stack = new EliteGenericsTraining.Stack<>();
            
            for (int i = 1; i <= 10; i++) {
                stack.push(i);
            }

            assertThat(stack.size()).isEqualTo(10);

            for (int i = 10; i >= 1; i--) {
                assertThat(stack.pop()).isEqualTo(i);
            }

            assertThat(stack.isEmpty()).isTrue();
        }

        @Test
        @DisplayName("Queue should handle multiple operations")
        void queueShouldHandleMultipleOperations() {
            EliteGenericsTraining.Queue<String> queue = new EliteGenericsTraining.Queue<>();
            
            String[] items = {"a", "b", "c", "d", "e"};
            for (String item : items) {
                queue.enqueue(item);
            }

            assertThat(queue.size()).isEqualTo(5);

            for (int i = 0; i < items.length; i++) {
                assertThat(queue.dequeue()).isEqualTo(items[i]);
            }

            assertThat(queue.isEmpty()).isTrue();
        }
    }

    @Nested
    @DisplayName("Real-World Scenarios")
    class RealWorldScenariosTests {

        @Test
        @DisplayName("Repository pattern with generics")
        void repositoryPatternWithGenerics() {
            EliteGenericsTraining.Cache<String, String> repository = new EliteGenericsTraining.Cache<>();
            
            repository.put("user:1", "Alice");
            repository.put("user:2", "Bob");
            repository.put("user:3", "Charlie");

            assertThat(repository.get("user:1")).isEqualTo("Alice");
            assertThat(repository.get("user:2")).isEqualTo("Bob");
            assertThat(repository.contains("user:3")).isTrue();
        }

        @Test
        @DisplayName("Builder pattern for data transformation")
        void builderPatternForDataTransformation() {
            List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
            
            String result = new EliteGenericsTraining.GenericBuilder<>(numbers)
                    .transform(nums -> nums.stream().mapToInt(n -> n).sum())
                    .transform(sum -> "Sum: " + sum)
                    .build();

            assertThat(result).isEqualTo("Sum: 15");
        }

        @Test
        @DisplayName("Validation chain for input validation")
        void validationChainForInputValidation() {
            EliteGenericsTraining.Validator<String> emailValidator = new EliteGenericsTraining.Validator<String>()
                    .addRule(s -> s != null)
                    .addRule(s -> !s.isEmpty())
                    .addRule(s -> s.contains("@"))
                    .addRule(s -> s.contains("."));

            assertThat(emailValidator.validate("test@example.com")).isTrue();
            assertThat(emailValidator.validate("invalid")).isFalse();
            assertThat(emailValidator.validate("")).isFalse();
        }
    }
}
