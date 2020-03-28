package com.haizhi.elasticsearchsql.entity;
/**
 * 测试实体，可以从ESBaseData对象继承meta属性，检索时会将文档的一下meta属性设置到对象实例中
 */

import com.frameworkset.orm.annotation.ESId;
import lombok.Data;
import org.frameworkset.elasticsearch.entity.ESBaseData;

@Data
public class Student extends ESBaseData {
    //设定文档标识字段
    @ESId
    private String studentId;
    private String name;
    private Integer age;
    private String sex;


    /**  当在mapping定义中指定了日期格式时，则需要指定以下两个注解,例如
     *
     "agentStarttime": {
     "type": "date",###指定多个日期格式
     "format":"yyyy-MM-dd HH:mm:ss.SSS||yyyy-MM-dd'T'HH:mm:ss.SSS||yyyy-MM-dd HH:mm:ss||epoch_millis"
     }
     @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss.SSS")
     @Column(dataformat = "yyyy-MM-dd HH:mm:ss.SSS")
     */

}
