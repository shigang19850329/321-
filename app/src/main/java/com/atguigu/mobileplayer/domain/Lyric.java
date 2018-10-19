package com.atguigu.mobileplayer.domain;

/**
 * 作者： 石刚
 * QQ号 342532640
 * 作用:歌词类
 *[01:21.35]我在这里寻找
 */
public class Lyric {
    /**
     * 歌词内容
     */
    private String content;
    /**
     * 时间戳
     */
    private long timePoint;
    /**
     * 休眠事件或者高亮显示时间
     */
    private long sleepTime;

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimePoint() {
        return timePoint;
    }

    public void setTimePoint(long timePoint) {
        this.timePoint = timePoint;
    }

    public long getSleepTime() {
        return sleepTime;
    }

    public void setSleepTime(long sleepTime) {
        this.sleepTime = sleepTime;
    }

    @Override
    public String toString() {
        return "Lyric{" +
                "content='" + content + '\'' +
                ", timePoint=" + timePoint +
                ", sleepTime=" + sleepTime +
                '}';
    }
}
