package pe.com.domain.bean;

import java.util.List;

public class RptaBean
{

    private Status status;
    private String message;
    private String time;
    private String server;
    private String exception;
    private List<String> callstack;
    private String code;

    public Status getStatus()
    {
        return status;
    }

    public void setStatus(Status status)
    {
        this.status = status;
    }

    public String getMessage()
    {
        return message;
    }

    public void setMessage(String message)
    {
        this.message = message;
    }

    public String getTime()
    {
        return time;
    }

    public void setTime(String time)
    {
        this.time = time;
    }

    public String getServer()
    {
        return server;
    }

    public void setServer(String server)
    {
        this.server = server;
    }

    public String getException()
    {
        return exception;
    }

    public void setException(String exception)
    {
        this.exception = exception;
    }

    public List<String> getCallstack()
    {
        return callstack;
    }

    public void setCallstack(List<String> callstack)
    {
        this.callstack = callstack;
    }

    public String getCode()
    {
        String value = "";
        if (status.getCode() != 200)
            value = message.substring(0, 5);
        else
            value = "OK";
        return value;
    }

    public void setCode(String code)
    {
        this.code = code;
    }

}