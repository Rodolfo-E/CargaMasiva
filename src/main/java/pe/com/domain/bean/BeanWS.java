package pe.com.domain.bean;

public class BeanWS
{
    private String MSG;
    private String RESULTADO;
    private String idError;
    private String msjError;

    public String getMSG()
    {
        return MSG;
    }

    public void setMSG(String mSG)
    {
        MSG = mSG;
    }

    public String getRESULTADO()
    {
        return RESULTADO;
    }

    public void setRESULTADO(String rESULTADO)
    {
        RESULTADO = rESULTADO;
    }

    public String getIdError()
    {
        return idError;
    }

    public void setIdError(String idError)
    {
        this.idError = idError;
    }

    public String getMsjError()
    {
        return msjError;
    }

    public void setMsjError(String msjError)
    {
        this.msjError = msjError;
    }

}