package rin.crecovery;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;
import java.util.UUID;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link MainFragInteractionListener} interface
 * to handle interaction events.
 * Use the {@link MainFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MainFragment extends Fragment implements ActivityInteractionListener {

    private final static int TMHR_NUM = 220;

    private BluetoothAdapter btAdapter = null;
    private BluetoothSocket btSocket = null;
    private boolean isBtConnected = false;
    private static final UUID uuid = UUID.fromString("7499f0e0-de32-11e5-b86d-9a79f06e9478");

    private EditText ptAge;
    private EditText minPercent;
    private EditText maxPercent;
    private TextView output1;
    private TextView output2;

    private FragmentInteractionListener mFragListener;
    private MainFragInteractionListener mListener;

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

        return view;
    }

    @Override
    public void onButtonPressed() {
        double[] hrpm = calculate();
        output1.setText("Lowest Heart Rate/minute: " + Double.toString(hrpm[0]));
        output2.setText("Highest Heart Rate/minute: " + Double.toString(hrpm[1]));

        /*double hr = getHeartRate();
        if (hr != 0) {
            if (hr <= hrpm[0] || hr >= hrpm[1])
                ledOn();
            else
                ledOff();
        }*/

        sendData(Double.toString(hrpm[0]), Double.toString(hrpm[1]));

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
    public void onDetach() {
        super.onDetach();
        mFragListener = null;
        mListener = null;
    }

    /*private double getHeartRate(BluetoothSocket socket) {
        double hr = 0;

        if (socket != null) {
            try {
                hr = socket.getInputStream().read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return hr;
    }*/

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
            if (btAdapter.isEnabled()) {
                mFragListener.onCreateSnackbar(ptAge, "Bluetooth already enabled.");
            } else {
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

        Set<BluetoothDevice> pairedDevices = btAdapter.getBondedDevices();
        ArrayList list = new ArrayList();

        if (pairedDevices.size() > 0) {
            for (BluetoothDevice bt : pairedDevices) {
                list.add(bt.getName() + "\n" + bt.getAddress());
            }
        } else {
            return false;
        }

        // Get the device MAC address, the last 17 chars in the View
        String info = ((TextView) view).getText().toString();
        String address = info.substring(info.length() - 17);

        new ConnectBluetooth(address).execute("");

        return true;
    }

    private void disconnectBt() {
        if (btSocket != null) {
            try {
                btSocket.close();
            } catch (IOException e) {
                mFragListener.onCreateSnackbar(ptAge, "Error disconnecting device.");
                e.printStackTrace();
            }
        }
    }

    private void sendData(String... data) {
        if (btSocket != null) {
            try {
                for (String d : data) {
                    btSocket.getOutputStream().write(d.getBytes());
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private class ConnectBluetooth extends AsyncTask {

        private boolean successConnect = true; //if here, it's almost connected

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
            try {
                if (btSocket == null || !isBtConnected) {
                    btAdapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice device = btAdapter.getRemoteDevice(address);
                    btSocket = device.createInsecureRfcommSocketToServiceRecord(uuid);
                    btSocket.connect(); // start connection
                }
            } catch (IOException e) {
                successConnect = false;
                e.printStackTrace();
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     */
    public interface MainFragInteractionListener {

    }
}
