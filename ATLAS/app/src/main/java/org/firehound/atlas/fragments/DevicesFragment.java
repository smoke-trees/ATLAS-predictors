package org.firehound.atlas.fragments;


import android.annotation.SuppressLint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;

import org.firehound.atlas.R;
import org.firehound.atlas.adapters.DeviceListRecyclerViewAdapter;
import org.firehound.atlas.models.BlockchainAPI;
import org.firehound.atlas.models.DeviceDataArrayModel;
import org.firehound.atlas.models.DeviceDataModel;
import org.firehound.atlas.models.EventBodyModel;
import org.firehound.atlas.models.EventDataModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

/**
 * A simple {@link Fragment} subclass.
 */
public class DevicesFragment extends Fragment {
    private static final String TAG = "DevicesFragment";
    private Retrofit retrofit;
    private BlockchainAPI blockchainAPI;
    private List<String> timeStamps = new ArrayList<>();
    private List<String> hashes = new ArrayList<>();
    private DeviceListRecyclerViewAdapter adapter = new DeviceListRecyclerViewAdapter(timeStamps, hashes);
    private ShimmerFrameLayout shimmerFrameLayout;
    public static String currentHash;

    public DevicesFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_devices, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        retrofit = new Retrofit.Builder()
                .baseUrl("https://swarm-blockchain.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        blockchainAPI = retrofit.create(BlockchainAPI.class);
        RecyclerView recyclerView = view.findViewById(R.id.device_data_recyclerview);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        shimmerFrameLayout = view.findViewById(R.id.shimmer_container);


        adapter.setClickListener(v -> {
            String hashText = ((TextView) v.findViewById(R.id.hash_text)).getText().toString();
            Call<EventDataModel> swarmDataCall = blockchainAPI.getSavedEvent(hashText);
            swarmDataCall.enqueue(new Callback<EventDataModel>() {
                @Override
                public void onResponse(@NonNull Call<EventDataModel> call, @NonNull Response<EventDataModel> response) {
                    new SwarmDialogFragment(response.body()).show(requireFragmentManager(), "ur_it");
                }

                @Override
                public void onFailure(@NonNull Call<EventDataModel> call, @NonNull Throwable t) {
                    Log.e(TAG, "onFailure: getting swarm data failed", t);
                }
            });
        });

//        Call<DeviceDataModel> deviceCountCall = blockchainAPI.getDeviceCount();
//        deviceCountCall.enqueue(new Callback<DeviceDataModel>() {
//            @Override
//            public void onResponse(@NonNull Call<DeviceDataModel> call, @NonNull Response<DeviceDataModel> response) {
//                deviceNumber = Integer.parseInt(Objects.requireNonNull(response.body()).getData());
//            }
//
//            @Override
//            public void onFailure(@NonNull Call<DeviceDataModel> call, @NonNull Throwable t) {
//                Log.e(TAG, "onFailure: getDeviceCount failed", t);
//            }
//        });
        FetchDataTask task = new FetchDataTask();
        task.execute();

    }

    @Override
    public void onResume() {
        super.onResume();
        shimmerFrameLayout.startShimmer();
    }

    @Override
    public void onPause() {
        shimmerFrameLayout.stopShimmer();
        super.onPause();
    }

    @SuppressLint("StaticFieldLeak")
    private class FetchDataTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            Call<DeviceDataArrayModel> deviceTimeStampCall = blockchainAPI.getDeviceTimeStamp("0x7AAF1FD79329c3Ba3fEab3FBbfdA0eb9C01344Ad");
            try {
                timeStamps.addAll(Objects.requireNonNull(deviceTimeStampCall.execute().body()).getData());
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (String timestamp : timeStamps) {
                int currTimestamp = Integer.parseInt(timestamp);
                Call<DeviceDataModel> deviceHashCall = blockchainAPI.getDeviceData("0x7AAF1FD79329c3Ba3fEab3FBbfdA0eb9C01344Ad", currTimestamp);
                try {
                    hashes.add(Objects.requireNonNull(deviceHashCall.execute().body()).getData());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            adapter.notifyDataSetChanged();
            shimmerFrameLayout.stopShimmer();
            shimmerFrameLayout.setVisibility(View.GONE);
        }
    }
}
