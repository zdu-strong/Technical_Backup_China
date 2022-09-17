package com.springboot.project.common.mysql;

import org.hibernate.dialect.function.SQLFunctionTemplate;
import org.hibernate.dialect.function.StandardSQLFunction;
import org.hibernate.type.StandardBasicTypes;
import org.hibernate.dialect.H2Dialect;

/**
 * In order to use the ifnull method when selecting
 * 
 * @author zdu
 *
 */
public class CustomH2Dialect extends H2Dialect {
    public CustomH2Dialect() {
        super();
        registerFunction("IFNULL", new StandardSQLFunction("IFNULL", StandardBasicTypes.LONG));
        registerFunction("IS_SORT_AT_BEFORE", new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, "(?1 < ?2)"));
        registerFunction("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND_MILLISECOND",
                new SQLFunctionTemplate(StandardBasicTypes.STRING,
                        "(TO_CHAR(DATEADD(MINUTE, CAST(CONCAT( SUBSTRING(?2, 1, 1), SUBSTRING(?2, 5, 2))  AS INT), DATEADD(HOUR, CAST(SUBSTRING(?2, 1, 3) AS INT), ?1)), 'YYYY-MM-DD HH24:mi:ss.ff3'))"));
        registerFunction("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND",
                new SQLFunctionTemplate(StandardBasicTypes.STRING,
                        "(TO_CHAR(DATEADD(MINUTE, CAST(CONCAT( SUBSTRING(?2, 1, 1), SUBSTRING(?2, 5, 2))  AS INT), DATEADD(HOUR, CAST(SUBSTRING(?2, 1, 3) AS INT), ?1)), 'YYYY-MM-DD HH24:mi:ss'))"));
        registerFunction("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR_MINUTE",
                new SQLFunctionTemplate(StandardBasicTypes.STRING,
                        "(TO_CHAR(DATEADD(MINUTE, CAST(CONCAT( SUBSTRING(?2, 1, 1), SUBSTRING(?2, 5, 2))  AS INT), DATEADD(HOUR, CAST(SUBSTRING(?2, 1, 3) AS INT), ?1)), 'YYYY-MM-DD HH24:mi'))"));
        registerFunction("FORMAT_DATE_AS_YEAR_MONTH_DAY_HOUR",
                new SQLFunctionTemplate(StandardBasicTypes.STRING,
                        "(TO_CHAR(DATEADD(MINUTE, CAST(CONCAT( SUBSTRING(?2, 1, 1), SUBSTRING(?2, 5, 2))  AS INT), DATEADD(HOUR, CAST(SUBSTRING(?2, 1, 3) AS INT), ?1)), 'YYYY-MM-DD HH24'))"));
        registerFunction("FORMAT_DATE_AS_YEAR_MONTH_DAY",
                new SQLFunctionTemplate(StandardBasicTypes.STRING,
                        "(TO_CHAR(DATEADD(MINUTE, CAST(CONCAT( SUBSTRING(?2, 1, 1), SUBSTRING(?2, 5, 2))  AS INT), DATEADD(HOUR, CAST(SUBSTRING(?2, 1, 3) AS INT), ?1)), 'YYYY-MM-DD'))"));
        registerFunction("FORMAT_DATE_AS_YEAR_MONTH",
                new SQLFunctionTemplate(StandardBasicTypes.STRING,
                        "(TO_CHAR(DATEADD(MINUTE, CAST(CONCAT( SUBSTRING(?2, 1, 1), SUBSTRING(?2, 5, 2))  AS INT), DATEADD(HOUR, CAST(SUBSTRING(?2, 1, 3) AS INT), ?1)), 'YYYY-MM'))"));
        registerFunction("FORMAT_DATE_AS_YEAR",
                new SQLFunctionTemplate(StandardBasicTypes.STRING,
                        "(TO_CHAR(DATEADD(MINUTE, CAST(CONCAT( SUBSTRING(?2, 1, 1), SUBSTRING(?2, 5, 2))  AS INT), DATEADD(HOUR, CAST(SUBSTRING(?2, 1, 3) AS INT), ?1)), 'YYYY'))"));
        registerFunction("CONVERT_TO_BIG_DECIMAL",
                new SQLFunctionTemplate(StandardBasicTypes.BIG_DECIMAL, "(CAST(?1 AS NUMERIC(65, 4)))"));
        registerFunction("CONVERT_TO_STRING",
                new SQLFunctionTemplate(StandardBasicTypes.STRING, "(CAST(?1 AS CHARACTER VARYING))"));
        this.registerFunctionOfIS_CHILD_ORGANIZE();
        this.registerFunctionOfIS_NOT_DELETE_OF_ORGANIZE_AND_ANCESTORS();

    }

    private void registerFunctionOfIS_CHILD_ORGANIZE() {
        var maxRecursionLevel = 10;
        var isChildStringBuilder = new StringBuilder();
        isChildStringBuilder.append("(");
        isChildStringBuilder.append("exists (");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("select organize.id");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("from organize_entity organize");
        isChildStringBuilder.append(" ");
        for (int i = 2; i <= maxRecursionLevel; i++) {
            var firstTableName = "organize" + (i == 2 ? "" : "_" + (i - 1));
            var secondTableName = "organize_" + i;
            isChildStringBuilder.append("left join  organize_entity " + secondTableName);
            isChildStringBuilder.append(" ");
            isChildStringBuilder.append("on " + firstTableName + ".parent_organize_id = " + secondTableName + ".id");
            isChildStringBuilder.append(" ");
        }
        isChildStringBuilder.append("where organize.id = ?1");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("and");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("?2");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("in");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("(");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("organize.id");
        isChildStringBuilder.append(",");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append("organize.parent_organize_id");
        isChildStringBuilder.append(" ");
        for (int i = 2; i <= maxRecursionLevel; i++) {
            var secondTableName = "organize_" + i;
            isChildStringBuilder.append(",");
            isChildStringBuilder.append(" ");
            isChildStringBuilder.append(secondTableName + ".parent_organize_id");
            isChildStringBuilder.append(" ");
        }
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append(")");
        isChildStringBuilder.append(" ");
        isChildStringBuilder.append(")");
        isChildStringBuilder.append(")");
        registerFunction("IS_CHILD_ORGANIZE",
                new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN, isChildStringBuilder.toString()));
    }

    private void registerFunctionOfIS_NOT_DELETE_OF_ORGANIZE_AND_ANCESTORS() {
        var maxRecursionLevel = 10;
        var isNotDeleteOfOrganizeAndAncestorsStringBuilder = new StringBuilder();
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("(");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("exists (");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("select organize.id");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("from organize_entity organize");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        for (int i = 2; i <= maxRecursionLevel; i++) {
            var firstTableName = "organize" + (i == 2 ? "" : "_" + (i - 1));
            var secondTableName = "organize_" + i;
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("left join  organize_entity " + secondTableName);
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder
                    .append("on " + firstTableName + ".parent_organize_id = " + secondTableName + ".id");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("and");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(secondTableName + ".`delete_key` = ''");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        }
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("where organize.id = ?1");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("and");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("organize.`delete_key` = ''");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("and");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("'PARENT_ORGANIZE_ID_NULL'");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("in");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("(");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("organize.id");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(",");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder
                .append("IFNULL(organize.parent_organize_id, 'PARENT_ORGANIZE_ID_NULL')");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        for (int i = 2; i <= maxRecursionLevel; i++) {
            var secondTableName = "organize_" + i;
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(",");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("IFNULL(");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(secondTableName + ".parent_organize_id");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(", ");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append("(case when " + secondTableName
                    + ".id is NULL THEN 'CHILD_ORGANIZE_ORGANIZE_ID_NULL' ELSE 'PARENT_ORGANIZE_ID_NULL' END)");
                    isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
                    isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(")");
            isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        }
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(")");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(" ");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(")");
        isNotDeleteOfOrganizeAndAncestorsStringBuilder.append(")");
        registerFunction("IS_NOT_DELETE_OF_ORGANIZE_AND_ANCESTORS",
                new SQLFunctionTemplate(StandardBasicTypes.BOOLEAN,
                        isNotDeleteOfOrganizeAndAncestorsStringBuilder.toString()));
    }
}