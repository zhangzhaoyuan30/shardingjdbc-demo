package com.zzy.shardingjdbcdemo.config;

import com.google.common.collect.Maps;
import com.google.common.hash.HashCode;
import com.google.common.hash.Hashing;
import com.zaxxer.hikari.HikariDataSource;
import io.shardingsphere.api.algorithm.sharding.PreciseShardingValue;
import io.shardingsphere.api.algorithm.sharding.standard.PreciseShardingAlgorithm;
import io.shardingsphere.api.config.ShardingRuleConfiguration;
import io.shardingsphere.api.config.TableRuleConfiguration;
import io.shardingsphere.api.config.strategy.InlineShardingStrategyConfiguration;
import io.shardingsphere.api.config.strategy.StandardShardingStrategyConfiguration;
import io.shardingsphere.core.keygen.DefaultKeyGenerator;
import io.shardingsphere.shardingjdbc.api.ShardingDataSourceFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.util.Collection;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @auther: zhangzhaoyuan
 * @date: 2019/03/18
 * @description: 数据分片配置（单数据源仅分表）
 */

@Configuration
public class ShardingConfigAll {

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

    //配置Hikari数据源
    private HikariDataSource getHikariDataSource(String url, String username, String password, Integer minimumIdle, Integer maximumPoolSize) {
        HikariDataSource dataSource = new HikariDataSource();
        dataSource.setJdbcUrl(url);
        dataSource.setUsername(username);
        dataSource.setPassword(password);
        dataSource.setMinimumIdle(minimumIdle);
        dataSource.setMaximumPoolSize(maximumPoolSize);
        return dataSource;
    }

    /**
     * 配置datasource的数据源，使用Hikari作为数据源
     * Primary的注解作用是在多数据源的情况下，优先加载该Bean
     *
     * @return
     */
    @Bean
    public DataSource buildDataSource() throws Exception {
        Map<String, DataSource> dataSourceMap = Maps.newHashMapWithExpectedSize(1);
        dataSourceMap.put("ds0", getHikariDataSource(
            "jdbc:mysql://192.166.65.174:3306/zzy0?useUnicode=true&characterEncoding=utf8&&useSSL=false&serverTimezone=Hongkong",
            username, password, minimumIdle, maximumPoolSize));
        dataSourceMap.put("ds1", getHikariDataSource(
            "jdbc:mysql://192.166.65.174:3306/zzy1?useUnicode=true&characterEncoding=utf8&&useSSL=false&serverTimezone=Hongkong",
            username, password, minimumIdle, maximumPoolSize));


        //生成数据库分表配置文件
        ShardingRuleConfiguration shardingRuleConfig = new ShardingRuleConfiguration();
        //添加表规则
        shardingRuleConfig.getTableRuleConfigs().add(createTableRuleConfigurationFactory(
            "t", "ds${0..1}.t${0..1}", "id", "id"));
        Properties properties = new Properties();
        return ShardingDataSourceFactory.createDataSource(dataSourceMap, shardingRuleConfig, new ConcurrentHashMap<>(), properties);
    }


    /**
     * @param logicTableName  逻辑表名
     * @param actualDataNodes 数据节点
     * @param primaryKey      主键
     * @param shardingColumn  分片列
     * @return
     */
    private TableRuleConfiguration createTableRuleConfigurationFactory(String logicTableName, String actualDataNodes,
                                                                       String primaryKey, String shardingColumn) {
        TableRuleConfiguration tableRuleConfig = new TableRuleConfiguration();
        //逻辑表
        tableRuleConfig.setLogicTable(logicTableName);
        //数据节点
        tableRuleConfig.setActualDataNodes(actualDataNodes);
        //分库策略
        tableRuleConfig.setDatabaseShardingStrategyConfig(new InlineShardingStrategyConfiguration(shardingColumn, "ds${id % 2}"));
        //分表策略
        tableRuleConfig.setTableShardingStrategyConfig(new InlineShardingStrategyConfiguration("c", "t${c % 2}"));
        //主键列名称
        tableRuleConfig.setKeyGeneratorColumnName(primaryKey);

        return tableRuleConfig;
    }


}
