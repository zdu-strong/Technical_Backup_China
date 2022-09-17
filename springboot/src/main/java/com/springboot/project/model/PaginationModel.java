package com.springboot.project.model;

import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Function;
import org.apache.commons.lang3.StringUtils;
import org.jinq.jpa.JPAJinqStream;
import org.jinq.orm.stream.JinqStream;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.springboot.project.common.mysql.MysqlFunction;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PaginationModel<T> {
    private Integer pageNum;
    private Integer pageSize;
    private Long totalRecord;
    private Integer totalPage;
    private List<T> list;

    public PaginationModel(Integer pageNum, Integer pageSize, JinqStream<T> stream) {
        if (pageNum < 1) {
            throw new RuntimeException("Page num must be greater than 1");
        }
        if (pageSize < 1) {
            throw new RuntimeException("Page size must be greater than 1");
        }

        this.pageNum = pageNum;
        this.pageSize = pageSize;
        if (stream instanceof JPAJinqStream) {
            try {
                this.totalRecord = stream.count();
            } catch (IllegalArgumentException e) {
                if (this.isTestEnviroment()) {
                    /*
                     * The test environment does not configure the dialect of sql, just take the
                     * total number
                     */
                    this.totalRecord = stream.sequential().count();
                } else {
                    /* Due to unknown reasons, the first execution returns a fixed value */
                    stream.select(s -> MysqlFunction.foundTotalRowsForGroupBy()).limit(1).findFirst().orElse(0L);
                    /* The second execution returns the correct value */
                    this.totalRecord = stream.select(s -> MysqlFunction.foundTotalRowsForGroupBy()).limit(1).findFirst()
                            .orElse(0L);
                }
            }
            this.setList(
                    stream.skip((pageNum - 1) * pageSize).limit(pageSize).toList());
        } else {
            var dataList = stream.toList();
            this.totalRecord = Integer.valueOf(dataList.size()).longValue();
            this.setList(JinqStream.from(dataList).skip((pageNum - 1) * pageSize).limit(pageSize)
                    .toList());
        }
        this.totalPage = new BigDecimal(this.totalRecord).divide(new BigDecimal(pageSize), 2, RoundingMode.FLOOR)
                .setScale(0, RoundingMode.CEILING).intValue();
    }

    public <U> PaginationModel(Integer pageNum, Integer pageSize, JinqStream<U> stream, Function<U, T> formatCallback) {
        if (pageNum < 1) {
            throw new RuntimeException("Page num must be greater than 1");
        }
        if (pageSize < 1) {
            throw new RuntimeException("Page size must be greater than 1");
        }

        this.pageNum = pageNum;
        this.pageSize = pageSize;
        if (stream instanceof JPAJinqStream) {
            try {
                this.totalRecord = stream.count();
            } catch (IllegalArgumentException e) {
                if (this.isTestEnviroment()) {
                    /*
                     * The test environment does not configure the dialect of sql, just take the
                     * total number
                     */
                    this.totalRecord = stream.sequential().count();
                } else {
                    /* Due to unknown reasons, the first execution returns a fixed value */
                    stream.select(s -> MysqlFunction.foundTotalRowsForGroupBy()).limit(1).findFirst().orElse(0L);
                    /* The second execution returns the correct value */
                    this.totalRecord = stream.select(s -> MysqlFunction.foundTotalRowsForGroupBy()).limit(1).findFirst()
                            .orElse(0L);
                }
            }
            this.setList(
                    stream.skip((pageNum - 1) * pageSize).limit(pageSize).map(formatCallback)
                            .toList());
        } else {
            var dataList = stream.toList();
            this.totalRecord = Integer.valueOf(dataList.size()).longValue();
            this.setList(JinqStream.from(dataList).skip((pageNum - 1) * pageSize).limit(pageSize).map(formatCallback)
                    .toList());
        }
        this.totalPage = new BigDecimal(this.totalRecord).divide(new BigDecimal(pageSize), 2, RoundingMode.FLOOR)
                .setScale(0, RoundingMode.CEILING).intValue();
    }

    private boolean isTestEnviroment() {
        try (InputStream input = ClassLoader.getSystemResourceAsStream("application.yml")) {
            YAMLMapper yamlMapper = new YAMLMapper();
            var text = yamlMapper.readTree(input).findValue("properties").findValue("StorageRootPath").asText();
            var signalOfTestEnv = "defaultTest-a56b075f-102e-edf3-8599-ffc526ec948a";
            var storageRootPathProperties = System.getenv("PROPERTIES_STORAGE_ROOT_PATH");
            if (StringUtils.isNotBlank(storageRootPathProperties)) {
                if (signalOfTestEnv.equals(storageRootPathProperties)) {
                    return true;
                }
            } else if (signalOfTestEnv.equals(text)) {
                return true;
            }
            return false;
        } catch (IOException e1) {
            throw new RuntimeException(e1.getMessage(), e1);
        }
    }

}
