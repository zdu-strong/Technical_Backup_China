package com.springboot.project.model;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.function.Function;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jinq.jpa.JPAJinqStream;
import org.jinq.orm.stream.JinqStream;
import org.springframework.core.io.ClassPathResource;
import com.fasterxml.jackson.dataformat.yaml.YAMLMapper;
import com.springboot.project.common.database.JPQLFunction;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

@Getter
@Setter
@Accessors(chain = true)
public class PaginationModel<T> {
    private Long pageNum;
    private Long pageSize;
    private Long totalRecord;
    private Long totalPage;
    private List<T> list;

    private static Boolean isSpannerEmulator = null;

    public PaginationModel(Long pageNum, Long pageSize, JinqStream<T> stream) {

        if (pageNum < 1) {
            throw new RuntimeException("Page num must be greater than 1");
        }
        if (pageSize < 1) {
            throw new RuntimeException("Page size must be greater than 1");
        }

        this.pageNum = pageNum;
        this.pageSize = pageSize;
        if (stream instanceof JPAJinqStream) {
            if (isSpannerEmulator()) {
                this.totalRecord = Integer.valueOf(stream.select(s -> "").toList().size()).longValue();
            } else {
                this.totalRecord = stream.select(s -> JPQLFunction.foundTotalRowsForGroupBy()).findFirst()
                        .orElse(0L);
            }
            this.setList(
                    stream.skip((pageNum - 1) * pageSize).limit(pageSize).toList());
        } else {
            var dataList = stream.toList();
            this.totalRecord = Long.valueOf(dataList.size());
            this.setList(JinqStream.from(dataList).skip((pageNum - 1) * pageSize).limit(pageSize)
                    .toList());
        }
        this.totalPage = new BigDecimal(this.totalRecord).divide(new BigDecimal(pageSize), 2, RoundingMode.FLOOR)
                .setScale(0, RoundingMode.CEILING).longValue();
    }

    public <U> PaginationModel(Long pageNum, Long pageSize, JinqStream<U> stream, Function<U, T> formatCallback) {
        if (pageNum < 1) {
            throw new RuntimeException("Page num must be greater than 1");
        }
        if (pageSize < 1) {
            throw new RuntimeException("Page size must be greater than 1");
        }

        this.pageNum = pageNum;
        this.pageSize = pageSize;
        if (stream instanceof JPAJinqStream) {
            if (isSpannerEmulator()) {
                this.totalRecord = Integer.valueOf(stream.select(s -> "").toList().size()).longValue();
            } else {
                this.totalRecord = stream.select(s -> JPQLFunction.foundTotalRowsForGroupBy()).findFirst()
                        .orElse(0L);
            }
            this.setList(
                    stream.skip((pageNum - 1) * pageSize).limit(pageSize).map(formatCallback)
                            .toList());
        } else {
            var dataList = stream.toList();
            this.totalRecord = Long.valueOf(dataList.size());
            this.setList(JinqStream.from(dataList).skip((pageNum - 1) * pageSize).limit(pageSize).map(formatCallback)
                    .toList());
        }
        this.totalPage = new BigDecimal(this.totalRecord).divide(new BigDecimal(pageSize), 2, RoundingMode.FLOOR)
                .setScale(0, RoundingMode.CEILING).longValue();
    }

    public static boolean isSpannerEmulator() {
        if (isSpannerEmulator == null) {
            synchronized (PaginationModel.class) {
                if (isSpannerEmulator == null) {
                    var databaseSourceUrlOfENV = System.getenv("SPRING_DATASOURCE_URL");
                    if (StringUtils.isNotBlank(databaseSourceUrlOfENV)) {
                        isSpannerEmulator = databaseSourceUrlOfENV.toLowerCase()
                                .contains("autoConfigEmulator=true".toLowerCase());
                    } else {
                        try (var input = new ClassPathResource("application.yml").getInputStream()) {
                            var urlOfDatasource = new YAMLMapper()
                                    .readTree(IOUtils.toString(input, StandardCharsets.UTF_8)).get("spring")
                                    .get("datasource").get("url").asText();
                            isSpannerEmulator = urlOfDatasource.toLowerCase()
                                    .contains("autoConfigEmulator=true".toLowerCase());
                        } catch (IOException e) {
                            throw new RuntimeException(e.getMessage(), e);
                        }
                    }
                }
            }
        }
        return isSpannerEmulator;
    }

}
