package retrofit.client;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.io.BufferedInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import ppc.signalize.perspectives.content.Signalize;
import retrofit.RetrofitError;
import retrofit.mime.TypedInput;
import retrofit.mime.TypedOutput;


public class URLClient extends UrlConnectionClient {

    private static final int CHUNK_SIZE = 4096;
    private final Field methodField;
    SharedPreferences preferences;
    private Context myContext;

    public URLClient(Activity active) {
        myContext = active.getApplicationContext();
        preferences = PreferenceManager.getDefaultSharedPreferences(myContext);
        try {
            this.methodField = HttpURLConnection.class.getDeclaredField("method");
            this.methodField.setAccessible(true);
        } catch (NoSuchFieldException e) {
            throw RetrofitError.unexpectedError(null, e);
        }
    }

    public URLClient(Signalize s) {
        this(s.getContext());
    }


    @Override
    public Response execute(Request request) throws IOException {
        HttpURLConnection connection = openConnection(request);
        prepareRequest(connection, request);
        return readResponse(connection);
    }

    protected HttpURLConnection openConnection(Request request) throws IOException {
        HttpsURLConnection connection =
                (HttpsURLConnection) new URL(request.getUrl()).openConnection();
        connection.setConnectTimeout(Defaults.CONNECT_TIMEOUT_MILLIS);
        connection.setReadTimeout(Defaults.READ_TIMEOUT_MILLIS);

        BufferedInputStream caInput = new BufferedInputStream(myContext.getResources().getAssets().open("chain.crt", Context.MODE_PRIVATE));

        try {
            // Load CAs from an InputStream
            // (could be from a resource or ByteArrayInputStream or ...)
            CertificateFactory cf = CertificateFactory.getInstance("X.509", new BouncyCastleProvider());
            // From https://www.washington.edu/itconnect/security/ca/load-der.crt
            //InputStream caInput = new BufferedInputStream(new FileInputStream("load-der.crt"));
            Certificate ca = cf.generateCertificate(caInput);
            System.out.println("ca=" + ((X509Certificate) ca).getSubjectDN());


            // Create a KeyStore containing our trusted CAs
            String keyStoreType = KeyStore.getDefaultType();
            KeyStore keyStore = KeyStore.getInstance(keyStoreType);
            keyStore.load(null, null);
            keyStore.setCertificateEntry("ca", ca);

            // Create a TrustManager that trusts the CAs in our KeyStore
            String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
            TrustManagerFactory tmf = TrustManagerFactory.getInstance(tmfAlgorithm);
            tmf.init(keyStore);


            // Create an SSLContext that uses our TrustManager
            SSLContext context = SSLContext.getInstance("TLS");
            context.init(null, tmf.getTrustManagers(), null);
            connection.setSSLSocketFactory(context.getSocketFactory());


        } catch (CertificateException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        } catch (KeyStoreException e) {
            e.printStackTrace();
        } catch (KeyManagementException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            caInput.close();
        }
        return connection;
    }

    void prepareRequest(HttpURLConnection connection, Request request) throws IOException {
        // HttpURLConnection artificially restricts request method
        try {
            connection.setRequestMethod(request.getMethod());
        } catch (ProtocolException e) {
            try {
                methodField.set(connection, request.getMethod());
            } catch (IllegalAccessException e1) {
                throw RetrofitError.unexpectedError(request.getUrl(), e1);
            }
        }

        connection.setDoInput(true);

        for (Header header : request.getHeaders()) {
            if (header.getName().equals("Authorization"))
                connection.addRequestProperty(header.getName(), "bearer " + header.getValue());
            else
                connection.addRequestProperty(header.getName(), header.getValue());
        }

        TypedOutput body = request.getBody();
        if (body != null) {
            connection.setDoOutput(true);
            connection.addRequestProperty("Content-Type", body.mimeType());
            long length = body.length();
            if (length != -1) {
                connection.setFixedLengthStreamingMode((int) length);
                connection.addRequestProperty("Content-Length", String.valueOf(length));
            } else {
                connection.setChunkedStreamingMode(CHUNK_SIZE);
            }
            body.writeTo(connection.getOutputStream());
        }
    }

    Response readResponse(HttpURLConnection connection) throws IOException {
        int status = connection.getResponseCode();
        String reason = connection.getResponseMessage();

        List<Header> headers = new ArrayList<Header>();
        for (Map.Entry<String, List<String>> field : connection.getHeaderFields().entrySet()) {
            String name = field.getKey();
            for (String value : field.getValue()) {
                headers.add(new Header(name, value));
            }
        }

        String mimeType = connection.getContentType();
        int length = connection.getContentLength();
        InputStream stream;
        if (status >= 400) {
            stream = connection.getErrorStream();
        } else {
            stream = connection.getInputStream();
        }
        TypedInput responseBody = new TypedInputStream(mimeType, length, stream);
        return new Response(connection.getURL().toString(), status, reason, headers, responseBody);
    }

    private static class TypedInputStream implements TypedInput {
        private final String mimeType;
        private final long length;
        private final InputStream stream;

        private TypedInputStream(String mimeType, long length, InputStream stream) {
            this.mimeType = mimeType;
            this.length = length;
            this.stream = stream;
        }

        @Override
        public String mimeType() {
            return mimeType;
        }

        @Override
        public long length() {
            return length;
        }

        @Override
        public InputStream in() throws IOException {
            return stream;
        }
    }

}

