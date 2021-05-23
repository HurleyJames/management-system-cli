package ac.hurley.managementsystemcli.entitiy;

import lombok.Data;

/**
 * 代码生成 列属性
 */
@Data
public class ColumnEntity {
    /**
     * 列名
     */
    private String columnName;

    /**
     * 列的数据类型
     */
    private String dataType;

    /**
     * 列名的备注
     */
    private String comments;

    /**
     * 属性名称（首字母大写）
     */
    private String attrName;

    /**
     * 属性名称（首字母小写）
     */
    private String attrname;

    /**
     * 属性类型
     */
    private String attrType;

    /**
     * 自增长
     */
    private String extra;

    public String getColumnName() {
        return columnName;
    }

    public void setColumnName(String columnName) {
        this.columnName = columnName;
    }

    public String getDataType() {
        return dataType;
    }

    public void setDataType(String dataType) {
        this.dataType = dataType;
    }

    public String getComments() {
        return comments;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getAttrName() {
        return attrName;
    }

    public void setAttrName(String attrName) {
        this.attrName = attrName;
    }

    public String getAttrname() {
        return attrname;
    }

    public void setAttrname(String attrname) {
        this.attrname = attrname;
    }

    public String getAttrType() {
        return attrType;
    }

    public void setAttrType(String attrType) {
        this.attrType = attrType;
    }

    public String getExtra() {
        return extra;
    }

    public void setExtra(String extra) {
        this.extra = extra;
    }
}
