package pe.com.domain.webscripts;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.extensions.webscripts.DeclarativeWebScript;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptRequest;
import org.springframework.extensions.webscripts.servlet.FormData;

import pe.com.domain.bean.Documento;
import pe.com.domain.util.ComponentService;
import pe.com.domain.util.Util;

public class Upload extends DeclarativeWebScript
{

    private static Log logger = LogFactory.getLog(Upload.class);

    private ComponentService service;
    private Util util;
    
    @Override
    protected Map<String, Object> executeImpl(WebScriptRequest req, Status status)
    {

        String key = null;
        String rutaCarga = null;
        NodeRef rutaBase = null;
        Map<String, Object> model = new HashMap<>();
        String result = "";
        
        final FormData form = (FormData) req.parseContent();
        for (FormData.FormField field : form.getFields())
        {
            if (field.getName().toLowerCase().equals("keyh"))
            {
                key = field.getValue();
            }
        }
        
        logger.debug("RECUPERANDO DATOS - KEY:" + key);
        List<Documento> listDocs = new ArrayList<>();
        try
        {
            listDocs = service.consultaGetDocs(key);
        } 
        catch (Exception ex)
        {
            ex.printStackTrace();
            return buildResponse(ex.getMessage());
        }
        logger.debug("CANTIDAD DE DOCUMENTOS RECUPERADOS: " + listDocs.size());
        
        if (listDocs.size() > 0)
            rutaCarga = ((Documento) listDocs.get(0)).getAlfRuta();
        
        rutaBase = util.createFolderByPath(rutaCarga);
        
        if (rutaBase == null)
            rutaBase = util.getCompanyHome();
        
        for (FormData.FormField field : form.getFields())
        {
            logger.debug("SE ENCONTRO: " + field.getName());
            String nameDoc = field.getName();
            if (field.getIsFile())
            {
                logger.debug("OBTENIENDO DATOS DEL DOCUMENTO");
                Documento doc = Util.obtenerDocumento(listDocs, field.getName());
                
                logger.debug("REGISTRO WS DE ESTADO: PROCESANDO");
                try
                {
                    service.registrarEstadoWS(doc.getSesTicket(), doc.getAlfTicket(), doc.getAlfUuid(), doc.getAlfNombre(), "", "", 
                            String.valueOf(doc.getAppIdDocumento()), String.valueOf(doc.getAppId()), "", doc.getAlfRuta(), String.valueOf(doc.getWsIdUnico()), 2);
                    saveToBitacora(doc.getIdDocumento(), 2, null, 4, null);
                }
                catch (Exception ex)
                {
                    logger.error(doc.getIdDocumento() + " - " +util.getMessage("EX002"));
                    saveToBitacora(doc.getIdDocumento(), 4, null, 3, util.getMessage("EX002"));
                    ex.printStackTrace();
                    return buildResponse(util.getMessage("EX002"));
                }
                
                InputStream is = null;
                long sizeFile = 0L;
                try
                {
                    logger.debug("CARGA ALFRESCO: INICIO");
                    is = field.getInputStream();
                    if (doc.getPeso() > 0)
                    {
                        sizeFile = Util.getSizeIns(field.getInputStream());
                        if (sizeFile / 1048576L > doc.getPeso())
                        {
                            result = String.valueOf(result) + doc.getAlfNombre() + ": EX005-" + util.getMessage("EX005") + "</br>";
                            continue;
                        }
                    }
                }
                catch (IOException ex)
                {
                    ex.printStackTrace();
                    result = String.valueOf(result) + doc.getAlfNombre() + ":" + ex.getMessage() + "</br>";
                    continue;
                }
                if (Util.isBlank(doc.getAlfUuid()))
                {
                    Timestamp timestamp = new Timestamp(System.currentTimeMillis());
                    nameDoc = String.valueOf(nameDoc) + "_" + timestamp.getTime() + "." + util.getExtension(field.getFilename());
                    String uuid = "";
                    try
                    {
                        uuid = util.upload(is, rutaBase, nameDoc).getId();
                        service.registrarEstadoWS(doc.getSesTicket(), doc.getAlfTicket(), uuid, nameDoc, "." + util.getExtension(nameDoc),
                                String.valueOf(sizeFile / 1024L), String.valueOf(doc.getAppIdDocumento()),
                                String.valueOf(doc.getAppId()), "", doc.getAlfRuta(), String.valueOf(doc.getWsIdUnico()), 3);
                        saveToBitacora(doc.getIdDocumento(), 3, uuid, 1, null);
                    } 
                    catch (FileExistsException ex)
                    {
                        logger.error(doc.getIdDocumento() + " - " + util.getMessage("EX007"));
                        saveToBitacora(doc.getIdDocumento(), 4, null, 2, util.getMessage("EX007"));
                        ex.printStackTrace();
                        return buildResponse(util.getMessage("EX007"));
                    }
                    catch (Exception ex)
                    {
                        logger.error(doc.getIdDocumento() + " - " +util.getMessage("EX002"));
                        saveToBitacora(doc.getIdDocumento(), 4, null, 3, util.getMessage("EX002"));
                        ex.printStackTrace();
                        return buildResponse(util.getMessage("EX002"));
                    }
                    
                    return buildResponse(nameDoc + ": OK");
                }
                else
                {
                    String name = "";
                    try
                    {
                        NodeRef updatedNode = util.update(is, doc.getAlfUuid());
                        name = util.getNodeName(updatedNode);
                        service.registrarEstadoWS(doc.getSesTicket(), doc.getAlfTicket(), doc.getAlfUuid(), name, "." + util.getExtension(field.getFilename()),
                                String.valueOf(sizeFile / 1024L), String.valueOf(doc.getAppIdDocumento()),
                                String.valueOf(doc.getAppId()), "", doc.getAlfRuta(), String.valueOf(doc.getWsIdUnico()), 3);
                        saveToBitacora(doc.getIdDocumento(), 3, doc.getAlfUuid(), 1, null);
                    }
                    catch (InvalidNodeRefException ex)
                    {
                        logger.error(doc.getIdDocumento() + " - " + util.getMessage("EX008"));
                        saveToBitacora(doc.getIdDocumento(), 4, null, 2, util.getMessage("EX008"));
                        ex.printStackTrace();
                        return buildResponse(util.getMessage("EX007"));
                    }
                    catch (Exception ex)
                    {
                        logger.error(doc.getIdDocumento() + " - " +util.getMessage("EX002"));
                        saveToBitacora(doc.getIdDocumento(), 4, null, 2, util.getMessage("EX002"));
                        ex.printStackTrace();
                        return buildResponse(util.getMessage("EX002"));
                    }
                    
                    return buildResponse(name + ": OK");
                }
            }
        }
        model.put("result", result);
        return model;
    }
    
    private void saveToBitacora(int idDocumento, int idEstado, String uuid, int idResultado, String observacion)
    {
        try
        {
            service.saveOrUpdateBitacora(idDocumento, idEstado, uuid, idResultado, observacion);
        }
        catch (Exception ex)
        {
            logger.error(idDocumento + " - " + util.getMessage("EX004"));
            ex.printStackTrace();
        }
    }
    
    private Map<String, Object> buildResponse(String result)
    {
        Map<String, Object> response = new HashMap<>();
        response.put("result", result);
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
