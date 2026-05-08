package com.yeoljeong.tripmate.order.application.port;

public interface OrderOutboxRecorder {
    void record(String topic, Object event);
}
