package me.yingrui.simple.crawler.service;

import com.google.common.collect.Lists;
import me.yingrui.simple.crawler.configuration.properties.WrapperSettings;
import me.yingrui.simple.crawler.fixture.WebLinkFixture;
import me.yingrui.simple.crawler.fixture.WrapperTestBase;
import me.yingrui.simple.crawler.model.WebLink;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class JsonWrapperTest extends WrapperTestBase {

    @Test
    public void should_convert_web_link_to_map() {
        WebLink webLink = new WebLinkFixture().stubJsonContent();
        Map<String, Object> result = new Wrapper(null).wrap(webLink);
        assertEquals(webLink.getUrl(), result.getOrDefault("url", ""));
        assertEquals(webLink.getWebsite(), result.getOrDefault("website", ""));
        assertEquals(webLink.getDepth(), result.getOrDefault("depth", 0));
        assertNull(result.get("parentUrl"));
    }

    @Test
    public void should_get_string_value_from_content() {
        WrapperSettings wrapperSettings = getWrapperSettings(extractorSettings("data.article_title", "title"));
        Wrapper wrapper = new Wrapper(wrapperSettings);

        Map<String, Object> result = wrapper.wrap(new WebLinkFixture().stubJsonContent());

        assertEquals("想接管别人的整个Java生态系统？只需一次中间人攻击", result.getOrDefault("title", ""));
    }

    @Test
    public void should_get_list_from_content() {
        WrapperSettings wrapperSettings = getWrapperSettings(extractorSettings("data.author[*].nickname", "author", "Array"));
        Wrapper wrapper = new Wrapper(wrapperSettings);

        Map<String, Object> result = wrapper.wrap(new WebLinkFixture().stubJsonContent());

        List<String> author = (List<String>) result.getOrDefault("author", Lists.newArrayList());
        assertEquals(1, author.size());
        assertEquals("Jonathan Leitschuh", author.get(0));
    }

    @Test
    public void should_be_null_if_list_is_empty() {
        WrapperSettings wrapperSettings = getWrapperSettings(extractorSettings("data.nothing[*].nickname", "test", "Array"));
        Wrapper wrapper = new Wrapper(wrapperSettings);

        Map<String, Object> result = wrapper.wrap(new WebLinkFixture().stubJsonContent());

        assertNull(result.get("test"));
    }

    @Test
    public void should_get_datetime_from_content() {
        WrapperSettings wrapperSettings = getWrapperSettings(extractorSettings("data.publish_time", "publish_time", "Datetime", "DatetimeExtractor"));
        Wrapper wrapper = new Wrapper(wrapperSettings);

        Map<String, Object> result = wrapper.wrap(new WebLinkFixture().stubJsonContent());

        String publishTime = (String) result.getOrDefault("publish_time", "");
        assertEquals("2019-06-30T20:48:07+08:00", publishTime);
    }

    @Test
    public void should_get_content_from_html() {
        WrapperSettings wrapperSettings = getWrapperSettings(extractorSettings("data.content", "content", "String", "HtmlContentExtractor"));
        Wrapper wrapper = new Wrapper(wrapperSettings);

        Map<String, Object> result = wrapper.wrap(new WebLinkFixture().stubJsonContent());

        String content = (String) result.getOrDefault("content", "");
        assertNotNull(content);
        assertTrue(content.startsWith("大受欢迎且广泛部署的几百个Java库和JVM编译器"));
    }

}