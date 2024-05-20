package pe.com.domain.webscripts;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;

import pe.com.domain.bean.Documento;
import pe.com.domain.util.ComponentService;
import pe.com.domain.util.Util;

public class ReadParams extends DeclarativeWebScript {

    private static Log logger = LogFactory.getLog(ReadParams.class);

	private ComponentService service;
	private Util util;

    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) 
    {
        logger.debug("ReadParams WebScript called.");
        
		Map<String, String> templateArgs = req.getServiceMatch().getTemplateVars();
		String key = templateArgs.get("key");
		List<Documento> resultado = new ArrayList<>();
		
		try 
		{
			if (Util.isBlank(key)) 
				return buildResponse(resultado, "0", "");
			
			resultado = service.consultaGetParams(key);
			String ticket = util.getTicket();
			
			if (Util.isBlank(ticket))
			    return buildResponse(resultado, "0", "");
			
			return buildResponse(resultado, key, ticket);
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
			return buildResponse(resultado, key, "");
		}
	}
    
    private Map<String, Object> buildResponse(List<Documento> filtro, String key, String alf_ticket)
    {
        Map<String, Object> response = new HashMap<>();
        response.put("filtro", filtro);
        response.put("key", key);
        response.put("alf_ticket", alf_ticket);
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