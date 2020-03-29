package com.haizhi.elasticsearchsql.controller;

import com.google.common.collect.Lists;
import com.haizhi.elasticsearchsql.entity.Teacher;
import com.haizhi.elasticsearchsql.util.ESUtil;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController("/es")
@Api(description = "controller上的描述信息")
public class EsDemoController {
    @Autowired
    private ESUtil esUtil;

    @GetMapping("/add/{docId}")
    @ApiOperation(value = "新增")
    public String add(@PathVariable("docId") String docId){
        Teacher teacher =  Teacher.builder().teacherId(docId).name("name"+docId).sex("男").build();
        Teacher teacher2 =  Teacher.builder().teacherId(docId+"copy").name("name"+docId+"copy").sex("女").build();
        List<Teacher> list = Lists.newArrayList(teacher,teacher2);
        String documents = esUtil.addOrUpdateDocuments("teacher", list);
        return documents;
    }

    @GetMapping("delete")
    @ApiOperation(value = "删除")
    public String delete(){
        esUtil.deleteDocumentById("teacher","1");
        return "ok";
    }

    @GetMapping("get")
    @ApiOperation(value = "查询")
    public List<Teacher> get(){
        Teacher teacher = new Teacher();
        teacher.setTeacherId("2");
        List<Teacher> t = esUtil.exec("teacher",teacher,"searchTeacher");
        return t;
    }
}
