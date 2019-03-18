package com.xul.dbdoc.web;

import com.xul.dbdoc.domain.Table;
import com.xul.dbdoc.utils.FreemarkerUtils;
import com.xul.dbdoc.utils.JdbcHelper;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by lxu on 2018/12/14.
 */
@Api(tags = "数据库文档", description = "暂时只支持mysql")
@RestController
@Slf4j
public class DBDocController {

    @ApiOperation(value = "生成数据库文档")
    @ApiImplicitParams({
            @ApiImplicitParam(name = "host", value = "主机ip", dataType = "string", paramType = "query", required = true, example = "localhost"),
            @ApiImplicitParam(name = "db", value = "数据库名", dataType = "string", paramType = "query", required = true, example = "dbdoc"),
            @ApiImplicitParam(name = "username", value = "用户名", dataType = "string", paramType = "query", required = true, example = "root"),
            @ApiImplicitParam(name = "password", value = "密码", dataType = "string", paramType = "query", required = true, example = "123456"),
            @ApiImplicitParam(name = "dbType", value = "数据库类型", dataType = "string", paramType = "query", required = true, example = "mysql")
    })
    @GetMapping("generate")
    public ResponseEntity generate(String host, String db, String username, String password, String dbType) throws Exception {
        List<Table> tableList = JdbcHelper.connect(host + "/" + db, username, password, dbType).getTableList();
        Map<String, List<Table>> dataMap = new HashMap<>();
        dataMap.put("tableList", tableList);
        byte[] bytes = FreemarkerUtils.getBytes(dataMap);
        return new ResponseEntity<>(bytes, FreemarkerUtils.headers("数据库文档.docx"), HttpStatus.OK);
    }

}
