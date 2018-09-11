package com.company.rnd.scriptrepo.repository.factory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.BeanCreationException;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.support.AbstractBeanDefinition;
import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.xml.BeanDefinitionParser;
import org.springframework.beans.factory.xml.ParserContext;
import org.springframework.util.StringUtils;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScriptRepositoryConfigurationParser implements BeanDefinitionParser {

    private static final Logger log = LoggerFactory.getLogger(ScriptRepositoryConfigurationParser.class);

    @Override
    public BeanDefinition parse(Element element, ParserContext parserContext) throws BeanCreationException {
        List<String> packagesToScan;
        Map<Class<? extends Annotation>, ScriptInfo>  customAnnotationsConfig;
        try {
            packagesToScan = getPackagesToScan(element);
            customAnnotationsConfig = getCustomAnnotationsConfig(element);
        } catch (ClassNotFoundException e) {
            throw new BeanCreationException(String.format("Error parsing bean definitions: %s", e.getMessage()), e);
        }

        BeanDefinitionBuilder builder = BeanDefinitionBuilder.genericBeanDefinition(ScriptRepositoryFactoryBean.class);
        builder.addConstructorArgValue(packagesToScan);
        builder.addConstructorArgValue(customAnnotationsConfig);
        AbstractBeanDefinition beanDefinition = builder.getBeanDefinition();
        parserContext.getRegistry().registerBeanDefinition(ScriptRepositoryFactoryBean.NAME, beanDefinition);
        return beanDefinition;
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
                log.trace("Package found: {} content {}", node.getNodeName(), node.getTextContent());
                result.add(node.getTextContent());
            }
        }
        return result;
    }

    private static Map<Class<? extends Annotation>, ScriptInfo> getCustomAnnotationsConfig(Element element) throws ClassNotFoundException {
        log.trace("Reading annotations configurations to create script methods later");
        NodeList elementsByTagName = element.getElementsByTagName(String.format("%sannotations-config", createPrefix(element)));
        Node item = elementsByTagName.item(0);
        NodeList configurations =  item.getChildNodes();
        int configurationsCount = configurations.getLength();
        Map<Class<? extends Annotation>, ScriptInfo> result = new HashMap<>(configurationsCount);
        for (int i = 0; i < configurationsCount; i++){
            Node node = configurations.item(i);
            if (String.format("%sannotation-mapping", createPrefix(element)).equals(node.getNodeName())) {
                NamedNodeMap attributes = node.getAttributes();
                log.trace("Annotation configuration found: {} annotation is {}", node.getNodeName(), attributes.getNamedItem("annotation-class"));
                String providerBeanName = attributes.getNamedItem("provider-bean-name").getTextContent();
                String executorBeanName = attributes.getNamedItem("executor-bean-name").getTextContent();
                Class<? extends Annotation> annotationClass = (Class<? extends Annotation>)Class.forName(attributes.getNamedItem("annotation-class").getTextContent());
                result.put(annotationClass, new ScriptInfo(annotationClass, providerBeanName, executorBeanName));
            }
        }
        return result;
    }


    private static String createPrefix(Element element) {
        return StringUtils.isEmpty(element.getPrefix())?"":element.getPrefix()+":";
    }

    static class ScriptInfo {
        final Class<? extends Annotation> scriptAnnotation;
        final String provider;
        final String executor;

        public ScriptInfo(Class<? extends Annotation> scriptAnnotation, String provider, String executor) {
            this.scriptAnnotation = scriptAnnotation;
            this.provider = provider;
            this.executor = executor;
        }

        @Override
        public String toString() {
            return "ScriptInfo{" +
                    "scriptAnnotation=" + scriptAnnotation.getName() +
                    ", provider='" + provider + '\'' +
                    ", executor='" + executor + '\'' +
                    '}';
        }
    }
}
