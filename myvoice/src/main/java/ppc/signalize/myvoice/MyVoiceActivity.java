package ppc.signalize.myvoice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.text.Layout;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.style.AlignmentSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.webkit.JavascriptInterface;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import org.apache.http.conn.util.InetAddressUtils;
import org.xwalk.core.XWalkView;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import it.gmariotti.cardslib.library.internal.Card;
import ppc.signalize.mira.body.MiraAbstractActivity;
import ppc.signalize.mira.body.parts.nervous.concurrent.AsyncMiraTextResponse;
import ppc.signalize.mira.body.parts.nervous.wifi.DeviceListFragment;
import ppc.signalize.mira.body.parts.nervous.wifi.VoiceCard;
import ppc.signalize.mira.body.parts.nervous.wifi.WiFiDirectBroadcastReceiver;
import ppc.signalize.mira.body.util.DiscussionDisplay;
import ppc.signalize.myvoice.util.SystemUiHider;


/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 *
 * @see SystemUiHider
 */
public class MyVoiceActivity extends MiraAbstractActivity {

    public static final String TAG = "MyVoiceActivity";
    private static final int SERVER_PORT = 1030;
    private final IntentFilter intentFilter = new IntentFilter();
    protected WifiP2pDevice mDevice;
    protected WifiP2pManager.PeerListListener peerListListener;
    Thread serverThread = null;
    Handler updateConversationHandler;
    private boolean retryChannel = false;
    private BroadcastReceiver receiver = null;
    private TextView mira_text;
    private ToggleButton mira_ear;
    private boolean bypassCheckChange = false;
    private LayoutInflater vi;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private boolean isWifiP2pEnabled;
    private ArrayList<WifiP2pDevice> peers = new ArrayList<WifiP2pDevice>();
    private DiscussionDisplay mira_display;
    private boolean serverIsRunning = false;
    private boolean clientIsRunning = false;
    private boolean connectionEstablished = false;
    private String sIP;
    private ArrayList<InetAddress> clients = new ArrayList<InetAddress>();
    private ServerSocket serverSocket;
    private Socket socket;

    public static InetAddress getInetAddress() {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        return addr;
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return null;
    }

    public static String getIPAddress(boolean useIPv4) {

        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = InetAddressUtils.isIPv4Address(sAddr);
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim < 0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "";
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {


        setContentView(R.layout.activity_my_voice);
        vi = (LayoutInflater) getApplicationContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        super.onCreate(savedInstanceState);
        updateConversationHandler = new Handler();
        peerListListener = new DeviceListFragment(this);

        // Add the fragment to the 'fragment_container' FrameLayout
        getFragmentManager().beginTransaction()
                .add(R.id.content_frame, (android.app.Fragment) peerListListener).commit();

        //  Indicates a change in the Wi-Fi P2P status.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        prepareHeading(vi);
        prepareAlert(vi);
        prepareInput(vi);

        //prepareContent(vi);

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);



    }

    @Override
    public void onResume() {
        super.onResume();
        receiver = new WiFiDirectBroadcastReceiver(mManager, mChannel, this);
        registerReceiver(receiver, intentFilter);
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                Log.d(TAG, "woot");
                // Code for when the discovery initiation is successful goes here.
                // No services have actually been discovered yet, so this method
                // can often be left blank.  Code for peer discovery goes in the
                // onReceive method, detailed below.
            }

            @Override
            public void onFailure(int reasonCode) {
                // Code for when the discovery initiation fails goes here.
                // Alert the user that something went wrong.
            }
        });
        //init();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        View contentView = findViewById(R.id.fullscreen_content);
        if (hasFocus) {
            contentView.setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                            | View.SYSTEM_UI_FLAG_FULLSCREEN
                            | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
            );
        }
    }

    @Override
    public Spannable createSpan(String words, int aligned) {
        Spannable wordtoSpan = new SpannableString("\r\n" + words + "\r\n");
        int end = wordtoSpan.length();
        if (aligned == ALIGN_MIRA)
            wordtoSpan.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_NORMAL), 0, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        else
            wordtoSpan.setSpan(new AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE), 0, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);

        return colorSpan(wordtoSpan, aligned);
    }

    public Spannable colorSpan(Spannable span, int color) {
        int end = span.length();
        if (color == ALIGN_MIRA)
            span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.mira_text)), 0, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        else
            span.setSpan(new ForegroundColorSpan(getResources().getColor(R.color.voice_text)), 0, end, Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        return span;
    }

    private void prepareContent(LayoutInflater vi) {
        mira_display = new DiscussionDisplay();
        getFragmentManager().beginTransaction()
                .add(R.id.content_frame, mira_display).commit();

        mira_text = (TextView) findViewById(R.id._text);
        mira_ear = (ToggleButton) findViewById(R.id.mira_ear);

        mira_ear.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (bypassCheckChange)
                    return;
                if (isChecked & !myVoice.isSpeechRecognitionServiceActive) {
                    mira.listen();
                } else if (!isChecked && myVoice.isSpeechRecognitionServiceActive) {
                    mira.stop_listen();
                }
            }
        });


    }

    @Override
    public void prependMiraText(String toSpan, int aligned) {
        Spannable span = createSpan(toSpan, aligned);
        Spannable currentText = (Spannable) mira_text.getText();
        CharSequence newText = TextUtils.concat(span, currentText);
        mira_text.setText(newText);
    }

    @Override
    protected void startListening() {
        bypassCheckChange = true;
        mira_ear.setChecked(true);
        bypassCheckChange = false;
    }

    @Override
    public boolean canListen() {

        return mira_ear.isChecked();
    }

    @JavascriptInterface
    public void postOffer(String offer) {

        Toast.makeText(MyVoiceActivity.this, offer,
                Toast.LENGTH_SHORT).show();
    }

    private void prepareAlert(LayoutInflater vi) {

        XWalkView v = new XWalkView(getApplicationContext(), this);
        View insertPoint = findViewById(R.id.alert_area);
        ((LinearLayout) insertPoint).addView(v, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

        v.loadUrl("http://repository.signalize.ws/webrtc.html");
        v.addJavascriptInterface(this, "MyVoice");


    }

    private void prepareHeading(LayoutInflater vi) {
        View display = vi.inflate(R.layout.content_head, null);
        TextView text = (TextView) display.findViewById(R.id.header_text);
        text.setText(getString(R.string.mira_header));

        View insertPoint = findViewById(R.id.heading_frame);
        ((LinearLayout) insertPoint).addView(display);

    }

    private void prepareInput(LayoutInflater vi) {
        View display = vi.inflate(R.layout.text_entry, null);

        final EditText eddie = (EditText) display.findViewById(R.id.entry_text);
        eddie.setInputType(InputType.TYPE_NULL);

        final ImageButton keys = (ImageButton) display.findViewById(R.id.keys);

        keys.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                touchEddie(eddie);
            }
        });

        eddie.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                touchEddie(eddie);
                return false;
            }
        });
        View insertPoint = findViewById(R.id.input_frame);
        ((LinearLayout) insertPoint).addView(display);

    }

    private void touchEddie(final EditText eddie) {
        eddie.setInputType(InputType.TYPE_CLASS_TEXT);
        eddie.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null) {
                    // if shift key is down, then we want to insert the '\n' char in the TextView;
                    // otherwise, the default action is to send the message.
                    if (!event.isShiftPressed()) {
                        new AsyncMiraTextResponse(myVoice).execute(String.valueOf(v.getText()));
                        v.setText("");
                        return true;
                    }
                    return false;
                }


                new AsyncMiraTextResponse(myVoice).execute(String.valueOf(v.getText()));
                v.setText("");
                eddie.setInputType(InputType.TYPE_NULL);
                InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.toggleSoftInput(0, 0);

                return true;

            }
        });
        eddie.setTextColor(Color.DKGRAY);
        eddie.requestFocus();
        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.showSoftInput(eddie, InputMethodManager.SHOW_FORCED);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);


    }

    @Override
    public void setIsWifiP2pEnabled(boolean isWifiP2pEnabled) {
        this.isWifiP2pEnabled = isWifiP2pEnabled;
    }

    @Override
    public WifiP2pManager.PeerListListener getPeerListener() {
        return peerListListener;
    }

    @Override
    public WifiP2pDevice getDevice() {
        return mDevice;
    }

    @Override
    public void setDevice(WifiP2pDevice parcelableExtra) {
        mDevice = parcelableExtra;
    }

    @Override
    public List<WifiP2pDevice> getPeers() {
        return peers;
    }

    @Override
    public void onChannelDisconnected() {

    }

    @Override
    public void showDetails(WifiP2pDevice device) {

    }

    @Override
    public void cancelDisconnect() {

    }

    @Override
    public void connect() {
        // Picking the first device found on the network.
        WifiP2pDevice device = getDevice();

        WifiP2pConfig config = new WifiP2pConfig();
        config.deviceAddress = device.deviceAddress;
        config.wps.setup = WpsInfo.PBC;

        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {

            @Override
            public void onSuccess() {
                mManager.requestConnectionInfo(mChannel, MyVoiceActivity.this);
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MyVoiceActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void disconnect() {

    }

    public void startServer() throws IOException {
        clients.clear();
        ServerSocket serverSocket = new ServerSocket(SERVER_PORT);

        // Collect client ip's
        while (true) {
            Socket clientSocket = serverSocket.accept();
            clients.add(clientSocket.getInetAddress());
            clientSocket.close();
        }
    }

    @Override
    public void onConnectionInfoAvailable(final WifiP2pInfo info) {

        // InetAddress from WifiP2pInfo struct.
        InetAddress groupOwnerAddress = info.groupOwnerAddress;

        // After the group negotiation, we can determine the group owner.
        if (info.groupFormed && info.isGroupOwner & !serverIsRunning) {
            serverThread = new Thread(new ServerThread());
            serverThread.start();
            serverIsRunning = true;
        } else if (info.groupFormed & !clientIsRunning) {
            sIP = groupOwnerAddress.getHostAddress().toUpperCase();
            new Thread(new ClientThread(groupOwnerAddress, getIPAddress(true))).start();
            serverThread = new Thread(new ServerThread());
            serverThread.start();
            clientIsRunning = true;
        }
    }

    @Override
    public void onItemSelected(String id) {


    }

    @Override
    public void onClick(Card c, View v) {
        if (c instanceof VoiceCard) {
            VoiceCard w = (VoiceCard) c;

        }
    }

    class ClientThread implements Runnable {
        private final InetAddress mIP;
        private final String mMess;

        ClientThread(InetAddress ip, String message) {
            mIP = ip;
            mMess = message;
        }

        @Override
        public void run() {

            try {
                InetAddress serverAddr = mIP;

                socket = new Socket(serverAddr, SERVER_PORT);
                PrintWriter out = null;
                try {
                    out = new PrintWriter(new BufferedWriter(
                            new OutputStreamWriter(socket.getOutputStream())),
                            true
                    );

                    out.println(mMess);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } catch (UnknownHostException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

        }

    }

    class ServerThread implements Runnable {

        public void run() {
            Socket socket = null;
            try {
                serverSocket = new ServerSocket(SERVER_PORT);
            } catch (IOException e) {
                e.printStackTrace();
            }
            while (!Thread.currentThread().isInterrupted()) {

                try {

                    socket = serverSocket.accept();

                    CommunicationThread commThread = new CommunicationThread(socket);
                    new Thread(commThread).start();

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class CommunicationThread implements Runnable {

        private Socket clientSocket;

        private BufferedReader input;

        public CommunicationThread(Socket clientSocket) {

            this.clientSocket = clientSocket;

            try {

                this.input = new BufferedReader(new InputStreamReader(this.clientSocket.getInputStream()));

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void run() {

            while (!Thread.currentThread().isInterrupted()) {

                try {

                    String read = input.readLine();
                    if (read == null || read.equals(""))
                        continue;
                    Log.d(TAG, "received from client: " + read);
                    if (read.equals("connection-complete")) {
                        updateConversationHandler.post(new establishTwoWayComm(sIP, "offer will go here"));
                    } else if (InetAddressUtils.isIPv4Address(read)) {
                        sIP = read;
                        updateConversationHandler.post(new establishTwoWayComm(sIP, "connection-complete"));
                    } else if (read.equals("testing connection")) {
                        updateConversationHandler.post(new establishTwoWayComm(sIP, "connection is good"));
                    } else if (read.equals("connection is good")) {
                        continue;
                    } else {
                        updateConversationHandler.post(new establishTwoWayComm(sIP, "testing connection"));
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

    }

    class establishTwoWayComm implements Runnable {
        private final String ip;
        private String msg;

        public establishTwoWayComm(String ip, String str) {
            this.msg = str;
            this.ip = ip;
        }

        @Override
        public void run() {
            Log.d(TAG, "sending " + msg + " to: " + ip);
            try {
                new Thread(new ClientThread(InetAddress.getByName(ip), msg)).start();
            } catch (UnknownHostException e) {
                e.printStackTrace();
            }
        }
    }
}
