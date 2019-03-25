package org.firehound.atlas.fragments;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import org.firehound.atlas.R;
import org.firehound.atlas.models.BlockchainAPI;
import org.firehound.atlas.models.DeviceDataModel;
import org.firehound.atlas.models.EventBodyModel;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddEntryFragment extends Fragment {
    @BindView(R.id.event_data_TIL)
    TextInputEditText eventData;
    @BindView(R.id.event_status_TIL)
    TextInputEditText eventStatus;
    @BindView(R.id.hash_TIL)
    TextInputEditText hash;
    @BindView(R.id.submit_button)
    MaterialButton submitButton;
    private Retrofit retrofit;
    private BlockchainAPI blockchainAPI;

    public AddEntryFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_add_entry, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, requireActivity());
        retrofit = new Retrofit.Builder()
                .baseUrl("https://swarm-blockchain.herokuapp.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        blockchainAPI = retrofit.create(BlockchainAPI.class);
        submitButton.setOnClickListener(v -> {
            EventBodyModel bodyModel = new EventBodyModel();
            bodyModel.setDeviceId(Objects.requireNonNull(hash.getText()).toString());
            bodyModel.setEventData(Objects.requireNonNull(eventData.getText()).toString());
            bodyModel.setEventStatus(Boolean.parseBoolean(Objects.requireNonNull(eventStatus.getText()).toString()));

            Call<DeviceDataModel> createEventCall = blockchainAPI.sendData(bodyModel);
            createEventCall.enqueue(new Callback<DeviceDataModel>() {
                @Override
                public void onResponse(@NonNull Call<DeviceDataModel> call, @NonNull Response<DeviceDataModel> response) {

                }

                @Override
                public void onFailure(@NonNull Call<DeviceDataModel> call, @NonNull Throwable t) {

                }
            });
        });


    }
}
