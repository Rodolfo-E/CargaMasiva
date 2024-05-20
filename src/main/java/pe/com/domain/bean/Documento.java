package pe.com.domain.bean;

public class Documento
{
    private int idDocumento;
    private String alfTicket;
    private String sesTicket;
    private String alfRuta;
    private String alfNombre;
    private int peso;
    private String extension;
    private int appId;
    private int appIdDocumento;
    private int wsIdUnico;
    private String alfUuid;
    private int idEstado;

    public Documento()
    {
        super();
    }

    public Documento(int idDocumento, String alfTicket, String alfRuta, String alfNombre, int peso, String extension, int appId, int appIdDocumento, int wsIdUnico, String sesTicket, String alfUuid,
            int idEstado)
    {
        super();
        this.idDocumento = idDocumento;
        this.alfTicket = alfTicket;
        this.alfRuta = alfRuta;
        this.alfNombre = alfNombre;
        this.peso = peso;
        this.extension = extension;
        this.appId = appId;
        this.appIdDocumento = appIdDocumento;
        this.wsIdUnico = wsIdUnico;
        this.sesTicket = sesTicket;
        this.idEstado = idEstado;
        this.alfUuid = alfUuid;

    }

    public String getAlfUuid()
    {
        return alfUuid;
    }

    public void setAlfUuid(String alfUuid)
    {
        this.alfUuid = alfUuid;
    }

    public int getIdEstado()
    {
        return idEstado;
    }

    public void setIdEstado(int idEstado)
    {
        this.idEstado = idEstado;
    }

    public String getSesTicket()
    {
        return sesTicket;
    }

    public void setSesTicket(String sesTicket)
    {
        this.sesTicket = sesTicket;
    }

    public int getIdDocumento()
    {
        return idDocumento;
    }

    public void setIdDocumento(int idDocumento)
    {
        this.idDocumento = idDocumento;
    }

    public String getAlfTicket()
    {
        return alfTicket;
    }

    public void setAlfTicket(String alfTicket)
    {
        this.alfTicket = alfTicket;
    }

    public String getAlfRuta()
    {
        return alfRuta;
    }

    public void setAlfRuta(String alfRuta)
    {
        this.alfRuta = alfRuta;
    }

    public String getAlfNombre()
    {
        return alfNombre;
    }

    public void setAlfNombre(String alfNombre)
    {
        this.alfNombre = alfNombre;
    }

    public int getPeso()
    {
        return peso;
    }

    public void setPeso(int peso)
    {
        this.peso = peso;
    }

    public String getExtension()
    {
        return extension;
    }

    public void setExtension(String extension)
    {
        this.extension = extension;
    }

    public int getAppId()
    {
        return appId;
    }

    public void setAppId(int appId)
    {
        this.appId = appId;
    }

    public int getAppIdDocumento()
    {
        return appIdDocumento;
    }

    public void setAppIdDocumento(int appIdDocumento)
    {
        this.appIdDocumento = appIdDocumento;
    }

    public int getWsIdUnico()
    {
        return wsIdUnico;
    }

    public void setWsIdUnico(int wsIdUnico)
    {
        this.wsIdUnico = wsIdUnico;
    }
    
}