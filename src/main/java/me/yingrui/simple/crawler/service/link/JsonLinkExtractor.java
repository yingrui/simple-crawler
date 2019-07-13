package me.yingrui.simple.crawler.service.link;

import com.jayway.jsonpath.DocumentContext;
import com.jayway.jsonpath.JsonPath;
import me.yingrui.simple.crawler.dao.WebLinkRepository;
import me.yingrui.simple.crawler.model.CrawlerTask;

import java.util.List;
import java.util.stream.Collectors;

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
        return getLinks(srcPath);
    }

    public List<String> getLinks(String path) {
        return jsonContext.read(path);
    }
}
