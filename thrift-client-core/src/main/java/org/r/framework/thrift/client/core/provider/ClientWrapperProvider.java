package org.r.framework.thrift.client.core.provider;

import org.r.framework.thrift.client.core.wrapper.ClientWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * date 2020/5/7 22:28
 *
 * @author casper
 */
public class ClientWrapperProvider {

    private final Logger log = LoggerFactory.getLogger(ClientWrapperProvider.class);

    private List<ClientWrapper> clientWrapperList;

    private final String scanPath;

    private final ClassLoader classLoader;

    /**
     * @param scanPath 扫描路径：com.test，
     * @param loader   类加载器
     */
    public ClientWrapperProvider(String scanPath, ClassLoader loader) {
        this.scanPath = scanPath.replaceAll("\\.","/");
        this.classLoader = loader;
        refresh();
    }

    public void refresh() {
        ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();
        MetadataReaderFactory metaReader = new CachingMetadataReaderFactory();
        Resource[] resources = new Resource[0];
        try {
            resources = resolver.getResources("classpath*:" + scanPath + "/**/*.class");
            if (resources.length > 0) {
                List<Resource> resourceList = Stream.of(resources).filter(t -> t.getFilename() != null && t.getFilename().endsWith("$Client.class")).collect(Collectors.toList());
                this.clientWrapperList = new ArrayList<>(resourceList.size());
                for (Resource resource : resourceList) {
                    MetadataReader reader = metaReader.getMetadataReader(resource);
                    String className = reader.getClassMetadata().getClassName();
                    Class<?> target = classLoader.loadClass(className);
                    String serverName = target.getDeclaringClass().getSimpleName();
                    this.clientWrapperList.add(new ClientWrapper(serverName, target));
                    log.info("find client:" + serverName);
                }
            } else {
                this.clientWrapperList = Collections.emptyList();
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    public List<ClientWrapper> getAllClients() {
        return clientWrapperList;
    }


}
