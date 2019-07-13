package me.yingrui.simple.crawler.service.link;

import me.yingrui.simple.crawler.configuration.properties.LinkExtractorSettings;
import me.yingrui.simple.crawler.configuration.properties.PaginationSettings;
import me.yingrui.simple.crawler.model.CrawlerTask;
import me.yingrui.simple.crawler.service.link.JsonLinkExtractor;
import me.yingrui.simple.crawler.service.link.LinkExtractor;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;

public class JsonLinkExtractorTest {

    String linkPath = "data[*].uuid";
    String prefix = "https://www.infoq.cn/article/";
    String urlTemplate = "https://www.infoq.cn/public/v1/article/getDetail";
    String bodyTemplate = "{\"uuid\":\"[(${src})]\"}";

    @Test
    public void should_extract_link_from_json() {
        Map<String, String> headers = new HashMap<>();
        LinkExtractorSettings linkExtractorSettings = new LinkExtractorSettings(linkPath, prefix, "POST",
                urlTemplate, headers, bodyTemplate);

        CrawlerTask crawlerTask = new CrawlerTask("url", "homepage.html", "POST", defaultHeaders(), "",
                linkExtractorSettings, null);
        crawlerTask.setResponseContent(jsonText);
        crawlerTask.setResponseContentType("application/json; charset=utf-8");

        LinkExtractor linkExtractor = new JsonLinkExtractor();
        List<CrawlerTask> links = linkExtractor.extract(crawlerTask);
        assertEquals(12, links.size());
    }

    @Test
    public void should_extract_child_crawler_task_request() {
        Map<String, String> headers = new HashMap<>();
        headers.put("referer", "[(${url})]");
        headers.put("content-type", "application/json");
        LinkExtractorSettings linkExtractorSettings = new LinkExtractorSettings(linkPath, prefix, "POST",
                urlTemplate, headers, bodyTemplate);

        CrawlerTask crawlerTask = new CrawlerTask("url", "homepage.html", "POST", defaultHeaders(), "",
                linkExtractorSettings, null);
        crawlerTask.setResponseContent(jsonText);
        crawlerTask.setResponseContentType("application/json; charset=utf-8");

        LinkExtractor linkExtractor = new JsonLinkExtractor();
        List<CrawlerTask> links = linkExtractor.extract(crawlerTask);
        CrawlerTask child = links.get(0);
        assertEquals("https://www.infoq.cn/article/hds2GVBZcs6Ezvt7K*M5", child.getUrl());
        assertEquals(urlTemplate, child.getRequestUrl());
        assertEquals("{\"uuid\":\"hds2GVBZcs6Ezvt7K*M5\"}", child.getRequestBody());

        Map<String, String> childRequestHeaders = child.getRequestHeaders();
        assertEquals("https://www.infoq.cn/article/hds2GVBZcs6Ezvt7K*M5", childRequestHeaders.get("referer"));
        assertEquals("application/json", childRequestHeaders.get("content-type"));
    }

    @Test
    public void should_extract_next_page_crawler_task() {
        String startUrl = "homepage.html";
        String nextPagePath = "data[-1:].score";
        String nextPagePathPrefix = "https://www.infoq.cn/?score=";
        String nextPageUrlTemplate = "nextpage-url";
        String nextPageBodyTemplate = "{\"type\": 1, \"size\": 12, \"score\": [(${src})]}";

        LinkExtractorSettings linkExtractorSettings =
                new LinkExtractorSettings(linkPath, prefix, "POST", urlTemplate, new HashMap<>(), bodyTemplate);

        PaginationSettings paginationSettings = new PaginationSettings(nextPagePath, nextPagePathPrefix, nextPageUrlTemplate, nextPageBodyTemplate);
        paginationSettings.setNoPagination(false);

        CrawlerTask crawlerTask = new CrawlerTask("url", startUrl, "POST", defaultHeaders(), "",
                linkExtractorSettings, paginationSettings);
        crawlerTask.setResponseContent(jsonText);
        crawlerTask.setResponseContentType("application/json; charset=utf-8");

        LinkExtractor linkExtractor = new JsonLinkExtractor();
        List<CrawlerTask> links = linkExtractor.extract(crawlerTask);
        assertEquals(13, links.size());

        CrawlerTask lastCrawlerTask = links.get(links.size() - 1);
        assertEquals("https://www.infoq.cn/?score=1561009115698", lastCrawlerTask.getUrl());
        assertEquals(nextPageUrlTemplate, lastCrawlerTask.getRequestUrl());
        assertEquals("{\"type\": 1, \"size\": 12, \"score\": 1561009115698}", lastCrawlerTask.getRequestBody());
    }


    private String jsonText = "{\"code\":0,\"data\":[{\"aid\":23534,\"article_cover\":\"https://static001.infoq.cn/resource/image/7a/61/7aae37de93aa1e995c8c933c610b5361.jpg\",\"article_cover_point\":\"{\\\"big\\\":{\\\"point\\\":{\\\"x\\\":0,\\\"y\\\":286,\\\"w\\\":1920,\\\"h\\\":989}},\\\"small\\\":{\\\"point\\\":{\\\"x\\\":0,\\\"y\\\":0,\\\"w\\\":1723,\\\"h\\\":1279}},\\\"width\\\":1920,\\\"height\\\":1280}\",\"article_sharetitle\":\"由收购引发的思考：为何BI足以让两大巨头砸下百亿美金？\",\"article_subtitle\":\"\",\"article_summary\":\"Looker和Tableau都是BI领域的重要厂商，两大巨头前后两周砸下重金收购，本文综合并采访了多位专家观点，试图说明收购背后的重要原因及中国BI市场的演进过程和发展。\",\"article_title\":\"由收购引发的思考：为何BI足以让两大巨头砸下百亿美金？\",\"author\":[{\"uid\":1359415,\"nickname\":\"赵钰莹\",\"avatar\":\"https://static001.geekbang.org/account/avatar/00/14/be/37/90a4931e.jpg\"}],\"ctime\":1561423205159,\"is_collect\":false,\"is_promotion\":false,\"no_author\":\"\",\"publish_time\":1561423200949,\"score\":1561423200949,\"topic\":[{\"id\":11,\"name\":\"云计算\",\"alias\":\"cloud-computing\"},{\"id\":3,\"name\":\"文化 \\u0026 方法\",\"alias\":\"culture-methods\"},{\"id\":31,\"name\":\"AI\",\"alias\":\"AI\"},{\"id\":19,\"name\":\"Google\",\"alias\":\"google\"},{\"id\":47,\"name\":\"精益\",\"alias\":\"lean\"},{\"id\":64,\"name\":\"设计模式\",\"alias\":\"DesignPattern\"},{\"id\":147,\"name\":\"企业动态\",\"alias\":\" industry news\"},{\"id\":140,\"name\":\"数据可视化\",\"alias\":\"data visualization\"},{\"id\":126,\"name\":\"NLP\",\"alias\":\"Natural Language Processing\"}],\"type\":1,\"utime\":1561423205159,\"uuid\":\"hds2GVBZcs6Ezvt7K*M5\",\"views\":0},{\"aid\":23502,\"article_cover\":\"https://static001.infoq.cn/resource/image/0a/d5/0a0e2c2f0f669cca190687924224c5d5.jpg\",\"article_cover_point\":\"{\\\"big\\\":{\\\"point\\\":{\\\"x\\\":0,\\\"y\\\":183,\\\"w\\\":999,\\\"h\\\":515}},\\\"small\\\":{\\\"point\\\":{\\\"x\\\":59,\\\"y\\\":0,\\\"w\\\":940,\\\"h\\\":698}},\\\"width\\\":1000,\\\"height\\\":699}\",\"article_sharetitle\":\"InfoQ 专访：Akamai IoT Edge Connect将 MQTT 引入其无服务器 Edge 平台\",\"article_subtitle\":\"\",\"article_summary\":\"InfoQ采访了Akamai技术公司物联网副总裁兼首席技术官Lior Netzer。\",\"article_title\":\"InfoQ 专访：Akamai IoT Edge Connect将 MQTT 引入其无服务器 Edge 平台\",\"author\":[{\"uid\":1277174,\"nickname\":\"Sergio De Simone\",\"avatar\":\"\"}],\"ctime\":1561334408370,\"is_collect\":false,\"is_promotion\":false,\"no_author\":\"\",\"publish_time\":1561334400214,\"score\":1561334400214,\"topic\":[{\"id\":11,\"name\":\"云计算\",\"alias\":\"cloud-computing\"},{\"id\":45,\"name\":\"物联网\",\"alias\":\"wulianwang\"},{\"id\":13,\"name\":\"移动\",\"alias\":\"mobile\"},{\"id\":119,\"name\":\"Serverless\",\"alias\":\"Serverless\"}],\"translator\":[{\"uid\":1368875,\"nickname\":\"平川\",\"avatar\":\"\"}],\"type\":1,\"utime\":1561380089518,\"uuid\":\"KlBDDDgSLzeXWc-P36aQ\",\"views\":444},{\"aid\":23523,\"article_cover\":\"https://static001.infoq.cn/resource/image/18/d0/18b15b11f98f54d1e655ac72f6f604d0.jpg\",\"article_cover_point\":\"{\\\"big\\\":{\\\"point\\\":{\\\"x\\\":0,\\\"y\\\":0,\\\"w\\\":5999,\\\"h\\\":3092}},\\\"small\\\":{\\\"point\\\":{\\\"x\\\":1200,\\\"y\\\":0,\\\"w\\\":4544,\\\"h\\\":3375}},\\\"width\\\":6000,\\\"height\\\":3376}\",\"article_sharetitle\":\"Kubernetes 1.15 正式发布，详细解读多项关键特性\",\"article_subtitle\":\"\",\"article_summary\":\"2019年6月20日，Kubernetes重磅发布了1.15版本，不管你是Kubernetes用户，还是IT从业者都不能错过这个版本。\",\"article_title\":\"Kubernetes 1.15 正式发布，详细解读多项关键特性\",\"author\":[{\"uid\":1574456,\"nickname\":\"华为云原生团队\",\"avatar\":\"\"}],\"ctime\":1561267009573,\"is_collect\":false,\"is_promotion\":false,\"no_author\":\"\",\"publish_time\":1561267010228,\"score\":1561267010228,\"topic\":[{\"id\":11,\"name\":\"云计算\",\"alias\":\"cloud-computing\"},{\"id\":106,\"name\":\"云原生\",\"alias\":\"\"},{\"id\":51,\"name\":\"Kubernetes\",\"alias\":\"Kubernetes\"},{\"id\":145,\"name\":\"华为\",\"alias\":\"huawei\"}],\"type\":1,\"utime\":1561293629609,\"uuid\":\"5RqPl94bbh1-CWwgRNvH\",\"views\":409},{\"aid\":23480,\"article_cover\":\"https://static001.infoq.cn/resource/image/61/99/6112ff76d8cc844346a9b602f0279f99.png\",\"article_cover_point\":\"{\\\"big\\\":{\\\"point\\\":{\\\"x\\\":20,\\\"y\\\":38,\\\"w\\\":991,\\\"h\\\":511}},\\\"small\\\":{\\\"point\\\":{\\\"x\\\":127,\\\"y\\\":11,\\\"w\\\":757,\\\"h\\\":562}},\\\"width\\\":1068,\\\"height\\\":600}\",\"article_sharetitle\":\"网商银行×OceanBase：云上银行的分布式数据库应用实践\",\"article_subtitle\":\"\",\"article_summary\":\"本文将带读者深入了解网商银行在金融级分布式数据库 OceanBase 上的应用实践。\",\"article_title\":\"网商银行×OceanBase：云上银行的分布式数据库应用实践\",\"author\":[{\"uid\":1585321,\"nickname\":\"杨祥合\",\"avatar\":\"\"}],\"ctime\":1561248003873,\"is_collect\":false,\"is_promotion\":false,\"no_author\":\"\",\"publish_time\":1561248000738,\"score\":1561248000738,\"topic\":[{\"id\":29,\"name\":\"数据库\",\"alias\":\"Database\"},{\"id\":11,\"name\":\"云计算\",\"alias\":\"cloud-computing\"},{\"id\":159,\"name\":\"金融\",\"alias\":\"bank\"}],\"type\":1,\"utime\":1561248003873,\"uuid\":\"gN1WC22hpuO5zT_JvvDV\",\"views\":0},{\"aid\":23515,\"article_cover\":\"https://static001.infoq.cn/resource/image/b7/4d/b704219e6e3e3a55a2830c54d7601b4d.jpg\",\"article_cover_point\":\"{\\\"big\\\":{\\\"point\\\":{\\\"x\\\":0,\\\"y\\\":0,\\\"w\\\":1920,\\\"h\\\":989}},\\\"small\\\":{\\\"point\\\":{\\\"x\\\":467,\\\"y\\\":0,\\\"w\\\":1452,\\\"h\\\":1078}},\\\"width\\\":1920,\\\"height\\\":1079}\",\"article_sharetitle\":\"国内首例云服务器侵权案二审改判，阿里云胜诉\",\"article_subtitle\":\"\",\"article_summary\":\"国内首例云服务器知识产权侵权案件二审改判，北京知识产权法院驳回一审原告的所有诉讼请求，阿里云公司不承担法律责任。\",\"article_title\":\"国内首例云服务器侵权案二审改判，阿里云胜诉\",\"author\":[{\"uid\":1359415,\"nickname\":\"赵钰莹\",\"avatar\":\"https://static001.geekbang.org/account/avatar/00/14/be/37/90a4931e.jpg\"}],\"ctime\":1561111797099,\"is_collect\":false,\"is_promotion\":false,\"no_author\":\"\",\"publish_time\":1561111796337,\"score\":1561111796337,\"topic\":[{\"id\":3,\"name\":\"文化 \\u0026 方法\",\"alias\":\"culture-methods\"},{\"id\":11,\"name\":\"云计算\",\"alias\":\"cloud-computing\"},{\"id\":106,\"name\":\"云原生\",\"alias\":\"\"},{\"id\":21,\"name\":\"安全\",\"alias\":\"Security\"},{\"id\":61,\"name\":\"阿里巴巴\",\"alias\":\"alibaba\"},{\"id\":74,\"name\":\"阿里云\",\"alias\":\"aliyun\"}],\"type\":1,\"utime\":1561113257271,\"uuid\":\"yle*cO7zS9flA9uDbCYy\",\"views\":0},{\"aid\":23514,\"article_cover\":\"https://static001.infoq.cn/resource/image/93/cb/93bb3ca92155754de4915235b6916acb.jpg\",\"article_cover_point\":\"{\\\"big\\\":{\\\"point\\\":{\\\"x\\\":0,\\\"y\\\":24,\\\"w\\\":1279,\\\"h\\\":659}},\\\"small\\\":{\\\"point\\\":{\\\"x\\\":48,\\\"y\\\":0,\\\"w\\\":1148,\\\"h\\\":852}},\\\"width\\\":1280,\\\"height\\\":853}\",\"article_sharetitle\":\"甲骨文公布Q4财报：大举进军云计算能否助其重回巅峰\",\"article_subtitle\":\"\",\"article_summary\":\"为了刺激营收增长，甲骨文在最近几个季度逐步降低成本，而在云服务方面实际成本则增长了5％。\",\"article_title\":\"甲骨文公布Q4财报：大举进军云计算能否助其重回巅峰\",\"author\":[{\"uid\":1579192,\"nickname\":\"王文婧\",\"avatar\":\"\"}],\"ctime\":1561108615635,\"is_collect\":false,\"is_promotion\":false,\"no_author\":\"\",\"publish_time\":1561108615991,\"score\":1561108615991,\"topic\":[{\"id\":11,\"name\":\"云计算\",\"alias\":\"cloud-computing\"},{\"id\":15,\"name\":\"大数据\",\"alias\":\"bigdata\"},{\"id\":114,\"name\":\"财报\",\"alias\":\"\"},{\"id\":46,\"name\":\"Oracle\",\"alias\":\"oracle\"}],\"type\":1,\"utime\":1561111153964,\"uuid\":\"47D2kR5_zQyy5dWOIORL\",\"views\":0},{\"aid\":23444,\"article_cover\":\"https://static001.infoq.cn/resource/image/c7/55/c75daf4c23f6f7fded6545f8d9986a55.png\",\"article_cover_point\":\"{\\\"big\\\":{\\\"point\\\":{\\\"x\\\":0,\\\"y\\\":44,\\\"w\\\":1169,\\\"h\\\":602}},\\\"small\\\":{\\\"point\\\":{\\\"x\\\":295,\\\"y\\\":0,\\\"w\\\":870,\\\"h\\\":646}},\\\"width\\\":1169,\\\"height\\\":647}\",\"article_sharetitle\":\"深度揭秘腾讯云数据库技术7年变迁史\",\"article_subtitle\":\"\",\"article_summary\":\"从利用开源到定制适配，再到自主研发的腾讯云数据库发展历程技术详解。\",\"article_title\":\"深度揭秘腾讯云数据库技术7年变迁史\",\"author\":[{\"uid\":1583178,\"nickname\":\"孟婧\",\"avatar\":\"\"}],\"ctime\":1560831785773,\"is_collect\":false,\"is_promotion\":false,\"no_author\":\"\",\"publish_time\":1561102715525,\"score\":1561102715525,\"topic\":[{\"id\":11,\"name\":\"云计算\",\"alias\":\"cloud-computing\"},{\"id\":29,\"name\":\"数据库\",\"alias\":\"Database\"},{\"id\":79,\"name\":\"腾讯云\",\"alias\":\"tencent-cloud\"}],\"type\":1,\"utime\":1561102715535,\"uuid\":\"jXea_mT5D1vQTHN85r8K\",\"views\":526},{\"aid\":23491,\"article_cover\":\"https://static001.infoq.cn/resource/image/dc/d0/dc082c2d22e8a9153b5909cf84aef5d0.jpg\",\"article_cover_point\":\"{\\\"big\\\":{\\\"point\\\":{\\\"x\\\":352,\\\"y\\\":0,\\\"w\\\":2649,\\\"h\\\":1365}},\\\"small\\\":{\\\"point\\\":{\\\"x\\\":616,\\\"y\\\":0,\\\"w\\\":1838,\\\"h\\\":1365}},\\\"width\\\":3288,\\\"height\\\":1366}\",\"article_sharetitle\":\"Linux内核被曝TCP “SACK PANIC”漏洞，多家云服务商给出紧急修复建议\",\"article_subtitle\":\"\",\"article_summary\":\"近日，Linux内核发现三个TCP网络处理相关软件缺陷，最严重的漏洞可触发内核崩溃，从而影响系统可用性，多家云服务商给出紧急修复建议。\",\"article_title\":\"Linux内核被曝TCP “SACK PANIC”漏洞，多家云服务商给出紧急修复建议\",\"author\":[{\"uid\":1359415,\"nickname\":\"赵钰莹\",\"avatar\":\"https://static001.geekbang.org/account/avatar/00/14/be/37/90a4931e.jpg\"},{\"uid\":1277259,\"nickname\":\"盖磊\",\"avatar\":\"\"}],\"ctime\":1561080267193,\"is_collect\":false,\"is_promotion\":false,\"no_author\":\"\",\"publish_time\":1561080265314,\"score\":1561080265314,\"topic\":[{\"id\":11,\"name\":\"云计算\",\"alias\":\"cloud-computing\"},{\"id\":17,\"name\":\"开源\",\"alias\":\"opensource\"},{\"id\":8,\"name\":\"架构\",\"alias\":\"architecture\"},{\"id\":21,\"name\":\"安全\",\"alias\":\"Security\"},{\"id\":38,\"name\":\"运维\",\"alias\":\"operation\"},{\"id\":34,\"name\":\"社区\",\"alias\":\"community\"},{\"id\":146,\"name\":\"华为云\",\"alias\":\"huaweicloud\"},{\"id\":91,\"name\":\"RedHat\",\"alias\":\"redhat\"},{\"id\":74,\"name\":\"阿里云\",\"alias\":\"aliyun\"},{\"id\":79,\"name\":\"腾讯云\",\"alias\":\"tencent-cloud\"},{\"id\":44,\"name\":\"Linux\",\"alias\":\"Linux\"},{\"id\":26,\"name\":\"AWS\",\"alias\":\"AWS\"}],\"type\":1,\"utime\":1561081401387,\"uuid\":\"gc3*ZGs49Tj5fHkh9frM\",\"views\":0},{\"aid\":23493,\"article_cover\":\"https://static001.infoq.cn/resource/image/45/4c/45948cd26d8484a11ae7b94787fbee4c.jpg\",\"article_cover_point\":\"{\\\"big\\\":{\\\"point\\\":{\\\"x\\\":375,\\\"y\\\":1064,\\\"w\\\":5384,\\\"h\\\":2775}},\\\"small\\\":{\\\"point\\\":{\\\"x\\\":590,\\\"y\\\":0,\\\"w\\\":5169,\\\"h\\\":3839}},\\\"width\\\":5760,\\\"height\\\":3840}\",\"article_sharetitle\":\"构建面向整合服务网格的开放API\",\"article_subtitle\":\"\",\"article_summary\":\"我们建议使用一个简化的、工作流友好的API来保护组织平台代码不受特定服务网格实现细节的影响。\",\"article_title\":\"构建面向整合服务网格的开放API\",\"author\":[{\"uid\":1280053,\"nickname\":\"Christian Posta\",\"avatar\":\"\"}],\"ctime\":1561079675306,\"is_collect\":false,\"is_promotion\":false,\"no_author\":\"\",\"publish_time\":1561079675342,\"score\":1561079675342,\"topic\":[{\"id\":1,\"name\":\"语言 \\u0026 开发\",\"alias\":\"development\"},{\"id\":8,\"name\":\"架构\",\"alias\":\"architecture\"},{\"id\":11,\"name\":\"云计算\",\"alias\":\"cloud-computing\"},{\"id\":70,\"name\":\"微服务\",\"alias\":\"microservice\"}],\"translator\":[{\"uid\":1540985,\"nickname\":\"刘雅梦\",\"avatar\":\"\"}],\"type\":1,\"utime\":1561079675306,\"uuid\":\"OvkIK26P_J6lobKr13mu\",\"views\":0},{\"aid\":23490,\"article_cover\":\"https://static001.infoq.cn/resource/image/38/06/38beaaf93664b348582ce46c14ad4806.jpg\",\"article_cover_point\":\"{\\\"big\\\":{\\\"point\\\":{\\\"x\\\":0,\\\"y\\\":0,\\\"w\\\":1901,\\\"h\\\":979}},\\\"small\\\":{\\\"point\\\":{\\\"x\\\":0,\\\"y\\\":0,\\\"w\\\":1319,\\\"h\\\":979}},\\\"width\\\":1920,\\\"height\\\":980}\",\"article_sharetitle\":\"Knative 系列（三）：Serving篇\",\"article_subtitle\":\"\",\"article_summary\":\"前一篇[文章](https://www.infoq.cn/article/D489mKhQ96dGwfj*w05E)已经介绍了Build的使用方式和原理，本文将紧接上文介绍Serving。\",\"article_title\":\"Knative 系列（三）：Serving篇\",\"author\":[{\"uid\":1574456,\"nickname\":\"华为云原生团队\",\"avatar\":\"\"}],\"ctime\":1561077604118,\"is_collect\":false,\"is_promotion\":false,\"no_author\":\"\",\"publish_time\":1561077600302,\"score\":1561077600302,\"topic\":[{\"id\":1,\"name\":\"语言 \\u0026 开发\",\"alias\":\"development\"},{\"id\":3,\"name\":\"文化 \\u0026 方法\",\"alias\":\"culture-methods\"},{\"id\":106,\"name\":\"云原生\",\"alias\":\"\"},{\"id\":11,\"name\":\"云计算\",\"alias\":\"cloud-computing\"},{\"id\":17,\"name\":\"开源\",\"alias\":\"opensource\"},{\"id\":36,\"name\":\"最佳实践\",\"alias\":\"best practices\"},{\"id\":145,\"name\":\"华为\",\"alias\":\"huawei\"},{\"id\":146,\"name\":\"华为云\",\"alias\":\"huaweicloud\"},{\"id\":64,\"name\":\"设计模式\",\"alias\":\"DesignPattern\"},{\"id\":119,\"name\":\"Serverless\",\"alias\":\"Serverless\"}],\"type\":1,\"utime\":1561080335213,\"uuid\":\"PYGUCS*yWxhLM5Rok9vF\",\"views\":0},{\"aid\":23474,\"article_cover\":\"https://static001.infoq.cn/resource/image/a4/7a/a468599d3dec0cb8b1c50e8dbd5d7b7a.jpg\",\"article_cover_point\":\"{\\\"big\\\":{\\\"point\\\":{\\\"x\\\":0,\\\"y\\\":607,\\\"w\\\":6015,\\\"h\\\":3100}},\\\"small\\\":{\\\"point\\\":{\\\"x\\\":379,\\\"y\\\":0,\\\"w\\\":5405,\\\"h\\\":4015}},\\\"width\\\":6016,\\\"height\\\":4016}\",\"article_sharetitle\":\"MongoDB推出Atlas Data Lake预览版本，可直接访问Amazon S3\",\"article_subtitle\":\"\",\"article_summary\":\"近日，MongoDB在MongoDB World上宣布了MongoDB Atlas家族的新成员——MongoDB Atlas Data Lake，目前已发布公开测试版本。\",\"article_title\":\"MongoDB推出Atlas Data Lake预览版本，可直接访问Amazon S3\",\"author\":[{\"uid\":1402165,\"nickname\":\"Dj Walker-Morgan\",\"avatar\":\"\"}],\"ctime\":1561014827511,\"is_collect\":false,\"is_promotion\":false,\"no_author\":\"\",\"publish_time\":1561014825174,\"score\":1561014825174,\"topic\":[{\"id\":11,\"name\":\"云计算\",\"alias\":\"cloud-computing\"},{\"id\":29,\"name\":\"数据库\",\"alias\":\"Database\"},{\"id\":1118,\"name\":\"数据处理\",\"alias\":\"data\"},{\"id\":122,\"name\":\"MongoDB\",\"alias\":\"mongodb\"}],\"translator\":[{\"uid\":1510337,\"nickname\":\"田晓旭\",\"avatar\":\"https://static001.geekbang.org/account/avatar/00/17/0b/c1/245671b6.jpg\"}],\"type\":1,\"utime\":1561018250125,\"uuid\":\"olUHYO6SEfsdIySY_SZm\",\"views\":0},{\"aid\":23471,\"article_cover\":\"https://static001.infoq.cn/resource/image/5c/27/5ce81dd7b1f47fa933697592e477cd27.jpg\",\"article_cover_point\":\"{\\\"big\\\":{\\\"point\\\":{\\\"x\\\":0,\\\"y\\\":0,\\\"w\\\":2046,\\\"h\\\":1055}},\\\"small\\\":{\\\"point\\\":{\\\"x\\\":267,\\\"y\\\":0,\\\"w\\\":1554,\\\"h\\\":1154}},\\\"width\\\":2048,\\\"height\\\":1155}\",\"article_sharetitle\":\"Blue Matador 使用 Terraform 从自托管的 Kubernetes 迁移到 AWS EKS\",\"article_subtitle\":\"\",\"article_summary\":\"借助Terraform的自动化设置，Blue Matador将他们自托管的Kubernetes集群迁移到AWS EKS，以利用更好的安全模型、托管控制平面以及降低成本。\",\"article_title\":\"Blue Matador 使用 Terraform 从自托管的 Kubernetes 迁移到 AWS EKS\",\"author\":[{\"uid\":1277258,\"nickname\":\"Hrishikesh Barua\",\"avatar\":\"\"}],\"ctime\":1561009116149,\"is_collect\":false,\"is_promotion\":false,\"no_author\":\"\",\"publish_time\":1561009115698,\"score\":1561009115698,\"topic\":[{\"id\":38,\"name\":\"运维\",\"alias\":\"operation\"},{\"id\":11,\"name\":\"云计算\",\"alias\":\"cloud-computing\"},{\"id\":51,\"name\":\"Kubernetes\",\"alias\":\"Kubernetes\"},{\"id\":26,\"name\":\"AWS\",\"alias\":\"AWS\"}],\"translator\":[{\"uid\":1368875,\"nickname\":\"平川\",\"avatar\":\"\"}],\"type\":1,\"utime\":1561009116149,\"uuid\":\"fwB-DXclbjauLBSpbUH6\",\"views\":0}],\"error\":{},\"extra\":{\"cost\":0.035978975,\"request-id\":\"111a9a9fc19cd7420f61e84f479efbe3@2@infoq\"}}";

    private Map<String, String> defaultHeaders() {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Referer", "https://www.infoq.cn/topic/cloud-computing");
        return headers;
    }
}