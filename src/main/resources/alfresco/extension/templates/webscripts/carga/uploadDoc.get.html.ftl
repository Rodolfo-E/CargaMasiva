<html>
<head>
	<meta charset="utf-8">
	<style type="text/css">
	.enjoy-input {
		display: inline-block;
		padding: 5px 10px;
		border: 1px solid black;
		border-radius: 5px;
		color: rgba(76, 76, 76, 0.82);
		background: rgba(252,252,252,1);
		box-shadow: 2px 2px 2px 0 rgba(0,0,0,0.2) inset;
		text-shadow: 1px 1px 0 rgba(255,255,255,0.66) ;
		transition: all 200ms cubic-bezier(0.42, 0, 0.58, 1);
		
	}

	.enjoy-input:hover:enabled  {
		border: 1px solid #a3a3a3;
		background: rgba(255,255,255,1);
		transition: all 100ms cubic-bezier(0.42, 0, 0.58, 1);
	}

	.enjoy-input:focus:enabled  {
		border: 1px solid #018dc4;
		box-shadow: 4px 4px 4px 0 rgba(0,0,0,0.2) inset;
		transition: all 50ms cubic-bezier(0.42, 0, 0.58, 1);
	}

	.enjoy-css {
		display: inline-block;
		-webkit-box-sizing: content-box;
		-moz-box-sizing: content-box;
		box-sizing: content-box;
		margin: 5px;
		padding: 0 2em;
		border: 1px solid rgba(211,211,211,1);
		-webkit-border-radius: 0.2em;
		border-radius: 0.2em;
		font: normal normal bold 1em/2em Arial, Helvetica, sans-serif;
		color: rgba(114,114,114,1);
		-o-text-overflow: clip;
		text-overflow: clip;
		white-space: nowrap;
		background: rgba(234,234,234,1);
		box-shadow: 0 0 1px 1px rgba(255,255,255,0.8) , 0 1px 0 0 rgba(0,0,0,0.298039) ;
		text-shadow: 0 1px 0 rgba(255,255,255,0.8) ;
		transition: all 200ms cubic-bezier(0.42, 0, 0.58, 1) 10ms;
	}

	.enjoy-css:hover:enabled {
		cursor: pointer;
		border: 1px solid rgba(178,178,178,1);
		color: rgba(76,76,76,1);
		box-shadow: 0 0 1px 1px rgba(255,255,255,0.8) inset, 0 1px 0 0 rgba(0,0,0,0.298039) ;
	}

	.enjoy-css:active:enabled {
		position: relative;
		cursor: default;
		top: 1px;
		border: 1px solid rgba(211,211,211,1);
		color: rgba(114,114,114,1);
		background: rgba(247,247,247,1);
		box-shadow: 0 0 1px 1px rgba(255,255,255,0.8) inset, 0 1px 0 0 rgba(0,0,0,0.298039) inset;
		transition: none;
	}

	.span-tt {
		color: rgba(76, 76, 76, 0.82);
		padding: 5px 5px;
		text-align: center;
		font-weight: bold;
		font: normal normal bold 1em/2em Arial, Helvetica, sans-serif;
	}

	.div-spc {
		padding-left: 30px;
	}

	.span-t2 {
		color: rgba(0, 81, 6, 0.82);
		padding-left: 30px;
		font-weight: bold;
		font-family: Arial, Helvetica, sans-serif;
		font-size: 10px;
	}
	.container1 {
		width: 100%;  
		display: flex;
		flex-wrap: wrap;
		justify-content: center;
		align-items: center;
		background-repeat: no-repeat;
		background-size: 30%;
		background-position: center top;
	}
	.container2 {
		width: 600px;
		background: #ffffff00;
		border-radius: 8px;
	}
</style>
<script type="text/javascript">
	function ValidateSingleInput(oInput,extensionsPermitidas,numaxsize) {
		if (oInput.type == "file" && (extensionsPermitidas != null || extensionsPermitidas != "")) {
			var validFileExtensions = extensionsPermitidas.split(",");
			var sFileName = oInput.value;
			if (sFileName.length > 0) {
				var blnValid = false;
				for (var j = 0; j < validFileExtensions.length; j++) {
					var sCurExtension = validFileExtensions[j];
					if (sFileName.substr(sFileName.length - sCurExtension.length, sCurExtension.length).toLowerCase() == sCurExtension.toLowerCase()) {
						blnValid = true;
						break;
					}
				}			             
				if (!blnValid) {
					alert("Lo siento, " + sFileName + " es invalido, las extensiones permitidas son: " + validFileExtensions.join(", "));
					oInput.value = "";
					return false;
				}
			}
			var filesize = oInput.files[0].size / 1024 / 1024;
			numaxsize = parseInt(numaxsize.replace(".", "").replace(",", ""),10);
			if(filesize > numaxsize) {
				alert('Solo se permiten archivos de máximo ' + numaxsize + ' MB.\nSu archivo pesa ' + filesize.toFixed(1) + ' MB');
				oInput.value = "";
				return false;
			} else if (filesize == 0) {
				alert('El archivo está vacío.');
				oInput.value = "";
				return false;
			}
		}
		return true;
	}

	function validateInputs() {

		var oForm = document.forms["formUpload"];
		for (i = 0; i < oForm.length-1; i++) { 
			var oInputFile = oForm.elements[i];
			if(oInputFile.value == "")
			{ 
				alert("Falta Seleccionar el archivo: "+oInputFile.name);
				return false;
			}
		}
		return true;
	}

	function validateForm(formObj) {  
		formObj.btnCargar.disabled = true;  
		formObj.btnCargar.value = 'Espere por favor...';
		document.getElementById("divdisa").style.display = "none";
		/*var oForm = document.forms["formUpload"];
		for (i = 0; i < oForm.length-1; i++) { 
			var oInputFile = oForm.elements[i];
			if(oInputFile.type.indexOf("file") != -1){
				oInputFile.readOnly = true;
			}
		}*/
		return true;
	} 
</script>
</head>
<body>
	<div class="container1" >
		<div class="container2">
			<form action="${url.service}?alf_ticket=${alf_ticket}" method="post" enctype="multipart/form-data" name="formUpload" 
			onsubmit="return validateForm(this);">
			<#if key != "0">
			<#if filtro?size != 0>
			<div id="divdisa">
				<#list filtro as fil>
				<span class="span-tt">${fil.alfNombre}:</span></br>
				<div class="div-spc" >
					<input type="file" name="${fil.alfNombre}" class="enjoy-input" 
					onchange="return ValidateSingleInput(this,'${fil.extension}','${fil.peso}');" accept="${fil.extension}"/>
				</div>
				<span class ="span-t2">Tamaño Maximo Permitido: ${fil.peso?replace(",", "")?replace(".", "")} MB</span> </br>
				<span class ="span-t2">Extensión(es) Permitida(s): 
					<#if fil.extension?length == 0>
					.*
					<#else>
					${fil.extension}
					</#if>
				</span></br>
				</#list>
			</div>
		</br>
		<input type="submit" class="enjoy-css" name="btnCargar" value="Cargar" onclick="return validateInputs()">
		<input type="hidden" name="keyh" value="${key}">
		<#else>
		<p>NO SE ENCONTRARON ELEMENTOS CON EL KEY: ${key}.</p>
		</#if>
		<#else>
		<p>KEY INVALIDO, NO SE PUEDE RECUPERAR DATOS.</p>
		</#if>
	</form>
</div>
</div>
</body>
</html>