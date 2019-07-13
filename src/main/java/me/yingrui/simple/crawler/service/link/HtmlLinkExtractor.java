package me.yingrui.simple.crawler.service.link;

import com.google.common.collect.Lists;
import me.yingrui.simple.crawler.dao.WebLinkRepository;
import me.yingrui.simple.crawler.model.CrawlerTask;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.List;
import java.util.stream.Collectors;

public class HtmlLinkExtractor extends AbstractLinkExtractor {

    private Document doc;

    public HtmlLinkExtractor(WebLinkRepository webLinkRepository) {
        this.setWebLinkRepository(webLinkRepository);
    }

    public HtmlLinkExtractor() {
    }

    public void parseContent(CrawlerTask crawlerTask) {
        doc = Jsoup.parse(crawlerTask.getResponseContent());
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
        Elements elements = doc.select(path);

        List<String> hrefList = Lists.newArrayList();
        for (int i = 0; i < elements.size(); i++) {
            Element element = elements.get(i);
            String nodeName = element.nodeName();

            if (nodeName.equals("a")) {
                hrefList.add(element.attr("href"));
            }
        }
        return hrefList;
    }

}
