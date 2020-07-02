package org.r.framework.thrift.eureka.client.provider;


import com.alibaba.fastjson.JSONObject;
import org.r.framework.thrift.common.Constants;
import org.r.framework.thrift.springboot.starter.provider.ServerInfoProvider;
import org.r.framework.thrift.springboot.starter.wrapper.ServiceBeanWrapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.eureka.EurekaInstanceConfigBean;
import org.springframework.cloud.netflix.eureka.metadata.DefaultManagementMetadataProvider;
import org.springframework.cloud.netflix.eureka.metadata.ManagementMetadata;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * date 20-5-9 下午3:20
 *
 * @author casper
 **/
public class ServerMetaDataProvider extends DefaultManagementMetadataProvider {

    private final Logger log = LoggerFactory.getLogger(ServerMetaDataProvider.class);

    private final ServerInfoProvider serverInfoProvider;

    public ServerMetaDataProvider(ServerInfoProvider serverInfoProvider) {
        this.serverInfoProvider = serverInfoProvider;
    }

    @Override
    public ManagementMetadata get(EurekaInstanceConfigBean instance, int serverPort, String serverContextPath, String managementContextPath, Integer managementPort) {
        Map<String, String> metadataMap = instance.getMetadataMap();
        if (metadataMap == null) {
            metadataMap = new HashMap<>();
            instance.setMetadataMap(metadataMap);
        }
        int port = this.serverInfoProvider.getPort();
        List<ServiceBeanWrapper> allService = serverInfoProvider.getAllService();
        List<String> serverNameList = allService.stream().map(t -> t.getName() + ":" + port).collect(Collectors.toList());
        String serviceListStr = JSONObject.toJSONString(serverNameList);
        metadataMap.put(Constants.SERVERINFO, serviceListStr);
        log.info("Registry service to eureka [{}]",serviceListStr);
        return super.get(instance, serverPort, serverContextPath, managementContextPath, managementPort);
    }
}
