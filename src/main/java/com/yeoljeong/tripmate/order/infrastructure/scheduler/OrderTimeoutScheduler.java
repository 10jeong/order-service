package com.yeoljeong.tripmate.order.infrastructure.scheduler;

import com.yeoljeong.tripmate.order.application.service.command.OrderCommandService;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class OrderTimeoutScheduler {

    private final OrderCommandService commandService;

    // 매 분 0초마다 확인
    @Scheduled(cron = "0 * * * * *")
    public void cancelPaymentTimeoutOrders() {
        commandService.cancelTimeoutOrders();
    }
}
