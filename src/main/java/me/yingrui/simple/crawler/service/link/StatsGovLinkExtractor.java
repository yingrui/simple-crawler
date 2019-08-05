package me.yingrui.simple.crawler.service.link;

import com.jayway.jsonpath.JsonPath;
import joptsimple.internal.Strings;
import me.yingrui.simple.crawler.dao.WebLinkRepository;
import me.yingrui.simple.crawler.model.CrawlerTask;
import me.yingrui.simple.crawler.model.SupportHttpMethod;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static java.util.Collections.emptyList;
import static org.apache.commons.lang.StringUtils.isNotEmpty;


public class StatsGovLinkExtractor extends JsonLinkExtractor {
    public StatsGovLinkExtractor(WebLinkRepository webLinkRepository) {
        this.setWebLinkRepository(webLinkRepository);
    }

    public List<CrawlerTask> extractTasks(CrawlerTask crawlerTask) {
        List<Map> result = emptyList();
        try {
            final List read = JsonPath.read(this.jsonContext.jsonString(), "$[*]");
            if (read.get(0) instanceof Integer) {
                // if the first element is http status code, means there are no sub tree to process.
                return emptyList();
            } else {
                result = read;
            }
        } catch (ClassCastException | UnsupportedOperationException ignore) {
        }

        return result.stream()
                .filter(it -> it.get("isParent").toString() != null)
                .map(parent -> {
                    if (parent.get("isParent").toString().equals("true")) {
                        return handleSubTreeNodes(crawlerTask, parent);
                    } else {
                        return handleLeafs(crawlerTask, parent);
                    }
                }).collect(Collectors.toList());
    }

    private CrawlerTask handleLeafs(CrawlerTask crawlerTask, Map parent) {
        final Map<String, String> keyToValue = Arrays.stream(crawlerTask.getRequestBody().split("&"))
                .map(it -> it.split("="))
                .collect(Collectors.toMap(it -> it[0], it -> it[1]));

        String url = crawlerTask.getLinkExtractorSettings().getUrlTemplate();
        final String dbcode = keyToValue.get("dbcode");
        final String requestUrl = url.replace("${db}", dbcode)
                .replace("${rowcode}", getRowCode(dbcode))
                .replace("${colcode}", "sj")
                .replace("${wds}", URLEncoder.encode(getWds(dbcode, parent)))
                .replace("${dfwds}", URLEncoder.encode(getTimeWd(dbcode, parent)))
                .replace("${k1}", "1563783167237");
        return new CrawlerTask(requestUrl, requestUrl, crawlerTask.getIndexerType(), SupportHttpMethod.GET.toString(), crawlerTask.getLinkExtractorSettings().getHeaders(), null, crawlerTask.getEncode(), crawlerTask.getLinkExtractorSettings(), crawlerTask.getPaginationSettings());
    }

    private String getWds(String dbcode, Map parent) {
        if (dbcode.equals("hgyd") || dbcode.equals("hgjd") || dbcode.equals("hgnd")) {
            return "[]";
        } else {
            return "[{\"wdcode\":\"zb\",\"valuecode\":\"" + parent.get("id") + "\"}]";
        }
    }

    @SuppressWarnings("DuplicateBranchesInSwitch")
    private String getTimeWd(String dbcode, Map parent) {
        switch (dbcode) {
            case "hgyd":
                return "[{\"wdcode\":\"zb\",\"valuecode\":\"" + parent.get("id") + "\"}, {\"wdcode\":\"sj\",\"valuecode\":\"LAST36\"}]";
            case "hgjd":
                return "[{\"wdcode\":\"zb\",\"valuecode\":\"" + parent.get("id") + "\"}, {\"wdcode\":\"sj\",\"valuecode\":\"LAST18\"}]";
            case "hgnd":
                return "[{\"wdcode\":\"zb\",\"valuecode\":\"" + parent.get("id") + "\"}, {\"wdcode\":\"sj\",\"valuecode\":\"LAST20\"}]";
            case "fsyd":
                return "[{\"wdcode\":\"sj\",\"valuecode\":\"LAST36\"}]";
            case "fsjd":
                return "[{\"wdcode\":\"sj\",\"valuecode\":\"LAST18\"}]";
            case "fsnd":
                return "[{\"wdcode\":\"sj\",\"valuecode\":\"LAST20\"}]";
            case "gatyd":
                return "[{\"wdcode\":\"sj\",\"valuecode\":\"LAST36\"}]";
            case "gatnd":
                return "[{\"wdcode\":\"sj\",\"valuecode\":\"LAST20\"}]";
            default:
                return Strings.EMPTY;
        }
    }

    private String getRowCode(String dbcode) {
        if (dbcode.equals("hgyd") || dbcode.equals("hgjd") || dbcode.equals("hgnd")) {
            return "zb";
        } else {
            return "reg";
        }
    }

    private CrawlerTask handleSubTreeNodes(CrawlerTask crawlerTask, Map parent) {
        String dataTemplate = Arrays.stream(crawlerTask.getRequestBody().split("&"))
                .map(it -> {
                    final String[] nameValue = it.split("=");
                    if (nameValue[0].equalsIgnoreCase("id")) {
                        return nameValue[0] + "=" + parent.get("id");
                    } else {
                        return it;
                    }
                }).collect(Collectors.joining("&"));
        return new CrawlerTask(crawlerTask.getUrl(), crawlerTask.getUrl(), crawlerTask.getIndexerType(), SupportHttpMethod.POST.toString(), crawlerTask.getRequestHeaders(), dataTemplate, crawlerTask.getEncode(), crawlerTask.getLinkExtractorSettings(), crawlerTask.getPaginationSettings());
    }

    @Override
    public List<CrawlerTask> extract(CrawlerTask crawlerTask) {
        setCrawlerTask(crawlerTask);
        if (isNotEmpty(crawlerTask.getResponseContent())) {
            parseContent(crawlerTask);

            List<CrawlerTask> tasks = extractTasks(crawlerTask);

            LOGGER.info("new task size: " + tasks.size());
            return tasks;
        }
        return new ArrayList<>();
    }
}
