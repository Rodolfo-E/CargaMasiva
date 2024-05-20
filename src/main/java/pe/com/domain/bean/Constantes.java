package pe.com.domain.bean;

public class Constantes
{
    // TABLA MAESTRA DE ESTADO
    public static final int ESTADO_REGISTRADO = 1;
    public static final int ESTADO_PROCESANDO = 2;
    public static final int ESTADO_FINALIZADO = 3;
    public static final int ESTADO_ERROR = 4;

    // TABLA MAESTRA DE RESULTADO
    public static final int RESULTADO_OK = 1;
    public static final int RESULTADO_ERROR_ALFRESCO = 2;
    public static final int RESULTADO_ERROR_WS = 3;
    public static final int RESULTADO_INICIO = 4;

    // TABLA MAESTRA DE METODO
    public static final int METODO_CARGA = 1;
    public static final int METODO_ACTUALIZACION = 2;
    public static final int METODO_DESCARGA = 3;

    // STORE PROCEDURE DE BD AUDITORIA
    public static final String SP_SOLICITUD = "{call INS_AUD_SOLICITUD(?,?,?,?,?,?,?)}";
    public static final String SP_BITACORA = "{call INS_AUD_BITACORA(?,?,?,?,?,?)}";
    public static final String SP_DOCUMENTO = "{call INS_AUD_DOCUMENTO(?,?,?,?,?,?,?,?)}";
    
}