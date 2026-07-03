# Module 44: Compression & Encoding

## Overview
This module explores how Java handles binary data transformation. You will learn the critical differences between compressing data to save space and encoding data to ensure safe transmission. You will also learn how to defend against severe security vulnerabilities associated with parsing untrusted archives and byte streams.

## Learning Objectives
By the end of this module, you will be able to:
1. Distinguish between Compression (GZIP/ZIP) and Encoding (Base64).
2. Utilize `GZIPOutputStream` and `ZipOutputStream` to compress data efficiently.
3. Understand the necessity of URL-Safe Base64 encoding in modern web APIs (like JWTs).
4. Identify the fatal flaw of relying on the OS default `Charset` and enforce UTF-8 across boundaries.
5. Identify and prevent the Zip Bomb (Decompression Bomb) vulnerability.
6. Identify and prevent the Zip Slip (Directory Traversal) vulnerability.

## Module Contents

### 1. Core Learning
*   [**Deep Dive**](./DEEP_DIVE.md): Comprehensive theory on GZIP, ZIP, Base64, URL-Safe Base64, and Character Encoding.

### 2. Practical Application
*   [**Mini Project**](./MINI_PROJECT.md): Build a Secure Payload Packager that compresses data, encodes it to a URL-safe string, and unpacks it with built-in Zip Bomb protection.
*   [**Edge Cases & Pitfalls**](./EDGE_CASES.md): Learn about default Charset corruption, Zip Slip vulnerabilities, Base64 padding issues, and stream truncation.

### 3. Assessment & Interview Prep
*   [**Quizzes**](./QUIZZES.md): Test your understanding of GZIP vs ZIP, Base64 mechanics, and Charset rules.
*   [**Interview Preparation**](./INTERVIEW_PREP.md): Master common interview questions regarding Zip Bombs, Zip Slips, and why `new String(bytes)` is an anti-pattern.

### 4. Teaching & Learning Support
*   [**Pedagogic Guide**](./PEDAGOGIC_GUIDE.md): Recommended learning paths, teaching strategies, and mental models.

## Prerequisites
*   Solid understanding of basic File I/O and Streams (Module 07).
*   Understanding of Exception Handling and `try-with-resources` (Module 06).