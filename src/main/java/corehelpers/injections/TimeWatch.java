package corehelpers.injections;

import testbody.skoda.MySkodaHeater;

import java.util.concurrent.TimeUnit;

public class TimeWatch {
    long starts;

    public void start() {
        reset();
    }

    public void reset() {
        starts = System.currentTimeMillis();
    }

    public long time() {
        long ends = System.currentTimeMillis();
        return ends - starts;
    }

    public long time(TimeUnit unit) {
        return unit.convert(time(), TimeUnit.MILLISECONDS);
    }
}