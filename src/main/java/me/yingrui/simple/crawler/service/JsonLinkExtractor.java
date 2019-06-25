package me.yingrui.simple.crawler.service;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import me.yingrui.simple.crawler.model.CrawlerTask;
import me.yingrui.simple.crawler.util.TemplateUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.yingrui.simple.crawler.util.TemplateUtils.render;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

public class JsonLinkExtractor implements LinkExtractor {

    @Override
    public List<CrawlerTask> extract(CrawlerTask crawlerTask) {
        if (isNotEmpty(crawlerTask.getResponseContent())) {
            DocumentContext jsonContext = JsonPath.parse(crawlerTask.getResponseContent());

            String linkPath = crawlerTask.getLinkExtractorSettings().getPath();
            List<String> srcList = jsonContext.read(linkPath);
            List<CrawlerTask> urls = srcList.stream()
                    .map(src -> createChildTask(crawlerTask, src))
                    .collect(Collectors.toList());
            return urls;
        }
        return new ArrayList<>();
    }


    private CrawlerTask createChildTask(CrawlerTask crawlerTask, String src) {
        Map<String, String> context = getContext(crawlerTask, src);

        String url = getCrawlerTaskUrl(crawlerTask, context);
        String requestBody = getRequestBody(crawlerTask, context);
        Map<String, String> headers = getRequestHeaders(crawlerTask, context);

        CrawlerTask childTask = new CrawlerTask(url,
                crawlerTask.getLinkExtractorSettings().getHttpMethod(),
                headers,
                requestBody,
                crawlerTask.getLinkExtractorSettings(),
                crawlerTask.getUrl(),
                crawlerTask.getDepth() + 1);

        return childTask;
    }

    private Map<String, String> getRequestHeaders(CrawlerTask crawlerTask, Map<String, String> context) {
        Map<String, String> headers = new HashMap<>();
        for (String name: crawlerTask.getLinkExtractorSettings().getHeaders().keySet()) {
            String template = crawlerTask.getLinkExtractorSettings().getHeaders().get(name);
            String value = render(template, context);
            headers.put(name, value);
        }
        return headers;
    }

    private String getRequestBody(CrawlerTask crawlerTask, Map<String, String> context) {
        String bodyTemplate= crawlerTask.getLinkExtractorSettings().getBodyTemplate();
        if (isNotEmpty(bodyTemplate)) {
            return render(bodyTemplate, context);
        }
        return "";
    }

    private String getCrawlerTaskUrl(CrawlerTask crawlerTask, Map<String, String> context) {
        String urlTemplate = crawlerTask.getLinkExtractorSettings().getUrlTemplate();
        if (isNotEmpty(urlTemplate)) {
            return render(urlTemplate, context);
        }
        return context.get("url");
    }

    private Map<String, String> getContext(CrawlerTask crawlerTask, String src) {
        Map<String, String> context = new HashMap<>();

        context.put("src", src);
        String url = getUrl(crawlerTask, src);
        context.put("url", url);
        return context;
    }

    private String getUrl(CrawlerTask crawlerTask, String src) {
        if (isNotEmpty(crawlerTask.getLinkExtractorSettings().getPrefix())) {
            return crawlerTask.getLinkExtractorSettings().getPrefix() + src;
        } else {
            return src;
        }
    }
}
