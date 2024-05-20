package pe.com.domain.util;

import java.io.IOException;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.model.Repository;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.model.FileExistsException;
import org.alfresco.service.cmr.model.FileFolderService;
import org.alfresco.service.cmr.repository.ContentReader;
import org.alfresco.service.cmr.repository.ContentService;
import org.alfresco.service.cmr.repository.ContentWriter;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.MimetypeService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.security.AuthenticationService;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpMethod;
import org.apache.commons.httpclient.HttpMethodRetryHandler;
import org.apache.commons.httpclient.NoHttpResponseException;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import pe.com.domain.bean.Documento;

public class Util
{

    @SuppressWarnings("unused")
    private static final Log logger = LogFactory.getLog(Util.class);
    private static final StoreRef STORE_REF = StoreRef.STORE_REF_WORKSPACE_SPACESSTORE;
    private static final String BASE_PATH = "workspace/SpacesStore/Company Home";
    static HttpMethodRetryHandler myretryhandler;
    
    private NodeService nodeService;
    private FileFolderService fileFolderService;
    private Repository repository;
    private DictionaryService dictionaryService;
    private AuthenticationService authenticationService;
    private ContentService contentService;

    public static boolean isBlank(String ptext)
    {
        return ptext == null || ptext.trim().length() == 0 || ptext.trim().toLowerCase().equals("null");
    }

    public static boolean containsIllegals(String toExamine)
    {
        Pattern pattern = Pattern.compile("[~#@*+%{}<>\\[\\]|\"\\^]");
        Matcher matcher = pattern.matcher(toExamine);
        return matcher.find();
    }

    public static Documento obtenerDocumento(List<Documento> listDocs, String name)
    {
        Documento resultado = null;
        for (Documento documento : listDocs)
        {
            if (documento.getAlfNombre().equals(name))
                resultado = documento;
        }
        return resultado;
    }

    static HttpClient setHttp()
    {
        HttpClient client = new HttpClient();
        client.getHttpConnectionManager().getParams().setConnectionTimeout(30000);
        myretryhandler = new HttpMethodRetryHandler()
        {
            public boolean retryMethod(final HttpMethod method, final IOException exception, int executionCount)
            {
                if (executionCount >= 1)
                    return false;
                
                if (exception instanceof NoHttpResponseException)
                    return true;
                
                if (!method.isRequestSent())
                    return true;
                
                return false;
            }
        };
        return client;
    }

    public static boolean validarParametros(String sesTicket, String alfTicket, String userOefa, String idSistema, String listUuid,
            String listName, String pathAlfresco, String listExt, String listSize, String listIdDocumento)
    {
        if (isBlank(sesTicket) || isBlank(alfTicket) || isBlank(userOefa) || isBlank(idSistema))
            return false;
        
        int sizeList = 0;
        if (isBlank(listUuid))
        {
            if (isBlank(listName) || isBlank(pathAlfresco))
                return false;
            
            if (containsIllegals(pathAlfresco))
                return false;
            
            sizeList = listName.split(";").length;
            
            if (listIdDocumento.split(";").length != sizeList)
                return false;
            
            if (listSize.split(";").length != sizeList)
                return false;
            
            if (listExt.split(";").length != sizeList)
                return false;
        } 
        else
        {
            if (isBlank(listName))
                return false;
            
            sizeList = listUuid.split(";").length;
            
            if (listIdDocumento.split(";").length != sizeList)
                return false;
            
            if (listSize.split(";").length != sizeList)
                return false;
            
            if (listExt.split(";").length != sizeList)
                return false;
        }
        
        if (!isBlank(listExt))
        {
            if (listExt.split(";").length != sizeList)
                return false;
        }
        
        if (!isBlank(listSize))
        {
            if (listSize.split(";").length != sizeList)
                return false;
        }
        
        if (!isBlank(listIdDocumento))
        {
            if (listIdDocumento.split(";").length != sizeList)
                return false;
        }
        
        if (!isInt(idSistema))
        {
            return false;
        }
        
        for (int i = 0; i < sizeList; i++)
        {
            if (!isInt(listSize.split(";")[i]) || !isInt(listIdDocumento.split(";")[i]))
                return false;
        }
        
        return true;
    }

    public static boolean isInt(String val)
    {
        try
        {
            Integer.parseInt(val);
            return true;
        }
        catch (NumberFormatException ex)
        {
            return false;
        }
    }
    
    public String getTicket()
    {
        return authenticationService.getCurrentTicket();
    }

    public NodeRef getCompanyHome()
    {
        return repository.getCompanyHome();
    }

    public NodeRef getNodeByUuid(String uuid)
    {
        if (isBlank(uuid))
            return null;
        
        return new NodeRef(STORE_REF, uuid);
    }
    
    public NodeRef getNodeByPath(String nodePath)
    {
        if (isBlank(nodePath))
            return null;
        
        nodePath = BASE_PATH + "/" + nodePath;
        return repository.findNodeRef("path", formatPath(nodePath).split("/"));
    }
    
    public String getDownloadUrl(String uuid)
    {
        String fileName = (String) nodeService.getProperty(new NodeRef(STORE_REF, uuid), ContentModel.PROP_NAME);
        StringBuilder downloadUrl = new StringBuilder("/s/slingshot/node/content/workspace/SpacesStore/");
        downloadUrl.append(uuid);
        downloadUrl.append("/");
        downloadUrl.append(fileName);
        downloadUrl.append("?a=true&alf_ticket=");
        downloadUrl.append(getTicket());
        
        return downloadUrl.toString();
    }
    
    public String getNodeName(NodeRef node) throws InvalidNodeRefException
    {
        return (String) nodeService.getProperty(node, ContentModel.PROP_NAME);
    }
    
    public NodeRef upload(InputStream is, NodeRef parent, String fileName) throws FileExistsException
    {
        NodeRef newNode = fileFolderService.create(parent, fileName, ContentModel.TYPE_CONTENT).getNodeRef();
        
        if (!nodeService.hasAspect(newNode, ContentModel.ASPECT_VERSIONABLE))    
            nodeService.addAspect(newNode, ContentModel.ASPECT_VERSIONABLE, null);
        
        ContentWriter writer = contentService.getWriter(newNode, ContentModel.PROP_CONTENT, true);
        writer.guessMimetype(fileName);
        writer.guessEncoding();
        writer.putContent(is);
        return newNode;
    }
    
    public NodeRef update(InputStream is, String uuid) throws InvalidNodeRefException
    {
        NodeRef nodeToUpdate = getNodeByUuid(uuid);
        
        if (!nodeService.hasAspect(nodeToUpdate, ContentModel.ASPECT_VERSIONABLE))    
            nodeService.addAspect(nodeToUpdate, ContentModel.ASPECT_VERSIONABLE, null);
        
        ContentWriter writer = contentService.getWriter(nodeToUpdate, ContentModel.PROP_CONTENT, true);
        writer.putContent(is);
        return nodeToUpdate;
    }
    
    public String getExtension(String fileName)
    {
        return FilenameUtils.getExtension(fileName);
    }

    public NodeRef createFolderByPath(String folderPath)
    {
        if (isBlank(folderPath))
            return null;
        
        folderPath = formatPath(folderPath);
        NodeRef currentNodeRef = repository.getCompanyHome();
        String currentPath = BASE_PATH;
        List<String> pathElements = Arrays.asList(folderPath.split("/"));
        for (String folderName : pathElements)
        {
            currentPath += "/" + folderName;
            NodeRef foundNodeRef = repository.findNodeRef("path", currentPath.split("/"));
            if (foundNodeRef != null)
            {
                currentNodeRef = foundNodeRef;
                continue;
            }
            foundNodeRef = createFolder(currentNodeRef, folderName);
            currentNodeRef = foundNodeRef;
        }
        return currentNodeRef;
    }
    
    public NodeRef createFolder(NodeRef parent, String folderName)
    {
        return fileFolderService.create(parent, folderName, ContentModel.TYPE_FOLDER).getNodeRef();
    }
    
    public String formatPath(String path)
    {
        if (path.trim().startsWith("/"))
            path = path.trim().substring(1, path.length());
        
        if (path.trim().endsWith("/"))
            path = path.trim().substring(0, path.length() - 1);
        
        return path;
    }
    
    public String getMessage(String key)
    {
        String message = dictionaryService.getMessage(key);
        
        if (message == null)
            return "No se encontró mensaje para el codigo de error.";
        
        return message;
    }
    
    public String getMessage(String key, Object... params)
    {
        String message = dictionaryService.getMessage(key, params);
        return message;
    }
    
    public static long getSizeIns(InputStream is) throws IOException
    {
        long size = 0L;
        int chunk = 0;
        byte[] buffer = new byte[1024];
        while ((chunk = is.read(buffer)) != -1)
            size += chunk;
        return size;
    }
    
    @SuppressWarnings("unused")
    private static String normalize(String name) 
    {
        String convertedString = Normalizer.normalize(name, Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
        return convertedString.replaceAll("[^-A-Za-z0-9-.]", "_");
    }
    
    public void setNodeService(NodeService nodeService)
    {
        this.nodeService = nodeService;
    }

    public void setFileFolderService(FileFolderService fileFolderService)
    {
        this.fileFolderService = fileFolderService;
    }

    public void setRepository(Repository repository)
    {
        this.repository = repository;
    }

    public void setDictionaryService(DictionaryService dictionaryService)
    {
        this.dictionaryService = dictionaryService;
    }

    public void setAuthenticationService(AuthenticationService authenticationService)
    {
        this.authenticationService = authenticationService;
    }

    public void setContentService(ContentService contentService)
    {
        this.contentService = contentService;
    }

}
