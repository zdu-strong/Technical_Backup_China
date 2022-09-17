package com.springboot.project.common.mysql;

import org.hibernate.dialect.MySQL8Dialect;
import org.hibernate.dialect.function.NoArgSQLFunction;
import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;

/**
 * In order to use the ifnull method when selecting. In order to use the
 * found_rows method to get the total number of items in group by.
 * 
 * @author zdu
 *
 */
public class CustomMySQL8Dialect extends MySQL8Dialect {
    public CustomMySQL8Dialect() {
        super();
        registerFunction("IFNULL", new StandardSQLFunction("IFNULL", StandardBasicTypes.LONG));
        registerFunction("FOUND_ROWS", new NoArgSQLFunction("SQL_CALC_FOUND_ROWS FOUND_ROWS", StandardBasicTypes.LONG));
        var isChildStringBuilder = new StringBuilder();
        isChildStringBuilder.append("?2");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("in");
        isChildStringBuilder.append("(");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("with recursive cte as (");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("select id");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("from organize_entity");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("where id = ?1");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("union all");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("select organize.parent_organize_id");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("from cte inner join organize_entity organize");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("on cte.id = organize.id");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append(")");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("select * from cte");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append(")");
        registerFunction("IS_CHILD_ORGANIZE",
                new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, isChildStringBuilder.toString()));
        var isNotDeleteOfOrganizeAndAncestorsStringBuilder = new StringBuilder();
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("'NULL'");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("IN");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("(");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("with recursive cte as (");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("select id");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("from organize_entity");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("where id = ?1");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("and");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("`delete_key` = ''");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("union all");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("select IFNULL(organize.parent_organize_id, 'NULL')");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("from cte inner join organize_entity organize");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("on cte.id = organize.id");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("and");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("`organize`.`delete_key` = ''");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(")");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("select * from cte");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(")");
        registerFunction("IS_NOT_DELETE_OF_ORGANIZE_AND_ANCESTORS",
                new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN,
                        isNotDeleteOfOrganizeAndAncestorsStringBuilder.toString()));
        registerFunction("IS_SORT_AT_BEFORE", new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, "(?1 < ?2)"));
        registerFunction("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MILLISECOND",
                new SQLFunctionTemplate(StandardBasicTypes.STRING,
                        "(SUBSTRING(DATE_FORMAT(CONVERT_TZ(?1, '+00:00', ?2), '%Y-%m-%d %H:%i:%s.%f'), 1, 23))"));
        registerFunction("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND", new SQLFunctionTemplate(
                StandardBasicTypes.STRING, "(DATE_FORMAT(CONVERT_TZ(?1, '+00:00', ?2), '%Y-%m-%d %H:%i:%s'))"));
        registerFunction("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR_MINUTE", new SQLFunctionTemplate(StandardBasicTypes.STRING,
                "(DATE_FORMAT(CONVERT_TZ(?1, '+00:00', ?2), '%Y-%m-%d %H:%i'))"));
        registerFunction("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR", new SQLFunctionTemplate(StandardBasicTypes.STRING,
                "(DATE_FORMAT(CONVERT_TZ(?1, '+00:00', ?2), '%Y-%m-%d %H'))"));
        registerFunction("FORMAT_DATE_AS_YEAR_MONTH_DAY", new SQLFunctionTemplate(StandardBasicTypes.STRING,
                "(DATE_FORMAT(CONVERT_TZ(?1, '+00:00', ?2), '%Y-%m-%d'))"));
        registerFunction("FORMAT_DATE_AS_YEAR_MONTH", new SQLFunctionTemplate(StandardBasicTypes.STRING,
                "(DATE_FORMAT(CONVERT_TZ(?1, '+00:00', ?2), '%Y-%m'))"));
        registerFunction("FORMAT_DATE_AS_YEAR", new SQLFunctionTemplate(StandardBasicTypes.STRING,
                "(DATE_FORMAT(CONVERT_TZ(?1, '+00:00', ?2), '%Y'))"));
        registerFunction("CONVERT_TO_BIG_DECIMAL",
                new SQLFunctionTemplate(StandardBasicTypes.BIG_DECIMAL, "(CAST(?1 AS DECIMAL(65,4)))"));
        registerFunction("CONVERT_TO_STRING",
                new SQLFunctionTemplate(StandardBasicTypes.STRING, "(CAST(?1 AS NCHAR))"));
    }
}
