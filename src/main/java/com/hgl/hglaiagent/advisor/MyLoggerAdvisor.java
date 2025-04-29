package com.hgl.hglaiagent.advisor;

import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

/**
 * ClassName: MyAdvisor
 * Package: com.hgl.hglaiagent.advisor
 * Description:
 * 自定义日志 Advisor
 * 打印 info 级别日志、只输出单次用户提示词和 AI 回复的文本
 *
 * @Author HGL
 * @Create: 2025/4/28 16:10
 */
@Slf4j
@Component
public class MyLoggerAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {


    private AdvisedRequest before(AdvisedRequest request) {
        log.info("对AI发起的 Request:{}", request.userText());
        return request;
    }

    private void observeAfter(AdvisedResponse advisedResponse) {
        log.info("AI响应回来的 Response:{}", advisedResponse.response().getResult().getOutput().getText());
    }
    @NotNull
    @Override
    public AdvisedResponse aroundCall(@NotNull AdvisedRequest advisedRequest, CallAroundAdvisorChain chain) {
        AdvisedRequest request = before(advisedRequest);
        AdvisedResponse advisedResponse = chain.nextAroundCall(request);
        observeAfter(advisedResponse);
        return advisedResponse;
    }

    @NotNull
    @Override
    public Flux<AdvisedResponse> aroundStream(@NotNull AdvisedRequest advisedRequest, StreamAroundAdvisorChain chain) {
        AdvisedRequest request = before(advisedRequest);
        Flux<AdvisedResponse> advisedResponseFlux = chain.nextAroundStream(request);
        // 对流式响应进行聚合，并打印日志
        return new MessageAggregator().aggregateAdvisedResponse(advisedResponseFlux,this::observeAfter);
    }

    @NotNull
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 10;
    }
}
