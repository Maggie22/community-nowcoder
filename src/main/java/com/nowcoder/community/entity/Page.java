package com.nowcoder.community.entity;

public class Page {

    private Integer current = 1;

    private Integer limit = 5;

    private Integer totalRows;

    private Integer totalPage;

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    private String path = "/index";

    public void setTotalPage() {
        if(totalRows % limit == 0)
            totalPage = totalRows / limit;
        else
            totalPage = totalRows / limit + 1;
    }

    public Integer getTotalPage(){
        return totalPage;
    }

    public Integer getCurrent() {
        return current;
    }

    public void setCurrent(Integer current) {
        if(current >= 1)
            this.current = current;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        if(limit >= 1 && limit <= 100)
            this.limit = limit;
    }

    public Integer getTotalRows() {
        return totalRows;
    }

    public void setTotalRows(Integer totalRows) {
        this.totalRows = totalRows;
    }

    public int getOffset() {
        return (current - 1) * limit;
    }

    public int getTo(){
        int to = current + 2;
        return Math.min(to, getTotalPage());
    }

    public int getFrom(){
        int from = current - 2;
        return Math.max(from, 1);
    }
}
