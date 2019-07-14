package me.yingrui.simple.crawler.service;

import com.google.common.collect.Lists;
import me.yingrui.simple.crawler.configuration.properties.WrapperSettings;
import me.yingrui.simple.crawler.fixture.WebLinkFixture;
import me.yingrui.simple.crawler.fixture.WrapperTestBase;
import org.junit.Test;

import java.util.List;
import java.util.Map;

import static org.junit.Assert.*;

public class HtmlWrapperTest extends WrapperTestBase {

    @Test
    public void should_get_string_value_from_content() {
        WrapperSettings wrapperSettings = getWrapperSettings(extractorSettings("h1.entry-title", "title"));
        Wrapper wrapper = new Wrapper(wrapperSettings);

        Map<String, Object> result = wrapper.wrap(new WebLinkFixture().stubHtmlContent());

        assertEquals("敏捷测试之借力DSL", result.getOrDefault("title", ""));
    }

    @Test
    public void should_get_list_from_content() {
        WrapperSettings wrapperSettings = getWrapperSettings(extractorSettings("a[rel=tag]", "topic", "Array"));
        Wrapper wrapper = new Wrapper(wrapperSettings);

        Map<String, Object> result = wrapper.wrap(new WebLinkFixture().stubHtmlContent());

        List<String> author = (List<String>) result.getOrDefault("topic", Lists.newArrayList());
        assertEquals(4, author.size());
        assertEquals("敏捷", author.get(0));
    }

    @Test
    public void should_get_author_list_from_content() {
        WrapperSettings wrapperSettings = getWrapperSettings(extractorSettings("a[rel=author] span", "author", "Array"));
        Wrapper wrapper = new Wrapper(wrapperSettings);

        Map<String, Object> result = wrapper.wrap(new WebLinkFixture().stubHtmlContent());

        List<String> author = (List<String>) result.getOrDefault("author", Lists.newArrayList());
        assertEquals(1, author.size());
        assertTrue(author.get(0).startsWith("覃"));
    }

    @Test
    public void should_be_null_if_list_is_empty() {
        WrapperSettings wrapperSettings = getWrapperSettings(extractorSettings("a[rel=not_exist]", "test", "Array"));
        Wrapper wrapper = new Wrapper(wrapperSettings);

        Map<String, Object> result = wrapper.wrap(new WebLinkFixture().stubHtmlContent());

        assertNull(result.get("test"));
    }

    @Test
    public void should_get_datetime_from_content() {
        WrapperSettings wrapperSettings = getWrapperSettings(extractorSettings("time.entry-time", "publish_time", "Datetime", "DatetimeExtractor"));
        Wrapper wrapper = new Wrapper(wrapperSettings);

        Map<String, Object> result = wrapper.wrap(new WebLinkFixture().stubHtmlContent());

        String publishTime = (String) result.getOrDefault("publish_time", "");
        assertEquals("2015-03-19T00:00:00+08:00", publishTime);
    }

    @Test
    public void should_get_content_from_html() {
        WrapperSettings wrapperSettings = getWrapperSettings(extractorSettings("div.entry-content", "content"));
        Wrapper wrapper = new Wrapper(wrapperSettings);

        Map<String, Object> result = wrapper.wrap(new WebLinkFixture().stubHtmlContent());

        String content = (String) result.getOrDefault("content", "");
        assertNotNull(content);
        assertTrue(content.startsWith("随着敏捷越来越广为人知，敏捷测试也更多受到了大家的关注。"));
        assertTrue(content.contains("\n"));
    }

}