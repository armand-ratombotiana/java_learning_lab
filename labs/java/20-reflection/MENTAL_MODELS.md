# Mental Models — Reflection

## Class as Blueprint
`Class<?>` is the blueprint for an object. You can examine the blueprint (methods, fields) and create instances from it.

## Reflective Call as Office Mail
Direct call: walk to colleague's desk and talk. Reflective call: send a letter addressed to "the person at desk 4 who handles X" — slower, indirect, but flexible.

## setAccessible as Master Key
`field.setAccessible(true)` is using a master key to open a locked door. Powerful but dangerous — you can bypass intended access restrictions.

## Proxy as Screener
`Proxy` wraps the real object like a telephone screener. Every call goes through the screener, who can log, redirect, or block it.

## MethodHandle as Direct Dial
`MethodHandle` is like a phone number you can call directly — faster than `Method.invoke()` because it avoids reflection overhead.
