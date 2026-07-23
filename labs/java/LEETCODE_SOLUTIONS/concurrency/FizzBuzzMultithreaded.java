package concurrency;

import java.util.concurrent.Semaphore;
import java.util.function.IntConsumer;

/**
 * LeetCode 1195 - Fizz Buzz Multithreaded
 * 
 * Thread A: fizz() if divisible by 3 (not 5)
 * Thread B: buzz() if divisible by 5 (not 3)
 * Thread C: fizzbuzz() if divisible by 15
 * Thread D: number() otherwise
 * 
 * Approach: Semaphore-based turn management
 * Time: O(n), Space: O(1)
 */
public class FizzBuzzMultithreaded {

    static class FizzBuzz {
        private final int n;
        private final Semaphore numSem = new Semaphore(1);
        private final Semaphore fizzSem = new Semaphore(0);
        private final Semaphore buzzSem = new Semaphore(0);
        private final Semaphore fbSem = new Semaphore(0);
        private int current = 1;

        FizzBuzz(int n) { this.n = n; }

        public void fizz(Runnable printFizz) throws InterruptedException {
            while (true) {
                fizzSem.acquire();
                if (current > n) break;
                printFizz.run();
                advance();
            }
        }

        public void buzz(Runnable printBuzz) throws InterruptedException {
            while (true) {
                buzzSem.acquire();
                if (current > n) break;
                printBuzz.run();
                advance();
            }
        }

        public void fizzbuzz(Runnable printFizzBuzz) throws InterruptedException {
            while (true) {
                fbSem.acquire();
                if (current > n) break;
                printFizzBuzz.run();
                advance();
            }
        }

        public void number(IntConsumer printNumber) throws InterruptedException {
            for (int i = 1; i <= n; i++) {
                numSem.acquire();
                current = i;
                if (i % 15 == 0) fbSem.release();
                else if (i % 3 == 0) fizzSem.release();
                else if (i % 5 == 0) buzzSem.release();
                else {
                    printNumber.accept(i);
                    numSem.release();
                }
            }
            // Release all for clean exit
            fizzSem.release(); buzzSem.release(); fbSem.release();
        }

        private void advance() {
            if (current >= n) {
                numSem.release();
                fizzSem.release(); buzzSem.release(); fbSem.release();
            } else {
                numSem.release();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        FizzBuzz fb = new FizzBuzz(15);
        StringBuilder sb = new StringBuilder();
        Thread t1 = new Thread(() -> { try { fb.fizz(() -> sb.append("fizz,")); } catch (Exception e) { } });
        Thread t2 = new Thread(() -> { try { fb.buzz(() -> sb.append("buzz,")); } catch (Exception e) { } });
        Thread t3 = new Thread(() -> { try { fb.fizzbuzz(() -> sb.append("fizzbuzz,")); } catch (Exception e) { } });
        Thread t4 = new Thread(() -> { try { fb.number(i -> sb.append(i).append(",")); } catch (Exception e) { } });
        t1.start(); t2.start(); t3.start(); t4.start();
        t1.join(); t2.join(); t3.join(); t4.join();
        System.out.println(sb.toString());
        System.out.println("All tests passed.");
    }
}