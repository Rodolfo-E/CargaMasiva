package pe.com.domain.util;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.json.JSONObject;

import com.google.gson.Gson;

import oracle.jdbc.OracleTypes;
import pe.com.domain.bean.BeanWS;
import pe.com.domain.bean.Constantes;
import pe.com.domain.bean.Documento;

public class ComponentServiceImpl implements ComponentService
{
    
    private static Log logger = LogFactory.getLog(ComponentServiceImpl.class);

    private DataSource dataSource;
    private String wsEndpoint;

    @Override
    public List<Documento> consultaGetDocs(String key) throws Exception
    {
        List<Documento> list = new ArrayList<Documento>();
        String procedureConsultaDocs = "{call GET_DOCS(?,?)}";

        Connection connection = null;
        CallableStatement callStatement = null;
        ResultSet resultSet = null;
        try
        {
            connection = dataSource.getConnection();
            callStatement = connection.prepareCall(procedureConsultaDocs);
            callStatement.setString(1, key);
            callStatement.registerOutParameter(2, OracleTypes.CURSOR);
            callStatement.execute();
            resultSet = (ResultSet) callStatement.getObject(2);
            ResultSetMetaData rsetMeta = resultSet.getMetaData();
            int count_rows = rsetMeta.getColumnCount();
            while (resultSet.next())
            {
                Documento doc = new Documento();
                for (int i = 1; i <= count_rows; i++)
                {
                    switch (rsetMeta.getColumnName(i).toUpperCase())
                    {
                        case "ALF_RUTA":
                            doc.setAlfRuta(resultSet.getString(i));
                            break;
                        case "WS_TICKET":
                            doc.setSesTicket(resultSet.getString(i));
                            break;
                        case "ALF_TICKET":
                            doc.setAlfTicket(resultSet.getString(i));
                            break;
                        case "APP_ID_DOCUMENTO":
                            doc.setAppIdDocumento(resultSet.getInt(i));
                            break;
                        case "APP_ID":
                            doc.setAppId(resultSet.getInt(i));
                            break;
                        case "WS_ID_UNICO":
                            doc.setWsIdUnico(resultSet.getInt(i));
                            break;
                        case "PESO":
                            doc.setPeso(resultSet.getInt(i));
                            break;
                        case "EXTENSION":
                            doc.setExtension(resultSet.getString(i));
                            break;
                        case "ALF_NOMBRE":
                            doc.setAlfNombre(resultSet.getString(i));
                            break;
                        case "ALF_UUID":
                            doc.setAlfUuid(resultSet.getString(i));
                            break;
                        case "ID_ESTADO":
                            doc.setIdEstado(resultSet.getInt(i));
                            break;
                        case "ID_DOCUMENTO":
                            doc.setIdDocumento(resultSet.getInt(i));
                            break;
                    }
                }
                list.add(doc);
            }
            return list;
        }
        catch (SQLException ex)
        {
            throw new Exception(ex);
        } 
        finally
        {
            try { if (connection != null) connection.close(); } catch (Exception ex) { ex.printStackTrace(); }
            try { if (resultSet != null) resultSet.close(); } catch (Exception ex) { ex.printStackTrace(); }
            try { if (callStatement != null) callStatement.close(); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    @Override
    public List<Documento> consultaGetParams(String key) throws Exception
    {
        List<Documento> list = new ArrayList<Documento>();
        String procedureConsultaParams = "{call GET_PARAMS(?,?)}";

        Connection connection = null;
        CallableStatement callStatement = null;
        ResultSet resultSet = null;
        try
        {
            connection = dataSource.getConnection();
            callStatement = connection.prepareCall(procedureConsultaParams);
            callStatement.setString(1, key);
            callStatement.registerOutParameter(2, OracleTypes.CURSOR);
            callStatement.execute();
            resultSet = (ResultSet) callStatement.getObject(2);
            ResultSetMetaData rsetMeta = resultSet.getMetaData();
            int count_rows = rsetMeta.getColumnCount();
            while (resultSet.next())
            {
                Documento doc = new Documento();
                for (int i = 1; i <= count_rows; i++)
                {
                    switch (rsetMeta.getColumnName(i).toUpperCase()) 
                    {
                        case "PESO":
                            doc.setPeso(resultSet.getInt(i));
                            break;
                        case "EXTENSION":
                            if (!Util.isBlank(resultSet.getString(i)))
                                doc.setExtension(resultSet.getString(i).replace("|", ","));
                            else
                                doc.setExtension("");
                            break;
                        case "ALF_NOMBRE":
                            doc.setAlfNombre(resultSet.getString(i));
                            break;
                    }
                    
                }
                list.add(doc);
            }
            return list;
        }
        catch (Exception ex)
        {
            throw new Exception(ex);
        } 
        finally
        {
            try { if (connection != null) connection.close(); } catch (Exception ex) { ex.printStackTrace(); }
            try { if (resultSet != null) resultSet.close(); } catch (Exception ex) { ex.printStackTrace(); }
            try { if (callStatement != null) callStatement.close(); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }

    @Override
    public String registrarCargaBitacoraBD(String sesTicket, String alfTicket, String listUuid, String listName, String listExt, String listSize,
            String listIdDocumento, String idSistema, String userOefa, String pathAlfresco, String listIdUnico, String observacion,
            boolean isUpload) throws Exception
    {
        String key = null;
        
        String[] arrayIdDocumento = listIdDocumento.split(";");
        String[] arrayName = listName.split(";");
        
        String[] arrayUuid = null;
        if (!isUpload) arrayUuid = listUuid.split(";");
        
        String[] arraySize = listSize.split(";");
        String[] arrayExt = listExt.split(";");
        String[] arrayIdUnico = listIdUnico.split(";");
        
        Connection connection = null;
        CallableStatement callStatement = null;
        try
        {
            connection = dataSource.getConnection();
            callStatement = connection.prepareCall(Constantes.SP_SOLICITUD);
            callStatement.setString(1, sesTicket);
            callStatement.setString(2, alfTicket);
            callStatement.setString(3, userOefa);
            callStatement.setInt(4, isUpload ? Constantes.METODO_CARGA : Constantes.METODO_ACTUALIZACION);
            callStatement.setString(5, pathAlfresco);
            callStatement.registerOutParameter(6, Types.VARCHAR);
            callStatement.registerOutParameter(7, Types.INTEGER);
            callStatement.execute();
            key = callStatement.getString(6);
            logger.debug("key: " + key);
            int idSolicitud = callStatement.getInt(7);
            
            for (int i = 0; i < arrayIdDocumento.length; i++) 
            {
                int size = Util.isBlank(arraySize[i]) ? 0 : Integer.parseInt(arraySize[i]);
                String ext = Util.isBlank(arrayExt[i]) ? null : arrayExt[i];
                int idDocumento = saveAuditDocumento(idSolicitud, arrayName[i], size, ext, Integer.parseInt(idSistema), Integer.parseInt(arrayIdDocumento[i]), arrayIdUnico[i]);
                if (idDocumento > 0)
                {
                    String uuid = isUpload ? null : arrayUuid[i];
                    saveOrUpdateBitacora(idDocumento, Constantes.ESTADO_REGISTRADO, uuid, Constantes.RESULTADO_INICIO, observacion);
                }
            }
            return key;
        }
        catch (Exception ex)
        {
            throw new Exception(ex);
        } 
        finally
        {
            try { if (connection != null) connection.close(); } catch (Exception ex) { ex.printStackTrace(); }
            try { if (callStatement != null) callStatement.close(); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
    
    @Override
    public String registrarDescargaBitacoraBD(String sesTicket, String alfTicket, String uuid, String idSistema) throws Exception
    {
        String key = null;
        
        Connection connection = null;
        CallableStatement callStatement = null;
        try
        {
            connection = dataSource.getConnection();
            callStatement = connection.prepareCall(Constantes.SP_SOLICITUD);
            callStatement.setString(1, sesTicket);
            callStatement.setString(2, alfTicket);
            callStatement.setString(3, null);
            callStatement.setInt(4, Constantes.METODO_DESCARGA);
            callStatement.setString(5, null);
            callStatement.registerOutParameter(6, Types.VARCHAR);
            callStatement.registerOutParameter(7, Types.INTEGER);
            callStatement.execute();
            key = callStatement.getString(6);
            logger.debug("key: " + key);
            int idSolicitud = callStatement.getInt(7);
            
            int idDocumento = saveAuditDocumento(idSolicitud, null, 0, null, Integer.parseInt(idSistema), 0, null);
            if (idDocumento > 0)
                saveOrUpdateBitacora(idDocumento, Constantes.ESTADO_REGISTRADO, uuid, Constantes.RESULTADO_INICIO, null);
            
            return key;
        }
        catch (Exception ex)
        {
            throw new Exception(ex);
        } 
        finally
        {
            try { if (connection != null) connection.close(); } catch (Exception ex) { ex.printStackTrace(); }
            try { if (callStatement != null) callStatement.close(); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
    
    @Override
    public int saveOrUpdateBitacora(int idDocumento, int idEstado, String uuid, int idResultado, String observacion) throws Exception
    {
        Connection connection = null;
        CallableStatement callStatement = null;
        try
        {
            connection = dataSource.getConnection();
            callStatement = connection.prepareCall(Constantes.SP_BITACORA);
            callStatement.setInt(1, idDocumento);
            callStatement.setInt(2, idEstado);
            callStatement.setString(3, uuid);
            callStatement.setInt(4, idResultado);
            callStatement.setString(5, observacion);
            callStatement.registerOutParameter(6, Types.INTEGER);
            callStatement.execute();
            int idBitacora = callStatement.getInt(6);
            logger.debug("Registrado o actualizado en BD auditoria con idBitacora: " + idBitacora);
            return idBitacora;
        }
        catch (SQLException ex)
        {
            throw new Exception(ex);
        }
        finally
        {
            try { if (connection != null) connection.close(); } catch (Exception ex) { ex.printStackTrace(); }
            try { if (callStatement != null) callStatement.close(); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
    
    @Override
    public boolean validarTicketWS(String idSistema, String wsTicket) throws Exception
    {
        logger.debug("Validando ticket WS");
        logger.debug("\tParameters[" + idSistema + ", " + wsTicket + "]");
        URL url = null;
        try
        {
            url = new URL(wsEndpoint + "/sessionkeyws/validarSessionKey/" + idSistema + "/" + wsTicket);
            logger.debug("\tEndpoint: " + url.toString());
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.setRequestProperty("Accept", "application/json");
            
            if (urlConnection.getResponseCode() != 200) {
                throw new RuntimeException("Failed : HTTP Error code: "  + urlConnection.getResponseCode());
            }
            
            InputStreamReader in = new InputStreamReader(urlConnection.getInputStream());
            BufferedReader br = new BufferedReader(in);
            String output;
            String value = "";
            while ((output = br.readLine()) != null) {
                value += output;
            }
            
            logger.debug("\tResponse: " + value);
            BeanWS resultado = new Gson().fromJson(value, BeanWS.class);
            urlConnection.disconnect();
            if (!resultado.getRESULTADO().toUpperCase().equals("OK"))
                throw new Exception(resultado.getRESULTADO());
            return true;
        }
        catch (Exception ex)
        {
            throw new Exception(ex);
        }
    }

    @Override
    public BeanWS registrarEstadoWS(String sesTicket, String alfTicket, String uuid, String name, String ext, String size,
            String idDocumento, String idSistema, String userOefa, String pathAlfresco, String idUnico, int idEstado) throws Exception
    {
        logger.debug("Registrando estado en WS");
        HttpClient client = Util.setHttp();
        try
        {
            BeanWS resultado = null;
            String cadena = "";
            JSONObject jsonObj = new JSONObject();
            jsonObj.put("ticketSession", sesTicket);
            jsonObj.put("ticketAutenticacion", alfTicket);
            jsonObj.put("idDOcumento", Util.isBlank(idDocumento) ? "" : idDocumento);
            jsonObj.put("idSistema", idSistema);
            jsonObj.put("idUnico", idUnico);
            jsonObj.put("rutaAlfresco", Util.isBlank(pathAlfresco) ? "" : pathAlfresco);
            jsonObj.put("tamanho", size);
            jsonObj.put("extension", ext);
            jsonObj.put("nombreArchivo", Util.isBlank(name) ? "" : name);
            jsonObj.put("uuid", uuid);
            jsonObj.put("estado", idEstado + "");
            jsonObj.put("usuarioOEFAReg", userOefa);
            jsonObj.put("usuarioOEFAMod", userOefa);
            logger.debug("\tJSON: " + jsonObj.toString());
            
            PostMethod mPost = new PostMethod(wsEndpoint + "/auditoriaOEFAws/");
            logger.debug("\tEndpoint: " + mPost.getURI().toString());
            mPost.setRequestEntity(new StringRequestEntity(jsonObj.toString(),
                    "application/json", "UTF-8"));
            mPost.getParams().setParameter(HttpMethodParams.RETRY_HANDLER, Util.myretryhandler);
            client.executeMethod(mPost);
            
            ByteArrayOutputStream result = new ByteArrayOutputStream();
            byte[] buffer = new byte[1024];
            int length;
            while ((length = mPost.getResponseBodyAsStream().read(buffer)) != -1) {
                result.write(buffer, 0, length);
            }
            cadena = result.toString("UTF-8");
            logger.debug("\tResponse: " + cadena);
            result.close();
            resultado = new Gson().fromJson(cadena, BeanWS.class);
            return resultado;
        }
        catch (UnsupportedEncodingException ex)
        {
            throw new Exception(ex);
        }
        catch (HttpException ex)
        {
            throw new Exception(ex);
        }
        catch (IOException ex)
        {
            throw new Exception(ex);
        }
    }
    
    private int saveAuditDocumento(int idSolicitud, String name, int size, String ext, int idSistema, 
            int inIdDocumento, String idUnico)
    {
        Connection connection = null;
        CallableStatement callStatement = null;
        try
        {
            connection = dataSource.getConnection();
            callStatement = connection.prepareCall(Constantes.SP_DOCUMENTO);
            callStatement.setInt(1, idSolicitud);
            callStatement.setString(2, name);
            callStatement.setInt(3, size);
            callStatement.setString(4, ext);
            callStatement.setInt(5, idSistema);
            callStatement.setInt(6, (inIdDocumento == 0) ? null : inIdDocumento);
            callStatement.setString(7, idUnico);
            callStatement.registerOutParameter(8, Types.INTEGER);
            callStatement.execute();
            int idDocumento = callStatement.getInt(8);
            logger.debug("Registrado en BD auditoria con idDocumento: " + idDocumento);
            return idDocumento;
        }
        catch (Exception ex)
        {
           ex.printStackTrace();
           return 0;
        }
        finally
        {
            try { if (connection != null) connection.close(); } catch (Exception ex) { ex.printStackTrace(); }
            try { if (callStatement != null) callStatement.close(); } catch (Exception ex) { ex.printStackTrace(); }
        }
    }
    
    public void setDataSource(DataSource dataSource)
    {
        this.dataSource = dataSource;
    }

    public void setWsEndpoint(String wsEndpoint)
    {
        this.wsEndpoint = wsEndpoint;
    }

}
