package org.r.framework.thrift.client.eureka;

import org.r.framework.thrift.client.eureka.provider.EurekaServiceInfoProvider;
import org.r.framework.thrift.netty.provider.ServiceInfoProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.client.discovery.event.HeartbeatEvent;
import org.springframework.context.ApplicationListener;

/**
 * date 2020/5/26 20:36
 *
 * @author casper
 */
public class ServerRefreshListener implements ApplicationListener<HeartbeatEvent> {

    private final Logger log = LoggerFactory.getLogger(ServerRefreshListener.class);

    private final ServiceInfoProvider eurekaServiceInfoProvider;


    public ServerRefreshListener(ServiceInfoProvider eurekaServiceInfoProvider) {
        this.eurekaServiceInfoProvider = eurekaServiceInfoProvider;
    }

    @Override
    public void onApplicationEvent(HeartbeatEvent event) {
        if(eurekaServiceInfoProvider instanceof EurekaServiceInfoProvider){
            log.info("Get a eureka refresh event");
            ((EurekaServiceInfoProvider)eurekaServiceInfoProvider).refresh();
        }
    }
}
