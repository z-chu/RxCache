package com.zchu.sample;

import java.io.Serializable;
import java.util.List;

/**
 * Created by Chu on 2016/10/25.
 */

public class GankBean implements Serializable{

    /**
     * error : false
     * results : [{"_id":"5808f2a0421aa90e6f21b41e","createdAt":"2016-10-21T00:36:48.978Z","desc":"关于SharedPreference踩的那些坑","publishedAt":"2016-10-25T11:35:01.586Z","source":"web","type":"Android","url":"http://shaohui.me/2016/10/20/%E5%85%B3%E4%BA%8ESharedPreference%E8%B8%A9%E7%9A%84%E9%82%A3%E4%BA%9B%E5%9D%91/","used":true,"who":"邵辉Vista"},{"_id":"5809e222421aa90e799ec1ea","createdAt":"2016-10-21T17:38:42.687Z","desc":"轻巧易用、功能强大且配置灵活的日志封装库","publishedAt":"2016-10-25T11:35:01.586Z","source":"web","type":"Android","url":"https://github.com/Muyangmin/Android-PLog","used":true,"who":"Muyangmin"},{"_id":"580c7807421aa91369f95970","createdAt":"2016-10-23T16:42:47.788Z","desc":"Android Java 程序员开发调试与测试工具，非常实用","images":["http://img.gank.io/cdc8e35d-7601-4790-80f5-8f0d5926a788"],"publishedAt":"2016-10-25T11:35:01.586Z","source":"web","type":"Android","url":"https://github.com/kiruto/debug-bottle","used":true,"who":"yuriel"},{"_id":"580d8e4c421aa913769745a5","createdAt":"2016-10-24T12:30:04.148Z","desc":"有可能是目前最新最全最实用的混淆博客","publishedAt":"2016-10-25T11:35:01.586Z","source":"web","type":"Android","url":"http://mp.weixin.qq.com/s?__biz=MzI4NTQ2OTI4MA==&mid=2247483651&idx=1&sn=85f0d6c6a0f6c4f2ece97429f423c51c&chksm=ebeafe0cdc9d771a31344d0d6861e3b864bfe36d46652770aa522631eb0115a754e1be579d3b#rd","used":true,"who":"zhenghuiy"},{"_id":"580ea1bc421aa91369f9597c","createdAt":"2016-10-25T08:05:16.267Z","desc":"仿 Smartisan OneStep，可以的，速度很快。","images":["http://img.gank.io/531277ff-1433-4914-a1ef-33e5f4f579d7"],"publishedAt":"2016-10-25T11:35:01.586Z","source":"chrome","type":"Android","url":"https://github.com/gavinliu/SimpleOneStep","used":true,"who":"代码家"},{"_id":"580ebea1421aa90e6f21b445","createdAt":"2016-10-25T10:08:33.519Z","desc":"带有徽标(数字，小红点)的按钮","images":["http://img.gank.io/7b9c683f-606e-4c3e-9e0e-358cfff32b74"],"publishedAt":"2016-10-25T11:35:01.586Z","source":"web","type":"Android","url":"https://github.com/czy1121/badgebutton","used":true,"who":"ezy"},{"_id":"5809d8e0421aa90e6f21b429","createdAt":"2016-10-21T16:59:12.407Z","desc":"模仿了 Smartisan OS 的 BigBang 功能","images":["http://img.gank.io/20762814-cf05-486a-90bf-5935381b5be0"],"publishedAt":"2016-10-24T11:25:22.197Z","source":"chrome","type":"Android","url":"https://github.com/baoyongzhang/BigBang","used":true,"who":"onlylemi"},{"_id":"580c53e7421aa90e799ec1f2","createdAt":"2016-10-23T14:08:39.502Z","desc":"通话录音","publishedAt":"2016-10-24T11:25:22.197Z","source":"chrome","type":"Android","url":"https://github.com/aykuttasil/CallRecorder","used":true,"who":"家家~"},{"_id":"580d6b27421aa91369f95973","createdAt":"2016-10-24T10:00:07.831Z","desc":"精准计步器","publishedAt":"2016-10-24T11:25:22.197Z","source":"chrome","type":"Android","url":"https://github.com/linglongxin24/DylanStepCount","used":true,"who":"Jason"},{"_id":"580d796e421aa913769745a2","createdAt":"2016-10-24T11:01:02.155Z","desc":"一个轻量级的dex解析器","publishedAt":"2016-10-24T11:25:22.197Z","source":"web","type":"Android","url":"https://github.com/zjutkz/Dexer","used":true,"who":null}]
     */

    private boolean error;
    /**
     * _id : 5808f2a0421aa90e6f21b41e
     * createdAt : 2016-10-21T00:36:48.978Z
     * desc : 关于SharedPreference踩的那些坑
     * publishedAt : 2016-10-25T11:35:01.586Z
     * source : web
     * type : Android
     * url : http://shaohui.me/2016/10/20/%E5%85%B3%E4%BA%8ESharedPreference%E8%B8%A9%E7%9A%84%E9%82%A3%E4%BA%9B%E5%9D%91/
     * used : true
     * who : 邵辉Vista
     */

    private List<ResultsBean> results;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public List<ResultsBean> getResults() {
        return results;
    }

    public void setResults(List<ResultsBean> results) {
        this.results = results;
    }

    public static class ResultsBean implements Serializable{
        private String _id;
        private String createdAt;
        private String desc;
        private String publishedAt;
        private String source;
        private String type;
        private String url;
        private boolean used;
        private String who;

        public String get_id() {
            return _id;
        }

        public void set_id(String _id) {
            this._id = _id;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getDesc() {
            return desc;
        }

        public void setDesc(String desc) {
            this.desc = desc;
        }

        public String getPublishedAt() {
            return publishedAt;
        }

        public void setPublishedAt(String publishedAt) {
            this.publishedAt = publishedAt;
        }

        public String getSource() {
            return source;
        }

        public void setSource(String source) {
            this.source = source;
        }

        public String getType() {
            return type;
        }

        public void setType(String type) {
            this.type = type;
        }

        public String getUrl() {
            return url;
        }

        public void setUrl(String url) {
            this.url = url;
        }

        public boolean isUsed() {
            return used;
        }

        public void setUsed(boolean used) {
            this.used = used;
        }

        public String getWho() {
            return who;
        }

        public void setWho(String who) {
            this.who = who;
        }
    }
}
