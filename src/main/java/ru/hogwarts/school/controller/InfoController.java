package ru.hogwarts.school.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.LongStream;
import java.util.stream.Stream;

@RestController
public class InfoController {

    private static final Logger logger = LoggerFactory.getLogger(InfoController.class);

    @Value("${server.port}")
    private int serverPort;

    @GetMapping("/port")
    public int getServerPort() {
        return serverPort;
    }

    @GetMapping("/sum")
    public long computeSum(@RequestParam(defaultValue = "range") String method) {
        int parallelism = ForkJoinPool.commonPool().getParallelism();
        logger.debug("Number of threads in common ForkJoinPool: {}", parallelism);

        final long n = 1_000_000L;
        long start = System.nanoTime();
        long sum;

        if ("formula".equalsIgnoreCase(method)) {
            sum = n * (n + 1) / 2;
        } else if ("iterate".equalsIgnoreCase(method)) {
            sum = Stream.iterate(1L, a -> a + 1)
                    .limit(n)
                    .reduce(0L, Long::sum);
        } else {
            sum = LongStream.rangeClosed(1, n)
                    .parallel()
                    .reduce(0L, Long::sum);
        }

        long end = System.nanoTime();
        long durationMs = TimeUnit.NANOSECONDS.toMillis(end - start);
        logger.debug("Время выполнения метода computeSum с использованием '{}' метода: {} мс", method, durationMs);

        return sum;
    }
}
