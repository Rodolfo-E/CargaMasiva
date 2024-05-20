package pe.com.domain.webscripts;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.FormData;

import pe.com.domain.bean.BeanWS;
import pe.com.domain.bean.Constantes;
import pe.com.domain.util.ComponentService;
import pe.com.domain.util.Util;

public class RegisterParams extends DeclarativeWebScript {

    private static Log logger = LogFactory.getLog(RegisterParams.class);

	private ComponentService service;
	private Util util;

	protected Map<String, Object> executeImpl(WebScriptRequest req, Status status) 
	{
	    logger.debug("RegisterParams WebScript called.");
	    
		String key = null;
		String sesTicket = null;
		String alfTicket = req.getParameter("alf_ticket");
		String listUuid = null;
		String listName = null;
		String listExt = null;
		String listSize = null;
		String listIdDocumento = null;
		String idSistema = null;
		String userOefa = null;
		String pathAlfresco = null;

		final FormData form = (FormData) req.parseContent();
		for (FormData.FormField field : form.getFields()) 
		{
			switch (field.getName()) 
			{
    			case "sesTicket":
    				sesTicket = field.getValue();
    				break;
    			case "listUuid":
    				listUuid = field.getValue();
    				break;
    			case "listName":
    				listName = field.getValue();
    				break;
    			case "listExt":
    				listExt = field.getValue();
    				break;
    			case "listSize":
    				listSize = field.getValue();
    				break;
    			case "listIdDocumento":
    				listIdDocumento = field.getValue();
    				break;
    			case "idSistema":
    				idSistema = field.getValue();
    				break;
    			case "userOefa":
    				userOefa = field.getValue();
    				break;
    			case "pathAlfresco":
    				pathAlfresco = field.getValue();
    				break;
			}
		}
		
		logger.debug("VALIDANDO PARAMETROS...");
		if (!Util.validarParametros(sesTicket, alfTicket, userOefa, idSistema, listUuid, listName, pathAlfresco, listExt, listSize, listIdDocumento)) 
		    return buildResponse("0", "EX003", util.getMessage("EX003"));
		    
		boolean session = false;
		try 
		{
			session = service.validarTicketWS(idSistema, sesTicket);
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
			return buildResponse("0", "EX002", util.getMessage("EX002"));
		}
		
		logger.debug("SESION WS:" + session);
		String listIdUnico = "";
		
		if (!session) 
		    return buildResponse("0", "EX001", util.getMessage("EX001"));
		
		logger.debug("REGISTRANDO EN WS: ESTADO REGISTRADO");
		boolean isUpload = false;
		int registros = 0;
		if (Util.isBlank(listUuid)) 
		{
			registros = listName.split(";").length;
			isUpload = true;
		} 
		else 
		{
			registros = listUuid.split(";").length;
			isUpload = false;
		}
		
		logger.debug("CANTIDAD DE REGISTROS: "+registros);
		for (int i = 0; i < registros; i++) 
		{
			BeanWS res = new BeanWS();
			try 
			{
				if (isUpload) 
				{
					res = service.registrarEstadoWS(sesTicket, alfTicket,
							"", listName.split(";")[i],
							listExt.split(";")[i], listSize.split(";")[i],
							listIdDocumento.split(";")[i], idSistema,
							userOefa, pathAlfresco, "",
							Constantes.ESTADO_REGISTRADO);
				} 
				else 
				{
					res = service.registrarEstadoWS(sesTicket, alfTicket,
							listUuid.split(";")[i], listName.split(";")[i],
							listExt.split(";")[i], listSize.split(";")[i],
							listIdDocumento.split(";")[i], idSistema,
							userOefa, pathAlfresco, "",
							Constantes.ESTADO_REGISTRADO);
				}
				
				if (res.getIdError().equals("1")) 
				{
					listIdUnico += res.getMsjError() + ";";
				} 
				else 
				{
					logger.error("IdError WS OEFA: " + res.getIdError());
					logger.error("MsjError WS OEFA: " + res.getMsjError());
					throw new Exception(res.getMsjError());
				}
			} 
			catch (Exception ex) 
			{
				ex.printStackTrace();
				return buildResponse("0", "EX002", util.getMessage("EX002"));
			}
		}
		logger.debug("SE REGISTRO EN WS");				
		try {
			if (!Util.isBlank(listIdUnico)) 
				listIdUnico = listIdUnico.substring(0, listIdUnico.length() - 1);
			
			logger.debug("REGISTRANDO EN AUDITORIA: ESTADO REGISTRADO CON IDs UNICOS ("+listIdUnico+")");
			key = service.registrarCargaBitacoraBD(sesTicket, alfTicket,
					listUuid, listName, listExt, listSize,
					listIdDocumento, idSistema, userOefa, pathAlfresco,
					listIdUnico, null,isUpload);
			
			if(Util.isBlank(key))
				return buildResponse("0", "EX003", util.getMessage("EX003"));
			
			return buildResponse(key, "00000", "Registro Exitoso");
		} 
		catch (Exception ex) 
		{
			ex.printStackTrace();
			return buildResponse("0", "EX004", util.getMessage("EX004"));
		}
	}
	
	private Map<String, Object> buildResponse(String key, String code, String message)
	{
	    Map<String, Object> response = new HashMap<>();
	    response.put("key", key);
	    response.put("code", code);
	    response.put("message", message);
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