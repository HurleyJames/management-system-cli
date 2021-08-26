package ac.hurley.managementsystemcli.entitiy;

import java.util.List;

/**
 * 代码生成表的数据
 *
 * @author hurley
 */
public class TableEntity {

    /**
     * 表的名称
     */
    private String tableName;

    /**
     * 表的备注
     */
    private String comments;

    /**
     * 主键
     */
    private ColumnEntity primaryKey;

    /**
     * 表的列名（不包含主键）
     */
    private List<ColumnEntity> columns;

    /**
     * 类名（首字母大写）
     */
    private String className;

    /**
     * 类名（首字母小写）
     */
    private String classname;

    public String getTableName() {
        return tableName;
    }

    public void setTableName(String tableName) {
        this.tableName = tableName;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public ColumnEntity getPrimaryKey() {
        return primaryKey;
    }

    public void setPrimaryKey(ColumnEntity primaryKey) {
        this.primaryKey = primaryKey;
    }

    public List<ColumnEntity> getColumns() {
        return columns;
    }

    public void setColumns(List<ColumnEntity> columns) {
        this.columns = columns;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getClassname() {
        return classname;
    }

    public void setClassname(String classname) {
        this.classname = classname;
    }
}
