package com.learning.kotlin

import kotlinx.coroutines.*
import kotlinx.coroutines.flow.*

fun main() = runBlocking {
    println("=== Kotlin Coroutines Lab ===\n")

    // 1. Basic Coroutine
    println("1. Basic Coroutine:")
    launch {
        delay(100)
        println("   Hello from coroutine!")
    }
    delay(50)
    println("   Hello from main!")

    // 2. Async/Await
    println("\n2. Async/Await:")
    val deferred1 = async { fetchData("API-1", 200) }
    val deferred2 = async { fetchData("API-2", 150) }
    val result1 = deferred1.await()
    val result2 = deferred2.await()
    println("   Results: $result1, $result2")

    // 3. Structured Concurrency
    println("\n3. Structured Concurrency:")
    coroutineScope {
        val job1 = launch {
            repeat(3) { i ->
                delay(100)
                println("   Task 1 - iteration $i")
            }
        }
        val job2 = launch {
            repeat(2) { i ->
                delay(150)
                println("   Task 2 - iteration $i")
            }
        }
        job1.join()
        job2.join()
    }
    println("   All tasks completed")

    // 4. Error Handling
    println("\n4. Error Handling:")
    try {
        supervisorScope {
            val job = launch {
                throw RuntimeException("Coroutine failed!")
            }
            job.join()
        }
    } catch (e: Exception) {
        println("   Caught: ${e.message}")
    }

    // 5. Flow (Reactive Stream)
    println("\n5. Flow (Cold Stream):")
    flow {
        for (i in 1..5) {
            delay(100)
            emit(i)
        }
    }
        .map { it * it }
        .filter { it % 2 == 0 }
        .collect { println("   Received: $it") }

    // 6. Channel (Hot Stream)
    println("\n6. Channel (Hot Stream):")
    val channel = Channel<String>()
    launch {
        channel.send("Message 1")
        channel.send("Message 2")
        channel.close()
    }
    for (msg in channel) {
        println("   Received: $msg")
    }

    // 7. Dispatchers
    println("\n7. Dispatchers:")
    launch(Dispatchers.Default) {
        println("   Running on Default dispatcher: ${Thread.currentThread().name}")
    }
    launch(Dispatchers.IO) {
        println("   Running on IO dispatcher: ${Thread.currentThread().name}")
    }
    launch(Dispatchers.Main) {
        // Uncomment on Android: println("Running on Main thread")
        println("   Main dispatcher requires UI environment")
    }.also { it.cancel() }

    // 8. Timeouts
    println("\n8. Timeouts:")
    try {
        withTimeout(100) {
            delay(200)
            println("   This won't execute")
        }
    } catch (e: TimeoutCancellationException) {
        println("   Operation timed out as expected")
    }

    println("\n=== Kotlin Coroutines Lab Complete ===")
}

suspend fun fetchData(name: String, delayMs: Long): String {
    delay(delayMs)
    return "Data from $name"
}
