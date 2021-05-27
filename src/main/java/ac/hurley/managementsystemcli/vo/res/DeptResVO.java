package ac.hurley.managementsystemcli.vo.res;

import lombok.Data;

import java.util.List;

@Data
public class DeptResVO {

    private String id;

    private String deptNo;

    private String title;

    private String label;

    private String pid;

    private Integer status;

    private String relation;

    private boolean spread = true;

    private boolean checked = false;

    private boolean disabled = false;

    private List<?> children;

    public String getLabel() {
        return title;
    }
}
