package pe.com.domain.webscripts;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import pe.com.domain.util.Util;

public class TestWebScript extends DeclarativeWebScript
{
    private static Log logger = LogFactory.getLog(TestWebScript.class);

    private Util util;

    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status, Cache cache)
    {
        Map<String, Object> model = new HashMap<String, Object>();
        model.put("fromJava", "HelloFromJava");

        Map<String, String> templateArgs = req.getServiceMatch().getTemplateVars();
        String folderPath = templateArgs.get("folderpath");

        logger.info("Test WebScript");
        try
        {
            NodeRef nodeRef = util.createFolderByPath(folderPath);
            if (nodeRef == null)
                model.put("folder", util.getMessage("EX006"));
            else
                model.put("folder", nodeRef.toString());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        return model;
    }

    public void setUtil(Util util)
    {
        this.util = util;
    }
    

}
