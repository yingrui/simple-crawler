package me.yingrui.simple.crawler.service;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import me.yingrui.simple.crawler.configuration.properties.PaginationSettings;
import me.yingrui.simple.crawler.model.CrawlerTask;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.yingrui.simple.crawler.util.TemplateUtils.render;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

public class JsonLinkExtractor implements LinkExtractor {

    private CrawlerTask crawlerTask;

    @Override
    public List<CrawlerTask> extract(CrawlerTask crawlerTask) {
        setCrawlerTask(crawlerTask);
        if (isNotEmpty(crawlerTask.getResponseContent())) {
            DocumentContext jsonContext = JsonPath.parse(crawlerTask.getResponseContent());

            String srcPath = crawlerTask.getLinkExtractorSettings().getPath();
            List<String> srcList = jsonContext.read(srcPath);
            List<CrawlerTask> tasks = srcList.stream()
                    .map(src -> createChildTask(src))
                    .collect(Collectors.toList());

            CrawlerTask nextPage = extractNextPage(jsonContext);
            if (nextPage != null) {
                tasks.add(nextPage);
            }

            return tasks;
        }
        return new ArrayList<>();
    }

    private CrawlerTask extractNextPage(DocumentContext jsonContext) {
        if (crawlerTask.getPaginationSettings() != null) {
            PaginationSettings paginationSettings = crawlerTask.getPaginationSettings();
            String srcPath = paginationSettings.getPath();
            List<String> srcLinks = jsonContext.read(srcPath);
            if (srcLinks.size() == 1) {
                String src = String.valueOf(srcLinks.get(0));
                String url = getUrl(src, paginationSettings.getPrefix());

                Map<String, String> context = getContext(src, url);

                String requestUrl = render(paginationSettings.getUrlTemplate(), context);
                String requestBody = render(paginationSettings.getBodyTemplate(), context);

                CrawlerTask nextPage = new CrawlerTask(url,
                        requestUrl,
                        crawlerTask.getLinkExtractorSettings().getHttpMethod(),
                        crawlerTask.getRequestHeaders(),
                        requestBody,
                        crawlerTask.getLinkExtractorSettings(),
                        paginationSettings);
                return nextPage;
            }
        }
        return null;
    }

    private CrawlerTask createChildTask(String src) {
        String url = getUrl(src, crawlerTask.getLinkExtractorSettings().getPrefix());
        Map<String, String> context = getContext(src, url);

        String requestUrl = getCrawlerTaskUrl(crawlerTask, context);
        String requestBody = getRequestBody(crawlerTask, context);
        Map<String, String> headers = getRequestHeaders(crawlerTask, context);

        CrawlerTask childTask = new CrawlerTask(url, requestUrl,
                crawlerTask.getLinkExtractorSettings().getHttpMethod(),
                headers,
                requestBody,
                crawlerTask.getLinkExtractorSettings(),
                null,
                crawlerTask.getUrl(),
                crawlerTask.getDepth() + 1);

        return childTask;
    }

    private Map<String, String> getRequestHeaders(CrawlerTask crawlerTask, Map<String, String> context) {
        Map<String, String> headers = new HashMap<>();
        for (String name : crawlerTask.getLinkExtractorSettings().getHeaders().keySet()) {
            String template = crawlerTask.getLinkExtractorSettings().getHeaders().get(name);
            String value = render(template, context);
            headers.put(name, value);
        }
        return headers;
    }

    private String getRequestBody(CrawlerTask crawlerTask, Map<String, String> context) {
        String bodyTemplate = crawlerTask.getLinkExtractorSettings().getBodyTemplate();
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

    private Map<String, String> getContext(String src, String url) {
        Map<String, String> context = new HashMap<>();

        context.put("src", src);
        context.put("url", url);
        return context;
    }

    private String getUrl(String src, String prefix) {
        if (isNotEmpty(prefix)) {
            return prefix + src;
        } else {
            return src;
        }
    }

    public CrawlerTask getCrawlerTask() {
        return crawlerTask;
    }

    private void setCrawlerTask(CrawlerTask crawlerTask) {
        this.crawlerTask = crawlerTask;
    }
}
