package com.xul.dbdoc.domain;

import lombok.Data;

/**
 * Created by lxu on 2018/12/14.
 */
@Data
public class Field {

    private String name = "";
    private String comment = "";
    private String type = "";
    private String notNull = "";
    private String key = "";
    private String defaultValue = "";

}
