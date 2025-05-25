package com.hgl.hglaiagent.demo.invoke;

import java.util.Arrays;
import java.util.Collections;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversation;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationParam;
import com.alibaba.dashscope.aigc.multimodalconversation.MultiModalConversationResult;
import com.alibaba.dashscope.common.MultiModalMessage;
import com.alibaba.dashscope.common.Role;
import com.alibaba.dashscope.exception.ApiException;
import com.alibaba.dashscope.exception.NoApiKeyException;
import com.alibaba.dashscope.exception.UploadFileException;
import com.alibaba.dashscope.utils.JsonUtils;
/**
 * @author 请别把我整破防
 * @Desription: 多模态对话示例
 */
public class MultimodalInvoke {
    public static void simpleMultiModalConversationCall() throws ApiException, NoApiKeyException, UploadFileException {
        MultiModalConversation conv = new MultiModalConversation();
        MultiModalMessage userMessage = MultiModalMessage.builder()
                .role(Role.USER.getValue())
                .content(
                        Arrays.asList(
                                Collections.singletonMap("image", "https://dashscope.oss-cn-beijing.aliyuncs.com/images/dog_and_girl.jpeg"),
                                Collections.singletonMap("image", "F:\\图片\\其它\\狗头.jpg"),
                                Collections.singletonMap("image", "F:\\图片\\其它\\猫头.jpg"),
                                Collections.singletonMap("image", "F:\\图片\\Camera Roll\\照片.jpg"),
                                Collections.singletonMap("image", "F:\\图片\\Camera Roll\\证件照.jpg"),
                                Collections.singletonMap("image", "https://dashscope.oss-cn-beijing.aliyuncs.com/images/tiger.png"),
                                Collections.singletonMap("image", "https://dashscope.oss-cn-beijing.aliyuncs.com/images/rabbit.png"),
                                Collections.singletonMap("text", "这些是什么?"),
                                Collections.singletonMap("text", "第四第五张是一个人吗?")
                        )
                )
                .build();
        MultiModalConversationParam param = MultiModalConversationParam.builder()
                // 若没有配置环境变量，请用百炼API Key将下行替换为：.apiKey("sk-xxx")
                .apiKey(TestApiKey.API_KEY)
                // 此处以qwen-vl-plus为例，可按需更换模型名称。模型列表：https://help.aliyun.com/zh/model-studio/getting-started/models
                .model("qwen-vl-plus")
                .message(userMessage)
                .build();
        MultiModalConversationResult result = conv.call(param);
        System.out.println(JsonUtils.toJson(result));
    }

    public static void main(String[] args) {
        try {
            simpleMultiModalConversationCall();
        } catch (ApiException | NoApiKeyException | UploadFileException e) {
            System.out.println(e.getMessage());
        }
        System.exit(0);
    }
}