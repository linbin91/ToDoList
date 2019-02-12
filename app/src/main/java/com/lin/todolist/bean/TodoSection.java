package com.lin.todolist.bean;

import com.chad.library.adapter.base.entity.SectionEntity;

/**
 * 作者：wangwnejie on 2018/8/9 14:33
 * 邮箱：wang20080990@163.com
 */
public class TodoSection extends SectionEntity<TodoDesBean> {

    public TodoSection(boolean isHeader, String header) {
        super(isHeader, header);
    }

    public TodoSection(TodoDesBean todoBean) {
        super(todoBean);
    }
}
