package com.hgl.hglaiagent.tools;

import cn.hutool.core.date.DateUtil;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import java.util.Date;

/**
 * ClassName: TimeUtil
 * Package: com.hgl.hglaiagent.tools
 * Description: 时间工具类，用于获取当前时间、格式化时间等操作
 *
 * @Author HGL
 * @Create: 2025/5/14 14:46
 */
public class TimeUtilTool {

    @Tool(description = "获取当前时间并格式化输出")
    public String getCurrentTime(@ToolParam(description = "时间格式，默认为yyyy-MM-dd HH:mm:ss") String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }

        String currentTime = DateUtil.format(new Date(), format);
        return "当前时间是：" + currentTime;
    }

    @Tool(description = "获取当前时间戳（毫秒）")
    public String getCurrentTimestamp() {
        long timestamp = System.currentTimeMillis();
        return "当前时间戳（毫秒）：" + timestamp;
    }

    @Tool(description = "将时间戳转换为格式化的时间字符串")
    public String convertTimestampToDate(@ToolParam(description = "时间戳（毫秒）") long timestamp, @ToolParam(description = "目标格式，默认为yyyy-MM-dd HH:mm:ss") String format) {
        if (format == null || format.isEmpty()) {
            format = "yyyy-MM-dd HH:mm:ss";
        }

        String formattedDate = DateUtil.format(DateUtil.date(timestamp), format);
        return "转换后的时间是：" + formattedDate;
    }
}
