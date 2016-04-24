import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.XmlRpcRequest;
import org.apache.xmlrpc.server.RequestProcessorFactoryFactory;

public class ApiFactoryPlant implements
        RequestProcessorFactoryFactory {
    private final RequestProcessorFactory factory =
            new ApiFactory();
    private final Api api;

    public ApiFactoryPlant(Api api) {
        this.api = api;
    }

    public RequestProcessorFactory getRequestProcessorFactory(Class aClass)
            throws XmlRpcException {
        return factory;
    }

    private class ApiFactory implements RequestProcessorFactory {
        public Object getRequestProcessor(XmlRpcRequest xmlRpcRequest)
                throws XmlRpcException {
            return api;
        }
    }
}