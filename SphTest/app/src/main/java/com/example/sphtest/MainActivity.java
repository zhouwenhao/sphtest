package com.example.sphtest;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import com.example.sphtest.model.AppState;
import com.example.sphtest.model.SaleFieldEntity;
import com.example.sphtest.model.SaleRecordEntity;
import com.example.sphtest.viewmodel.SphViewModel;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.test_load)
    Button mTestLoad;
    @BindView(R.id.test_net_text)
    TextView mTestNetText;
    @BindView(R.id.test_feild_text)
    TextView mTestFeildText;
    @BindView(R.id.test_record_text)
    TextView mTestRecordText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        initSubscribe();
        loadData();
    }

    private void initSubscribe() {
        SphViewModel.getInstance().subscribeAppState(this, new Observer<AppState>() {
            @Override
            public void onChanged(AppState appState) {
                mTestNetText.setText("Net State = " + appState.mIsSaleApiOk);
            }
        });
        SphViewModel.getInstance().subscribeSaleField(this, new Observer<List<SaleFieldEntity>>() {
            @Override
            public void onChanged(List<SaleFieldEntity> list) {
                mTestFeildText.setText("Filed.size = " + list.size());
            }
        });
        SphViewModel.getInstance().subscribeSaleRecord(this, new Observer<List<SaleRecordEntity>>() {
            @Override
            public void onChanged(List<SaleRecordEntity> list) {
                mTestRecordText.setText("Record.size = " + list.size());
            }
        });
    }

    private void loadData() {
        SphViewModel.getInstance().loadSaleData();
    }

    @OnClick(R.id.test_load)
    public void onViewClicked() {
        loadData();
    }
}
