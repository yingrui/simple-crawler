package me.yingrui.simple.crawler.service;

import me.yingrui.simple.crawler.configuration.properties.LinkExtractorSettings;
import me.yingrui.simple.crawler.dao.WebLinkRepository;
import me.yingrui.simple.crawler.model.CrawlerTask;
import me.yingrui.simple.crawler.model.WebLink;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static me.yingrui.simple.crawler.util.TemplateUtils.render;
import static org.apache.commons.lang.StringUtils.isNotEmpty;

public abstract class AbstractLinkExtractor implements LinkExtractor {

    protected static final Logger LOGGER = LoggerFactory.getLogger(LinkExtractor.class);
    private WebLinkRepository webLinkRepository = null;
    private CrawlerTask crawlerTask;

    @Override
    public List<CrawlerTask> extract(CrawlerTask crawlerTask) {
        setCrawlerTask(crawlerTask);
        if (isNotEmpty(crawlerTask.getResponseContent())) {
            parseContent(crawlerTask);

            List<String> links = extractLinks(crawlerTask);
            List<CrawlerTask> tasks = getCrawlerTasks(crawlerTask, links);

            LOGGER.info("links size: " + links.size() + " new task size: " + tasks.size());

            if (skipPagination(crawlerTask, links, tasks)) {
                LOGGER.info("skip pagination");
            } else {
                CrawlerTask nextPage = extractNextPage();
                if (nextPage != null) {
                    tasks.add(nextPage);
                }
            }
            return tasks;
        }
        return new ArrayList<>();
    }

    public abstract void parseContent(CrawlerTask crawlerTask);

    public abstract List<String> extractLinks(CrawlerTask crawlerTask);

    public abstract List<CrawlerTask> getCrawlerTasks(CrawlerTask crawlerTask, List<String> srcList);

    public abstract CrawlerTask extractNextPage();

    public WebLinkRepository getWebLinkRepository() {
        return webLinkRepository;
    }

    public void setWebLinkRepository(WebLinkRepository webLinkRepository) {
        this.webLinkRepository = webLinkRepository;
    }

    protected boolean filter(CrawlerTask task, LinkExtractorSettings linkExtractorSettings) {
        if (linkExtractorSettings.isNewLinksOnly() && getWebLinkRepository() != null) {
            WebLink webLink = task.toWebLink();
            boolean exists = getWebLinkRepository().exists(webLink.getRowKey());
            return !exists;
        } else {
            return true;
        }
    }

    public CrawlerTask getCrawlerTask() {
        return crawlerTask;
    }

    public void setCrawlerTask(CrawlerTask crawlerTask) {
        this.crawlerTask = crawlerTask;
    }

    public boolean skipPagination(CrawlerTask crawlerTask, List<String> links, List<CrawlerTask> tasks) {
        return links.size() > 0 && tasks.size() == 0 && crawlerTask.getPaginationSettings().isStopWhenAllLinksCrawled();
    }

    public CrawlerTask createChildTask(String src) {
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

    protected Map<String, String> getContext(String src, String url) {
        Map<String, String> context = new HashMap<>();

        context.put("src", src);
        context.put("url", url);
        return context;
    }

    protected String getUrl(String src, String prefix) {
        if (isNotEmpty(prefix)) {
            return prefix + src;
        } else {
            return src;
        }
    }
}
