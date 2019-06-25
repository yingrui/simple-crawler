package me.yingrui.simple.crawler.util;

import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;
import org.thymeleaf.templatemode.TemplateMode;
import org.thymeleaf.templateresolver.StringTemplateResolver;

import java.util.Map;

public class TemplateUtils {

    private static TemplateEngine templateEngine = createTemplateEngine();

    private static TemplateEngine createTemplateEngine() {
        TemplateEngine templateEngine = new TemplateEngine();
        StringTemplateResolver templateResolver = new StringTemplateResolver();
        templateResolver.setTemplateMode(TemplateMode.TEXT);
        templateEngine.addTemplateResolver(templateResolver);
        return templateEngine;
    }

    public static String render(String template, Map<String, String> contextMap) {
        Context context = new Context();
        for (String name : contextMap.keySet()) {
            context.setVariable(name, contextMap.get(name));
        }
        return templateEngine.process(template, context);
    }

}
