package com.keyuwang.gencode.util;

import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.apache.velocity.app.VelocityEngine;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;

import java.io.IOException;
import java.util.Properties;

/**
 * Created by wky77 on 2017/8/2.
 */
public class TemplateUtil {

    /**
     * 根据模板名称，获取模板
     *
     * @param templateName
     * @return
     * @throws IOException
     */
    public static Template getTemplateByName(String templateName) {
        try {
            VelocityEngine ve = new VelocityEngine();
            Properties p = new Properties();
            p.setProperty(Velocity.ENCODING_DEFAULT, "UTF-8");
            p.setProperty(Velocity.INPUT_ENCODING, "UTF-8");
            p.setProperty(Velocity.OUTPUT_ENCODING, "UTF-8");
            p.put("file.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
            ve.init(p);
            Template t = ve.getTemplate("/codetemplate/" + templateName);
            return t;
        } catch (ResourceNotFoundException e) {
            e.printStackTrace();
        } catch (ParseErrorException e) {
            e.printStackTrace();
        }
        return null;
    }

}
