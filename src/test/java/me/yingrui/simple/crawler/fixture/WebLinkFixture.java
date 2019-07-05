package me.yingrui.simple.crawler.fixture;

import me.yingrui.simple.crawler.model.WebLink;
import org.apache.commons.io.IOUtils;
import org.springframework.util.ResourceUtils;

import java.io.IOException;

public class WebLinkFixture {

    public WebLink stub() {
        try {
            String content = IOUtils.toString(ResourceUtils.getURL("classpath:article.json"));
            WebLink webLink = new WebLink(
                    "https://www.infoq.cn/article/lO41KaRSdjPOEx*oDO8i", 1561898887006L,
                    content, "application/json; charset=utf-8", null, 1);
            return webLink;
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

}
