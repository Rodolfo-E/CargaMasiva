package pe.com.domain.webscripts;

import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import pe.com.domain.util.ComponentService;
import pe.com.domain.util.Util;

public class Download extends DeclarativeWebScript {

    private static Log logger = LogFactory.getLog(Download.class);

	private ComponentService service;
	private Util util;

    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) 
    {
        logger.debug("Download WebScript called.");
        
		String uuid = req.getParameter("id");
		String wsTicket = req.getParameter("wsTicket");
		String idSistema = req.getParameter("idSistema");
		logger.debug("Parameters[" + uuid + "," + wsTicket + "," + idSistema + "]");
		
		if (uuid == null || uuid.length() == 0) 
			return buildResponse("EX003", "");
		
		try
        {
		    if (!service.validarTicketWS(idSistema, wsTicket))
	            return buildResponse("EX001", "");
		    
		    String downloadUrl = util.getDownloadUrl(uuid);
		    return buildResponse("00000", downloadUrl);
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return buildResponse("EX001", "");
        }
		finally { AuthenticationUtil.clearCurrentSecurityContext(); }
	}
	
	private Map<String, Object> buildResponse(String code, String downloadUrl)
	{
	    Map<String, Object> response = new HashMap<>();
	    response.put("result", util.getMessage(code));
	    response.put("downloadUrl", downloadUrl);
	    return response;
	}

    public void setService(ComponentService service)
    {
        this.service = service;
    }

    public void setUtil(Util util)
    {
        this.util = util;
    }

}