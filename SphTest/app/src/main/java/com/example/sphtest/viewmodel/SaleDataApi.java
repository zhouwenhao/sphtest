package com.example.sphtest.viewmodel;

import com.example.sphtest.model.SaleFieldEntity;
import com.example.sphtest.model.SaleRecordEntity;
import com.example.sphtest.net.UrlConf;
import com.example.sphtest.net.api.BaseApi;
import com.example.sphtest.net.tag.Api;
import com.example.sphtest.net.tag.ParamGet;

import java.util.ArrayList;
import java.util.List;

public class SaleDataApi extends BaseApi<SaleDataApi.SaleResponse> {

    public SaleDataApi(int nums){
        limit = nums;
        resource_id = "a807b7ab-6cad-4aa6-87d0-e283a7353a0f";
    }

    @Api
    public static String url = UrlConf.SPH_SALE_DATA;

    @ParamGet
    public Integer limit;
    @ParamGet
    public String resource_id;

    public static class SaleResponse extends BaseApi.BaseResponse{
        private SaleResultEntity result;

        public SaleResultEntity getResult() {
            return result;
        }

        public void setResult(SaleResultEntity result) {
            this.result = result;
        }
    }

    public static class SaleResultEntity{
        private String resource_id;
        private List<SaleFieldEntity> fields;
        private List<SaleRecordEntity> records;
        private int limit;
        private int total;

        public String getResource_id() {
            return resource_id;
        }

        public void setResource_id(String resource_id) {
            this.resource_id = resource_id;
        }

        public List<SaleFieldEntity> getFields() {
            return fields == null ? new ArrayList<SaleFieldEntity>(0) : fields;
        }

        public void setFields(List<SaleFieldEntity> fields) {
            this.fields = fields;
        }

        public List<SaleRecordEntity> getRecords() {
            return records == null ? new ArrayList<SaleRecordEntity>(0) : records;
        }

        public void setRecords(List<SaleRecordEntity> records) {
            this.records = records;
        }

        public int getLimit() {
            return limit;
        }

        public void setLimit(int limit) {
            this.limit = limit;
        }

        public int getTotal() {
            return total;
        }

        public void setTotal(int total) {
            this.total = total;
        }
    }
}
