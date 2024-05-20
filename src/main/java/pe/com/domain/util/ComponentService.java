package pe.com.domain.util;

import java.util.List;

import pe.com.domain.bean.BeanWS;
import pe.com.domain.bean.Documento;

public interface ComponentService
{

    public List<Documento> consultaGetDocs(String key) throws Exception;

    public List<Documento> consultaGetParams(String key) throws Exception;

    public String registrarCargaBitacoraBD(String sesTicket, String alfTicket, String listUuid, String listName, String listExt,
            String listSize, String listIdDocumento, String idSistema, String userOefa, String pathAlfresco, String listIdUnico,
            String observacion, boolean isUpload) throws Exception;

    public String registrarDescargaBitacoraBD(String sesTicket, String alfTicket, String uuid, String idSistema) throws Exception;

    public int saveOrUpdateBitacora(int idDocumento, int idEstado, String uuid, int idResultado, String observacion) throws Exception;

    public boolean validarTicketWS(String idSistema, String wsTicket) throws Exception;

    public BeanWS registrarEstadoWS(String sesTicket, String alfTicket, String uuid, String name, String ext, String size,
            String idDocumento, String idSistema, String userOefa, String pathAlfresco, String idUnico, int idEstado) throws Exception;

}
