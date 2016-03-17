package rin.crecovery;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.util.UUID;
import java.util.concurrent.ExecutionException;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements ActivityInteractionListener {

    // Message types used by the Handler
    public static final int MESSAGE_WRITE = 1;
    public static final int MESSAGE_READ = 2;

    private final static int TMHR_NUM = 220;
    private double hr = 0;

    private BluetoothAdapter btAdapter = null;
    private BtSendReceive mBtData;

    private EditText ptAge;
    private EditText minPercent;
    private EditText maxPercent;
    private TextView output1;
    private TextView output2;
    private TextView bpm;

    private FragmentInteractionListener mFragListener;
    private MainFragInteractionListener mListener;

    private BtDiscoveryReceiver receiver;

    public MainFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment MainFragment.
     */
    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        /*Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        IntentFilter filter = new IntentFilter(BluetoothDevice.ACTION_FOUND);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_STARTED);
        filter.addAction(BluetoothAdapter.ACTION_DISCOVERY_FINISHED);

        receiver = new BtDiscoveryReceiver();
        getContext().registerReceiver(receiver,
                filter);

        /*if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
        }*/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_main, container, false);

        ptAge = (EditText) view.findViewById(R.id.ptAge);
        minPercent = (EditText) view.findViewById(R.id.minPercentage);
        maxPercent = (EditText) view.findViewById(R.id.maxPercentage);
        output1 = (TextView) view.findViewById(R.id.txtOutput1);
        output2 = (TextView) view.findViewById(R.id.txtOutput2);
        bpm = (TextView) view.findViewById(R.id.bpm);

        return view;
    }

    @Override
    public void onButtonPressed() {
        double[] hrpm = calculate();
        output1.setText("Lowest Heart Rate/minute: " + Double.toString(hrpm[0]));
        output2.setText("Highest Heart Rate/minute: " + Double.toString(hrpm[1]));

        if (mBtData != null) {
            try {
                Runnable r =  new Runnable() {
                    @Override
                    public void run() {
                        try {
                            hr = BtSendReceive.toDouble(mBtData.read());
                            String beats = hr + " BPM";
                            bpm.setText(beats);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        } catch (ExecutionException e) {
                            e.printStackTrace();
                        }
                    }
                };
                new Thread(r).start();

                if (hr != 0) {
                    if (hr <= hrpm[0] || hr >= hrpm[1])
                        mBtData.send("on");
                    else
                        mBtData.send("off");
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    @Override
    public void onLongButtonPressed() {
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof MainFragInteractionListener &&
                context instanceof FragmentInteractionListener) {
            mFragListener = (FragmentInteractionListener) context;
            mListener = (MainFragInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement MainFragInteractionListener");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContext().unregisterReceiver(receiver);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mFragListener = null;
        mListener = null;
    }

    private double[] calculate() {
        double[] hrpm = new double[2];

        try {
            int tmhr = TMHR_NUM - Integer.parseInt(ptAge.getText().toString());
            double minimum = Double.parseDouble(minPercent.getText().toString());
            double maximum = Double.parseDouble(maxPercent.getText().toString());

            if (minimum > maximum) {
                throw new Exception("");
            }

            hrpm[0] = Math.round(minimum * 0.01 * tmhr);
            hrpm[1] = Math.round(maximum * 0.01 * tmhr);
        } catch (NumberFormatException e) {
            if (mFragListener != null)
                mFragListener.onCreateSnackbar(ptAge, "Please enter a number for your info.");
        } catch (Exception e) {
            if (mFragListener != null)
                mFragListener.onCreateSnackbar(ptAge, "Make sure you enter the correct min and max values.");
        }

        return hrpm;
    }

    @Override
    public boolean bluetooth() {
        btAdapter = BluetoothAdapter.getDefaultAdapter();

        if (btAdapter != null) {
            if (!btAdapter.isEnabled()) {
                // ask user to turn bluetooth on
                Intent i = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(i, 1);
            }
        } else {
            mFragListener.onCreateSnackbar(ptAge, "Bluetooth not available.");
            return false;
        }

        if (!btAdapter.isDiscovering())
            btAdapter.startDiscovery();

        return true;
    }

    public class ConnectBluetooth extends AsyncTask {

        private final UUID uuid = UUID.fromString("7499f0e0-de32-11e5-b86d-9a79f06e9478");
        private boolean isBtConnected = false;

        private boolean successConnect = true; //if here, it's almost connected

        private BluetoothSocket btSocket = null;

        private String address;

        public ConnectBluetooth(String address) {
            this.address = address;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Object doInBackground(Object[] params) {
            btAdapter = BluetoothAdapter.getDefaultAdapter();
            BluetoothDevice device = btAdapter.getRemoteDevice(address);

            try {
                if (btSocket == null || !isBtConnected) {
                    btSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                    btAdapter.cancelDiscovery();

                    mBtData = new BtSendReceive(btSocket);
                    btSocket.connect(); // start connection
                }
            } catch (IOException e) {
                e.printStackTrace();

                try {
                    btSocket = (BluetoothSocket) device.getClass()
                            .getMethod("createRfcommSocket", new Class[] {int.class})
                            .invoke(device, 1);
                    btSocket.connect();
                } catch (Exception e2) {
                    successConnect = false;
                    e2.printStackTrace();
                }
            }

            return null;
        }

        @Override
        protected void onPostExecute(Object o) {
            super.onPostExecute(o);

            if (!successConnect) {
                mFragListener.onCreateSnackbar(ptAge, "Cannot connect to device.");
            } else {
                mFragListener.onCreateSnackbar(ptAge, "Connected to device.");
                isBtConnected = true;
            }
        }

    }

    public class BtDiscoveryReceiver extends BroadcastReceiver {

        private ConnectBluetooth connectBluetooth;

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.e("BtDiscoveryReceiver", "Broadcast receiver");

            if (BluetoothDevice.ACTION_FOUND.equals(action)) {
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                mFragListener.onCreateSnackbar(ptAge, "Found device.");

                //if (discoveredDevice.equals("linvor")) {
                connectBluetooth = new ConnectBluetooth(device.getAddress());
                connectBluetooth.execute("");
                //}
            }
        }

    }



    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface MainFragInteractionListener {
    }

}
