package com.zzy.shardingjdbcdemo.config;

import com.google.common.collect.Maps;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.zaxxer.hikari.HikariDataSource;
import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import io.shardingsphere.api.config.ShardingRuleConfiguration;
import io.shardingsphere.api.config.TableRuleConfiguration;
import io.shardingsphere.api.config.strategy.StandardShardingStrategyConfiguration;
import io.shardingsphere.core.keygen.DefaultKeyGenerator;
import io.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther: zhangzhaoyuan
 * @date: 2019/03/18
 * @description:
 */

@Configuration
public class ShardingJdbcConfig {

    @Value("${spring.datasource.url}")
    private String url;
    @Value("${spring.datasource.username}")
    private String username;
    @Value("${spring.datasource.password}")
    private String password;
    @Value("${spring.datasource.hikari.minimum-idle}")
    private Integer minimumIdle;
    @Value("${spring.datasource.hikari.maximum-pool-size}")
    private Integer maximumPoolSize;


    private HikariDataSource dataSource() {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMinimumIdle(minimumIdle);
        dataSource.setMaximumPoolSize(maximumPoolSize);
        return dataSource;
    }

    /**
     * 配置datasource的数据源，使用druid作为数据源
     * Primary的注解作用是在多数据源的情况下，优先加载该Bean
     *
     * @return
     */
    @Bean
    @Primary
    public DataSource buildDataSource() throws Exception {
        Map<String, DataSource> dataSourceMap = Maps.newHashMapWithExpectedSize(1);
        dataSourceMap.put("db0", dataSource());
        //生成数据库分表配置文件
        ShardingRuleConfiguration shardingRuleConfiguration = new ShardingRuleConfiguration();
        //添加device_channel_list对表规则
        shardingRuleConfiguration.getTableRuleConfigs().add(tTableRuleConfiguration());
        //默认使用snowFlake算法实现分布式主键id增加
        shardingRuleConfiguration.setDefaultKeyGenerator(new DefaultKeyGenerator());
        Properties properties = new Properties();
        return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfiguration, new ConcurrentHashMap<>(), properties);
    }


    private TableRuleConfiguration createTableRuleConfigurationFactory(String logicTableName, String actualDataNodes,
                                                                       String tableId) {
        TableRuleConfiguration tableRuleConfiguration = new TableRuleConfiguration();
        //逻辑表
        tableRuleConfiguration.setLogicTable(logicTableName);
        //具体节点
        tableRuleConfiguration.setActualDataNodes(actualDataNodes);
        //配置数据分页算法
        tableRuleConfiguration.setTableShardingStrategyConfig(new StandardShardingStrategyConfiguration(tableId, new ShardingStandardAlgorithm()));
        //分布式自增主键,snowFlake算法
        tableRuleConfiguration.setKeyGeneratorColumnName(tableId);
        return tableRuleConfiguration;
    }

    private TableRuleConfiguration tTableRuleConfiguration() {
        return createTableRuleConfigurationFactory("t",
            "db${0}.t_${0..1}",
            "id");
    }

    public class ShardingStandardAlgorithm implements PreciseShardingAlgorithm<Long> {

        @Override
        public String doSharding(Collection<String> availableTargetNames, PreciseShardingValue<Long> shardingValue) {
            for (String element : availableTargetNames) {
                //通过一致性hash算法获得对应序列号的bucket
                int bucket = Hashing.consistentHash(HashCode.fromLong(shardingValue.getValue()), availableTargetNames.size());
                if (element.endsWith(String.valueOf(bucket))) {
                    return element;
                }
            }
            //如果执行失败抛出不合法参数异常
            throw new IllegalArgumentException();
        }
    }

}
