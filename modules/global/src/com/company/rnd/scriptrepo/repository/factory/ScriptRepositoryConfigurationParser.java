package com.company.rnd.scriptrepo.repository.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.util.ArrayList;
import java.util.List;

public class ScriptRepositoryConfigurationParser implements BeanDefinitionParser {

    private static final Logger log = LoggerFactory.getLogger(ScriptRepositoryConfigurationParser.class);

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) {
        List<String> packagesToScan = getPackagesToScan(element);
        return null;
    }

    private static List<String> getPackagesToScan(Element element){
        log.trace("Reading packages to be scanned to find Script Repositories");
        NodeList elementsByTagName = element.getElementsByTagName(String.format("%sbase-packages", createPrefix(element)));
        Node item = elementsByTagName.item(0);
        NodeList basePackages =  item.getChildNodes();
        int basePackagesCount = basePackages.getLength();
        List<String> result = new ArrayList<>(basePackagesCount);
        for (int i = 0; i < basePackagesCount; i++){
            Node node = basePackages.item(i);
            if (String.format("%sbase-package", createPrefix(element)).equals(node.getNodeName())) {
                log.trace(String.format("Package found: %s content %s", node.getNodeName(), node.getTextContent()));
                result.add(node.getTextContent());
            }
        }
        return result;
    }

    private static String createPrefix(Element element) {
        return StringUtils.isEmpty(element.getPrefix())?"":element.getPrefix()+":";
    }
}
