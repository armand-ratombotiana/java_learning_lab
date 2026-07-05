package com.arch.cqrs;

public interface CommandHandler<T extends Command, R> {
    R handle(T command) throws Exception;
}
