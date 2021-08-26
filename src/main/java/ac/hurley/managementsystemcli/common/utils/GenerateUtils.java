package ac.hurley.managementsystemcli.common.utils;

import ac.hurley.managementsystemcli.common.exception.SysException;
import ac.hurley.managementsystemcli.common.worker.SnowflakeIdWorker;
import ac.hurley.managementsystemcli.entitiy.ColumnEntity;
import ac.hurley.managementsystemcli.entitiy.TableEntity;
import org.apache.commons.configuration.Configuration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.PropertiesConfiguration;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.app.Velocity;
import org.thymeleaf.util.DateUtils;

import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 * 本项目的代码生成器工具类
 *
 * @author hurley
 */
public class GenerateUtils {

    /**
     * 获取模板
     *
     * @return
     */
    public static List<String> getTemplates() {
        List<String> templates = new ArrayList<>();
        templates.add("template/Entity.java.vm");
        templates.add("template/Dao.java.vm");
        templates.add("template/Service.java.vm");
        templates.add("template/ServiceImpl.java.vm");
        templates.add("template/Controller.java.vm");
        templates.add("template/Dao.xml.vm");
        templates.add("template/menu.sql.vm");
        templates.add("template/list.html.vm");

        return templates;
    }

    /**
     * 生成代码
     *
     * @param table
     * @param zipOutputStream
     */
    public static void generateCode(Map<String, String> table, List<Map<String, String>> columns,
                                    ZipOutputStream zipOutputStream) {

        // 配置信息
        Configuration configuration = getConfig();
        // 是否含有 Decimal
        boolean hasBigDecimal = false;

        // 表的信息
        TableEntity tableEntity = new TableEntity();
        tableEntity.setTableName(table.get("tableName"));
        tableEntity.setComments(table.get("tableComment"));

        // 设置 Java 类名
        String className = tableToJava(tableEntity.getTableName(), configuration.getStringArray("tablePrefix"));
        tableEntity.setClassName(className);
        tableEntity.setClassName(StringUtils.uncapitalize(className));
        tableEntity.setClassName(className.toLowerCase());

        // 列的信息
        List<ColumnEntity> columnList = new ArrayList<>();
        for (Map<String, String> column : columns) {
            ColumnEntity columnEntity = new ColumnEntity();
            columnEntity.setColumnName(column.get("columnName"));
            columnEntity.setDataType(column.get("dataType"));
            columnEntity.setComments(column.get("columnComment"));
            columnEntity.setExtra(column.get("extra"));

            String attrName = columnToJava(columnEntity.getColumnName());
            columnEntity.setAttrName(attrName);
            columnEntity.setAttrName(StringUtils.uncapitalize(attrName));

            String attrType = configuration.getString(columnEntity.getDataType(), "unknownType");
            columnEntity.setAttrType(attrType);
            if (!hasBigDecimal && "BigDecimal".equals(attrType)) {
                hasBigDecimal = true;
            }

            if ("PRI".equalsIgnoreCase(column.get("columnKey")) && tableEntity.getPrimaryKey() == null) {
                tableEntity.setPrimaryKey(columnEntity);
            }

            columnList.add(columnEntity);
        }

        tableEntity.setColumns(columnList);

        if (tableEntity.getPrimaryKey() == null) {
            tableEntity.setPrimaryKey(tableEntity.getColumns().get(0));
        }

        Properties prop = new Properties();
        prop.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init(prop);
        String mainPath = configuration.getString("mainPath");
        mainPath = StringUtils.isBlank(mainPath) ? "com.company" : mainPath;

        Map<String, Object> map = new HashMap<>(15);
        map.put("tableName", tableEntity.getTableName());
        map.put("comments", tableEntity.getComments());
        map.put("pk", tableEntity.getPrimaryKey());
        map.put("className", tableEntity.getClassName());
        map.put("classname", tableEntity.getClassname());
        map.put("pathName", tableEntity.getClassname().toLowerCase());
        map.put("columns", tableEntity.getColumns());
        map.put("classNameLower", tableEntity.getClassName());
        map.put("hasBigDecimal", hasBigDecimal);
        map.put("mainPath", mainPath);
        map.put("package", configuration.getString("package"));
        map.put("author", configuration.getString("author"));
        map.put("email", configuration.getString("email"));
        map.put("datetime", DateUtils.format(new Date(), "yyyy-MM-dd HH:mm:ss", Locale.ENGLISH));
        SnowflakeIdWorker idWorker = new SnowflakeIdWorker(0, 0);
        map.put("identity", idWorker.nextId());
        map.put("addId", idWorker.nextId());
        map.put("updateId", idWorker.nextId());
        map.put("deleteId", idWorker.nextId());
        map.put("selectId", idWorker.nextId());

        VelocityContext context = new VelocityContext(map);

        // 获取模板列表
        List<String> templates = getTemplates();
        for (String template : templates) {
            StringWriter writer = new StringWriter();
            Template tpl = Velocity.getTemplate(template, "UTF-8");
            tpl.merge(context, writer);

            try {
                // 添加到 zip
                zipOutputStream.putNextEntry(
                        new ZipEntry(
                                Objects.requireNonNull(
                                        getFileName(template, tableEntity.getClassName(), configuration.getString("package")))));
                IOUtils.write(writer.toString(), zipOutputStream, "UTF-8");
                IOUtils.closeQuietly(writer);
                zipOutputStream.closeEntry();
            } catch (IOException e) {
                throw new SysException("渲染模板失败，表名：" + tableEntity.getTableName());
            }
        }
    }

    /**
     * 将列名转换为 Java 属性名
     *
     * @param field
     * @return
     */
    public static String columnToJava(String field) {
        String[] fields = field.split("_");
        StringBuilder builder = new StringBuilder(fields[0]);
        for (int i = 1; i < fields.length; i++) {
            char[] c = fields[i].toCharArray();
            c[0] -= 32;
            builder.append(String.valueOf(c));
        }
        return builder.toString().substring(0, 1).toUpperCase() + builder.toString().substring(1);
    }

    /**
     * 将表名转换为 Java 类名
     *
     * @param tableName
     * @param tablePrefixArray
     * @return
     */
    public static String tableToJava(String tableName, String[] tablePrefixArray) {
        tableName = tableName.toLowerCase();
        if (null != tablePrefixArray && tablePrefixArray.length > 0) {
            for (String tablePrefix : tablePrefixArray) {
                tablePrefix = tablePrefix.toLowerCase();
                tableName = tableName.replace(tablePrefix, "");
            }
        }
        return columnToJava(tableName);
    }

    /**
     * 获取配置信息
     *
     * @return
     */
    public static Configuration getConfig() {
        try {
            return new PropertiesConfiguration("generator.properties");
        } catch (ConfigurationException e) {
            throw new SysException("获取配置文件失败");
        }
    }

    /**
     * 获取文件名路径
     *
     * @param template
     * @param className
     * @param packageName
     * @return
     */
    public static String getFileName(String template, String className, String packageName) {
        // 包路径
        String packagePath = "main" + File.separator + "java" + File.separator;
        if (StringUtils.isNotBlank(packageName)) {
            // 将 separator 替换成 .
            packagePath += packageName.replace(".", File.separator) + File.separator;
        }

        // Entity
        if (template.contains("Entity.java.vm")) {
            return packagePath + "entity" + File.separator + className + "Entity.java";
        }

        // Dao
        if (template.contains("Dao.java.vm")) {
            return packagePath + "mapper" + File.separator + className + "Mapper.java";
        }

        // Service
        if (template.contains("Service.java.vm")) {
            return packagePath + "service" + File.separator + className + "Service.java";
        }

        // ServiceImpl
        if (template.contains("ServiceImpl.java.vm")) {
            return packagePath + "service" + File.separator + "impl" + File.separator + className + "ServiceImpl.java";
        }

        if (template.contains("Controller.java.vm")) {
            return packagePath + "controller" + File.separator + className + "Controller.java";
        }

        if (template.contains("Dao.xml.vm")) {
            return "main" + File.separator + "resources" + File.separator + "mapper" + File.separator + className + "Mapper.xml";
        }

        if (template.contains("menu.sql.vm")) {
            return className.toLowerCase() + "_menu.sql";
        }

        if (template.contains("list.html.vm")) {
            return "main" + File.separator + "resources" + File.separator + "templates"
                    + File.separator + className.toLowerCase() + File.separator + "list.html";
        }

        return null;
    }
}
