# Quantum Simulation — Mental Models

## Coin Flip in Multiple Dimensions

A qubit is like a coin spinning in the air. Before it lands, it is simultaneously heads and tails (superposition). The probability of heads vs tails depends on how the coin was flipped. A quantum gate is like altering the spin mid-air. Measurement is like catching the coin: the superposition collapses to heads or tails, and you lose information about the spin.

## Quantum Gate as a Mirror Reflection

A quantum gate is a rotation or reflection of the qubit state on the Bloch sphere. Hadamard gate is like rotating 90 degrees around the Y-axis then reflecting through X. Pauli-X (quantum NOT) is like a 180-degree rotation around the X-axis. CNOT is like a conditional swap: if the control qubit is 1, apply X to the target qubit.

## Grover's Algorithm as Amplifying a Whisper

Grover's algorithm is like finding a person in a dark stadium by listening for their whisper. Initially, everyone whispers at the same volume (uniform superposition). The oracle flips the phase of the target person (they suddenly shout instead of whisper). The diffusion operator amplifies the shout while quieting everyone else. After sqrt(N) rounds, the target person is shouting while everyone else is silent.

## Quantum Parallelism as Spinning Many Coins

Quantum parallelism is like spinning many coins simultaneously and having the coins interact while spinning. Classically, you would spin each coin one at a time (sequential) or have n separate spinning coins (parallel quantum simulation). True quantum computation exploits interference between these spinning coins, where the orientation of one coin affects the quantum state of others through entanglement.

## QFT as a Phase Winding

QFT is like placing numbers on a clock face. Each number gets a phase proportional to its position. The QFT operation distributes the amplitude across basis states with carefully controlled phase relationships. Applying QFT and then inverse QFT returns the original state. In the period-finding subroutine of Shor's algorithm, QFT reveals the period by constructive interference: only states corresponding to multiples of the period receive non-zero amplitude.