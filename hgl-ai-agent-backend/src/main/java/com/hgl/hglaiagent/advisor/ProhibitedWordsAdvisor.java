package com.hgl.hglaiagent.advisor;

import org.jetbrains.annotations.NotNull;
import org.springframework.ai.chat.client.advisor.api.*;
import org.springframework.ai.chat.model.MessageAggregator;
import reactor.core.publisher.Flux;

/**
 * ClassName: ProhibitedWordsAdvisor
 * Package: com.hgl.hglaiagent.advisor
 * Description:
 *
 * @Author HGL
 * @Create: 2025/4/29 9:15
 * @Desription: 违禁词检查
 */
public class ProhibitedWordsAdvisor implements CallAroundAdvisor, StreamAroundAdvisor {
    private static final String[] PROHIBITED_WORDS = {"色情", "暴力", "政治"," 毒品",  "赌博", "恐怖", "反动"};

    private boolean containsProhibitedWords(String text) {
        for (String word : PROHIBITED_WORDS) {
            if (text.contains(word)) {
                return true;
            }
        }
        return false;
    }

    private AdvisedRequest before(AdvisedRequest request) {
        if (containsProhibitedWords(request.userText())) {
            throw new RuntimeException("请勿使用违禁词");
        }
        return request;
    }

    private void observeAfter(AdvisedResponse advisedResponse) {
        if (containsProhibitedWords(advisedResponse.response().getResult().getOutput().getText())) {
            throw new RuntimeException("请勿使用违禁词");
        }
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
        return new MessageAggregator().aggregateAdvisedResponse(advisedResponseFlux,this::observeAfter);
    }

    @NotNull
    @Override
    public String getName() {
        return this.getClass().getSimpleName();
    }

    @Override
    public int getOrder() {
        return 100;
    }
}
