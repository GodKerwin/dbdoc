package com.xul.dbdoc.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by lxu on 2018/12/14.
 */
@Data
public class Table {

    private String name = "";
    private String comment = "";
    private List<Field> fieldList = new ArrayList<>();

}
