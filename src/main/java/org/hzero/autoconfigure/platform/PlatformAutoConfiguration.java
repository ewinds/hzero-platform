package org.hzero.autoconfigure.platform;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.hzero.core.jackson.annotation.EnableObjectMapper;
import org.hzero.core.util.CommonExecutor;
import org.hzero.platform.infra.properties.DataHierarchyProperties;
import org.hzero.platform.infra.properties.PlatformProperties;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.scheduling.annotation.EnableAsync;

import com.google.common.util.concurrent.ThreadFactoryBuilder;

import io.choerodon.resource.annoation.EnableChoerodonResourceServer;

/**
 * @author bojiangzhou 2018/10/25
 */
@ComponentScan(value = {"org.hippius.wd", "org.hzero.platform.api", "org.hzero.platform.app",
        "org.hzero.platform.config", "org.hzero.platform.domain", "org.hzero.platform.infra"})
@EnableFeignClients({"org.hzero.platform", "io.choerodon", "org.hzero.plugin"})
@EnableAspectJAutoProxy(proxyTargetClass = true, exposeProxy = true)
@EnableChoerodonResourceServer
@EnableObjectMapper
@EnableAsync
@EnableConfigurationProperties({PlatformProperties.class, DataHierarchyProperties.class})
public class PlatformAutoConfiguration {

    /**
     * 通用线程池
     */
    @Bean
    @Qualifier("commonAsyncTaskExecutor")
    public ThreadPoolExecutor commonAsyncTaskExecutor() {
        int coreSize = CommonExecutor.getCpuProcessors();
        int maxSize = coreSize * 8;
        ThreadPoolExecutor executor = new ThreadPoolExecutor(
                coreSize, maxSize,
                30, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(16),
                new ThreadFactoryBuilder().setNameFormat("CommonExecutor-%d").build(),
                new ThreadPoolExecutor.CallerRunsPolicy()
        );

        CommonExecutor.displayThreadPoolStatus(executor, "HpfmCommonExecutor");
        CommonExecutor.hookShutdownThreadPool(executor, "HpfmCommonExecutor");

        return executor;
    }

}
