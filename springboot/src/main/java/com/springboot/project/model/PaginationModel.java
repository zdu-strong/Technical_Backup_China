package com.springboot.project.model;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.function.Function;
import org.jinq.jpa.JPAJinqStream;
import org.jinq.orm.stream.JinqStream;
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
            this.totalRecord = stream.select(s -> JPQLFunction.foundTotalRowsForGroupBy()).findFirst()
                    .orElse(0L);
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
            this.totalRecord = stream.select(s -> JPQLFunction.foundTotalRowsForGroupBy()).findFirst()
                    .orElse(0L);
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

}
