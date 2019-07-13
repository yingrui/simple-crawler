package me.yingrui.simple.crawler.service.link;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import me.yingrui.simple.crawler.configuration.properties.PaginationSettings;
import me.yingrui.simple.crawler.dao.WebLinkRepository;
import me.yingrui.simple.crawler.model.CrawlerTask;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static me.yingrui.simple.crawler.util.TemplateUtils.render;

public class JsonLinkExtractor extends AbstractLinkExtractor {

    private DocumentContext jsonContext;

    public JsonLinkExtractor(WebLinkRepository webLinkRepository) {
        this.setWebLinkRepository(webLinkRepository);
    }

    public JsonLinkExtractor() {
    }

    public void parseContent(CrawlerTask crawlerTask) {
        jsonContext = JsonPath.parse(crawlerTask.getResponseContent());
    }

    public List<CrawlerTask> getCrawlerTasks(CrawlerTask crawlerTask, List<String> srcList) {
        return srcList.stream()
                .map(src -> createChildTask(src))
                .filter(task -> filter(task, crawlerTask.getLinkExtractorSettings()))
                .collect(Collectors.toList());
    }

    public List<String> extractLinks(CrawlerTask crawlerTask) {
        String srcPath = crawlerTask.getLinkExtractorSettings().getPath();
        return jsonContext.read(srcPath);
    }

    public CrawlerTask extractNextPage() {
        if (getCrawlerTask().getPaginationSettings() != null && !getCrawlerTask().getPaginationSettings().isNoPagination()) {
            PaginationSettings paginationSettings = getCrawlerTask().getPaginationSettings();
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
                        getCrawlerTask().getLinkExtractorSettings().getHttpMethod(),
                        getCrawlerTask().getRequestHeaders(),
                        requestBody,
                        getCrawlerTask().getLinkExtractorSettings(),
                        paginationSettings);
                return nextPage;
            }
        }
        return null;
    }


}
