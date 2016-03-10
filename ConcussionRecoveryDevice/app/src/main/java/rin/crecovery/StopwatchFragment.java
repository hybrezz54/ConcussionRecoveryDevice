package rin.crecovery;

import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link StopwatchFragInteractionListener} interface
 * to handle interaction events.
 * Use the {@link StopwatchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class StopwatchFragment extends Fragment implements ActivityInteractionListener {

    private StopwatchFragInteractionListener mListener;

    private TextView watch;

    private Handler handler;
    private Runnable runnable;

    private boolean isStopwatchStarted = false;
    private int minute = 0;
    private int seconds = 0;

    public StopwatchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment StopwatchFragment.
     */
    public static StopwatchFragment newInstance() {
        StopwatchFragment fragment = new StopwatchFragment();
        /*Bundle args = new Bundle();
        fragment.setArguments(args);*/
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_stopwatch, container, false);

        watch = (TextView) view.findViewById(R.id.watch);

        handler = new Handler();
        runnable = new Runnable() {
            @Override
            public void run() {
                if (seconds == 60) {
                    seconds = 0;
                    minute += 1;
                } else {
                    seconds += 1;
                }

                String time = String.valueOf(minute) + ":"
                        + String.valueOf(seconds);
                watch.setText(time);
            }
        };

        return view;
    }

    @Override
    public void onButtonPressed() {
        if (isStopwatchStarted) {
            handler.removeCallbacks(runnable);
        } else {
            handler.postDelayed(runnable, 1000L);
            isStopwatchStarted = true;
        }
    }

    @Override
    public boolean bluetooth() {
        return false;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof StopwatchFragInteractionListener) {
            mListener = (StopwatchFragInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement StopwatchFragInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
        handler.removeCallbacks(runnable);
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface StopwatchFragInteractionListener {
    }
}
