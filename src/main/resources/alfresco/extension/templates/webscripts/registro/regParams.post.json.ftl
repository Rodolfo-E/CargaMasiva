<#escape x as jsonUtils.encodeJSONString(x)>
{
   "message": "${message}",
   "key": "${key}",
   "status": {
   		"code": "200",
   		"description": "Proceso finalizado."
   },
   "code": "${code}"
}
</#escape>